/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.loggermanager.data.LoggerData;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class DatabaseSocketHandler implements Runnable {
    public static HookupHandler hookupHandler;
    private ConcurrentLinkedQueue<DatabaseSocketThread> currentlyRunning = new ConcurrentLinkedQueue();
    private HashMap<String, UnsentMessageQueue> unsentQueues = new HashMap();
    private AddressList addressList;
    
    public AtomicBoolean paused = new AtomicBoolean(false);
    
   
    @Autowired
    private Database database;
    
    private Logger log;
    
    @Autowired
    public DatabaseSocketHandler(Logger log) {
        this.log = log;
        addressList = new AddressList(log);
    }
    
    public void startListener(String localAddress) {
        if (!localAddress.equals(""))
            addressList.register(localAddress);
        
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        ServerSocket serverSocket = getServerSocket();
        sendStartupMessage();
        resendAllPersistedObjects();
        handleSockets(serverSocket);
    }
    
    /**
     *        ....TODO....
     * make this function async and
     * persist object to disk until it
     * is able to be processed by the 
     * rest
     */
    public synchronized void objectSaved(DataCommon data, Credentials credentials) {
        if (ignoreSyncData(data)) {
            return;
        }
            
        for (String address : addressList.getIpaddressesThatAreNotMine()) {
            DataObjectSavedMessage saved = createObjectSaved(data, credentials);
            sendMessage(saved, address);
        }
    }
    
    public synchronized void resend(String address) {
        UnsentMessageQueue messageQueue = getUnsentQueue(address);
        List<DataObjectSavedMessage> savedObjects = messageQueue.getObjects();
        unsentQueues.remove(address);
        messageQueue.destroy();
        
        for (DataObjectSavedMessage objectSaved : savedObjects) {
            DataCommon data = database.getObject(objectSaved.credentials, objectSaved.data.id);
            DataObjectSavedMessage saved = createObjectSaved(data, objectSaved.credentials);
            sendMessage(saved, address);
        }
    }
    
    public synchronized void hookup(String localAddress, String remoteAddress) {
        addressList.clear();
        addressList.register(localAddress);
        addressList.register(remoteAddress);
        
        if (addressList.getMyAddress().equals("")) {
            System.out.println("The address you set as local address is not connected to any interfaces");
            System.exit(0);
        }
            
        if (addressList.getMyAddress().equals(remoteAddress)) {
            System.out.println("You can not sync with your own computer");
            System.exit(0);  
        } 
        
        log.info(this, "Starting hookup procedure, connecting to " + remoteAddress);
        HookupMessage hookup = new HookupMessage();
        hookup.ownerIpaddress = addressList.getMyAddress();
        
        boolean success = sendMessage(hookup, remoteAddress);
        if (!success) {
            System.out.println("Was not able start hookup process, connection refused: " + remoteAddress);
            System.exit(0);
        }
    }
    
    public synchronized void hookupMessageRetreived(HookupHandler hookupHandler) throws ErrorException { 
        addressList.register(hookupHandler.getClientServerAddress());
        for (String address : addressList.getIpaddressesThatAreNotMine()) {
            AddServerMessage message = new AddServerMessage();
            message.fromServer = addressList.getMyAddress();
            message.serverAddress = hookupHandler.getClientServerAddress();
            boolean success = sendMessage(message, address);
            if (!success) {
                throw new ErrorException(1);
            }
        }
    }
    
    public synchronized void addServer(String serverAddress, String fromServer) {
        addressList.register(serverAddress);
        Paused pausedMessage = new Paused();
        pausedMessage.ipaddress = addressList.getMyAddress();
        sendMessage(pausedMessage, fromServer);
        paused.set(true);
        
        int counter = 0;
        log.info(this, "Paused");
        while(paused.get()) {
            try { Thread.sleep(100); } catch (Exception ex) {}
            counter++;
            if (counter > 1200) {
                log.warning(this, "Did not receive continue message after 120sec pausing, continueing as normalt");
                break;
            }
        }
        
        log.info(this, "Continued");
    }
   
    private void handleSockets(ServerSocket serverSocket) {
        while(true) {
            try {
                Socket socket = serverSocket.accept();
                DatabaseSocketThread thread = new DatabaseSocketThread(socket, database, this);
                currentlyRunning.add(thread);
                new Thread(thread).start();
            } catch (IOException ex) {
                ex.printStackTrace();
            } 
       }    
    }
    
    private ServerSocket getServerSocket() {
        try {
            return new ServerSocket(31337);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        
        return null;
    }
   
    private DataObjectSavedMessage createObjectSaved(DataCommon data, Credentials credentials) {
        DataObjectSavedMessage saved = new DataObjectSavedMessage();
        saved.data = data;
        saved.credentials = credentials;
        return saved;
    }
    
    private boolean sendMessage(Serializable object, String address) {
        try (
            Socket socket = new Socket(address, 31337); 
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())
        ) {
            log.info(this, "sending message " + object);
            objectOutputStream.writeObject(object);
        } catch (Exception ex) {
            if (object instanceof DataObjectSavedMessage) {
                getUnsentQueue(address).addObject((DataObjectSavedMessage)object);
            }
            return false;
        }
        
        return true;
    }

    private boolean ignoreSyncData(DataCommon data) {
        if (data instanceof LoggerData) {
            return true;
        }
            
        return false;
    }

    private UnsentMessageQueue getUnsentQueue(String address) {
        UnsentMessageQueue queue = unsentQueues.get(address);
        if (queue == null) {
            queue = new UnsentMessageQueue(address);
            unsentQueues.put(address, queue);
        }
        return queue;
    }
    
    private void sendStartupMessage() {
        String address = addressList.getMyAddress();

        if (address.equals("")) {
            log.error(this, "Could not send startup message, not able to find local ipaddress");
            System.exit(-1);
        } else {
            ServerStartedMessage serverStarted = new ServerStartedMessage();
            serverStarted.address = address;

            for (String sendToAddress : addressList.getIpaddressesThatAreNotMine()) {
                sendMessage(serverStarted, sendToAddress);
            }
        }
    }

    private void resendAllPersistedObjects() {
        unsentQueues = UnsentMessageQueue.loadPersisted();
        for (UnsentMessageQueue unsentQueue : unsentQueues.values()) {
            resend(unsentQueue.getAddress());
        }
    }
    
    public void remove(DatabaseSocketThread remove) {
        currentlyRunning.remove(remove);
    }

    public synchronized void sendContinueMessage() {
        for (String address : addressList.getIpaddressesThatAreNotMine()) {
            ContinueMessage continueMessage = new ContinueMessage();
            sendMessage(continueMessage, address);
        }
    }
    
    public synchronized void sendDatabase(String clientServerAddress) {
        DatabaseSyncMessage syncMessage = new DatabaseSyncMessage();
        sendMessage(syncMessage, clientServerAddress);
    }

}
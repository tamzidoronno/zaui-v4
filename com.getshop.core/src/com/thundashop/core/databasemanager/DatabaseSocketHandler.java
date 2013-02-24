/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.thundashop.core.common.ASyncHandler;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.loggermanager.data.LoggerData;
import com.thundashop.core.reportingmanager.data.Report;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class DatabaseSocketHandler implements Runnable {
    private File messageQueueFile = new File("data/messagesToProcess.txt");
    public ReentrantLock fairLock = new ReentrantLock(true);
    public static HookupHandler hookupHandler;
    private ConcurrentLinkedQueue<DatabaseSocketThread> currentlyRunning = new ConcurrentLinkedQueue();
    private HashMap<String, UnsentMessageQueue> unsentQueues = new HashMap();
    private AddressList addressList;
    private ArrayDeque messageQueue = new ArrayDeque();
    public AtomicBoolean paused = new AtomicBoolean(false);
    
   
    @Autowired
    private Database database;
    
    private Logger log;
    
    @Autowired
    public DatabaseSocketHandler(Logger log) {
        this.log = log;
        addressList = new AddressList(log);
        loadMessageQueueFromDisk();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() { 
                System.out.println("Checking if there is now currently running data transfer before exiting, please wait");
                int i = 0;
                while(!currentlyRunning.isEmpty()) {
                    i++;
                    try { Thread.sleep(100); } catch (Exception ex) { }
                    if (i > 20) {
                        i=0;
                        System.out.println("Running: " + currentlyRunning.size());
                    }
                }
            }
         });
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
    public void objectSaved(DataCommon data, Credentials credentials) {
        ASyncHandler handler = new ASyncHandler(data, credentials);
        messageQueue.add(handler);
        saveMessageQueueToDisk();
        handler.handleMessage(this);
    }
    
    public void handleMessage(DataCommon data, Credentials credentials, ASyncHandler handler) {
        fairLock.lock();
        if (!ignoreSyncData(data)) {
            for (String address : addressList.getIpaddressesThatAreNotMine()) {
                DataObjectSavedMessage saved = createObjectSaved(data, credentials);
                sendMessage(saved, address);
            }            
        }
        messageQueue.remove(handler);
        saveMessageQueueToDisk();
        fairLock.unlock();
    }
    
    private void saveMessageQueueToDisk() {
        try (
            FileOutputStream outFileStream = new FileOutputStream(messageQueueFile);
            ObjectOutputStream outStream = new ObjectOutputStream(outFileStream);
        ) {
            outStream.writeObject(messageQueue);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }
    
    private void loadMessageQueueFromDisk() {
        if (!messageQueueFile.exists()) {
            return;
        }
        
        try (
            FileInputStream inStream = new FileInputStream(messageQueueFile);
            ObjectInputStream objectStream = new ObjectInputStream(inStream);
        ) {
            messageQueue = (ArrayDeque)objectStream.readObject();
            Iterator<ASyncHandler> i = messageQueue.iterator();
            while(i.hasNext()) {
                ASyncHandler handler = i.next();
                handler.handleMessage(this);
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex2) {
            ex2.printStackTrace();
        } catch (IOException ex3) {
            ex3.printStackTrace();
        }
    }
    
    public void resend(String address) {
        fairLock.lock();
        UnsentMessageQueue messageQueue = getUnsentQueue(address);
        List<DataObjectSavedMessage> savedObjects = messageQueue.getObjects();
        unsentQueues.remove(address);
        messageQueue.destroy();
        
        for (DataObjectSavedMessage objectSaved : savedObjects) {
            DataCommon data = database.getObject(objectSaved.credentials, objectSaved.data.id);
            DataObjectSavedMessage saved = createObjectSaved(data, objectSaved.credentials);
            sendMessage(saved, address);
        }
        fairLock.unlock();
    }
    
    public void hookup(String localAddress, String remoteAddress) {
        fairLock.lock();
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
        hookup.connectedToIpaddress = remoteAddress;
        hookup.ownerIpaddress = addressList.getMyAddress();
        
        
        boolean success = sendMessage(hookup, remoteAddress);
        if (!success) {
            System.out.println("Was not able start hookup process, connection refused: " + remoteAddress);
            System.exit(0);
        }
        fairLock.unlock();
    }
    
    public void hookupMessageRetreived(HookupHandler hookupHandler) throws ErrorException { 
        fairLock.lock();
        addressList.register(hookupHandler.getClientServerAddress());
        addressList.register(hookupHandler.getClientConnectedToAddress());
        for (String address : addressList.getIpaddressesThatAreNotMine()) {
            AddServerMessage message = new AddServerMessage();
            message.fromServer = addressList.getMyAddress();
            message.serverAddress = hookupHandler.getClientServerAddress();
            boolean success = sendMessage(message, address);
            if (!success) {
                throw new ErrorException(1);
            }
        }
        fairLock.unlock();
    }
    
    public void addServer(String serverAddress, String fromServer) {
        fairLock.lock();
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
        fairLock.unlock();
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
        if (data instanceof LoggerData || data instanceof Report) {
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

    /**
     * This function will ensure that
     * all threads are finished processing before
     * exporting of database.
     */
    public void waitForSingleThreadRunning() {
        while (currentlyRunning.size() > 1) {
            try {Thread.sleep(1000);} catch (Exception ex) {}
        }
    }
    
    public void sendContinueMessageAndDatabase(String clientServerAddress) {
        fairLock.lock();
        for (String address : addressList.getIpaddressesThatAreNotMine()) {
            ContinueMessage continueMessage = new ContinueMessage();
            sendMessage(continueMessage, address);
        }
        
        DatabaseSyncMessage syncMessage = database.getSyncMessage();
        syncMessage.completeNetwork = addressList.getAllAddresses();
        sendMessage(syncMessage, clientServerAddress);
        fairLock.unlock();
    }

    public void processNormalMessage(DataObjectSavedMessage objectSaved) {
        fairLock.lock();
        DataCommon data = objectSaved.data;
        Credentials credentials = objectSaved.credentials;
        database.objectFromOtherSource(data, credentials);
        fairLock.unlock();
    }

    public void syncServer(DatabaseSyncMessage sync) {
        fairLock.lock();
        database.save(sync);
        for (String address : sync.completeNetwork) {
            addressList.register(address);
        }
        fairLock.unlock();
    }
}
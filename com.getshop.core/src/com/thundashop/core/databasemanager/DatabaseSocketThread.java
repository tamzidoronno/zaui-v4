/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ErrorMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */
public class DatabaseSocketThread implements Runnable {
    private Socket socket;
    private DatabaseSocketHandler socketHandler;
    
    public DatabaseSocketThread(Socket socket, DatabaseSocketHandler socketHandler) {
        this.socket = socket;
        this.socketHandler = socketHandler;
    }

    private void processNormalMessage(Object object) {
        if (object instanceof DataObjectSavedMessage) {
            DataObjectSavedMessage objectSaved = (DataObjectSavedMessage)object;
            socketHandler.processNormalMessage(objectSaved);
        }
    }
    
    private void processStartupMessage(Object object) {
        if (object instanceof ServerStartedMessage) {
            ServerStartedMessage serverStarted = (ServerStartedMessage)object;
            socketHandler.resend(serverStarted.address);
        }
    }
    
    private void handleHookupMessage(Object object) throws ErrorException {
        if (object instanceof HookupMessage) {
            HookupMessage hookup = (HookupMessage)object;
            DatabaseSocketHandler.hookupHandler = new HookupHandler(hookup);
            socketHandler.hookupMessageRetreived(DatabaseSocketHandler.hookupHandler);
        }
    }
    
    private void handleErrorMessage(Object object) {
        if (object instanceof ErrorMessage) {
            ErrorMessage error = (ErrorMessage)object;
            if (error.errorCode == 0) {
                System.out.println("Timed out while waiting for a server to respond, not all server responded to the wait signal");
            }
            if (error.errorCode == 1) {
                System.out.println("Can not hookup now, there are servers that are offline. Please check that and try again.");
            }
        }
    }
    
    private void processAddServerMessage(Object object) {
        if (object instanceof AddServerMessage) {
            AddServerMessage addServer = (AddServerMessage)object;
            socketHandler.addServer(addServer.serverAddress, addServer.fromServer);
        }
    }
    
    private void handleContinueMessage(Object object) {
        if (object instanceof ContinueMessage) {
            socketHandler.paused.set(false);
        }
    }
    
    private void handlePausedMessage(Object object) {
        if (object instanceof Paused &&  DatabaseSocketHandler.hookupHandler != null) {
            Paused paused = (Paused)object;            
            DatabaseSocketHandler.hookupHandler.serverResponded(paused);
            if (DatabaseSocketHandler.hookupHandler.ready()) {
                socketHandler.waitForSingleThreadRunning();
                socketHandler.sendContinueMessageAndDatabase(DatabaseSocketHandler.hookupHandler.getClientServerAddress());
                DatabaseSocketHandler.hookupHandler = null;
            }
        }
    }
    
    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Object object = objectInputStream.readObject(); 
            processNormalMessage(object);
            processStartupMessage(object);
            processAddServerMessage(object);
            handleHookupMessage(object);
            handleErrorMessage(object);
            handlePausedMessage(object);
            handleContinueMessage(object);
            handleSyncMessage(object);
        } catch (ErrorException ex) {
            sendErrorMessage(ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseSocketThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatabaseSocketThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        socketHandler.remove(this);
       
    } 

    private void sendErrorMessage(ErrorException ex) {
        try (
            ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream())) 
        {
            ErrorMessage message  = new ErrorMessage(ex);
            stream.writeObject(message);
        } catch (IOException ex2) {
            ex.printStackTrace();
            System.out.println("================");
            ex2.printStackTrace();
        }
    }

    private void handleSyncMessage(Object object) {
        if (object instanceof DatabaseSyncMessage) {
            DatabaseSyncMessage sync = (DatabaseSyncMessage)object;
            socketHandler.syncServer(sync);
            socketHandler.remove(this);
            System.out.println("Sync completed, ready to start server");
            System.exit(0);
        }
    }

}
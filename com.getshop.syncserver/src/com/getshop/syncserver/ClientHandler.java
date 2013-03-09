package com.getshop.syncserver;

import com.thundashop.api.managers.GetShopApi;
import com.thundashop.core.usermanager.data.User;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author boggi
 */
class ClientHandler extends Thread {

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private String storeAddress;
    private String username;
    private String password;
    private GetShopApi api;

    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    }

    @Override
    public void run() {
        try {
            if(!authenticate()) {
                return;
            }
            MonitorOutgoingEvents outgoing = new MonitorOutgoingEvents(socket, api);
            outgoing.start();
            monitorIncomingEvents();
        } catch (IOException ex) {
            try {
                //Failed reading should cause disconnect.
                socket.close();
            } catch (IOException ex1) {
                ex1.printStackTrace();
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String readSocketLine() {
        try {
            return in.readLine();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean authenticate() throws Exception {
        storeAddress = readSocketLine();
        username = readSocketLine();
        password = readSocketLine();
        
        System.out.println("Got connection:" + storeAddress + " : " + username + " : " + password);
        
        UUID idOne = UUID.randomUUID();
        
        api = new GetShopApi(25554, "localhost", idOne.toString(), storeAddress);
        System.out.println("Logging on");
        User result = api.getUserManager().logOn(username, password);
        System.out.println("Logged on as : " + result.fullName);
        if(api.getUserManager().isLoggedIn()) {
            writeLineToSocket("OK");
            return true;
        } else {
            writeLineToSocket("Logon failed, please check your input");
            return false;
        }
    }

    private void writeLineToSocket(String message) {
        out.write(message+ "\n");
        out.flush();
    }

    private void monitorIncomingEvents() throws IOException {
        while(true) {
            String line = readSocketLine();
            System.out.println(line);
            if(line == null) {
                return;
            }
            
            switch(line) {
                case "IN_CLOSE_WRITE":
                    fetchFile();
                    break;
                case "IN_DELETE":
                    removeFile();
                    break;
            }
        }
    }

    private void fetchFile() throws IOException {
        String path = readSocketLine();
        System.out.println("Path: " + path);
        path = translatePath(path);
        long length = Long.parseLong(readSocketLine());
        InputStream stream = socket.getInputStream();
        byte[] by = new byte[1024];

        long total = 0;
        File file = new File(path);
        String folderPath = path.replace(file.getName(), "");
        File fPath = new File(folderPath);
        fPath.mkdirs();
        file.delete();
        
        if(length == 0) {
            file.createNewFile();
        } else {
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path))) {
                while(true){
                    int size = stream.read(by);
                    if(size > 0) {
                        System.out.println(size);
                        bos.write(by, 0, size);
                        bos.flush();
                        total += size;
                        if(total == length) {
                            break;
                        }
                    } else {
                        writeLineToSocket("An error occured while streaming the file.");
                        return;
                    }
                }
            }
        }
        
        System.out.println("Total bytes read: " + total);
        this.writeLineToSocket("OK");
    }

    private String translatePath(String path) {
        if(!path.contains("/apps/") && !path.contains("\\apps\\")) {
            writeLineToSocket("Invalid path in file trying to be sent, the apps folder has to be its root");
        }
        
        if(path.contains("/apps/")) {
            path = path.substring(path.indexOf("/apps/")+6);
        }
        if(path.contains("\\apps\\")) {
            path = path.substring(path.indexOf("\\apps\\")+6);
        }
        
        return "/getshop/" + storeAddress + "/" + path;
    }

    private void removeFile() {
        String path = readSocketLine();
        path = translatePath(path);
        File file = new File(path);
        file.delete();
        writeLineToSocket("OK");
    }
}

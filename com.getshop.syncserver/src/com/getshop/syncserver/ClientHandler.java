package com.getshop.syncserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
    private String storeId;
    private String username;
    private String password;

    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    }

    @Override
    public void run() {
        if(!authenticate()) {
            return;
        }
        try {
            monitorIncomingEvents();
        } catch (IOException ex) {
            try {
                //Failed reading should cause disconnect.
                socket.close();
            } catch (IOException ex1) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return;
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

    private boolean authenticate() {
        storeId = readSocketLine();
        username = readSocketLine();
        password = readSocketLine();
        
        System.out.println("Got connection:" + storeId + " : " + username + " : " + password);
        
        writeLineToSocket("OK");
        return true;
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
        
        return "/getshop/" + storeId + "/" + path;
    }

    private void removeFile() {
        String path = readSocketLine();
        path = translatePath(path);
        File file = new File(path);
        file.delete();
        writeLineToSocket("OK");
    }
}

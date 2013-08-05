package com.getshop.syncclient;

import com.getshop.syncserver.Message;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GetShopCom extends Thread {

    List<FileObject> filesToPush = new ArrayList();
    private Socket socket;
    BufferedReader in = null;
    DataOutputStream writer = null;
    SyncClientJava client = null;
    Date lastping = new Date();
    private HashMap<Long, Path> movedFiles = new HashMap();
    public String user;
    public String password;
    public String address;
    //All filepaths found in this map will be ignored pushed to the server for 5 seconds.
    private HashMap<String, Date> disablePushFile = new HashMap();
    private DataInputStream reader;

    GetShopCom(SyncClientJava client) {
        this.client = client;
    }

    void addFileObject(FileObject object) {
        //Ignore "hidden" files.
        if (object.file != null && (object.file.getAbsolutePath().contains("/.") || object.file.getAbsolutePath().contains("\\."))) {
            return;
        }
        if (disablePushFile.containsKey(object.file.getAbsolutePath())) {
            Date waiter = disablePushFile.get(object.file.getAbsolutePath());
            if ((System.currentTimeMillis() - waiter.getTime()) > 5000) {
                disablePushFile.remove(object.file.getAbsolutePath());
            } else {
                return;
            }
        }

        filesToPush.add(object);
    }

    public void run() {
        connectToServer();
        int i = 0;
        while (true) {
            clearMovedFiles();
            if (filesToPush.isEmpty()) {
                try {
                    if (reader.available() > 0) {
                        processIncomingMessage(readMessageFromSocket());
                        i = 0;
                    }
                    Thread.sleep(100);
                } catch (Exception d) {
                }
                if (i == 50) {
                    i = 0;
                    if (!ping()) {
                        connectToServer();
                    }
                }
                i++;
            } else {
                FileObject object = filesToPush.get(0);
                filesToPush.remove(0);
                i = 0;
                try {
                    object = preProcessEvent(object);
                    if (object != null) {
                        processObject(object);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private void connectToServer() {
        while (true) {
            try {
                System.out.println("Connecting to : filesync.getshop.com");
                socket = new Socket("filesync.getshop.com", 25557);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new DataOutputStream(socket.getOutputStream());
                reader = new DataInputStream(socket.getInputStream());

                System.out.println("Connected to server, now authenticating");
                Message msg = new Message();
                msg.username = user;
                msg.password = password;
                msg.address = address;
                msg.type = Message.Types.authenticate;
                pushMessage(msg);
                Message response = readMessageFromSocket();
                if (response.type == Message.Types.authenticated) {
                    System.out.println("Authentication sucessful, writing configuration to config.txt");

                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("config.txt"), false))) {
                        bw.write(user);
                        bw.newLine();
                        bw.write(password);
                        bw.newLine();
                        bw.write(address);
                        bw.newLine();
                    }

                    checkAllFiles();
                    break;
                } else {
                    System.out.println("Failed to log on to server");
                    if (response.type == Message.Types.shutdown) {
                        System.exit(0);
                    }
                }
                Thread.sleep(2000);
            } catch (Exception e) {
                try {
                    Thread.sleep(2000);
                } catch (Exception d) {
                }
            }
        }
    }

    public byte[] readFile(File file) throws IOException {

        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ous != null) {
                    ous.close();
                }
            } catch (IOException e) {
            }

            try {
                if (ios != null) {
                    ios.close();
                }
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }

    private void pushMessage(Message msg) throws IOException {
        Gson gson = new Gson();
        byte[] result = gson.toJson(msg).getBytes();
        writer.writeInt(result.length);
        writer.write(result);
    }

    private Message readMessageFromSocket() throws IOException {
        int length = reader.readInt();
        int totalsize = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        do {
            int maxsizearray = length - totalsize;
            byte buffer[] = new byte[maxsizearray];
            int s = reader.read(buffer);
            baos.write(buffer, 0, s);
            totalsize += s;
        } while (totalsize != length);
        byte result[] = baos.toByteArray();

        Gson gson = new Gson();
        return gson.fromJson(new String(result), Message.class);
    }

    private void processObject(FileObject object) throws IOException {
        if (object.state.equals("ENTRY_MODIFY") || object.state.equals("ENTRY_CREATE")) {
            pushFileToServer(object);
        } else if (object.state.equals("ENTRY_DELETE")) {
            deleteFileFromServer(object);
        } else if (object.state.equals("ENTRY_MOVE")) {
            moveFileOnServer(object);
        }
    }

    private void pushFileToServer(FileObject object) throws IOException {
        if (!object.file.isFile()) {
            return;
        }
        Message msg = new Message();
        System.out.print("Sending: " + object.file.toString() + " : ");
        msg.type = Message.Types.sendfile;
        msg.filepath = object.file.getAbsolutePath();
        msg.data = readFile(object.file);
        pushMessage(msg);

        Message result = readMessageFromSocket();
        if (result.type != Message.Types.ok) {
            System.out.println("Failed sending file: " + result.errorMessage + " Type got : " + result.type);
        } else {
            System.out.println("File sent successfully");
        }
    }

    private void deleteFileFromServer(FileObject object) throws IOException {
        System.out.print("Deleting: " + object.file.toString() + " : ");
        Message msg = new Message();
        msg.type = Message.Types.deletefile;
        msg.filepath = object.file.getAbsolutePath();
        pushMessage(msg);

        Message result = readMessageFromSocket();
        if (result.type != Message.Types.ok) {
            System.out.println("Failed to delete file: " + result.errorMessage + " got type: " + result.type);
        } else {
            System.out.println("success");
        }
    }

    private boolean ping() {
        try {
            long diff = new Date().getTime() - lastping.getTime();
            if (diff < 5000) {
                return true;
            }
            lastping = new Date();
            
            Message msg = new Message();
            msg.type = Message.Types.ping;
            pushMessage(msg);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void moveFileOnServer(FileObject object) throws IOException {
        System.out.print("Moving: " + object.prev + " -> " + object.cur + " ");
        Message msg = new Message();
        msg.type = Message.Types.movefile;
        msg.fromPath = object.prev.toString();
        msg.toPath = object.cur.toString();
        pushMessage(msg);

        Message response = readMessageFromSocket();
        if (response.type == Message.Types.ok) {
            System.out.println(" success");
        } else {
            System.out.println(" failed " + response.errorMessage);
        }
    }

    private FileObject waitOneSecondForEvent() {
        int i = 0;
        do {
            if (!filesToPush.isEmpty()) {
                return filesToPush.get(0);
            }
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
            i++;
        } while (i != 100);
        return null;
    }

    private FileObject preProcessEvent(FileObject object) {
        if (object.state.equals("ENTRY_MOVE")) {
            for (Path movedPath : movedFiles.values()) {
                if (object.cur.startsWith(movedPath)) {
                    return null;
                }
            }
            movedFiles.put(new Date().getTime(), object.cur);
        }

        if (object.state.equals("ENTRY_DELETE")) {
            FileObject tmpEntry = waitOneSecondForEvent();
            if (tmpEntry != null) {
                if (tmpEntry.state.equals("ENTRY_CREATE")) {
                    //If an entry has been deleted and the next entry is a create event within 1 second time, then it is most likely a file that has been moved, watcher seems to think that it is okey to handle a move in two events...
                    filesToPush.remove(0);
                    tmpEntry.state = "ENTRY_MOVE";
                    tmpEntry.prev = object.cur;
                    return tmpEntry;
                }
                //The notifier seem to deleting the file and then moving it, 
                //java handles this stupid as well,  first delete the folder, then move event? WTF?
                if (tmpEntry.state.equals("ENTRY_MOVE")) {
                    filesToPush.remove(0);
                    return preProcessEvent(tmpEntry);
                }
            }
        }
        return object;
    }

    private void clearMovedFiles() {
        List<Long> toremove = new ArrayList();
        long now = new Date().getTime();
        for (Long time : movedFiles.keySet()) {
            long diff = now - time;
            if (diff >= 10000) {
                toremove.add(time);
            }
        }

        for (Long remove : toremove) {
            movedFiles.remove(remove);
        }
    }

    private void checkAllFiles() throws Exception {
        Message msg = new Message();
        msg.type = Message.Types.listfiles;

        pushMessage(msg);

        msg = readMessageFromSocket();

        if (msg.type == Message.Types.ok) {
            client.checkFileDirectory(msg.filelist);
        } else {
            System.out.println("Asked for filelist, but got something else in response");
        }
    }

    private void processIncomingMessage(Message msg) throws IOException {
        if (msg.type == Message.Types.sendfile) {
            //A new file is incoming.
            writeFile(msg.filepath, msg.data);
        }
        if (msg.type == Message.Types.ping) {
            msg = new Message();
            msg.type = Message.Types.pong;
            pushMessage(msg);
        }
    }

    private void writeFile(String filepath, byte[] data) {
        String completepath = client.rootpath + "/" + filepath;
        System.out.print("Writing : " + completepath);
        File f = new File(completepath);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        try {
            disablePushFile.put(f.getAbsolutePath(), new Date());
            Files.write(f.toPath(), data, StandardOpenOption.CREATE);
            disablePushFile.put(f.getAbsolutePath(), new Date());
        } catch (Exception e) {
            System.out.println(" - failed to write file.");
        }
        System.out.println(" - success");
    }
}

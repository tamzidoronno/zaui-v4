package com.getshop.syncserver;

import com.getshop.javaapi.GetShopApi;
import com.google.gson.Gson;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.ApplicationSynchronization;
import com.thundashop.core.appmanager.data.AvailableApplications;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO : Monitor server push messages, whenever a user wants to sync existing
 * app. TODO : Handle move files better. - Whenver a file is deleted, wait and
 * see if a create file event is instantly being added, then it is a move event.
 * - Whenever a folder is being moved, wait and check if subfolders is being
 * moved as well. - Getshop user should be able to handle all applications, no
 * mather what.
 *
 */
class ClientHandler extends Thread {

    private final Socket socket;
    private Message authObject;
    private GetShopApi api;
    private final DataInputStream reader;
    private User loggedOnUser;
    private String sessid;
    private String storeId;
    private final DataOutputStream writer;
    private boolean disconnected = false;
    AvailableApplications allApps;
    private String rootpath = "../com.getshop.client/app/";
    private Date lastActive;

    ClientHandler(Socket socket) throws IOException {
        File test = new File(".");
        this.socket = socket;
        this.socket.setTcpNoDelay(true);
        lastActive = new Date();
        reader = new DataInputStream(socket.getInputStream());
        writer = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            monitorIncomingEvents();
            System.out.println("Client got disconnected.");
        } catch (IOException ex) {
            try {
                ex.printStackTrace();
                //Failed reading should cause disconnect.
                System.out.println("Disconnecting");
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

    public static byte[] createChecksum(String filename) throws Exception {
        MessageDigest complete;
        try (InputStream fis = new FileInputStream(filename)) {
            byte[] buffer = new byte[1024];
            complete = MessageDigest.getInstance("MD5");
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
        }
        return complete.digest();
    }

    private void monitorIncomingEvents() throws IOException, Exception {
        int i = 0;
        while (true) {
            if (reader.available() > 0) {
                lastActive = new Date();
                Message msg = readMessageFromSocket();
                if (msg.type == Message.Types.authenticate) {
                    authenticate(msg);
                } else if (msg.type == Message.Types.sendfile) {
                    writeFile(msg);
                } else if (msg.type == Message.Types.ping) {
                    //Well, lastactive has been updated
                } else if (msg.type == Message.Types.deletefile) {
                    deleteFile(msg);
                } else if (msg.type == Message.Types.listfiles) {
                    listFiles();
                } else if (msg.type == Message.Types.movefile) {
                    moveFile(msg);
                }
                i = 0;
            } else {
                if (i == 200) {
                    checkActive();
                    checkForSyncRequests();
                    i = 0;
                }
                i++;
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                }
            }
            if (disconnected) {
                break;
            }
        }
    }

    private void authenticate(Message msg) throws Exception {
        System.out.println("Authenticating : " + msg.username);

        sessid = UUID.randomUUID().toString();
        Message response = new Message();
        try {
            api = new GetShopApi(25554, "localhost", sessid, msg.address);
            api.transport.setAutoReconnect(false);
            api.getStoreManager().initializeStore(msg.address, sessid);
            this.loggedOnUser = api.getUserManager().logOn(msg.username, msg.password);
            System.out.println("Logged on as : " + msg.username);

            if (api.getUserManager().isLoggedIn()) {
                response.type = Message.Types.authenticated;
                storeId = api.getStoreManager().getStoreId();
            } else {
                response.type = Message.Types.failed;
            }
        } catch (Exception e) {
            response.type = Message.Types.shutdown;
        }
        sendMessage(response);
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

    private void sendMessage(Message response) throws IOException {
        Gson gson = new Gson();
        byte[] result = gson.toJson(response).getBytes();

        writer.writeInt(result.length);
        writer.write(result);
    }

    private void writeFile(Message msg) throws Exception {
        System.out.println("Writing : " + msg.filepath);
        String translated = translatePath(msg.filepath);
        Message response = new Message();
        if (translated == null) {
            response.type = Message.Types.failed;
            response.errorMessage = "Invalid path in file trying to be sent, the apps folder has to be its root";
        } else {
            System.out.println("Translated to : " + translated);

            File file = new File(translated);
            if (!file.canWrite()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream output = new FileOutputStream(file);
            output.write(msg.data);
            output.flush();
            output.close();
            response.type = Message.Types.ok;
        }
        sendMessage(response);
    }

    private String translatePath(String path) throws Exception {
        if (!path.contains("/apps/") && !path.contains("\\apps\\")) {
            return null;
        }

        String appName = "";
        if (path.contains("/apps/")) {
            path = path.substring(path.indexOf("/apps/") + 6);
            if (path.indexOf("/") > 0) {
                appName = path.substring(0, path.indexOf("/"));
                path = path.substring(path.indexOf("/"));
            } else {
                appName = path;
            }
        }
        if (path.contains("\\apps\\")) {
            path = path.substring(path.indexOf("\\apps\\") + 6);
            if (path.indexOf("\\") > 0) {
                appName = path.substring(0, path.indexOf("\\"));
                path = path.substring(path.indexOf("\\"));
            } else {
                appName = path;
            }
        }

        ApplicationSettings settings = null;
        if (allApps != null) {
            for (ApplicationSettings apps : allApps.applications) {
                if (apps.appName.equals(appName)) {
                    settings = apps;
                }
            }
        }

        if (settings == null) {
            try {
                buildAllApps();
                for (ApplicationSettings apps : allApps.applications) {
                    if (apps.appName.equals(appName)) {
                        settings = apps;
                    }
                }
            } catch (Exception e) {
                settings = null;
            }
        }
        if (settings == null) {
            return null;
        }

        String translated = rootpath + convertUUID(settings.id) + "/" + path;
        return translated;
    }

    private String convertUUID(String uuid) {
        uuid = "ns_" + uuid.replace("-", "_");
        return uuid;
    }

    private void deleteFile(Message msg) throws Exception {
        String translated = translatePath(msg.filepath);
        Message response = new Message();
        if (translated == null) {
            response.type = Message.Types.failed;
            response.errorMessage = "Path does not exists";
        } else {
            File file = new File(translated);
            file.delete();
            response.type = Message.Types.ok;
            File parent = file.getParentFile();
            while(true) {
                if(parent.isDirectory() && parent.listFiles().length == 0) {
                    parent.delete();
                }
                if(parent.getName().startsWith("ns_")) {
                    break;
                }
                parent = parent.getParentFile();
            }
            
        }

        sendMessage(response);
    }

    private void moveFile(Message msg) throws Exception {

        String from = translatePath(msg.fromPath);
        String to = translatePath(msg.toPath);

        File curFile = new File(from);
        Message response = new Message();
        if (from == null || to == null) {
            response.type = Message.Types.failed;
            response.errorMessage = "Invalid path";
            sendMessage(response);
            return;
        }

        if (!curFile.exists()) {
            response.type = Message.Types.failed;
            response.errorMessage = "Source file does not exists";
            sendMessage(response);
            return;
        }
        System.out.println("Moving:" + from + " -> " + to);

        curFile.renameTo(new File(to));
        response.type = Message.Types.ok;
        sendMessage(response);
    }

    private void listFiles() throws Exception {
        System.out.println("Asked for filelist");
        Message msg = new Message();

        if (allApps == null) {
            buildAllApps();
        }
        List<FileSummary> summary = new ArrayList();
        msg.filelist = summary;
        for (ApplicationSettings app : allApps.applications) {
            File file = new File(rootpath + "/" + "ns_" + app.id.replaceAll("-", "_"));
            if(!file.exists()) {
                file.mkdirs();
            }
            getFileSummaryForApp(file.listFiles(), summary, app);
        }
        msg.type = Message.Types.ok;
        sendMessage(msg);
    }

    private List<FileSummary> getFileSummaryForApp(File[] files, List<FileSummary> result, ApplicationSettings app) throws Exception {
        if (files == null) {
            System.out.println("Failed to find file summery for app " + app.appName);
            return new ArrayList();
        }
        for (File file : files) {
            if (file.isDirectory()) {
                getFileSummaryForApp(file.listFiles(), result, app);
            } else {
                result.add(buildSummary(file, app));
            }
        }
        return result;
    }

    private FileSummary buildSummary(File file, ApplicationSettings app) throws Exception {
        FileSummary summary = new FileSummary();
        summary.md5 = createChecksum(file.getAbsolutePath());
        String path = file.getAbsolutePath();
        summary.path = app.appName + path.substring(path.indexOf("ns_" + app.id.replaceAll("-", "_")) + app.id.length() + 3);
        return summary;
    }

    private void buildAllApps() {
        try {
            allApps = api.getAppManager().getAllApplications();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(1000);
            } catch (Exception d) {
            }
        }
    }

    private void checkForSyncRequests() {
        if (api != null) {
            try {
                if (!api.transport.isConnected()) {
                    System.out.print("Disconnected from core server, ");
                    if (api.transport.reconnect()) {
                        System.out.println(" reconnected to core.");
                    } else {
                        System.out.println(" not able to connect");
                    }
                } else {
                    List<ApplicationSynchronization> appsToSync = api.getAppManager().getSyncApplications();
                    System.out.println(appsToSync.size());
                    if (appsToSync != null && appsToSync.size() > 0) {
                        for (ApplicationSynchronization appToSync : appsToSync) {
                            publishServer(appToSync);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Problem connecting to core server, disconnecting.");
//                disconnected = true;
//                e.printStackTrace();
            }
        }
    }

    private void publishServer(ApplicationSynchronization appToSync) {
        System.out.println(appToSync.appName);
        System.out.println(new File(".").getAbsolutePath());

        File rootFolder = new File(rootpath + "/" + convertUUID(appToSync.appId));
        System.out.println(rootFolder.getAbsolutePath());
        sendFiles(rootFolder, convertUUID(appToSync.appId), appToSync.appName);
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

    private void sendFiles(File file, String namespace, String appname) {
        if(file.listFiles() == null) {
            return;
        }
        for (File toSend : file.listFiles()) {
            if (toSend.isDirectory()) {
                sendFiles(toSend, namespace, appname);
            } else {
                String path = toSend.getAbsolutePath();
                path = path.replace(namespace, appname);
                path = path.substring(path.indexOf("/app/") + 5);
                System.out.println("Need to send : " + path);

                Message msg = new Message();
                msg.type = Message.Types.sendfile;
                msg.filepath = path;
                try {
                    msg.data = readFile(toSend);
                    sendMessage(msg);
                }catch(Exception e) {
                    System.out.println("Failed to read file  (before sending it to the client) : " + msg.address);
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkActive() {
        if((System.currentTimeMillis() - lastActive.getTime()) > 15000) {
            System.out.println("Client pinged out");
            disconnected = true;
        }
    }
}

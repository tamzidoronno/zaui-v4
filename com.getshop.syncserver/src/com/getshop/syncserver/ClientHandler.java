package com.getshop.syncserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thundashop.api.managers.GetShopApi;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.AvailableApplications;
import com.thundashop.core.usermanager.data.User;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
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
    private MonitorOutgoingEvents monitoroutgoing;
    private final InputStream stream;
    private final BufferedInputStream bis;
    private String storeId;
    private AvailableApplications allApps;

    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.socket.setTcpNoDelay(true);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bis = new BufferedInputStream(socket.getInputStream());
        stream = socket.getInputStream();
    }

    @Override
    public void run() {
        try {
            if (!authenticate()) {
                return;
            }
            monitoroutgoing = new MonitorOutgoingEvents(socket, api);
            monitoroutgoing.start();
            monitorIncomingEvents();
        } catch (IOException ex) {
            try {
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

    private String readSocketLine() {
        try {
            String result = "";
            while(true) {
                byte[] b = new byte[1];
                bis.read(b);
                String character = new String(b);
                if(character.equals("\n")) {
                    break;
                }
                result += character;
            }
            return result;
        } catch (Exception e) {
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
        if (api.getUserManager().isLoggedIn()) {
            writeLineToSocket("OK");
            storeId = api.getStoreManager().getStoreId();
            allApps = api.getAppManager().getAllApplications();
            return true;
        } else {
            writeLineToSocket("Logon failed, please check your input");
            return false;
        }
    }

    private void writeLineToSocket(String message) {
        out.write(message + "\n");
        out.flush();
    }

    private void monitorIncomingEvents() throws IOException, Exception {
        while (true) {
            String line = readSocketLine();
            System.out.println(line);
            if (line == null) {
                monitoroutgoing.setDisconnected(true);
                return;
            }

            System.out.println("TEST");
            switch (line) {
                case "IN_CLOSE_WRITE":
                    fetchFile();
                    break;
                case "IN_DELETE":
                    removeFile();
                    break;
                case "IN_MOVE":
                    moveFile();
                    break;
                case "FETCH_UNKNOWN":
                    ArrayList<String> filesOnClient = fetchUnknown();
                    checkNewFiles(filesOnClient);
                    break;
            }
        }
    }

    private void fetchFile() throws IOException, Exception {
        String path = readSocketLine();
        path = translatePath(path);
        if (path == null) {
            this.writeLineToSocket("OK");
            return;
        }
        long length = Long.parseLong(readSocketLine());
        byte[] by = new byte[1024];

        long total = 0;
        File file = new File(path);
        String folderPath = path.replace(file.getName(), "");
        File fPath = new File(folderPath);
        fPath.mkdirs();
        file.delete();
        System.out.println("Put in: " + path + " size: " + length);
        if (length == 0) {
            file.createNewFile();
        } else {
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path))) {
                while (true) {
                    int size = bis.read(by);
                    if (size > 0) {
                        bos.write(by, 0, size);
                        bos.flush();
                        total += size;
                        if (total == length) {
                            break;
                        }
                    }
                    if(size < 0) {
                        break;
                    }
                }
            }
        }
        
        file = null;
        
        this.writeLineToSocket("OK");
    }

    private String translatePath(String path) throws Exception {
        if (!path.contains("/apps/") && !path.contains("\\apps\\")) {
            writeLineToSocket("Invalid path in file trying to be sent, the apps folder has to be its root");
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
        for (ApplicationSettings apps : allApps.applications) {
            if (apps.appName.equals(appName) && apps.ownerStoreId.equals(storeId)) {
                settings = apps;
            }
        }

        if (settings == null) {
            allApps = api.getAppManager().getAllApplications();
            for (ApplicationSettings apps : allApps.applications) {
                if (apps.appName.equals(appName) && apps.ownerStoreId.equals(storeId)) {
                    settings = apps;
                }
            }
        }
        if (settings == null) {
            return null;
        }

        String translated = "../com.getshop.client/app/" + convertUUID(settings.id) + "/" + path;
        return translated;
    }

    public static void removeRecursive(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // try to delete the file anyway, even if its attributes
                // could not be read, since delete-only access is
                // theoretically possible
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    // directory iteration failed; propagate exception
                    throw exc;
                }
            }
        });
    }

    private void removeFile() throws Exception {
        String path = readSocketLine();
        path = translatePath(path);
        if (path == null) {
            return;
        }
        File file = new File(path);
        if (file.isDirectory()) {
            removeRecursive(file.toPath());
        } else if (file.exists()) {
            System.out.println("Deleting: " + file.getAbsolutePath());
            file.delete();
        } else {
            System.out.println("Failed to delete: " + file.getAbsolutePath());
        }
        writeLineToSocket("OK");
    }

    private String convertUUID(String uuid) {
        uuid = "ns_" + uuid.replace("-", "_");
        return uuid;
    }

    private ArrayList<String> fetchUnknown() throws Exception {
        String existingFiles = readSocketLine();

        Gson gson = new GsonBuilder().serializeNulls().create();
        Type theType = new TypeToken<ArrayList>() {
        }.getType();
        ArrayList<String> object = gson.fromJson(existingFiles, theType);
        monitoroutgoing.doPush(object);
        return object;
    }

    private void moveFile() throws Exception {
        String source = translatePath(this.readSocketLine());
        String dest = translatePath(this.readSocketLine());
        
        File src = new File(source);
        src.renameTo(new File(dest));
    }

    private void checkNewFiles(ArrayList<String> filesOnClient) throws Exception {
        
        List<String> newFiles = new ArrayList();
        for(String file : filesOnClient) {
            File fileobj = new File(file);
            String translated = translatePath(fileobj.getCanonicalPath());
            if(translated != null) {
                fileobj = new File(translated);
                if(!fileobj.exists()) {
                    newFiles.add(file);
                }
            }
        }
        
        if(newFiles.size() > 0) {
            writeLineToSocket("FETCHFILES");
            Gson gson = new Gson();
            String toSend = gson.toJson(newFiles);
            writeLineToSocket(toSend);
        }
    }
}

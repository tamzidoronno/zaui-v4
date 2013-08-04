package com.getshop.syncclient;

import com.getshop.syncserver.FileSummary;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

public class SyncClientJava {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
    private boolean trace = true;
    private GetShopCom com = new GetShopCom(this);
    private boolean running;
    public final Path rootpath;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    private void register(Path dir) throws IOException {

        int start = 0;
        int stop = 0;
        if (this.running) {
            do {
                start = dir.toFile().listFiles().length;
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
                stop = dir.toFile().listFiles().length;
            } while (start != stop);
        }

        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        Path prev = keys.get(key);
        keys.put(key, dir);
        if (prev != null) {
            if (!dir.equals(prev)) {
                FileObject obj = new FileObject();
                obj.state = "ENTRY_MOVE";
                obj.prev = prev;
                obj.cur = dir;
                com.addFileObject(obj);
                return;
            }
        }
        if (running) {
            for (File file : dir.toFile().listFiles()) {
                if (file.isFile() && !file.isHidden()) {
                    FileObject object = new FileObject();
                    object.file = file;
                    object.state = "ENTRY_CREATE";
                    object.cur = file.toPath();
                    com.addFileObject(object);
                }
            }
        }

    }

    private void registerAll(final Path start) throws IOException {
        if (!start.toFile().exists()) {
            return;
        }
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException ioe) throws IOException {
                for (int i = 0; i < file.getNameCount(); i++) {
                    if (file.getName(i).startsWith(".")) {
                        return FileVisitResult.CONTINUE;
                    }
                }
                System.out.println("Failed on: " + file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes bfa) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    SyncClientJava(Path dir, boolean recursive) throws IOException {

        if (!dir.toFile().exists()) {
            dir.toFile().mkdirs();
        }

        readCredentials();

        this.rootpath = dir;

        this.com.start();
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap();
        this.recursive = recursive;

        if (recursive) {
            this.running = false;
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done.");
            this.running = true;
        } else {
            register(dir);
        }
        this.trace = true;
    }

    void processEvents() throws IOException {
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        x.printStackTrace();
                    }
                }
                pushModification(child, event.kind());
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // register directory and process its events
        Path dir = Paths.get("/home/boggi/projects/com.getshop.apps", "apps");
        new SyncClientJava(dir, true).processEvents();
    }

    private void pushModification(Path child, WatchEvent.Kind kind) throws IOException {

        for (int i = 0; i < child.getNameCount(); i++) {
            if (child.getName(i).toString().startsWith(".")) {
                return;
            }
        }

        FileObject object = new FileObject();
        object.file = child.toFile();
        object.state = kind.name();
        object.cur = child;
        com.addFileObject(object);
    }

    void checkFileDirectory(List<FileSummary> filelist) throws Exception {
        //First check all existing files if they have been modified.
        String root = rootpath.toString();
        for (FileSummary summary : filelist) {
            File file = new File(root, summary.path);
            if (!file.exists()) {
                //File has been deleted.
                deleteFile(file);
            } else {
                byte[] md5 = createChecksum(file.getAbsolutePath());
                if (!Arrays.equals(summary.md5, md5)) {
                    //File has been changed on the server.
                    uploadFile(file);
                }
            }
        }

        //Okey, and now, check for new files.
        checkNewFiles(rootpath.toFile().listFiles(), filelist);
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

    private void deleteFile(File file) throws IOException {
        FileObject obj = new FileObject();
        obj.state = "ENTRY_DELETE";
        obj.file = file;
        com.addFileObject(obj);
    }

    private void uploadFile(File file) {
        FileObject obj = new FileObject();
        obj.state = "ENTRY_MODIFY";
        obj.file = file;
        com.addFileObject(obj);
    }

    private void checkNewFiles(File[] listFiles, List<FileSummary> filelist) {
        for (File file : listFiles) {
            if (file.isFile()) {
                boolean found = false;
                for (FileSummary summary : filelist) {
                    if (file.getAbsolutePath().endsWith(summary.path)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    uploadFile(file);
                }
            } else {
                checkNewFiles(file.listFiles(), filelist);
            }
        }
    }

    private void readCredentials() throws IOException {
        String[] resultArray = new String[0];
        try {
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream("config.txt");
            String result = "";
            try (DataInputStream in = new DataInputStream(fstream)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    result += strLine + "\n";
                }
            }
            System.out.println(result);
            resultArray = result.trim().split("\n");
            if(resultArray.length == 3) {
                
            }
            
        } catch (IOException e) {
        }

        if(resultArray.length == 3) {
            System.out.println("Reading configuration from config.txt");
            this.com.user = resultArray[0];
            this.com.password = resultArray[1];
            this.com.address = resultArray[2];
        } else {
            InputStreamReader inp = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(inp);
            
            System.out.print("Enter username: ");
            this.com.user = br.readLine();
            System.out.print("Enter password: ");
            this.com.password = br.readLine();
            System.out.print("Enter address: ");
            this.com.address = br.readLine();
        }
    }
}

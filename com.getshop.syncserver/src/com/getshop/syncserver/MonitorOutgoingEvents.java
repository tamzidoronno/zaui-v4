package com.getshop.syncserver;

import com.thundashop.api.managers.GetShopApi;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.ApplicationSynchronization;
import com.thundashop.core.appmanager.data.AvailableApplications;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author boggi
 */
public class MonitorOutgoingEvents extends Thread {

    private final Socket socket;
    private final GetShopApi api;
    private String appPath = "../com.getshop.client/app/";
    private String eventsPath = "../com.getshop.client/events/";
    private String classesPath = "../com.getshop.client/classes/";
    private PrintWriter out;
    private final DataOutputStream output;
    private boolean disconnected = false;
    private Map<File, String> md5OfExtras;

    public MonitorOutgoingEvents(Socket socket, GetShopApi api) throws Exception {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.api = api;
        generateMd5OfExtras();
    }

    @Override
    public void run() {
//        while (true) {
//            try {
//                List<ApplicationSynchronization> allApps = api.getAppManager().getSyncApplications();
//                for (ApplicationSynchronization sync : allApps) {
//                    ApplicationSettings settings = api.getAppManager().getApplication(sync.appId);
//                    uploadApplication(settings);
//                    api.getAppManager().saveApplication(settings);
//                    String namespace = convertToNameSpace(settings.id);
//                    writeLineToSocket("STARTSYNC");
//                    pushAllFiles(new File(appPath + "/" + namespace), settings, null);
//                    writeLineToSocket("ENDSYNC");
//                }
//                if (disconnected) {
//                    break;
//                }
//
//                sleep(2000);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                System.out.println("Connection broken");
//                break;
//            }
//        }
        System.out.println("Cleaning up outgoing events");
    }

    private String convertToNameSpace(String uuid) {
        uuid = "ns_" + uuid.replace("-", "_");
        return uuid;
    }

    private void uploadApplication(ApplicationSettings settings) {
        System.out.println("Uploading application: " + settings.appName + " id: " + settings.id);

    }

    private void pushAllFiles(File allFiles, ApplicationSettings settings, ArrayList<String> excludeList) throws IOException {
        if (!allFiles.exists()) {
            System.out.println("Could not find app path: " + allFiles.getAbsolutePath());
            return;
        }
        File[] fileList = allFiles.listFiles();
        for (File file : fileList) {
            if (file.isDirectory()) {
                pushAllFiles(file, settings, excludeList);
            } else {
                String uploadPath = file.getAbsolutePath().replace(new File(appPath).getAbsolutePath(), "");
                String namespace = convertToNameSpace(settings.id);
                uploadPath = uploadPath.replace(namespace, "apps/" + settings.appName);
                boolean ignore = false;
                if (excludeList != null) {
                    for (String localPath : excludeList) {
                        if (localPath.endsWith(uploadPath)) {
                            ignore = true;
                            break;
                        }
                    }
                }

                if (!ignore) {
                    pushFile(uploadPath, file);
                }
            }
        }
    }

    private void pushFile(String uploadPath, File file) throws IOException {
        System.out.println("Pushing file : " + file.getAbsolutePath());

        writeLineToSocket("FILESYNC");
        writeLineToSocket(uploadPath);
        writeLineToSocket(file.length() + "");

        byte[] array = new byte[1024];
        InputStream in = new FileInputStream(file);
        long total = 0;
        while (true) {
            int read = in.read(array);
            if (read > 0) {
                output.write(array, 0, read);
                output.flush();
                total += read;
            } else {
                break;
            }
        }
        System.out.println("Pushed: " + total + " size");
        in.close();
    }

    private void writeLineToSocket(String line) throws IOException {
        out.println(line);
        out.flush();
    }

    void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    void doPush(ArrayList<String> excludeList) throws Exception {
        AvailableApplications allapps = api.getAppManager().getAllApplications();
        String storeid = api.getStoreManager().getStoreId();

        ArrayList<String> toPush = new ArrayList();
        Map<String, String> extrasPush = new HashMap();


        for (int i = 0; i < excludeList.size(); i += 2) {
            String line = excludeList.get(i);
            File file = new File(line);
            if (file.getCanonicalPath().replaceAll("\\\\", "/").contains("/getshopextras/")) {
                extrasPush.put(excludeList.get(i + 1), line);
            } else {
                toPush.add(line);
            }
        }

        writeLineToSocket("STARTSYNC");
        for (ApplicationSettings settings : allapps.applications) {
            if (settings.ownerStoreId.equals(storeid)) {
                String namespace = convertToNameSpace(settings.id);
                pushAllFiles(new File(appPath + "/" + namespace), settings, toPush);
            }
        }
        pushExtras(extrasPush);
        writeLineToSocket("ENDSYNC");
    }

    private void pushExtras(Map<String, String> extrasPush) throws IOException {
        for (File file : md5OfExtras.keySet()) {
            String filename = file.getCanonicalPath().replaceAll("\\\\", "/");
            if (filename.contains("/events/")) {
                filename = filename.substring(filename.indexOf("/events/"));
            }
            if (filename.contains("/classes/")) {
                filename = filename.substring(filename.indexOf("/classes/"));
            }

            boolean found = false;
            String md5Found = "";
            for (String md5 : extrasPush.keySet()) {
                String hasFile = extrasPush.get(md5);
                if (hasFile.endsWith(filename)) {
                    if(md5.equals(md5OfExtras.get(file))) {
                        found = true;
                    }
                }
            }
            if (!found) {
                pushFile("getshopextras/" + filename, file);
            }
        }

    }

    private List<File> getAllExtras() {
        List<File> allFiles = new ArrayList<>();
        Queue<File> dirs = new LinkedList<>();
        dirs.add(new File(eventsPath));
        while (!dirs.isEmpty()) {
            for (File f : dirs.poll().listFiles()) {
                if (f.isDirectory()) {
                    dirs.add(f);
                } else if (f.isFile()) {
                    allFiles.add(f);
                }
            }
        }

        allFiles.add(new File(classesPath + "/Application.php"));
        allFiles.add(new File(classesPath + "/ApplicationBase.php"));
        allFiles.add(new File(classesPath + "/Factory.php"));
        allFiles.add(new File(classesPath + "/FactoryBase.php"));
        allFiles.add(new File(classesPath + "/ReportingApplication.php"));
        allFiles.add(new File(classesPath + "/ShipmentApplication.php"));
        allFiles.add(new File(classesPath + "/SystemApplication.php"));
        allFiles.add(new File(classesPath + "/ThemeApplication.php"));
        allFiles.add(new File(classesPath + "/WebshopApplication.php"));
        allFiles.add(new File(classesPath + "/MarketingApplication.php"));
        allFiles.add(new File(classesPath + "/SiteBuilder.php"));
        allFiles.add(new File(classesPath + "/ApplicationPool.php"));

        return allFiles;
    }

    private void generateMd5OfExtras() throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        List<File> extras = getAllExtras();
        md5OfExtras = new HashMap();
        for (File file : extras) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);

            byte[] dataBytes = new byte[1024];

            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            byte[] mdbytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            md5OfExtras.put(file, sb.toString());
        }
    }
}

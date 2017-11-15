package com.thundashop.core.start;

import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author boggi
 */
public class ParseTranslation {
    private String sourcePath = "/source/getshop/3.0.0/com.getshop.client/";
    private List<Application> apps = new ArrayList();
    
    private int fileCount = 0;
    public static HashMap<String, TranslationKey> keyMap = new HashMap();
    public static HashMap<String, Application> applicationNames = new HashMap();

    public ParseTranslation(List<Application> apps) {
        this.apps = apps;
    }

    
    private void writeTranslationFile(String filename, ArrayList<String> webShopTranslation, boolean includeSuffix) {
        try {
            // Create file 
            FileWriter fstream = new FileWriter("/source/getshop/3.0.0/com.getshop.client/ROOT/translation/" + filename + ".csv");
            BufferedWriter out = new BufferedWriter(fstream);

            HashMap<String, List<TranslationKey>> sortedTranslation = buildSortedTranslation(webShopTranslation);

            for (String app : sortedTranslation.keySet()) {
                out.write("###### " + app + " ######\n");
                for (TranslationKey key : sortedTranslation.get(app)) {
                    if (includeSuffix) {
                        out.write(key.key + ";-;\n");
                    } else {
                        out.write(key.key + "\n");
                    }
                }
                out.write("\n\n");
            }
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void parsePath(String path) throws FileNotFoundException, IOException {

        File root = new File(path);
        File[] list = root.listFiles();
        if(list == null) {
            return;
        }
        for (File f : list) {
            String filePath = f.getAbsolutePath().toString().toLowerCase();
            String fileOriginal = f.getCanonicalPath().toString();
            if (f.isDirectory()) {
                parsePath(f.getAbsolutePath()); 
            } else if (filePath.endsWith(".php") || filePath.endsWith(".phtml") || filePath.endsWith(".js")) {
                if(!fileOriginal.contains("amcharts/plugins/dataloader/dataloader.min.js")) {
                    parseFile(fileOriginal);
                    fileCount++;
                }
            }
        }
    }

    private void parseFile(String filePath) throws FileNotFoundException, IOException {
        
        if (!fileIsInApp(filePath)) {
            return;
        }
        
        FileInputStream fstream = new FileInputStream(filePath);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            if(filePath.contains("com.getshop.client/ROOT/js/ace")) {
                return;
            }
            String strLine;
            int linenumber = 0;

            while ((strLine = br.readLine()) != null) {
                //Check if there is more translation at the same line
                int offset = 0;
                do {
                    offset = strLine.indexOf("__", offset);
                    if(offset >= 0) {
                        strLine = strLine.substring(offset);
                        offset = 3;
                        String key = "";
                        // Print the content on the console
                        if (strLine.contains("__(")) {
                            key = getTranslationKey(strLine);
                            addTranslationKey(key, filePath, linenumber, "framework");
                        } else if (strLine.contains("__w(")) {
                            key = getTranslationKey(strLine);
                            addTranslationKey(key, filePath, linenumber, "webshop");
                        } else if (strLine.contains("__f(")) {
                            key = getTranslationKey(strLine);
                            addTranslationKey(key, filePath, linenumber, "framework");
                        }
                        linenumber++;
                    }
                } while (offset >= 0);
            }
        } finally {
            br.close();
            in.close();
            fstream.close();
        }
    }

    private String getTranslationKey(String strLine) {
        String functionText = trimFunction(strLine);
        String key = "";
        if (functionText.charAt(0) == '\'') {
            key = extractLine(functionText, '\'');
        } else if (functionText.charAt(0) == '"') {
            key = extractLine(functionText, '"');
        }
        return key;
    }

    private String trimFunction(String strLine) {
        int start = strLine.indexOf("__(");
        if (start < 0) {
            start = strLine.indexOf("__f(");
        }
        if (start < 0) {
            start = strLine.indexOf("__w(");
        }

        if (start < 0) {
            System.exit(0);
        }

        String line = strLine.substring(start);
        line = line.substring(line.indexOf("(") + 1);
        return line.trim();
    }

    private String extractLine(String strLine, char string) {
        strLine = strLine.substring(strLine.indexOf(string) + 1);
        int end = -1;
        int offset = 0;
        do {
            int line = strLine.indexOf("\\" + string, offset);
            end = strLine.indexOf(string, offset);
            if (line > 0) {
                if ((end - 1) == line) {
                    offset = line + 2;
                }
            } else {
                break;
            }
        } while (true);

        if (end < 0) {
            GetShopLogHandler.logPrintStatic("Fuck.. did not find end for line: " + strLine, null);
            System.exit(0);
        }
        String result = strLine.substring(0, end);

        result = result.replace("\\" + string, string + "");
        return result;
    }

    private void addTranslationKey(String key, String filePath, int linenumber, String type) {
        if (key.trim().length() == 0) {
            return;
        }
        String app_ns = "framework";
        if (filePath.contains("/app/ns_")) {
            app_ns = filePath.substring(filePath.indexOf("/app/ns_") + 8);
            app_ns = app_ns.substring(0, app_ns.indexOf("/"));
            if(applicationNames.containsKey(app_ns)) {
                app_ns += " (" + applicationNames.get(app_ns).appName + ")";
            }
        }

        filePath = filePath.replace("/home/boggi/source/getshop/com.getshop.client/", "");
        TranslationKey transkey = keyMap.get(key);
        if (transkey == null) {
            transkey = new TranslationKey();
            transkey.key = key.trim();
            transkey.app_namespace = app_ns;
        }

        List<Integer> lines = transkey.files.get(filePath);
        if (lines == null) {
            lines = new ArrayList();
        }

        lines.add(linenumber);
        transkey.files.put(filePath, lines);
        if (transkey.type == null || type.equals("webshop")) {
            transkey.type = type;
        }
        
        if(keyMap.containsKey(key.trim())) {
            transkey.app_namespace = "framework";
        }

        keyMap.put(key.trim(), transkey);
    }

    private ArrayList<String> createKeyList(String type) {
        ArrayList<String> toAddList = new ArrayList();
        for (String key : keyMap.keySet()) {
            TranslationKey transkey = keyMap.get(key);
            if (!transkey.type.equals(type)) {
                continue;
            }
            toAddList.add(key);
        }

        java.util.Collections.sort(toAddList);
        return toAddList;
    }

    public void printSummary() {
        int count = 0;
        for (TranslationKey key : keyMap.values()) {
            if (key.files.size() > 2) {
                GetShopLogHandler.logPrintStatic(key.key + ";-;" + key.type + " ( " + key.files.size() + ")", null);
                count++;
            }
        }
        GetShopLogHandler.logPrintStatic("Duplicates: " + count, null);
    }

    private HashMap<String, List<TranslationKey>> buildSortedTranslation(ArrayList<String> webShopTranslation) {
        HashMap<String, List<TranslationKey>> result = new HashMap();
        for (String key : webShopTranslation) {
            TranslationKey translationkey = keyMap.get(key);
            String app = translationkey.app_namespace;

            List<TranslationKey> tocheck = result.get(app);
            if (tocheck == null) {
                tocheck = new ArrayList();
            }
            tocheck.add(translationkey);
            result.put(app, tocheck);
        }
        return result;
    }

    public void startParsing() throws IOException {
        parsePath(sourcePath);
        
        ArrayList<String> webShopTranslation = createKeyList("webshop");
        ArrayList<String> frameworkTranslation = createKeyList("framework");

        TranslationFile w_base = new TranslationFile("w_base");
        TranslationFile f_base = new TranslationFile("f_base");
        
        w_base.compareWithArray(webShopTranslation);
        f_base.compareWithArray(frameworkTranslation);

        writeTranslationFile("w_base", webShopTranslation, true);
        writeTranslationFile("f_base", frameworkTranslation, true);

        TranslationComparor tc = new TranslationComparor();
        tc.finalizeTranslationFiles();

        GetShopLogHandler.logPrintStatic(fileCount + " files parsed", null);
        GetShopLogHandler.logPrintStatic(frameworkTranslation.size() + " framework text lines found", null);
        GetShopLogHandler.logPrintStatic(webShopTranslation.size() + " webshop text lines found", null);
        
    }

    private boolean fileIsInApp(String filePath) {
        if (apps == null ||apps.isEmpty())
            return true;
        
        if (filePath.contains("/3.0.0/com.getshop.client/ROOT/javascripts"))
            return false;
        
        for (Application app : apps) {
            if (filePath.contains(app.appName)) {
                GetShopLogHandler.logPrintStatic(filePath, null);
                return true;
            }
        }
        
        return false;
    }
}

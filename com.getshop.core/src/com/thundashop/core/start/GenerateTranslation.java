package com.thundashop.core.start;

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
public class GenerateTranslation {

    private int fileCount = 0;
    private HashMap<String, TranslationKey> keyMap = new HashMap();

    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        GenerateTranslation gt = new GenerateTranslation();
        gt.parsePath("../com.getshop.client/");
        
        ArrayList<String> webShopTranslation = gt.createKeyList("webshop");
        ArrayList<String> frameworkTranslation = gt.createKeyList("framework");
        
        TranslationFile w_base = new TranslationFile("w_base");
        TranslationFile f_base = new TranslationFile("f_base");
        
        w_base.compareWithArray(webShopTranslation);
        f_base.compareWithArray(frameworkTranslation);
        
        writeTranslationFile("w_base", webShopTranslation, true);
        writeTranslationFile("f_base", frameworkTranslation, true);
        
        TranslationComparor tc = new TranslationComparor();
        
//        ArrayList<String> mergedFile = tc.createNewFile(frameworkTranslation, "fi_fi");
//        writeTranslationFile("f_fi_fi", mergedFile, false);
        
        tc.finalizeTranslationFiles();
        
        System.out.println(gt.fileCount + " files parsed");
        System.out.println(frameworkTranslation.size() + " framework text lines found");
        System.out.println(webShopTranslation.size() + " webshop text lines found");
        gt.printSummary();
    }
    
    private static void writeTranslationFile(String w_base, ArrayList<String> webShopTranslation, boolean includeSuffix) {
        try {
            // Create file 
            FileWriter fstream = new FileWriter("../com.getshop.client/ROOT/translation/" + w_base+".csv");
            BufferedWriter out = new BufferedWriter(fstream);
            
            for(String key : webShopTranslation) {
                if(includeSuffix) {
                    out.write(key + ";-;\n");
                } else {
                    out.write(key + "\n");
                }
            }
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }


    public void parsePath(String path) throws FileNotFoundException, IOException {

        File root = new File(path);
        File[] list = root.listFiles();

        for (File f : list) {
            String filePath = f.getAbsolutePath().toString().toLowerCase();
            String fileOriginal = f.getCanonicalPath().toString();
            if (f.isDirectory()) {
                parsePath(f.getAbsolutePath());
            } else if (filePath.endsWith(".php") || filePath.endsWith(".phtml")) {
//                System.out.println("File:" + f.getAbsoluteFile());
                parseFile(fileOriginal);
                fileCount++;
            }
        }
    }

    private void parseFile(String filePath) throws FileNotFoundException, IOException {
        FileInputStream fstream = new FileInputStream(filePath);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        int linenumber = 0;
        
        while ((strLine = br.readLine()) != null) {
            //Check if there is more translation at the same line
            int offset = 0;
            do {
                offset = strLine.indexOf("->__", offset);
                if(offset > 0) {
                    strLine = strLine.substring(offset);
                    offset = 5;
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
            }while(offset >= 0);
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
            System.out.println("Fuck it failed: " + strLine);
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
                        offset = line+2;
                    }
            } else {
                break;
            }
        }while(true);

        if(end < 0) {
            System.out.println("Fuck.. did not find end for line: " + strLine);
            System.exit(0);
        }
        String result = strLine.substring(0, end);
        
        result = result.replace("\\"+string, string+"");
        return result;
    }

    private void addTranslationKey(String key, String filePath, int linenumber, String type) {
        if(key.trim().length() == 0) {
            return;
        }
        filePath = filePath.replace("/home/boggi/source/getshop/com.getshop.client/", "");
        TranslationKey transkey = keyMap.get(key);
        if(transkey == null) {
            transkey = new TranslationKey();
            transkey.key = key.trim();
        }
        
        List<Integer> lines = transkey.files.get(filePath);
        if(lines == null) {
            lines = new ArrayList();
        }
        
        lines.add(linenumber); 
        transkey.files.put(filePath, lines);
        if(transkey.type == null || type.equals("webshop")) {
            transkey.type = type;
        }
            
        keyMap.put(key, transkey);
    }

    private ArrayList<String> createKeyList(String type) {
        ArrayList<String> toAddList = new ArrayList();
        for(String key : keyMap.keySet()) {
            TranslationKey transkey = keyMap.get(key);
            if(!transkey.type.equals(type))
                continue;
            toAddList.add(key);
        }
        
        java.util.Collections.sort(toAddList);
        return toAddList;
    }

    private void printSummary() {
        int count = 0;
        for(TranslationKey key : keyMap.values()) {
            if(key.files.size() > 2) {
                System.out.println(key.key + ";-;" + key.type + " ( " + key.files.size() + ")");
                count++;
            }
        }
        System.out.println("Duplicates: " + count);
    }
}

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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author boggi
 */
public class TranslationFile {

    public HashMap<String, String> translationMatrix = new HashMap();
    public String filename;

    TranslationFile(String fileName) throws FileNotFoundException, IOException {
        readFile(fileName);
        this.filename = fileName;
    }

    public void addKey(String key, String text) {
        if(key.startsWith("######")) {
            return;
        }
        translationMatrix.put(key, text);
    }

    public void removeKey(String key) {
        translationMatrix.remove(key);
    }

    private void readFile(String fileName) throws FileNotFoundException, IOException {
        String path = "../com.getshop.client/ROOT/translation/";

        File file = new File(path + fileName + ".csv");
        if (!file.exists()) {
            return;
        }

        FileInputStream fstream = new FileInputStream(path + fileName + ".csv");
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        while ((strLine = br.readLine()) != null) {
            String[] lineArgs = strLine.split(";-;");
            if(lineArgs[0].startsWith("#####")) {
                continue;
            }
            if (lineArgs.length == 2) {
                translationMatrix.put(lineArgs[0], lineArgs[1]);
            } else if (lineArgs.length == 1) {
                translationMatrix.put(lineArgs[0], "");
            }
        }
    }

    public void save(boolean translation) {
        try {
            String path = "../com.getshop.client/ROOT/translation/" + filename + ".csv";
            if (translation) {
                path = "../com.getshop.client/ROOT/translation/stripped/" + filename + ".csv";
            }
            FileWriter fstream = new FileWriter(path);
            BufferedWriter out = new BufferedWriter(fstream);
            boolean foundEntries = false;

            List<String> keyList = new ArrayList(translationMatrix.keySet());

            HashMap<String, List<String>> mappedKeyList = buildMappedKeyList(keyList);
            for (String app_ns : mappedKeyList.keySet()) {
                keyList = mappedKeyList.get(app_ns);
                
                java.util.Collections.sort(keyList);
                boolean written = false;
                for (String key : keyList) {
                    if (translation && translationMatrix.get(key).trim().length() > 0) {
                        continue;
                    }
                    foundEntries = true;
                    if (!translation && translationMatrix.get(key).isEmpty()) {
                        continue;
                    }
                    if(!written) {
                        out.write("###### " + app_ns + " ######\n");
                        written = true;
                    }
                    out.write(key + ";-;" + translationMatrix.get(key) + "\n");
                }
                if(written) {
                    out.write("\n\n");
                }
            }
            out.close();

            if (!foundEntries) {
                File file = new File(path);
                file.delete();
            }

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }

    void compareWithBase(Set<String> base) {
        //First check if some keys are new.
        List<String> toAdd = new ArrayList();
        for (String key : base) {
            boolean found = false;
            for (String myKey : translationMatrix.keySet()) {
                if (key.equals(myKey)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("New key found for file: " + filename + " : " + key);
                toAdd.add(key);
            }
        }
        for (String key : toAdd) {
            addKey(key, "");
        }

        //First check if some keys are removed.
        List<String> toRemove = new ArrayList();
        for (String myKey : translationMatrix.keySet()) {
            boolean found = false;
            for (String baseKey : base) {
                if (myKey.equals(baseKey)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Key removed from file: " + filename + " : " + myKey);
                toRemove.add(myKey);
            }
        }
        for (String key : toRemove) {
            removeKey(key);
        }
    }

    void compareWithArray(ArrayList<String> webShopTranslation) {
        Set<String> newSet = new HashSet();
        for (String key : webShopTranslation) {
            newSet.add(key);
        }
        compareWithBase(newSet);
    }

    void dump() {
        for (String key : translationMatrix.keySet()) {
            System.out.println(key + ";-;" + translationMatrix.get(key));
        }
    }

    void merge(HashMap<String, String> translationMatrix) {
        for (String key : translationMatrix.keySet()) {
            if (translationMatrix.get(key).trim().length() > 0) {
                addKey(key, translationMatrix.get(key));
            }
        }
    }

    private HashMap<String, List<String>> buildMappedKeyList(List<String> keyList) {
        HashMap<String, List<String>> result = new HashMap();
        for (String key : keyList) {
            if(key.isEmpty()) {
                continue;
            }
            TranslationKey tkey = GenerateTranslation.keyMap.get(key);
            if(tkey == null) {
                System.out.println("No translation key for : " + key);
                for(String test : GenerateTranslation.keyMap.keySet()) {
                    System.out.println("\t" + test);
                }
                System.exit(0);
            }
            
            List<String> keys = result.get(tkey.app_namespace);
            if (keys == null) {
                keys = new ArrayList();
            }

            keys.add(key);
            result.put(tkey.app_namespace, keys);
        }
        return result;
    }
}

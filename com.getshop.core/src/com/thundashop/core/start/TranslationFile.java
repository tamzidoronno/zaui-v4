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
    public HashMap<String,String> translationMatrix = new HashMap();
    public String filename;
    
    public TranslationFile(String fileName) throws FileNotFoundException, IOException {
        readFile(fileName);
        this.filename = fileName;
    }
    
    public void addKey(String key, String text) {
        translationMatrix.put(key, text);
    }
    
    public void removeKey(String key) {
        translationMatrix.remove(key);
    }

    private void readFile(String fileName) throws FileNotFoundException, IOException {
        String path = "../com.getshop.client/ROOT/translation/";
        
        File file = new File(path + fileName + ".csv");
        if(!file.exists()) {
            return;
        }
        
        FileInputStream fstream = new FileInputStream(path + fileName + ".csv");
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        while ((strLine = br.readLine()) != null) {
            String[] lineArgs = strLine.split(";-;");
            if(lineArgs.length == 2) {
                translationMatrix.put(lineArgs[0], lineArgs[1]);
            } else if(lineArgs.length == 1) {
                translationMatrix.put(lineArgs[0], "");
            }
        }
    }
    
    public void save(boolean translation) {
        try {
            String path = "../com.getshop.client/ROOT/translation/" + filename+".csv";
            if(translation) {
                path = "../com.getshop.client/ROOT/translation/stripped/" + filename+".csv";
            }
            FileWriter fstream = new FileWriter(path);
            BufferedWriter out = new BufferedWriter(fstream);
            boolean foundEntries = false;
            
            List<String> keyList = new ArrayList();
            
            for(String key : translationMatrix.keySet()) {
                keyList.add(key);
            }
        
            java.util.Collections.sort(keyList);
            
            
            for(String key : keyList) {
                if(translation && translationMatrix.get(key).trim().length() > 0) {
                    continue;
                }
                foundEntries = true;
                out.write(key + ";-;" + translationMatrix.get(key) + "\n");
            }
            out.close();
            
            if(!foundEntries) {
                File file = new File(path);
                file.delete();
            }
            
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    void compareWithBase(Set<String> base) {
        //First check if some keys are new.
        List<String> toAdd = new ArrayList();
        for(String key : base) {
            boolean found = false;
            for(String myKey : translationMatrix.keySet()) {
                if(key.equals(myKey)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                System.out.println("New key found for file: " + filename + " : "  + key);
                toAdd.add(key);
            }
        }
        for(String key : toAdd) {
            addKey(key, "");
        }
        
        //First check if some keys are removed.
        List<String> toRemove = new ArrayList();
        for(String myKey : translationMatrix.keySet()) {
            boolean found = false;
            for(String baseKey : base) {
                if(myKey.equals(baseKey)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                System.out.println("Key removed from file: " + filename + " : "  + myKey);
                toRemove.add(myKey);
            }
        }
        for(String key : toRemove) {
            removeKey(key);
        }
    }

    void compareWithArray(ArrayList<String> webShopTranslation) {
        Set<String> newSet = new HashSet();
        for(String key : webShopTranslation) {
            newSet.add(key);
        }
        compareWithBase(newSet);
    }

    void dump() {
        for(String key : translationMatrix.keySet()) {
            System.out.println(key + ";-;" + translationMatrix.get(key));
        }
    }

    void merge(HashMap<String, String> translationMatrix) {
        for(String key : translationMatrix.keySet()) {
            if(translationMatrix.get(key).trim().length() > 0) {
                addKey(key, translationMatrix.get(key));
            }
        }
    }
}

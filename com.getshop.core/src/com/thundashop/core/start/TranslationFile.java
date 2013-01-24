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
    private String filename;
    
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
            FileWriter fstream = new FileWriter("../com.getshop.client/ROOT/translation/" + filename+".csv");
            if(translation) {
                fstream = new FileWriter("../com.getshop.client/ROOT/translation/stripped/" + filename+".csv");
            }
            BufferedWriter out = new BufferedWriter(fstream);
            for(String key : translationMatrix.keySet()) {
                if(translation && translationMatrix.get(key).trim().length() > 0) {
                    continue;
                }
                out.write(key + ";-;" + translationMatrix.get(key) + "\n");
            }
            out.close();
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
}

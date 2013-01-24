/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.start;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author boggi
 */
public class TranslationComparor {

    private String path = "../com.getshop.client/ROOT/translation/";
    
    private List<TranslationFile> webShopTranslationFiles = new ArrayList();
    private List<TranslationFile> frameworkTranslationFiles = new ArrayList();

    public void finalizeTranslationFiles() throws FileNotFoundException, IOException {
        //First load them all.
        loadTranslationFiles();
        
        TranslationFile f_base = new TranslationFile("f_base");
        TranslationFile w_base = new TranslationFile("w_base");
        
        for(TranslationFile webshopTranslation : webShopTranslationFiles) {
            webshopTranslation.compareWithBase(w_base.translationMatrix.keySet());
            webshopTranslation.save(true);
            webshopTranslation.save(false);
        }
        
        for(TranslationFile webshopTranslation : frameworkTranslationFiles) {
            webshopTranslation.compareWithBase(f_base.translationMatrix.keySet());
            webshopTranslation.save(true);
            webshopTranslation.save(false);
        }
        
    }
    
    private void loadTranslationFiles() throws FileNotFoundException, IOException {
        List<String> languages = new ArrayList();
        languages.add("nb_NO");
        languages.add("en_en");
        languages.add("fi_fi");

        for (String language : languages) {
            webShopTranslationFiles.add(new TranslationFile("w_" + language));
            frameworkTranslationFiles.add(new TranslationFile("f_" + language));
        }
    }

    public ArrayList<String> createNewFile(ArrayList<String> baseKeys, String oldFile) throws FileNotFoundException, IOException {
        FileInputStream fstream = new FileInputStream(path + oldFile + ".csv");
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        HashMap<String, String> toMerge = new HashMap();
        while ((strLine = br.readLine()) != null) {
            String key = strLine.substring(0, strLine.indexOf(","));
            String trans = strLine.substring(strLine.indexOf(",") + 1);
            toMerge.put(key, trans);
        }
        int count = 0;
        ArrayList<String> result = new ArrayList();
        for (String key : baseKeys) {
            if (toMerge.get(key) == null) {
//                System.out.println("Unable to merge: " + key);
                result.add(key + ";-;");
                count++;
            } else {
                result.add(key + ";-;" + toMerge.get(key));
            }
        }
        return result;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
public class OcrManager extends ManagerBase implements IOcrManager {
    
    @PostConstruct
    public void init() {
        isSingleton = true;
        storeId = "all";
        initialize();
    }    
    
    HashMap<String, OcrFile> files = new HashMap();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon com : data.data) {
            if(com instanceof OcrFile) {
                OcrFile f = (OcrFile)com;
                files.put(f.name, f);
            }
        }
    }    
    
    @Override
    public void scanOcrFiles() {
        File file = new File("/ocr");
        File[] allfiles = file.listFiles();
        for(File f : allfiles) {
            if(files.containsKey(f.getName())) {
                continue;
            }
            try {
                String content = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
                OcrFile ocrfile = new OcrFile();
                ocrfile.addOcrFileContent(content, f.getName());
                files.put(f.getName(), ocrfile);
                saveObject(ocrfile);
                f.delete();
            } catch (IOException ex) {
                logPrintException(ex);
            }
        }
    }

    List<OcrFileLines> getNewOcrLines(String accountId) {
        List<OcrFileLines> newLines = new ArrayList();
        for(OcrFile file : files.values()) {
            boolean needSaving = false;
            for(OcrFileLines line : file.ocrLines) {
                if(line.isTransferred()) {
                    continue;
                }
                
                if(line.avtaleId.equals(accountId)) {
                    String transaksjonstype = line.getTransaksjonsType();
                    String recordtype = line.getRecordType();
                    if(transaksjonstype.equals("13") && recordtype.equals("30")) {
                        line.setBeenTransferred();
                        newLines.add(line);
                        needSaving = true;
                    }
                    
                }
            }
            if(needSaving) {
                saveObject(file);
            }
        }
        return newLines;
    }
}

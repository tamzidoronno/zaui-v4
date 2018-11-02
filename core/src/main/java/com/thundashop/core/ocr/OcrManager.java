/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

import com.thundashop.core.common.ManagerBase;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
public class OcrManager extends ManagerBase implements IOcrManager {
    
    @Override
    public void scanOcrFiles() {
        File file = new File("/ocr");
        File[] allfiles = file.listFiles();
        for(File f : allfiles) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
                System.out.println(content);
                OcrFile ocrfile = new OcrFile();
                ocrfile.addOcrFileContent(content);
            } catch (IOException ex) {
                logPrintException(ex);
            }
        }
    }
    
}

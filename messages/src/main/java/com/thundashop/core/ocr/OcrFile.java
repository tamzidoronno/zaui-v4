/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author boggi
 */
public class OcrFile extends DataCommon {
    String name = "";
    List<OcrFileLines> ocrLines = new ArrayList();
    
    public void addOcrFileContent(String content, String name) {
        String[] lines = content.split("\n");
        this.name = name;
        String avtaleId = "";
        for(String line : lines) {
            OcrFileLines ocrline = new OcrFileLines(line);
            if(ocrline.getTjenesteKode().equals("09") && ocrline.getRecordType().equals("20")) {
                avtaleId = ocrline.getAvtaleId();
            }
            if(avtaleId.isEmpty()) {
                continue;
            }
            ocrline.avtaleId = avtaleId;
            ocrLines.add(ocrline);
        }
    }
}

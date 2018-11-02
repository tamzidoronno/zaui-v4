/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author boggi
 */
public class OcrFile extends DataCommon {
    String name = "";
    
    public void addOcrFileContent(String content) {
        String[] lines = content.split("\n");
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
            String transaksjonstype = ocrline.getTransaksjonsType();
            String recordtype = ocrline.getRecordType();
            if(transaksjonstype.equals("13") && recordtype.equals("30")) {
                System.out.println("============== Kid found ===================");
                System.out.println("Avtaleid: " + ocrline.avtaleId);
                System.out.println("Kid: " + ocrline.getKid().trim());
                System.out.println("Amount: " + ocrline.getAmount().trim());
                System.out.println("Amount (double): " + ocrline.getAmountInDouble());
                System.out.println("Fortegn: " + ocrline.getFortegn().trim());
                System.out.println("----------");
            }
        }
    }
}

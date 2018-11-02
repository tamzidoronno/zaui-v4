/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

/**
 *
 * @author boggi
 */
public class OcrFileLines {

    private final String rawLine;
    String avtaleId = "";

    OcrFileLines(String line) {
        rawLine = line;
    }
    
    public String getTjenesteKode() {
        return rawLine.substring(2,4);
    }
    public String getForsType() {
        return rawLine.substring(4,6);
    }
    public String getRecordType() {
        return rawLine.substring(6,8);
    }
    public String getDataAvasender() {
        return rawLine.substring(8,16);
    }
    public String getForsNr() {
        return rawLine.substring(16,23);
    }
    public String getDataMottaker() {
        return rawLine.substring(23,31);
    }
    public String getAvtaleId() {
        return rawLine.substring(8,17);
    }
    public String getOppdragsNr() {
        return rawLine.substring(17,24);
    }
    public String getOppdragsKonto() {
        return rawLine.substring(24,35);
    }
    public String getKid() {
        return rawLine.substring(49,74);
    }
    public String getAmount() {
        return rawLine.substring(32,49);
    }
    public String getFortegn() {
        return rawLine.substring(31,32);
    }

    String getTransaksjonsType() {
        return rawLine.substring(4,6);
    }

    Double getAmountInDouble() {
        Integer amount = new Integer(getAmount());
        return (double)amount / 100;
    }
}

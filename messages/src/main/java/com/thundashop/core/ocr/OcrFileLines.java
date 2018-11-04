/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

import java.util.UUID;

/**
 *
 * @author boggi
 */
public class OcrFileLines {

    private String rawLine = "";
    String avtaleId = "";
    private boolean beenTransferred = false;
    private Double amount = 0.0;
    private Long matchOnOrderId = (long)-1;
    private String matchDate = "";
    private String kid = "";
    private String orcLineId = UUID.randomUUID().toString();
    
    public String getOcrLineId() {
        return orcLineId;
    }
    
    public OcrFileLines() {
    }
    
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

    boolean isTransferred() {
        return beenTransferred;
    }
    void setBeenTransferred() {
        amount = getAmountInDouble();
        matchDate = getOppgjorsDato();
        kid = getKid();
        beenTransferred = true;
    }
    void setMatchOnOrderId(Long orderId) {
        matchOnOrderId = orderId;
    }

    private String getOppgjorsDato() {
        return rawLine.substring(15,21);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

import com.thundashop.core.common.DataCommon;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author boggi
 */
public class OcrFileLines extends DataCommon {

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

    public Date getPaymentDate() {
        SimpleDateFormat slf = new SimpleDateFormat("ddMMyy HH:mm:ss");
        try {
            return slf.parse(getOppgjorsDato()+" 11:00:00");
        } catch (ParseException ex) {
            Logger.getLogger(OcrFileLines.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    boolean isForDay(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        
        cal.setTime(getPaymentDate());
        
        return cal.get(Calendar.YEAR) == year 
                && cal.get(Calendar.MONTH) == (month-1)
                && cal.get(Calendar.DAY_OF_MONTH) == day;
    }

    public String getBatchId() {
        return rawLine.substring(21, 31);
    }
}

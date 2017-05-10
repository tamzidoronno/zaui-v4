/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.Database;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author ktonder
 */
public class ViewSmsHistory {
    
    private int month;
    private int year;

    public ViewSmsHistory(int month, int year) {
        this.month = month;
        this.year = year;
    }
    
    public void view(Database database, String storeName, String orgNr) {
        List<DataCommon> datas = null;
        
        Date start = getDate(year, month,1);
        Date end = getDate(year, (month + 1),1);
        
        datas = database.find("col_"+getStoreId(storeName)+"_log", start, end, MessageManager.class.getSimpleName(), null);
        
        int utlandet = 0;
        int norge = 0;
        for (DataCommon data : datas) {
            if (data instanceof SmsMessage) {
                SmsMessage sms = (SmsMessage)data;
                double msgCount = (double)sms.message.length() / (double)160;
                msgCount = Math.ceil(msgCount);
                
                if (sms.prefix.equals("47")) {
                    norge += msgCount;
                } else {
                    utlandet += msgCount;
                }
            }
        }
        
        System.out.println("Norge: " + String.format("%10s", norge)  + " Utlandet: " + String.format("%10s", utlandet) + " (" + storeName + ")" + " (orgnr: " + orgNr + ")");
    }
    
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        Database database = context.getBean(Database.class);
        
        int month = 4;
        int year = 2017;
        
        Date start = getDate(year, month,1);
        Date end = getDate(year, (month + 1),1);
        
        System.out.println("Start: " + start + " to " + end);
        
        ViewSmsHistory hist = new ViewSmsHistory(month, year);
        hist.view(database, "wh", "");
        hist.view(database, "pronorge", "");
        hist.view(database, "prosweden", "");
        hist.view(database, "renahotell", "");
        hist.view(database, "kongsvinger", "");
        hist.view(database, "akershave", "");
        hist.view(database, "bolgenemoi", "");
        hist.view(database, "Fasthotel - lofoten", "916 563 035 ");
        hist.view(database, "Fasthotel - svolvær", "917 533 555");
        hist.view(database, "Fasthotel - havna", "818 772 092");
        
        System.exit(0);
    }

    private static Date getDate(int year, int mont, int day) {
        mont = mont - 1;
        Calendar cal = Calendar.getInstance();
        cal.set(year, mont, day,0,0,0);
       
        return cal.getTime();
    }

    private String getStoreId(String storeName) {
        if (storeName.equals("wh")) {
            return "123865ea-3232-4b3b-9136-7df23cf896c6";
        }
        
        if (storeName.equals("kongsvinger")) {
            return "9dda21a8-0a72-4a8c-b827-6ba0f2e6abc0";
        }
        
        if (storeName.equals("pronorge")) {
            return "17f52f76-2775-4165-87b4-279a860ee92c";
        }
        
        if (storeName.equals("prosweden")) {
            return "6524eb45-fa17-4e8c-95a5-7387d602a69b";
        }
        
        if (storeName.equals("renahotell")) {
            return "87cdfab5-db67-4716-bef8-fcd1f55b770b";
        }
        
        if (storeName.equals("akershave")) {
            return "75e5a890-1465-4a4a-a90a-f1b59415d841";
        }
        
        if (storeName.equals("Fasthotel - lofoten")) {
            return "e625c003-9754-4d66-8bab-d1452f4d5562";
        }
        
        if (storeName.equals("Fasthotel - svolvær")) {
            return "a152b5bd-80b6-417b-b661-c7c522ccf305";
        }
        
        if (storeName.equals("Fasthotel - havna")) {
            return "3b647c76-9b41-4c2a-80db-d96212af0789";
        }
        
        if (storeName.equals("bolgenemoi")) {
            return "178330ad-4b1d-4b08-a63d-cca9672ac329";
        }
        
        throw new NullPointerException("Not a known store");
    }
}

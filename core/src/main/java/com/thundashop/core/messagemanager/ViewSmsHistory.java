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
    
    public void view(Database database, String storeName) {
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
        
        System.out.println("Norge: " + String.format("%10s", norge)  + " Utlandet: " + String.format("%10s", utlandet) + " (" + storeName + ")");
    }
    
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        Database database = context.getBean(Database.class);
        
        int month = 10;
        int year = 2016;
        
        ViewSmsHistory hist = new ViewSmsHistory(month, year);
        hist.view(database, "wh");
        hist.view(database, "pronorge");
        hist.view(database, "prosweden");
        hist.view(database, "renahotell");
        
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
        
        if (storeName.equals("pronorge")) {
            return "17f52f76-2775-4165-87b4-279a860ee92c";
        }
        
        if (storeName.equals("prosweden")) {
            return "6524eb45-fa17-4e8c-95a5-7387d602a69b";
        }
        
        if (storeName.equals("renahotell")) {
            return "87cdfab5-db67-4716-bef8-fcd1f55b770b";
        }
        
        throw new NullPointerException("Not a known store");
    }
}

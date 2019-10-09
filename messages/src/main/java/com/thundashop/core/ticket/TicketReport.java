/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import java.util.Date;
import java.util.List;

/**
 *
 * @author boggi
 */
public class TicketReport {
    
    Integer billableHours;
    Integer billableMinutes;
    Integer billableSeconds;
    
    Integer supportHours;
    Integer supportMinutes;
    Integer supportSeconds;
    
    Integer hoursIncluded = 0;
    Double hoursToInvoice = 0.0;

    Date end;
    Date start;
    List<TicketReportLine> lines;
    
    Double toAddOnInvoice;
    Double toDeductOnInvoice;

    void summarizeSupport() {
        long millisecondsSupport = 0;
        long millisecondsBillable = 0;
        for(TicketReportLine line : lines) {
            long diff = line.endSupport.getTime() - line.startSupport.getTime();
            millisecondsSupport += diff;
            if(line.billable) {
                millisecondsBillable += diff;
            }
        }
        
        
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long elapsedHours = millisecondsSupport / hoursInMilli;
        millisecondsSupport = millisecondsSupport % hoursInMilli;

        long elapsedMinutes = millisecondsSupport / minutesInMilli;
        millisecondsSupport = millisecondsSupport % minutesInMilli;

        long elapsedSeconds = millisecondsSupport / secondsInMilli;

        supportHours = (int)elapsedHours;
        supportMinutes = (int)elapsedMinutes;
        supportSeconds = (int)elapsedSeconds;
        
        
        elapsedHours = millisecondsBillable / hoursInMilli;
        millisecondsBillable = millisecondsBillable % hoursInMilli;

        elapsedMinutes = millisecondsBillable / minutesInMilli;
        millisecondsBillable = millisecondsBillable % minutesInMilli;

        elapsedSeconds = millisecondsBillable / secondsInMilli;

        billableHours = (int)elapsedHours;
        billableMinutes = (int)elapsedMinutes;
        billableSeconds = (int)elapsedSeconds;
        
        
        
    }

    void generateHoursToInvoiceData() {
        long totalTime = 0;
        long totalBillable = 0;
        for(TicketReportLine line : lines) {
            totalTime += line.endSupport.getTime() - line.startSupport.getTime();
            if(line.billable) {
                totalBillable += line.endSupport.getTime() - line.startSupport.getTime();
            }
        }
        
        toAddOnInvoice = ((double)totalTime / (60*60*1000));
        double totalBillableTime = ((double)totalBillable / (60*60*1000));
        
        toDeductOnInvoice = toAddOnInvoice - totalBillableTime;
        toDeductOnInvoice += hoursIncluded;
        
        if(toDeductOnInvoice > toAddOnInvoice) {
            toDeductOnInvoice = toAddOnInvoice;
        }
    }
    
}

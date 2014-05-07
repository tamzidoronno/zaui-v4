/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.calendarmanager.data;

import com.google.code.morphia.annotations.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Entry implements Serializable, Comparable<Entry> {
    public List<String> attendees = new ArrayList();
    public List<String> dropDiploma = new ArrayList();
    public List<String> waitingList = new ArrayList();
    public List<ExtraDay> otherDays = new ArrayList();
    
    public int maxAttendees;
    
    public boolean availableForBooking = true;
    public boolean isOriginal = true;
    
    public boolean needConfirmation = false;
    public boolean lockedForSignup = false;
    
    public String linkToPage = "";
    public String description;
    public String starttime;
    
    public String extraText = "";
    
    @Transient
    public Location locationObject;
    
    public String locationId;
    
    public String location;
    public String locationExtended = "";
    
    public String stoptime;
    public String entryId;
    public String userId;
    public String title;
    public String color;
    public String eventHelder;
    
    
    public int year;
    public int month;
    public int day;

    public Entry() {
    }
    
    public Entry(Entry entry) {
        this.needConfirmation = entry.needConfirmation;
        this.locationExtended = entry.locationExtended;
        this.lockedForSignup = entry.lockedForSignup;
        this.maxAttendees = entry.maxAttendees;
        this.description = entry.description;
        this.attendees = entry.attendees;
        this.waitingList = entry.waitingList;
        this.locationId = entry.locationId;
        this.otherDays = entry.otherDays;
        this.starttime = entry.starttime;
        this.extraText = entry.extraText;
        this.stoptime = entry.stoptime;
        this.location = entry.location;
        this.entryId = entry.entryId;
        this.userId = entry.userId;
        this.title = entry.title;
        this.dropDiploma = entry.dropDiploma;
        this.color = entry.color;
        this.year = entry.year;
        this.eventHelder = entry.eventHelder;
        this.month = entry.month;
        this.linkToPage = entry.linkToPage;
        this.day = entry.day;
    }

    @Override
    public int compareTo(Entry o) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, o.year);
        cal.set(Calendar.MONTH, o.month);
        cal.set(Calendar.DAY_OF_MONTH, o.day);
        Date dateRepresentation = cal.getTime();
        
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.YEAR, year);
        cal2.set(Calendar.MONTH, month);
        cal2.set(Calendar.DAY_OF_MONTH, day);
        Date dateRepresentation2 = cal2.getTime();
        
        return dateRepresentation.compareTo(dateRepresentation2);
    }

    public boolean isInPast() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, this.year);
        cal.set(Calendar.MONTH, this.month-1);
        cal.set(Calendar.DAY_OF_MONTH, this.day);
        Date dateRepresentation = cal.getTime();
        
        return dateRepresentation.before(new Date());
    }
}

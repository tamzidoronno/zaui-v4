
package com.thundashop.core.pmseventmanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PmsEventListEntry {
    public Date date;
    public String starttime;
    public String title;
    public String place;
    public String shortdesc;
    public String category;
    public String eventId;
    public String bookingId;
    public String arrangedBy;
    public String description = "";
    public List<PmsBookingEventLink> lenker = new ArrayList();
    public String imageid;
    public String eventDateId = "";
    public boolean isDeleted = false;
    
    PmsEventListEntry(PmsBookingEventEntry entry, Date start, String roomName) {
        try {
            if(entry.starttime != null) {
                entry.starttime = entry.starttime.trim();
                String startTime = entry.starttime;
                if(startTime.length() >= 4) {
                    startTime = startTime.substring(0, 5);
                    String startTimes[] = startTime.split(":");
                    if(startTime.contains(".")) {
                        startTimes = startTime.split(".");
                    }
                    if(startTimes.length >= 2) {
                        Integer hour = new Integer(startTimes[0]);
                        Integer minute = new Integer(startTimes[1]);
                        Calendar test = Calendar.getInstance();
                        test.setTime(start);
                        test.set(Calendar.HOUR_OF_DAY, hour);
                        test.set(Calendar.MINUTE, minute);
                        start = test.getTime();
                    }
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        category = entry.category;
        shortdesc = entry.shortdesc;
        starttime = entry.starttime;
        date = start;
        title = entry.title;
        place = roomName;
        eventId = entry.id;
        arrangedBy = entry.arrangedBy;
        description = entry.description;
        lenker = entry.lenker;
        imageid = entry.imageId;
        bookingId = entry.bookingId;
    }
}

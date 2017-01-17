
package com.thundashop.core.pmseventmanager;

import java.util.ArrayList;
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
    
    PmsEventListEntry(PmsBookingEventEntry entry, Date start, String roomName) {
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

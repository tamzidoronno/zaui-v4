/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;

import org.mongodb.morphia.annotations.Transient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class ConferenceData extends DataCommon {
    public String note;
    
    public String nameOfEvent = "";
    
    /**
     * Always finalized to the count of guests on the booking
     */
    @Transient
    public int attendeesCount;
    
    @Transient
    public Date date;
    
    public List<ConferenceDataDay> days = new ArrayList<>();
    
    public String bookingId;
    
    public List<ConferenceData> getForEachDay() {
        List<ConferenceData> data = new ArrayList<>();
        
        for (ConferenceDataDay day : days) {
            ConferenceData singleDay = new ConferenceData();
            singleDay.attendeesCount = this.attendeesCount;
            singleDay.nameOfEvent = this.nameOfEvent;
            singleDay.note = this.note;
            singleDay.bookingId = this.bookingId;
            singleDay.days.add(day);
            data.add(singleDay);
        }
        
        return data;
    }
}

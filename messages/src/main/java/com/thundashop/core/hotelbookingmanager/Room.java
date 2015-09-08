/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.hotelbookingmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class Room extends DomainControlledObject {
    private List<ReservationPart> reservationParts = new ArrayList();
    
    public String name = "";
    public String roomTypeId = "";
    
    @Transient
    public RoomType roomType;
    
    
    /**
     * Check if room is available within two given dates, hours, minutes and seconds are also checked.
     * If room is available it will return true.
     * 
     * @param startDate
     * @param endDate
     * @return 
     */
    public boolean isAvailable(Date startDate, Date endDate) {
        for (ReservationPart part : reservationParts) {
            if (part.startDate.after(endDate))
                continue;
            
            if (part.endDate.before(startDate))
                continue;
            
            if (startDate.after(part.startDate) && startDate.before(part.endDate))
                return false;
            
            if (endDate.before(part.endDate) && endDate.after(part.startDate))
                return false;
            
            if (startDate.before(part.startDate) && endDate.after(part.endDate))
                return false;
            
            if (startDate.equals(part.startDate) && endDate.equals(part.endDate))
                return false;
        }
        
        return true;
    }

    public void addReservation(ReservationPart partReservation1January) {
        if (!isAvailable(partReservation1January.startDate, partReservation1January.endDate)) {
            throw new RoomNotAvailableException();
        }
        
        reservationParts.add(partReservation1January);
    }
}
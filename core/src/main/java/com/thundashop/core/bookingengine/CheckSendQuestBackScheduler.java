/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.getshop.javaapi.APIBookingEngine;
import com.getshop.javaapi.APIEventBookingManager;
import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.eventbooking.BookingItemTypeMetadata;
import com.thundashop.core.eventbooking.Event;
import com.thundashop.core.questback.data.QuestTest;
import com.thundashop.core.usermanager.data.User;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class CheckSendQuestBackScheduler extends GetShopSchedulerBase {

    private APIBookingEngine bookingEngine;
    private APIEventBookingManager eventBookingManager;
    
    public CheckSendQuestBackScheduler(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    @Override
    public void execute() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -5);
        Date from = cal.getTime();
        Date to = new Date();
        
        this.eventBookingManager = this.getApi().getEventBookingManager();
        this.bookingEngine = this.getApi().getBookingEngine();
        
        List<Event> events = eventBookingManager.getEventsWhereEndDateBetween(getMultiLevelName(), from, to);
        for (Event event : events) {
            createQuestBackIfNeeded(event);
        }
    }

    private void createQuestBackIfNeeded(Event event) throws Exception {
        if (event.questBackSent) {
           return;
        }
        
        if (event.bookingItemType == null) {
            return;
        }
        
        BookingItemTypeMetadata res = eventBookingManager.getBookingTypeMetaData(getMultiLevelName(), event.bookingItemType.id);
        
        if (res.questBackId != null && !res.questBackId.isEmpty()) {
            QuestTest test = getApi().getQuestBackManager().getTest(res.questBackId);
            
            if (test == null) {
                return;
            }
            
            List<Booking> bookings = bookingEngine.getAllBookingsByBookingItem(getMultiLevelName(), event.bookingItemId);
            for (Booking booking : bookings) {
                sendQuestBack(booking, test, event);
            }
        }
    }

    private void sendQuestBack(Booking booking, QuestTest test, Event event) throws Exception {
        User user = getApi().getUserManager().getUserById(booking.userId);
        if (user != null) {
            getApi().getQuestBackManager().assignUserToTest(test.id, user.id);
            getApi().getEventBookingManager().markQuestBackSent(getMultiLevelName(), event.id);
        }
    }
    
}

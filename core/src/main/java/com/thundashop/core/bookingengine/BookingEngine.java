/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.data.Availability;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingEngineConfiguration;
import com.thundashop.core.bookingengine.data.BookingGroup;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class BookingEngine extends GetShopSessionBeanNamed implements IBookingEngine {

    @Autowired
    public BookingEngineAbstract bookingEngineAbstract;
    
    @Override
    public BookingItemType createABookingItemType(String name) {
        return deepClone(bookingEngineAbstract.createABookingItemType(name));
    }

    @Override
    public List<BookingItemType> getBookingItemTypes() {
        return deepClone(bookingEngineAbstract.getBookingItemTypes());
    }

    @Override
    public void deleteABookingItemType(String id) {
        bookingEngineAbstract.deleteABookingItemType(id);
    }

    @Override
    public BookingItemType getABookingItemType(String id) {
        return deepClone(bookingEngineAbstract.getABookingItemType(id));
    }

    @Override
    public BookingItemType updateBookingItemType(BookingItemType type) {
        return deepClone(bookingEngineAbstract.updateBookingItemType(type));
    }

    @Override
    public BookingItem saveBookingItem(BookingItem item) {
        return deepClone(bookingEngineAbstract.saveBookingItem(item));
    }

    @Override
    public BookingItem getBookingItem(String id) {
        return deepClone(bookingEngineAbstract.getBookingItem(id));
    }

    @Override
    public List<BookingItem> getBookingItems() {
        return deepClone(bookingEngineAbstract.getBookingItems());
    }

    @Override
    public void deleteBookingItem(String id) {
        bookingEngineAbstract.deleteBookingItem(id);
    }

    @Override
    public void setConfirmationRequired(boolean confirmationRequired) {
        bookingEngineAbstract.setConfirmationRequired(confirmationRequired);
    }

    @Override
    public BookingEngineConfiguration getConfig() {
        return deepClone(bookingEngineAbstract.getConfig());
    }

    @Override
    public List<Booking> getAllBookings() {
        return deepClone(bookingEngineAbstract.getAllBookings());
    }

    @Override
    public void changeTypeOnBooking(String bookingId, String itemTypeId) {
        bookingEngineAbstract.changeTypeOnBooking(bookingId, itemTypeId);
    }

    @Override
    public void changeDatesOnBooking(String bookingId, Date start, Date end) {
        bookingEngineAbstract.changeDatesOnBooking(bookingId, start, end);
    }

    @Override
    public void changeBookingItemOnBooking(String booking, String item) {
        bookingEngineAbstract.changeBookingItemOnBooking(booking, item);
    }

    public boolean isConfirmationRequired() {
        return bookingEngineAbstract.isConfirmationRequired();
    }

    public boolean canAdd(List<Booking> bookingsToAdd) {
        return bookingEngineAbstract.canAdd(bookingsToAdd);
    }

    public BookingGroup addBookings(List<Booking> bookingsToAdd) {
        return deepClone(bookingEngineAbstract.addBookings(bookingsToAdd));
    }

    public Booking getBooking(String bookingId) {
        return deepClone(bookingEngineAbstract.getBooking(bookingId));
    }

    public void deleteBooking(String id) {
        bookingEngineAbstract.deleteBooking(id);
    }

    public BookingItemType getBookingItemType(String id) {
        return deepClone(bookingEngineAbstract.getBookingItemType(id));
    }

    public void addAvailability(String bookingItemId, Availability availability) {
        bookingEngineAbstract.addAvailability(bookingItemId, availability);
    }

    public Availability getAvailbility(String id) {
        return deepClone(bookingEngineAbstract.getAvailbility(id));
    }

    public List<Booking> getConfirmationList(String bookingItemTypeId) {
        return deepClone(bookingEngineAbstract.getConfirmationList(bookingItemTypeId));
    }

    public BookingTimeLineFlatten getTimelines(String id, Date startDate, Date endDate) {
        return deepClone(bookingEngineAbstract.getTimelines(id, startDate, endDate));
    }
    
}
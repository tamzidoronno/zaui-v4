/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsbookingprocess;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.Date;
import java.util.List;


/**
 * Booking process for property management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IPmsBookingProcess {
    public StartBookingResult startBooking(StartBooking arg);
    public GuestAddonsSummary getAddonsSummary(List<RoomsSelected> arg);
    public GuestAddonsSummary changeNumberOnType(BookingTypeChange change);
    public GuestAddonsSummary changeGuestCountForRoom(String roomId, int guestCount);
    public GuestAddonsSummary addAddons(AddAddons arg);
    public GuestAddonsSummary removeAddons(AddAddons arg);
    public GuestAddonsSummary saveGuestInformation(List<RoomInformation> arg);
    public GuestAddonsSummary removeRoom(String roomId);
    public GuestAddonsSummary removeGroupedRooms(RemoveGroupedRoomInput arg);
    public GuestAddonsSummary setGuestInformation(BookerInformation bookerInfo);
    public GuestAddonsSummary changeDateOnRoom(StartBooking arg);
    public BookingResult completeBooking(CompleteBookingInput input);
    public BookingResult completeBookingForTerminal(CompleteBookingInput input);
    public BookingConfig getConfiguration();
    public GuestAddonsSummary logOn(BookingLogonData logindata);
    public GuestAddonsSummary logOut();
    public void printReciept(BookingPrintRecieptData data);
    public StartPaymentProcessResult startPaymentProcess(StartPaymentProcess data);
    public void cancelPaymentProcess(StartPaymentProcess data);
    public void chargeOrderWithVerifoneTerminal(String orderId, String terminalId);
    public List<String> getTerminalMessages();
    public void addTestMessagesToQueue(String message);
    public List<BookingProcessRoomStatus> getBooking(String pmsBookingId);
    public void setBookingItemToCurrentBooking(String roomId, String itemId);
    @Administrator
    public String addBookingItemType(String bookingId, String type, Date start, Date end, String guestInfoFromRoom);
}

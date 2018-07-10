/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.PmsRoomSimple;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
interface IResturantManager {  
    @Customer
    public List<ResturantRoom> getRooms();
    
    @Customer
    public ResturantRoom getRoomById(String roomId);

    @Customer
    public ResturantTable getTableById(String tableId);

    @Customer
    public void addCartItems(List<ResturantCartItem> cartItems, String tableId);
    
    @Customer
    public TableSession createTableSession(String tableId);
    
    @Customer
    public List<TableSession> getAllSessions();
    
    @Administrator
    public void createTable(String roomId, String tableId);
    
    @Administrator
    public void deleteTable(String tableId);
    
    @Administrator
    public void createRoom(String roomName);
    /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     * 
     * @param tableId
     * @return 
     */
    @Customer
    public List<TableSession> getAllSessionsForTable(String tableId);
    
    @Customer 
    public List<ResturantCartItem> changeToDifferentSession(String sessionId, String tableId);
    
    @Customer 
    public TableData getCurrentTableData(String tableId);
    
    @Editor
    public Order completePayment(String paymentMethodId, List<ResturantCartItem> cartItemIds);
    
    @Editor
    public boolean isOrderPriceCorrect(String paymentMethodId, List<ResturantCartItem> cartItems, double price);
    
    @Editor
    public void payOnRoom(PmsRoomSimple room, List<ResturantCartItem> cartItemsIds);
    
    @Editor
    public PmsRoomSimple checkPinCode(String pincode, String bookingId, String pmsRoomId);
    
    @Editor
    public void addCartItemToCurrentTableSession(String tableId, ResturantCartItem cartItem);
    
    @Editor
    public void createCartForTable(String tableId);
    
    @Editor
    public void createCartForReservation(String reservationId);
    
    public void startNewReservation(Date start, Date end, String name, String tableId);
    
    @Editor
    public RestaurantTableDay getTableDayData(Date date, String tableId);
    
    @Editor
    public TableReservation getTableReservation(String reservationId);
    
    @Editor
    public void addCartItemsToReservation(List<ResturantCartItem> cartItems, String reservationId);
    
    @Customer 
    public TableData getTableDataForReservation(String reservationId);
    
    @Editor
    public void prePrint(String paymentMethodId, List<ResturantCartItem> cartItemIds, String printerId);
}

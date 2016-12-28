/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.thundashop.core.common.Customer;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.pmsmanager.PmsRoomSimple;
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
    
    @Customer
    public void completePayment(String paymentMethodId, List<String> cartItemIds);
    
    @Editor
    public void payOnRoom(PmsRoomSimple room, List<String> cartItemsIds);
}

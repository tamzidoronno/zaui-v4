/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.thundashop.core.common.Customer;
import com.thundashop.core.common.GetShopApi;
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
    public void addCartItems(List<ResturantCartItem> cartItems);
    
    @Customer
    public String createTableSession(String tableId);
}

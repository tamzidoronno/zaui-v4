/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class ResturantManager extends ManagerBase implements IResturantManager {
    private Map<String, ResturantRoom> rooms = new HashMap();
    private Map<String, ResturantTable> tables = new HashMap();

    @Autowired
    private CartManager cartManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        ResturantDemoData demoData = new ResturantDemoData();
        
        rooms = demoData.getRooms();
        tables = demoData.getTables();
    }


    @Override
    public List<ResturantRoom> getRooms() {
        rooms.values().stream()
                .forEach(room -> finalizeRoom(room));
        
        return new ArrayList(rooms.values());
    }
    
    private void finalizeRoom(ResturantRoom room) {
        room.tables.clear();
        for (String tableId : room.tableIds) {
            room.tables.add(getFinalizedTable(tableId));
        }
    }

    @Override
    public ResturantRoom getRoomById(String tableId) {
        ResturantRoom room = rooms.get(tableId);
        if (room == null)
            return null;
        
        finalizeRoom(room);
        
        return room;
    }

    private ResturantTable getFinalizedTable(String tableId) {
        ResturantTable table = tables.get(tableId);
        finalizeTable(table);
        return table;
    }

    private void finalizeTable(ResturantTable table) {
        if (table == null)
            return;
        
        if (table.currentCartId == null || table.currentCartId.isEmpty()) {
            startNewCartOnTable(table);
        }
        
        table.currentCart = cartManager.getCartById(table.currentCartId);
    }

    @Override
    public ResturantTable getTableById(String tableId) {
        return getFinalizedTable(tableId);
    }

    private void startNewCartOnTable(ResturantTable table) {
        String newCartId = UUID.randomUUID().toString();
        Cart cart = cartManager.getCartById(newCartId);
        cartManager.storeCart(newCartId);
        table.currentCartId = newCartId;
        saveObject(cart);
    }

    @Override
    public void addCartItems(List<ResturantCartItem> cartItems) {
        
    }

    @Override
    public String createTableSession(String tableId) {
        ResturantTableSession tableSession = new ResturantTableSession();
        tableSession.tableId = tableId;
        tableSession.createdByUserId = getSession().currentUser.id;
        saveObject(tableSession);
        return tableSession.id;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.SessionFactory;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.socket.WebSocketServerImpl;
import com.thundashop.core.usermanager.UserManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
    private Map<String, TableSession> sessions = new HashMap();

    public SessionFactory sessionFactory = new SessionFactory();
    
    @Autowired
    private CartManager cartManager;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    public WebSocketServerImpl webSocketServer;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        ResturantDemoData demoData = new ResturantDemoData();
        
        rooms = demoData.getRooms();
        tables = demoData.getTables();
        
        for (DataCommon idata : data.data) {
            if (idata instanceof TableSession) {
                sessions.put(idata.id, (TableSession)idata);
            }
        }
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
    public void addCartItems(List<ResturantCartItem> cartItems, String tableId) {
        TableSession session = getCurrentTableSession(tableId);
        session.changeCartItems(cartItems);
        saveObject(session);
        sendRefreshMessage(tableId);
    }

    @Override
    public TableSession createTableSession(String tableId) {
        return startNewTableSession(tableId);
    }

    private TableSession getCurrentTableSession(String tableId) {
        String currentTableSessionId = (String) sessionFactory.getObject("resturant_system_session", "resturantmanager_session_id_"+tableId);
        
        if (currentTableSessionId != null) {
            return sessions.get(currentTableSessionId);
        } 
        
        TableSession tableSession = startNewTableSession(tableId);
        return tableSession;
    }

    private TableSession startNewTableSession(String tableId) throws ErrorException {
        TableSession tableSession = new TableSession();
        tableSession.tableId = tableId;
        tableSession.createdByUserId = getSession().currentUser.id;
        
        saveObject(tableSession);
        sessions.put(tableSession.id, tableSession);
        sessionFactory.addToSession("resturant_system_session", "resturantmanager_session_id_"+tableId, tableSession.id);
        saveSessionFactory();
        
        sendRefreshMessage(tableId);
        
        return tableSession;
    }
    
    
    private void saveSessionFactory() throws ErrorException {
        sessionFactory.storeId = storeId;
        saveObject(sessionFactory);
    }

    @Override
    public List<TableSession> getAllSessions() {
        finalizeTableSessions();
        return new ArrayList(sessions.values());
    }

    @Override
    public List<TableSession> getAllSessionsForTable(String tableId) {
        finalizeTableSessions();
        
        return new ArrayList(sessions.values()
                .stream()
                .filter(s -> s.tableId != null && s.tableId.equals(tableId))
                .filter(s -> !s.id.equals(sessionFactory.getObject("resturant_system_session", "resturantmanager_session_id_"+tableId)))
                .sorted(TableSession.getSortingByCreatedDate()) 
                .collect(Collectors.toList()));
    }

    private void finalizeTableSessions() {
        for (TableSession session : sessions.values()) {
            session.createByUser = userManager.getUserById(session.createdByUserId);
        }
    }

    @Override
    public List<ResturantCartItem> changeToDifferentSession(String sessionId, String tableId) {
        TableSession session = sessions.get(sessionId);
        
        if (session == null) {
            throw new NullPointerException("Session should never be null here");
        }
        
        sessionFactory.addToSession("resturant_system_session", "resturantmanager_session_id_" + tableId, sessionId);
        
        sendRefreshMessage(tableId);
        
        return session.getCartItems();
    }

    private void sendRefreshMessage(String tableId) {
        RefreshMessage refreshMessage = new RefreshMessage();
        refreshMessage.tableId = tableId;
        refreshMessage.sentFromSessionId = getSession().id;
        webSocketServer.sendMessage(refreshMessage);
    }

    @Override
    public TableData getCurrentTableData(String tableId) {
        TableData data = new TableData();
        data.cartItems = getCurrentTableSession(tableId).getCartItems();
        return data;
    }
    
}

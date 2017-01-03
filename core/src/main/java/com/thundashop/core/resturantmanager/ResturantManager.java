/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.SessionFactory;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pmsmanager.PmsRoomSimple;
import com.thundashop.core.printmanager.PrintJob;
import com.thundashop.core.printmanager.PrintManager;
import com.thundashop.core.socket.WebSocketServerImpl;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.pdfbox.exceptions.COSVisitorException;
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
    private OrderManager orderManager;
    
    @Autowired
    public WebSocketServerImpl webSocketServer;
    
    @Autowired
    public GetShopSessionScope sessionScope;
    
    @Autowired
    public StoreApplicationPool storeApplicationPool;
    
    @Autowired
    public ProductManager productManager;
    
    @Autowired
    public PrintManager printManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof ResturantRoom) {
                ResturantRoom room = (ResturantRoom)dataCommon;
                rooms.put(room.id, room);
            }
            
            if (dataCommon instanceof ResturantTable) {
                ResturantTable table = (ResturantTable)dataCommon;
                tables.put(table.id, table);
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
        try {
            generateKitchenNote(session);
        } catch (Exception ex) {
            logPrintException(ex);
        }
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
            if (session.createByUser == null) {
                session.createByUser = userManager.getUserById(session.createdByUserId);
                saveObject(session);
            }
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
        data.tableId = tableId;
        return data;
    }

    @Override
    public boolean isOrderPriceCorrect(String paymentMethodId, List<ResturantCartItem> cartItems, double price) {
        Order order = createOrderInternally(paymentMethodId, cartItems, true);
        Double totalForOrder = order.cart.getTotal(false);
        
        if (price == totalForOrder) {
            return true;
        }
        
        return false;
    }

    private void addCartItems(List<ResturantCartItem> cartItems, boolean dummy) {
        List<CartItem> groupedCartItems = getGroupedCartItems(cartItems, dummy);
        groupedCartItems.stream().forEach(id -> addToCart(id));
    }
    
    @Override
    public Order completePayment(String paymentMethodId, List<ResturantCartItem> cartItems) {
        Order order = createOrderInternally(paymentMethodId, cartItems, false);
        orderManager.saveOrder(order);
        return orderManager.getOrder(order.id);
    }   

    private Order createOrderInternally(String paymentMethodId, List<ResturantCartItem> cartItems, boolean dummy) throws ErrorException {
        cartManager.clear();
        addCartItems(cartItems, dummy); 
        
        Order order = null;
        
        if (dummy) {
            order = orderManager.createOrderDummy(new Address());
        } else {
            order = orderManager.createOrder(new Address());
            orderManager.changeOrderStatus(order.id, Order.Status.PAYMENT_COMPLETED);
            orderManager.changeOrderType(order.id, paymentMethodId);
        }
        
        
        updateDiscountedPrices(order, cartItems);
        return order;
    }
    
    public List<ResturantCartItem> fetchAllCartItems(List<ResturantCartItem> ids, boolean dummy) {
        List<ResturantCartItem> retCartItems = new ArrayList();
        List<TableSession> usedSessions = new ArrayList();
        
        for (ResturantCartItem cartId : ids) {
            boolean found = false;
            for (TableSession tableSession : sessions.values()) {
                if (tableSession.hasItem(cartId.id)) {
                    
                    ResturantCartItem item = tableSession.fetch(cartId.id);
                    if (!dummy) {
                        item = tableSession.fetchAndRemove(cartId.id);
                    }
                     
                    item.discountedPrice = cartId.discountedPrice;
                    retCartItems.add(item);
                    usedSessions.add(tableSession);
                    found = true;
                }
            }
            
            if (!found) {
                retCartItems.add(cartId);
            }
        }
        
        for (TableSession sess : usedSessions) {
            if (sess.getCartItems().isEmpty()) {
                sess.ended = new Date();
                
                String currentTableSessionId = (String) sessionFactory.getObject("resturant_system_session", "resturantmanager_session_id_"+sess.tableId);
                if (currentTableSessionId != null && currentTableSessionId.equals(sess.id)) {
                    sessionFactory.addToSession("resturant_system_session", "resturantmanager_session_id_"+sess.tableId, null);
                }
                
                deleteObject(sess);
            }
        }
        
        return retCartItems;
    }
    
    public void addToCart(CartItem cartItem) {
        if (cartItem.getVariations().isEmpty()) {
            cartManager.addProductItem(cartItem.getProduct().id, cartItem.getCount());
        } else {
            cartManager.addProduct(cartItem.getProduct().id, cartItem.getCount(), cartItem.getVariations());
        }
    }

    @Override
    public void payOnRoom(PmsRoomSimple room, List<ResturantCartItem> cartItemsIds) {
        Application paymentApp = storeApplicationPool.getApplication("f86e7042-f511-4b9b-bf0d-5545525f42de");
        if (paymentApp == null)
            throw new NullPointerException("Can not pay to room as PayOnRoom is not activated");
        
        String bookingengine = paymentApp.getSetting("bookingengine");
        if (bookingengine == null || bookingengine.isEmpty())
            throw new NullPointerException("Can not pay to room as PayOnRoom has no bookingengine configures, configure this under settings");
        
        PmsManager pmsManager = sessionScope.getNamedSessionBean(bookingengine, PmsManager.class);
        List<CartItem> groupedCartItems = getGroupedCartItems(cartItemsIds, false);
        for (CartItem cartItem : groupedCartItems) {
            pmsManager.addCartItemToRoom(cartItem, room.pmsRoomId);
        }
    }

    private List<CartItem> getGroupedCartItems(List<ResturantCartItem> cartItems, boolean dummy) {
        List<ResturantCartItem> allItems = fetchAllCartItems(cartItems, dummy);
        HashMap<String, CartItem> allCartItems = new HashMap();
        
        for (ResturantCartItem item : allItems) {
            CartItem retItem = allCartItems.get(item.productId+"_"+item.getVariationId());
            
            if (retItem == null) {
                retItem = new CartItem();
                Product product = productManager.getProduct(item.productId).clone();
                if (item.discountedPrice > 0)
                    product.price = item.discountedPrice;
                
                retItem.setProduct(product);
                retItem.setVariations(item.options);
                allCartItems.put(product.id+"_"+item.getVariationId(), retItem);
            }
            
            retItem.setCount(retItem.getCount() + 1);
        }
        
        return new ArrayList(allCartItems.values());
    }

    private void updateDiscountedPrices(Order order, List<ResturantCartItem> cartItems) {
        for (ResturantCartItem cartItem : cartItems) {
            
            if (!cartItem.useDiscountedPrice) {
                continue;
            }
            
            List<CartItem> productCartItems = order.cart.getItemsByProductId(cartItem.productId);
            
            for (CartItem item : productCartItems) {
                order.updatePrice(item.getCartItemId(), cartItem.discountedPrice);
            }
        }
    }

    @Override
    public void createTable(String roomId, String tableName) {
        ResturantRoom room = rooms.get(roomId);
        if (room != null) {
            ResturantTable table = new ResturantTable();
            table.name = tableName;
            saveObject(table);
            tables.put(table.id, table);
            room.tableIds.add(table.id);
            saveObject(room);
        }
    }

    @Override
    public void deleteTable(String tableId) {
        ResturantTable table = tables.get(tableId);
        if (table != null) {
            for (ResturantRoom room : rooms.values()) {
                room.tableIds.remove(tableId);
                saveObject(room);
            }
        }
    }

    @Override
    public void createRoom(String roomName) {
        ResturantRoom room = new ResturantRoom();
        room.name = roomName;
        saveObject(room);
        rooms.put(room.id, room);
    }

    private void generateKitchenNote(TableSession session) throws IOException, COSVisitorException {
        if (!anyFoodProducts(session)) {
            return;
        }
        
        PdfKitchenNote note = new PdfKitchenNote(session, productManager);
        ResturantTable table = tables.get(session.tableId);
        String tableName = table.name;
        String tmpPdfName = note.createFile(tableName);
        
        File file = new File(tmpPdfName);
        byte[] bytes;
        try {
            bytes = InvoiceManager.loadFile(file);
            byte[] encoded = Base64.encodeBase64(bytes);
            String encodedString = new String(encoded);
            
            PrintJob job = new PrintJob();
            job.printerId = "utsikten_hotell_printer_1";
            job.content = encodedString;
            printManager.addPrintJob(job);
            
            file.delete();
        } catch (IOException ex) {
            throw new ErrorException(1033);
        }
    }

    private boolean anyFoodProducts(TableSession session) {
        return session.getCartItems().stream()
                .map(item -> productManager.getProduct(item.productId))
                .filter(product -> product.isFood)
                .count() > 0;
    }
}
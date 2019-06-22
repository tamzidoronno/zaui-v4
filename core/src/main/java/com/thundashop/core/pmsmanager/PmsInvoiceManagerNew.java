/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pos.PosConference;
import com.thundashop.core.pos.PosManager;
import com.thundashop.core.pos.PosTab;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class PmsInvoiceManagerNew {
    private OrderManager orderManager;
    private CartManager cartManager;
    private ProductManager productManager;
    private PmsManager pmsManager;
    private final SimpleDateFormat sdf;
    private final PosManager posManager;

    public PmsInvoiceManagerNew(OrderManager orderManager, CartManager cartManager, ProductManager productManager, PmsManager pmsManager, PosManager posManager) {
        this.orderManager = orderManager;
        this.cartManager = cartManager;
        this.productManager = productManager;
        this.pmsManager = pmsManager;
        this.posManager = posManager;
        this.sdf = new SimpleDateFormat("dd-MM-yyyy");
    }
    
    public Order createOrder(List<PmsOrderCreateRow> rows, String paymentMethodId, String userId) {
        cartManager.clear();
        
        Map<String, List<CartItem>> tabItemsAdded = new HashMap();
        
        for (PmsOrderCreateRow roomData : rows) {
            if (roomData.roomId != null && !roomData.roomId.isEmpty()) {
                addAccomodationToCart(roomData);
                addAddonsToCart(roomData);    
            }
            
            if (roomData.conferenceId != null && !roomData.conferenceId.isEmpty()) {
                List<CartItem> itemsAdded = addConferenceCartItems(roomData);
                tabItemsAdded.put(roomData.conferenceId, itemsAdded);
            }
            
        }
        
        if (cartManager.isCartConflictingWithClosedPeriode()) {
            cartManager.getCart().overrideDate = orderManager.getOrderManagerSettings().closedTilPeriode;
        }
        
        User user = pmsManager.userManager.getUserById(userId);
        Address address = user != null ? user.address : null;
        
        Order order = orderManager.createOrder(address);
        order.supportMultipleBookings = true;
        order.createByManager = "PmsDailyOrderGeneration";
        order.userId = userId;
        
        orderManager.saveOrder(order);
        orderManager.changeOrderType(order.id, paymentMethodId);
        
        finishTabs(tabItemsAdded, order);
        
        return order;
    }

    private void addAccomodationToCart(PmsOrderCreateRow roomData) {
        Map<String, List<PmsOrderCreateRowItemLine>> res = roomData.items.stream()
                .filter(o -> o.isAccomocation)
                .collect(Collectors.groupingBy(o -> o.createOrderOnProductId));

        for (String productId : res.keySet()) {
            List<PmsOrderCreateRowItemLine> days = res.get(productId);
            CartItem item = cartManager.addProductItem(productId, getCount(days));
            
            Product prod = item.getProduct();
            prod.price = getAveragePrice(days);
            prod.discountedPrice = prod.price;
            prod.externalReferenceId = roomData.roomId;
            
            if (prod.price != 0 && item.getCount() == 0) {
                item.setCount(1);
            }
            
            item.priceMatrix = new HashMap();
            setMetaData(item, roomData);
            setGuestName(item, roomData);
            setDates(item, roomData);
            
            days.stream().forEach(o -> {
                item.priceMatrix.put(o.date, (o.price * o.count));
            });
            
            item.getProduct().doFinalize();
        }
    }

    private double getAveragePrice(List<PmsOrderCreateRowItemLine> days) {
        double sum = days.stream()
                .mapToDouble(o -> {
                    return o.price * o.count;
                })
                .sum();
        
        int count = getCount(days);
        
        if (count == 0 && sum != 0) {
            count = 1;
        }
        
        if (count == 0)
            return 0;
        
        return sum / count;
    }

    private int getCount(List<PmsOrderCreateRowItemLine> days) {
        int count = days.stream()
                .mapToInt(o -> o.count)
                .sum();
        
        return count;
    }

    private void setMetaData(CartItem item, PmsOrderCreateRow roomData) {
        PmsBooking booking = pmsManager.getBookingFromRoom(roomData.roomId);
        PmsBookingRooms room = booking.getRoom(roomData.roomId);
        
        item.getProduct().additionalMetaData = "";
        
        if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
            BookingItem bookingItem = pmsManager.bookingEngine.getBookingItem(room.bookingItemId);
            item.getProduct().additionalMetaData = bookingItem.bookingItemName;
        }
    }

    private void setGuestName(CartItem item, PmsOrderCreateRow roomData) {
        PmsBooking booking = pmsManager.getBookingFromRoom(roomData.roomId);
        PmsBookingRooms room = booking.getRoom(roomData.roomId);
        
        item.getProduct().metaData = "";
        
        if (room.guests.size() > 0) {
            item.getProduct().metaData = room.guests.get(0).name;
        }
        
    }

    private void setDates(CartItem item, PmsOrderCreateRow roomData) {
        PmsBooking booking = pmsManager.getBookingFromRoom(roomData.roomId);
        PmsBookingRooms room = booking.getRoom(roomData.roomId);
        
        item.startDate = room.date.start;
        item.endDate = room.date.end;
        item.periodeStart = room.date.start;
    }

    private void addAddonsToCart(PmsOrderCreateRow roomData) {
        Map<String, List<PmsOrderCreateRowItemLine>> res = roomData.items.stream()
                .filter(o -> !o.isAccomocation && !o.createOrderOnProductId.isEmpty())
                .collect(Collectors.groupingBy(o -> o.createOrderOnProductId));

        for (String productId : res.keySet()) {
            List<PmsOrderCreateRowItemLine> days = res.get(productId);
            CartItem item = cartManager.addProductItem(productId, getCount(days));
            
            Product prod = item.getProduct();
            prod.price = getAveragePrice(days);
            prod.discountedPrice = prod.price;
            prod.externalReferenceId = roomData.roomId;
            
            item.setCount(getCount(days));
            
            setMetaData(item, roomData);
            setGuestName(item, roomData);
            setDates(item, roomData);
            
            item.itemsAdded = new ArrayList();
            
            days.stream().forEach(d -> {
                PmsBookingAddonItem addonItem = new PmsBookingAddonItem();
                addonItem.count = d.count;
                addonItem.price = d.price;
                addonItem.productId = item.getProduct().id;
                
                try {
                    addonItem.date = sdf.parse(d.date);
                } catch (ParseException ex) {
                    Logger.getLogger(PmsInvoiceManagerNew.class.getName()).log(Level.SEVERE, null, ex);
                }
                addonItem.isIncludedInRoomPrice = d.includedInRoomPrice;
                item.itemsAdded.add(addonItem);
            });
        }
    }

    private List<CartItem> addConferenceCartItems(PmsOrderCreateRow roomData) {
        PosConference posConference = posManager.getPosConference(roomData.conferenceId);
        PosTab tab = posManager.getTab(posConference.tabId);
        
        List<CartItem> itemsToAdd = roomData.items.stream()
                .map(row -> {
                    try {
                        CartItem item = (CartItem) tab.getCartItem(row.cartItemId).clone();
                        item.setProduct(item.getProduct().clone());
                        item.setCount(row.count);
                        item.getProduct().price = row.price;
                        item.conferenceId = roomData.conferenceId;
                        return item;
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(PmsInvoiceManagerNew.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    return null;
                })
                .filter(o -> o != null)
                .collect(Collectors.toList());
        
        if (!itemsToAdd.isEmpty()) {
            cartManager.getCart().addCartItems(itemsToAdd);
        }
        
        return itemsToAdd;
    }

    private void finishTabs(Map<String, List<CartItem>> tabItemsAdded, Order order) {
        for (String conferenceId : tabItemsAdded.keySet()) {
            List<CartItem> cartItems = tabItemsAdded.get(conferenceId);
            if (!cartItems.isEmpty()) {
                PosConference posConference = posManager.getPosConference(conferenceId);
                posManager.finishTabAndOrder(posConference.tabId, order, null, null);
                order.conferenceIds.add(conferenceId);
            }
        }
        
        orderManager.saveObject(order);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
public class PmsBookingPaymentDiffer {
    private final List<Order> orders;
    private final PmsBooking booking;
    private final PmsBookingRooms room;
    private final PmsManager pmsManager;
    private List<String> roomProductIds = new ArrayList();
    private final SimpleDateFormat sdf;

    public PmsBookingPaymentDiffer(List<Order> orders, PmsBooking booking, PmsBookingRooms room, PmsManager pmsManager) {
        this.orders = orders;
        this.booking = booking;
        this.room = room;
        this.pmsManager = pmsManager;
        this.sdf = new SimpleDateFormat("dd-MM-YYYY");

        setProductIdsForRoom();
    }
    
    public PmsRoomPaymentSummary getSummary() {
        PmsRoomPaymentSummary summary = new PmsRoomPaymentSummary();
        summary.pmsBookingId = booking.id;
        summary.pmsBookingRoomId = room.pmsBookingRoomId;
        summary.rows.addAll(createListRoom());
        summary.rows.addAll(createAddonList());
        removeIncludedAddonsFromRoomPrice(summary);
        return summary;
    }

    private List<PmsRoomPaymentSummaryRow> createAddonList() {
        List<PmsRoomPaymentSummaryRow> retList = new ArrayList();
        
        
        List<PmsBookingAddonItem> allItems = getAllItems();
        
        for (PmsBookingAddonItem addonItem : allItems) {
            double value = getCountForRoom(addonItem.productId, addonItem.date) * getPriceForRoom(addonItem.productId, addonItem.date);
            PmsRoomPaymentSummaryRow row = new PmsRoomPaymentSummaryRow();
            List<String> addonProductsIds = new ArrayList();
            addonProductsIds.add(addonItem.productId);
            row.date = sdf.format(addonItem.date);
            row.priceInBooking = value;
            row.cartItemIds = getCartItemsIds(addonProductsIds);
            row.includedInRoomPrice = addonItem.isIncludedInRoomPrice;
            row.createOrderOnProductId = addonItem.productId;
            row.count = addonItem.count;
            finalizeRow(row, retList);
        }

        return retList;
    }
    
    private List<PmsRoomPaymentSummaryRow> createListRoom() {
        List<PmsRoomPaymentSummaryRow> retList = new ArrayList();
        
        List<String> allDatesToLookAt = new ArrayList<String>(room.priceMatrix.keySet());
        allDatesToLookAt.addAll(getAllDatesFromOrders(allDatesToLookAt));
        
        for (String date : allDatesToLookAt) {
            PmsRoomPaymentSummaryRow row = new PmsRoomPaymentSummaryRow();
            row.date = date;
            row.count = 1;
            row.priceInBooking = room.priceMatrix.get(date) != null ? room.priceMatrix.get(date) : 0D;
            row.cartItemIds = getCartItemsIds(roomProductIds);
//            row.productIds = getProductIds(roomProductIds);
            row.isAccomocation = true;
            row.createOrderOnProductId = pmsManager.bookingEngine.getBookingItemType(room.bookingItemTypeId).productId;
            finalizeRow(row, retList);
        }
        
        return retList;
    }

    private void finalizeRow(PmsRoomPaymentSummaryRow row, List<PmsRoomPaymentSummaryRow> retList) {
        calculateAmountInOrders(row);
        calculatePaidAmount(row);
        retList.add(row);
    }

    private void setProductIdsForRoom() {
        roomProductIds = orders.stream()
                .flatMap(o -> o.getCartItems().stream())
                .filter(o -> o.isPriceMatrixItem() && o.getProduct().externalReferenceId != null && !o.getProduct().externalReferenceId.isEmpty())
                .map(o -> pmsManager.getBookingFromRoom(o.getProduct().externalReferenceId).getRoom(o.getProduct().externalReferenceId))
                .map(room -> pmsManager.bookingEngine.getBookingItemType(room.bookingItemTypeId))
                .map(type -> type.productId)
                .collect(Collectors.toList());
                
    }

    private HashMap<String, List<String>> getCartItemsIds(List<String> productList) {
        HashMap<String, List<String>> retMap = new HashMap();
        
        HashMap<String, List<CartItem>> items = getStream(productList);
        
        for (String orderId : items.keySet()) {
            List<String> retList = items.get(orderId).stream()
                .map(o -> o.getCartItemId())
                .collect(Collectors.toList());
            
            retMap.put(orderId, retList);
        }
                
        return retMap;
    }
    
//    private List<String> getProductIds(List<String> productList) {
//        return getStream(productList)
//                .map(o -> o.getProductId())
//                .collect(Collectors.toList());
//    }

    private HashMap<String, List<CartItem>> getStream(List<String> productList) {
        HashMap<String, List<CartItem>> retList = new HashMap();
        
        for (Order order : orders) {
            List<CartItem> itemsForOrder = order.getCartItems().stream()
                .filter(o -> productList.contains(o.getProductId()))
                .filter(o -> o.getProduct().externalReferenceId.equals(room.pmsBookingRoomId))
                .collect(Collectors.toList());
            
            retList.put(order.id, itemsForOrder);
        }
        
        return retList;
    }

    private void calculateAmountInOrders(PmsRoomPaymentSummaryRow row) {
        double sum = 0;
        
        for (String orderId : row.cartItemIds.keySet()) {
            for (String cartItemId : row.cartItemIds.get(orderId)) {
                CartItem cartItem = getCartItem(orderId, cartItemId);
                sum += getAmountForDate(cartItem, row.date);
            }
        }
        
        row.createdOrdersFor = sum;
    }

    private CartItem getCartItem(String orderId, String id) {
        CartItem retItem = null;
        
        for (Order order : orders) {
            if (!order.id.equals(orderId)) {
                continue;
            }
            for (CartItem item :order.getCartItems()) {
                if (item.getCartItemId().equals(id))
                    retItem = item;
            }
        }
        
        return retItem;
    }

    private void calculatePaidAmount(PmsRoomPaymentSummaryRow row) {
        
       row.actuallyPaidAmount = orders.stream()
                .filter(o -> o.status == Order.Status.PAYMENT_COMPLETED)
                .mapToDouble(order -> {
                    List<CartItem> cartItems = order.getCartItems();
                    List<String> cartItemIds = row.cartItemIds.get(order.id);
                    for (CartItem item : cartItems) {
                        if (cartItemIds.contains(item.getCartItemId())) {
                            return getAmountForDate(item, row.date);
                        }
                    }
                    return 0D;
                })
                .sum();
        
    }

    private Double getAmountForDate(CartItem item, String date) {
        if (item.priceMatrix != null && !item.priceMatrix.isEmpty() && item.priceMatrix.get(date) != null) {
            return item.priceMatrix.get(date);
        }
        
        if (item.itemsAdded != null && !item.itemsAdded.isEmpty()) {
            return item.itemsAdded.stream()
                    .filter(o -> sdf.format(o.date).equals(date))
                    .mapToDouble(o -> o.count * o.price)
                    .sum();
        }
        
        return 0D;
    }

    private void removeIncludedAddonsFromRoomPrice(PmsRoomPaymentSummary summary) {
        List<PmsRoomPaymentSummaryRow> rowsIncludedInRoomPrice = summary.rows.stream()
                .filter(row -> row.includedInRoomPrice)
                .collect(Collectors.toList());
        
        rowsIncludedInRoomPrice.stream()
                .forEach(includedInRoomPriceRow -> {
                    PmsRoomPaymentSummaryRow accomodationRow = getAccomodationRow(summary, includedInRoomPriceRow.date);
                    if (accomodationRow != null) {
                        accomodationRow.priceInBooking -= includedInRoomPriceRow.priceInBooking;
                    }
                });
    }

    private PmsRoomPaymentSummaryRow getAccomodationRow(PmsRoomPaymentSummary summary, String date) {
        return summary.rows.stream()
                .filter(row -> row.date.equals(date) && row.isAccomocation)
                .findFirst()
                .orElse(null);
    }

    private List<String> getAllDatesFromOrders(List<String> allDatesToLookAt) {
        List<String> dates = orders.stream()
                .flatMap(order -> order.getCartItems().stream())
                .flatMap(item -> {
                    if (item.priceMatrix != null && !item.priceMatrix.isEmpty()) {
                        return item.priceMatrix.keySet().stream();
                    }

                    if (item.itemsAdded != null && !item.itemsAdded.isEmpty()) {
                        return item.itemsAdded
                                .stream()
                                .map(o -> sdf.format(o.date));
                    }
                    
                    return new ArrayList<String>().stream();
                })
                .map(o -> (String)o)
                .distinct()
                .collect(Collectors.toList());
        
        dates.removeIf(date -> allDatesToLookAt.contains(date));
        
        return dates;
    }

    private List<PmsBookingAddonItem> getAllItems() {
        List<PmsBookingAddonItem> itemsFromOrders = orders.stream()
                .flatMap(o -> o.getCartItems().stream())
                .filter(o -> o.isPmsAddons())
                .flatMap(o -> o.getItemsAdded().stream())
                .collect(Collectors.toList());
        
        Gson gson = new Gson();
        List<PmsBookingAddonItem> ordersList = gson.fromJson(gson.toJson(itemsFromOrders), new TypeToken<List<PmsBookingAddonItem>>(){}.getType());
        
        List<PmsBookingAddonItem> retList = new ArrayList();
        retList.addAll(room.addons);
        
        for (PmsBookingAddonItem item : ordersList) {
            int countFromRoom = getCountForRoom(item.productId, item.date);
            double priceFromRoom = getPriceForRoom(item.productId, item.date);
            double diff = (item.count * item.price) - (priceFromRoom * countFromRoom);
            
            if (diff != 0) {
                item.count = item.count - countFromRoom;
                item.price = diff / (double)item.count;
                retList.removeIf(o -> o.productId.equals(item.productId) && sdf.format(o.date).equals(sdf.format(item.date)));
                retList.add(item);  
            } 
            
        }
        
        return retList;
    }
    
    private int getCountForRoom(String productId, Date date) {
        int count = 0;
        for (PmsBookingAddonItem addon : room.addons) {
            if (addon == null || addon.date == null || addon.productId == null || date == null) {
                continue;
            }
            
            if (sdf.format(addon.date).equals(sdf.format(date)) && addon.productId.equals(productId)) {
                count += addon.count;
            }
        }
        
        return count;
    }
    
    private double getPriceForRoom(String productId, Date date) {
        double count = 0;
        for (PmsBookingAddonItem addon : room.addons) {
            if (addon == null || addon.date == null || addon.productId == null || date == null) {
                continue;
            }
            
            if (sdf.format(addon.date).equals(sdf.format(date)) && addon.productId.equals(productId)) {
                count += addon.price;
            }
        }
        
        return count;
    }

}

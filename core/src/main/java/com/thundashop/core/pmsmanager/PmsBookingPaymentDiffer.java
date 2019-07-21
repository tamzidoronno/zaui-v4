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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class PmsBookingPaymentDiffer {
    private final List<Order> orders;
    private final Map<String, Order> ordersMap = new HashMap();
    private final PmsBooking booking;
    private final PmsBookingRooms room;
    private final PmsManager pmsManager;
    private List<String> roomProductIds = new ArrayList();
    private final SimpleDateFormat sdf;

    public PmsBookingPaymentDiffer(List<Order> orders, PmsBooking booking, PmsBookingRooms room, PmsManager pmsManager) {
        this.orders = orders.stream()
                .filter(o -> o != null)
                .collect(Collectors.toList());
        this.booking = booking;
        this.room = room;
        this.pmsManager = pmsManager;
        this.sdf = new SimpleDateFormat("dd-MM-yyyy");
        this.orders.stream()
                .forEach(o -> {
                    ordersMap.put(o.id, o);
                });

        setProductIdsForRoom();
    }
    
    public PmsRoomPaymentSummary getSummary() {
        PmsRoomPaymentSummary summary = new PmsRoomPaymentSummary();
        summary.pmsBookingId = booking.id;
        summary.pmsBookingRoomId = room.pmsBookingRoomId;
        summary.rows.addAll(createListRoom());
        summary.rows.addAll(createAddonList());
        removeIncludedAddonsFromRoomPrice(summary);
        calculateRoomCount(summary);
        setPriceToUseForOrders(summary);
        removeNullRows(summary);
        invertNegativeCountToCreateOrdersFor(summary);
        correctZeroCountOffset(summary);
        return summary;
    }

    private List<PmsRoomPaymentSummaryRow> createAddonList() {
        List<PmsBookingAddonItem> roomsAddon = clone(getAllPmsAddonsFromRoom(room)); 
        List<PmsBookingAddonItem> ordersAddon = clone(getAllPmsAddonsFromOrders());
        List<PmsBookingAddonItem> paidAddon = clone(getAllPmsAddonsFromOrdersPaid());
        
        changeCountAndPriceIfNegative(roomsAddon);
        changeCountAndPriceIfNegative(ordersAddon);
        changeCountAndPriceIfNegative(paidAddon);
        
        Map<String, List<PmsBookingAddonItem>> roomAddonsGroupedByDay = groupByDay(roomsAddon);
        Map<String, List<PmsBookingAddonItem>> orderAddonsGroupedByDay = groupByDay(ordersAddon);
        Map<String, List<PmsBookingAddonItem>> paidAddonsGroupedByDay = groupByDay(paidAddon);
        
        Map<String, List<String>> addAddonsByKey = getAddonKeys(roomAddonsGroupedByDay, orderAddonsGroupedByDay);
        
        List<PmsRoomPaymentSummaryRow> retList = new ArrayList();
        
        for (String date : addAddonsByKey.keySet()) {
            for (String key : addAddonsByKey.get(date)) {
                PmsRoomPaymentSummaryRow row = new PmsRoomPaymentSummaryRow();
                row.date = date;
                row.priceInBooking = getAvaragePrice(date, key, roomAddonsGroupedByDay);
                row.includedInRoomPrice = key.contains(";isincluded;");
                row.createOrderOnProductId = key.split(";")[0];
                row.textOnOrder = getTextOnOrder(date, key, roomAddonsGroupedByDay);
                
                row.countFromBooking = getCount(date, key, roomAddonsGroupedByDay);
                row.countFromOrders = getCount(date, key, orderAddonsGroupedByDay);
                row.count = row.countFromBooking - row.countFromOrders;
                if(key.split(";").length > 3) {
                    row.addonId = key.split(";")[3];
                }             
                
                row.actuallyPaidAmount = getAvaragePrice(date, key, paidAddonsGroupedByDay) * getCount(date, key, paidAddonsGroupedByDay);
                
                row.createdOrdersFor = getAvaragePrice(date, key, orderAddonsGroupedByDay);
                row.createdOrdersForByPaymentMethods = getAvaragePriceByPaymentType(date, key, orderAddonsGroupedByDay);
                retList.add(row);
            }
        }
        
        return retList;
    }
    
    private List<PmsBookingAddonItem> clone(List<PmsBookingAddonItem> toClone) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(toClone), new TypeToken<List<PmsBookingAddonItem>>(){}.getType());
    }
    
    private List<PmsRoomPaymentSummaryRow> createListRoom() {
        List<PmsRoomPaymentSummaryRow> retList = new ArrayList();
        
        List<String> allDatesToLookAt = new ArrayList<String>(room.priceMatrix.keySet());
        allDatesToLookAt.addAll(getAllDatesFromOrders(allDatesToLookAt));
        
        for (String date : allDatesToLookAt) {
            PmsRoomPaymentSummaryRow row = createEmptyRoomSummaryRow(date);
            
            calculateAmountInOrders(row);
            calculatePaidAmount(row);
            retList.add(row);
        }
        
        return retList;
    }

    private PmsRoomPaymentSummaryRow createEmptyRoomSummaryRow(String date) {
        PmsRoomPaymentSummaryRow row = new PmsRoomPaymentSummaryRow();
        row.date = date;
        row.priceInBooking = 0;
        if (!room.isDeleted() || room.nonrefundable) {
            row.priceInBooking = room.priceMatrix.get(date) != null ? room.priceMatrix.get(date) : 0D;
        }
        row.cartItemIds = getCartItemsIds(roomProductIds);
        row.isAccomocation = true;
        row.countFromBooking = 1;
        row.countFromOrders = 1;
        if (pmsManager.bookingEngine.getBookingItemType(room.bookingItemTypeId) != null) {
            row.createOrderOnProductId = pmsManager.bookingEngine.getBookingItemType(room.bookingItemTypeId).productId;
        }
        return row;
    }

    private void setProductIdsForRoom() {
        roomProductIds = pmsManager.bookingEngine.getBookingItemTypesIds()
                .stream()
                .map(id -> pmsManager.bookingEngine.getBookingItemType(id))
                .filter(type -> type.productId != null && !type.productId.isEmpty())
                .map(type -> type.productId)
                .collect(Collectors.toList());
                
    }

    private HashMap<String, List<String>> getCartItemsIds(PmsBookingAddonItem addonItem) {
        HashMap<String, List<String>> retMap = new HashMap();
        
        for (Order order : orders) {
            for (CartItem item : order.getCartItems()) {
                List<String> itemIds = new ArrayList();
                if (item.isPmsAddons() && item.itemsAdded.contains(addonItem)) {
                    itemIds.add(item.getCartItemId());
                }
                
                retMap.put(order.id, itemIds);
            }
        }
        
        return retMap;
        
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
        
        HashMap<String, Double> createdOrdersForByPaymentMethods = new HashMap();
        
        for (String orderId : row.cartItemIds.keySet()) {
            Order order = ordersMap.get(orderId);
            
            for (String cartItemId : row.cartItemIds.get(orderId)) {
                CartItem cartItem = getCartItem(orderId, cartItemId);
                double iSum = getAmountForDate(cartItem, row.date);
                sum += iSum;
                
                if (createdOrdersForByPaymentMethods.get(order.payment.paymentType) == null) {
                    createdOrdersForByPaymentMethods.put(order.payment.paymentType, 0D);
                }
                
                createdOrdersForByPaymentMethods.put(order.payment.paymentType, createdOrdersForByPaymentMethods.get(order.payment.paymentType) + iSum);
            }            
        }
        
        row.createdOrdersForByPaymentMethods = createdOrdersForByPaymentMethods;
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
                    
                    if (accomodationRow == null) {
                        accomodationRow = createEmptyRoomSummaryRow(includedInRoomPriceRow.date);    
                        summary.rows.add(accomodationRow);
                    }
                    
                    accomodationRow.priceInBooking -= (includedInRoomPriceRow.priceInBooking * includedInRoomPriceRow.countFromBooking);
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

    private List<PmsBookingAddonItem> getAllPmsAddonsFromOrders() {
        return orders.stream()
                .flatMap(o -> o.getCartItems().stream())
                .filter(item -> item.isPmsAddons())
                .filter(item -> item.getProduct().externalReferenceId.equals(room.pmsBookingRoomId))
                .flatMap(item -> item.itemsAdded.stream())
                .collect(Collectors.toList());
    }

    private Map<String, List<PmsBookingAddonItem>> groupByDay(List<PmsBookingAddonItem> roomsAddon) {
        List<PmsBookingAddonItem> allItems = new ArrayList();
        allItems.addAll(roomsAddon);
        Map<String, List<PmsBookingAddonItem>> res = allItems.stream()
                .filter(o -> !o.price.equals(0D))
                .collect(Collectors.groupingBy(PmsBookingAddonItem::getStringDate));
        
        return res;
    }

    private Map<String, List<String>> getAddonKeys(Map<String, List<PmsBookingAddonItem>> roomAddonsGroupedByDay, Map<String, List<PmsBookingAddonItem>> orderAddonsGroupedByDay) {
        Map<String, List<String>> retList = new HashMap();
        
        addKeys(roomAddonsGroupedByDay, retList);
        addKeys(orderAddonsGroupedByDay, retList);
        
        return retList;
    }

    private void addKeys(Map<String, List<PmsBookingAddonItem>> roomAddonsGroupedByDay, Map<String, List<String>> retList) {
        for (String date : roomAddonsGroupedByDay.keySet()) {
            List<String> keys = retList.get(date);
            if (keys == null) {
                keys = new ArrayList();
                retList.put(date, keys);
            }
            
            keys.addAll(roomAddonsGroupedByDay.get(date).stream()
                    .map(addon -> addon.getKey())
                    .collect(Collectors.toList()));
            
            keys = keys.stream().distinct().collect(Collectors.toList());
            retList.put(date, keys);
        }
    }

    private double getAvaragePrice(String date, String key, Map<String, List<PmsBookingAddonItem>> roomAddonsGroupedByDay) {
        if (roomAddonsGroupedByDay.get(date) == null)
            return 0;
        
        List<PmsBookingAddonItem> items = roomAddonsGroupedByDay.get(date).stream()
                .filter(addon -> addon.getKey().equals(key))
                .collect(Collectors.toList());
        
        double sum = items.stream().mapToDouble(item -> item.price * item.count).sum();
        double count = items.stream().mapToInt(item -> item.count).sum();
        
        if (sum == 0 || count == 0)
            return 0;
        
        return (sum/count);
    }
    
    private String getTextOnOrder(String date, String key, Map<String, List<PmsBookingAddonItem>> roomAddonsGroupedByDay) {
        if (roomAddonsGroupedByDay.get(date) == null)
            return "";
        
        String text = "";
        for(PmsBookingAddonItem tmpItm : roomAddonsGroupedByDay.get(date)) {
            if(tmpItm.getKey().equals(key) && tmpItm.getName() != null && tmpItm.getName().length() > 0) {
                text = tmpItm.getName();
            }
        }
        
        return text;
    }
    
    private HashMap<String, Double> getAvaragePriceByPaymentType(String date, String key, Map<String, List<PmsBookingAddonItem>> roomAddonsGroupedByDay) {
        if (roomAddonsGroupedByDay.get(date) == null)
            return null;
        
        List<PmsBookingAddonItem> items = roomAddonsGroupedByDay.get(date).stream()
                .filter(addon -> addon.getKey().equals(key))
                .collect(Collectors.toList());
        
        Map<String, List<PmsBookingAddonItem>> groupedItems = new HashMap();
        
        for(PmsBookingAddonItem addon : items) {
            Order order = getOrder(addon);
            if (groupedItems.get(order.payment.paymentType) == null) {
                groupedItems.put(order.payment.paymentType, new ArrayList());
            }
            
            groupedItems.get(order.payment.paymentType).add(addon);
        }
        
        HashMap<String, Double> retMap = new HashMap();
        
        for (String paymentType : groupedItems.keySet()) {
            double sum = groupedItems.get(paymentType).stream().mapToDouble(item -> item.price * item.count).sum();
            double count = groupedItems.get(paymentType).stream().mapToInt(item -> item.count).sum();

            if (sum != 0) {
                retMap.put(paymentType, (sum/count));
            }
        }
        
        return retMap;
        
    }

    private int getCount(String date, String key, Map<String, List<PmsBookingAddonItem>> roomAddonsGroupedByDay) {
        if (roomAddonsGroupedByDay.get(date) == null)
            return 0;
        
        List<PmsBookingAddonItem> items = roomAddonsGroupedByDay.get(date).stream()
                .filter(addon -> addon.getKey().equals(key))
                .collect(Collectors.toList());
        
        return items.stream()
                .mapToInt(item -> item.count).sum();
    }

    private void calculateRoomCount(PmsRoomPaymentSummary summary) {
        summary.rows.stream()
                .filter(row -> row.isAccomocation)
                .forEach(row -> {
                    
                    double diff = row.priceInBooking - row.createdOrdersFor;

                    if ( diff < 0.01 && diff > -0.01) {
                        row.count = 0;
                    } else if(diff < -0.1) {
                        row.count = -1;
                    } else {
                        row.count = 1;
                    }
                });
    }

    private List<PmsBookingAddonItem> getAllPmsAddonsFromOrdersPaid() {
            return orders.stream()
                .filter(o -> o.status == 7)
                .flatMap(o -> o.getCartItems().stream())
                .filter(item -> item.isPmsAddons())
                .filter(item -> item.getProduct().externalReferenceId.equals(room.pmsBookingRoomId))
                .flatMap(item -> item.itemsAdded.stream())
                .collect(Collectors.toList());
    }

    private void removeNullRows(PmsRoomPaymentSummary summary) {
        summary.rows.removeIf(o -> o.isEmpty());
    }

    private void setPriceToUseForOrders(PmsRoomPaymentSummary summary) {
        summary.rows.stream()
                .forEach(o -> {
                    o.priceToCreateOrders = (o.priceInBooking * o.countFromBooking) - (o.createdOrdersFor * o.countFromOrders);
                    
                    if (o.count != 0) {
                        o.priceToCreateOrders = o.priceToCreateOrders / o.count;
                    }
                });
        
    }

    private void changeCountAndPriceIfNegative(List<PmsBookingAddonItem> addons) {
        addons.stream().forEach(addon -> {
            if (addon.price < 0) {
                addon.price = addon.price * -1;
                addon.count = addon.count * -1;
            }
        });
    }

    private List<PmsBookingAddonItem> getAllPmsAddonsFromRoom(PmsBookingRooms room) {
        if (room.isDeleted() && !room.nonrefundable) {
            return room.addons.stream()
                .filter(o -> o.noRefundable)
                .collect(Collectors.toList());
        } 
        
        return room.addons;
    }

    private void invertNegativeCountToCreateOrdersFor(PmsRoomPaymentSummary summary) {

    }

    private Order getOrder(PmsBookingAddonItem pmsBookingAddonItem) {
        for (Order order : orders) {
            for (CartItem item : order.getCartItems()) {
                if (!item.isPmsAddons()) {
                    continue;
                }
                
                if (item.itemsAdded.stream()
                        .map(o -> o.addonId)
                        .collect(Collectors.toList())
                        .contains(pmsBookingAddonItem.addonId)) {
                    return order;
                }
            }
        }
        
        return null;
    }

    private void correctZeroCountOffset(PmsRoomPaymentSummary summary) {
        summary.rows.stream()
                .forEach(o -> {
                    double createdOrdersFor = o.priceInBooking - o.createdOrdersFor;
                    boolean isNull = createdOrdersFor > -0.001 && createdOrdersFor < 0.001;
                    if (o.count == 0 && !isNull) {
                        o.count = o.countFromBooking;
                        o.priceToCreateOrders = createdOrdersFor;
                    }
                    
                    if (o.count < 0 && o.priceToCreateOrders < 0) {
                        o.count *= -1;
                        o.priceToCreateOrders *= -1;
                    }
                    
                    if (o.count < 0 && o.priceToCreateOrders > 0) {
                        o.count *= -1;
                        o.priceToCreateOrders *= -1;
                    }
                    
                    
                    if (o.priceToCreateOrders == 0) {
                        o.count = 0;
                    }
                    
                });
    }
}

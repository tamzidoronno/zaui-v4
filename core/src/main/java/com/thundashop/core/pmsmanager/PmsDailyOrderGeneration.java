package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsDailyOrderGeneration extends GetShopSessionBeanNamed {

    PmsBooking currentBooking = null;
    NewOrderFilter currentFilter = null;
    
    List<CartItem> generatedCartItems = new ArrayList();
    
    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    ProductManager productManager;
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    CartManager cartManager;
    
    List<String> daysInPriceMatrix = new ArrayList();
    
    private void addCartItem(CartItem cartItem) {
        if(cartItem != null) {
            generatedCartItems.add(cartItem);
        }
    }
    
    private void clearGenerateCart() {
        generatedCartItems.clear();
    }
    
    public String createCart(String bookingId, NewOrderFilter filter) {
        if(filter.itemsToCreate != null && !filter.itemsToCreate.isEmpty()) {
            List<CartItem> itemsToRemove = new ArrayList();
            for(CartItem item : cartManager.getCart().getItems()) {
                if(!filter.itemsToCreate.contains(item.getCartItemId())) {
                    itemsToRemove.add(item);
                }
            }
            for(CartItem item : itemsToRemove) {
                cartManager.removeCartItem(item.getCartItemId());
            }
            return "";
        }
 
        
        clearGenerateCart();
        
        currentBooking = pmsManager.getBooking(bookingId);
        
        currentFilter = filter;
        makeSureEndStartDateIsCorrect();
        
        
        calculateRoomPrices();
        updateCart();
        return "";
    }

    private void calculateRoomPrices() {
        for(PmsBookingRooms room : currentBooking.getAllRoomsIncInactive()) {
            if(currentFilter.pmsRoomId != null && !currentFilter.pmsRoomId.isEmpty()) {
                if(!currentFilter.pmsRoomId.equals(room.pmsBookingRoomId)) {
                    continue;
                }
            }
            
            if(currentFilter.pmsRoomIds != null && !currentFilter.pmsRoomIds.isEmpty()) {
                if(!currentFilter.pmsRoomIds.contains(room.pmsBookingRoomId)) {
                    continue;
                }
            }
            
            //Sleepover prices.
            HashMap<String, Double> priceMatrix = generatePriceMatrix(room);
            generateDailyPriceItems(priceMatrix, room);
            
            //Addon prices.
            HashMap<String, List<PmsBookingAddonItem>> items = getUnpaidAddonsForRoom(room);
            for(String productId : items.keySet()) {
                generateAddonsCostForProduct(items.get(productId), room, true);
                generateAddonsCostForProduct(items.get(productId), room, false);
            }
        }
    }

    private HashMap<String, Double> generatePriceMatrix(PmsBookingRooms room) {
        LinkedHashMap<String, Double> currentMatrix = room.priceMatrix;
        
        if(room.deleted && !currentBooking.nonrefundable) {
            currentMatrix = new LinkedHashMap();
        }
        
        LinkedHashMap<String, Double> newPriceMatrix = new LinkedHashMap();
        for(String offset : currentMatrix.keySet()) {
            Date toCheck = room.convertOffsetToDate(offset);
            if(dateIsFiltered(toCheck)) {
                newPriceMatrix.put(offset, currentMatrix.get(offset));
            }
        }
        currentMatrix = newPriceMatrix;
        
        Gson gson = new Gson();
        String res = gson.toJson(currentMatrix);
        currentMatrix = gson.fromJson(res, LinkedHashMap.class);
        
        List<String> uniqueList = new ArrayList<String>(new HashSet<String>( currentBooking.orderIds ));
        
        for(String orderId : uniqueList) {
            Order order = orderManager.getOrder(orderId);
            for(CartItem item : order.cart.getItems()) {
                if(item.getProduct().externalReferenceId.equals(room.pmsBookingRoomId)) {
                    HashMap<String, Double> orderMatrix = item.priceMatrix;
                    if(orderMatrix != null) {
                        for(String offset : orderMatrix.keySet()) {
                            Double price = orderMatrix.get(offset);
                            
                            if(order.isCreditNote) {
                                price *= -1;
                            }
                            
                            if(currentMatrix.containsKey(offset)) {
                                price = currentMatrix.get(offset) - price;
                            } else {
                                price *= -1;
                            }
                            currentMatrix.put(offset, price);
                        }
                    }
                }
            }
        }
        
        //Include addons which is included in room price.
        if(!room.deleted) {
            Calendar cal = Calendar.getInstance();
            for(PmsBookingAddonItem item : room.addons) {
                if(item.isIncludedInRoomPrice) {
                    cal.setTime(item.date);
                    String offset = PmsBookingRooms.getOffsetKey(cal, currentBooking.priceType);
                    Double price = (item.price * item.count);
                    if(currentMatrix.containsKey(offset)) {
                        price = currentMatrix.get(offset) - price;
                    } else {
                        price *= -1;
                    }
                    currentMatrix.put(offset, price);
                }
            }
        }
        
        return currentMatrix;
    }


    private void generateDailyPriceItems(HashMap<String, Double> priceMatrix, PmsBookingRooms room) {
        TreeMap<Date, DailyPriceObject> dailyPrices = createDailyPriceObjects(priceMatrix);
        
        Date nextDay = null;
        Date lastDay = null;
        Date startDate = null;
        int count = 0;
        Double total = 0.0;
        HashMap<String, Double> priceMatrixToSave = new HashMap();
        for(Date dayToIterate : dailyPrices.keySet()) {
            Double priceOne = null;
            Double priceTwo = null;
            
            if(!dateIsFiltered(dayToIterate)) {
                continue;
            }
            daysInPriceMatrix.add(PmsBookingRooms.convertOffsetToString(dayToIterate));
            
            if(lastDay != null && dailyPrices.containsKey(lastDay)) { priceOne = dailyPrices.get(lastDay).price; }
            if(dailyPrices.containsKey(dayToIterate)) { priceTwo = dailyPrices.get(dayToIterate).price; }
            
            if(isNegated(priceOne,priceTwo)) {
                CartItem item = createCartItemForRoom(count, total, room, startDate, nextDay, null);
                addCartItem(item);
                addPriceMatrix(item, priceMatrix);
                count = 0;
                total = 0.0;
                startDate = null;
                nextDay = null;
            }
            
            if(nextDay != null && !room.isSameDay(nextDay, dayToIterate)) {
                CartItem item = createCartItemForRoom(count, total, room, startDate, nextDay, null);
                addCartItem(item);
                addPriceMatrix(item, priceMatrix);
                startDate = null;
                nextDay = null;
                count = 0;
                total = 0.0;
            }
            
            if(startDate == null) {
                startDate = dayToIterate;
            }

            count++;
            DailyPriceObject priceobject = dailyPrices.get(dayToIterate);
            total += priceobject.price;
            
            nextDay = priceobject.end;
            lastDay = dayToIterate;
        }
        if(count > 0 && total != 0.0) {
            CartItem item = createCartItemForRoom(count, total, room,startDate, nextDay, null);
            addPriceMatrix(item, priceMatrix);
            addCartItem(item);
        }
    }
    
    private void generateAddonsCostForProduct(List<PmsBookingAddonItem> items, PmsBookingRooms room, boolean positiveValues) {
        Collections.sort(items, new Comparator<PmsBookingAddonItem>(){
            public int compare(PmsBookingAddonItem o1, PmsBookingAddonItem o2){
                return o1.date.compareTo(o2.date);
            }
        });
        
        Date start = null;
        Date end = null;
        int count = 0;
        Double total = 0.0;
        String productId = "";
        
        List<PmsBookingAddonItem> itemsAdded = new ArrayList();
        for(PmsBookingAddonItem item : items) {
            if(positiveValues && item.price < 0.0) {
                continue;
            }
            if(!positiveValues && item.price > 0.0) {
                continue;
            }
            double price = item.price * item.count;
            if(price == 0.0) {
                continue;
            }
            if(start == null ||item.date.before(start)) {
                start = item.date;
            }
            if(end == null ||item.date.after(end)) {
                end = item.date;
            }
            productId = item.productId;
            count += item.count;
            total += item.price * item.count;
            itemsAdded.add(item);
        }
        
        if(itemsAdded.isEmpty()) {
            return;
        }
        if(end == null) {
            end = start;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        end = cal.getTime();
        
        CartItem item = createCartItemForRoom(count, total, room,start, end, productId);
        if(item != null) {
            item.itemsAdded = itemsAdded;
            addCartItem(item);
        }
    }

    
    
    private CartItem createCartItemForRoom(int count, Double total, PmsBookingRooms room, Date start, Date end, String productId) {
        boolean isAddon = (productId != null);
        String guestName = "";
        if(!room.guests.isEmpty() && room.guests.get(0).name != null) {
            guestName = room.guests.get(0).name;
        }
        
        String roomName = "";
        if(room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
            roomName = bookingEngine.getBookingItem(room.bookingItemId).bookingItemName;
        }
        
        BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
        boolean useTypeName = false;
        if(productId == null || productId.isEmpty()) {
            productId = type.productId;
            useTypeName = true;
        }
        
        Double avg = 0.0;
       
        if(count != 0.0) {
            avg = total / count;
        }
        
        if(total < 0.0) {
            avg *= -1;
            count *= -1;
        }
        
        CartItem item = createCartItem(productId, count, avg);
        item.startDate = start;
        item.endDate = end;
        item.getProduct().externalReferenceId = room.pmsBookingRoomId;
        item.getProduct().metaData = guestName;
        item.getProduct().additionalMetaData = roomName;
        if(isAddon) {
            item.groupedById = room.pmsBookingRoomId;
        }
        if(useTypeName) {
            item.getProduct().name = type.name;
        }
        return item;
    }
    
    
    private TreeMap<Date, DailyPriceObject> createDailyPriceObjects(HashMap<String, Double> priceMatrix) {
        TreeMap<Date, DailyPriceObject> result = new TreeMap();
        for(String day : priceMatrix.keySet()) {
            DailyPriceObject price = new DailyPriceObject();
            price.price = priceMatrix.get(day);
            price.start = PmsBookingRooms.convertOffsetToDate(day);

            Calendar cal = Calendar.getInstance();
            cal.setTime(price.start);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            price.end = cal.getTime();
            if(dateIsFiltered(price.start)) {
                result.put(price.start, price);
            }
 
        }
        
        TreeMap<Date, DailyPriceObject> finalresult = new TreeMap();
        for(Date date : result.keySet()) {
            if(result.get(date).price == 0.0) {
                continue;
            }
            finalresult.put(date,result.get(date));
        }
        
        return finalresult;
    }

    private CartItem createCartItem(String productId, int count, Double total) {
        CartItem item = new CartItem();
        Product product = productManager.getProductUnfinalized(productId);
        if(product == null) {
            product = productManager.getDeletedProduct(productId);
        }
        if(product == null) {
            logPrint("Product: " + productId + " does not exists anymore");
            return null;
        }
        item.setProduct(product.clone());
        item.getProduct().price = total;
        item.setCount(count);
        return item;
    }

    private void updateCart() {
        cartManager.getCart().clear();
        cartManager.getCart().addCartItems(generatedCartItems);
    }

    private boolean isNegated(Double priceOne, Double priceTwo) {
        if(priceOne == null || priceTwo == null) {
            return false;
        }
        
        if(priceOne < 0 && priceTwo > 0) {
            return true;
        }
        if(priceOne > 0 && priceTwo < 0) {
            return true;
        }
        
        return false;
        
    }

    private HashMap<String, List<PmsBookingAddonItem>> getUnpaidAddonsForRoom(PmsBookingRooms room) {
        HashMap<String, PmsBookingAddonItem> addonsToAdd = new HashMap();
        for(PmsBookingAddonItem item : room.addons) {
            if(!dateIsFiltered(item.date)) {
                continue;
            }
            addonsToAdd.put(item.addonId, item);
        }
        
        if(room.deleted && !currentBooking.nonrefundable) { 
            for(PmsBookingAddonItem item : addonsToAdd.values()) {
                item.price = 0.0;
            }
            
            //Include non refundable addons.
            for(PmsBookingAddonItem item : room.addons) {
                if(!dateIsFiltered(item.date)) {
                    continue;
                }
                PmsBookingAddonItem baseItem = pmsManager.getConfigurationSecure().getAddonFromProductId(item.productId);
                if(baseItem.noRefundable) {
                    addonsToAdd.put(item.addonId, item);
                }
            }
        }
        
        
        Type type = new TypeToken<HashMap<String, PmsBookingAddonItem>>(){}.getType();
        
        Gson gson = new Gson();
        String copy = gson.toJson(addonsToAdd);
        addonsToAdd = gson.fromJson(copy, type);
      
        List<String> uniqueList = new ArrayList<String>(new HashSet<String>( currentBooking.orderIds ));
        for(String orderId : uniqueList) {
            Order order = orderManager.getOrder(orderId);
            for(CartItem item : order.cart.getItems()) {
                if(item.getProduct().externalReferenceId.equals(room.pmsBookingRoomId)) {
                    if(item.itemsAdded != null) {
                        copy = gson.toJson(item.itemsAdded);
                        
                        
                        type = new TypeToken<List<PmsBookingAddonItem>>(){}.getType();
                        List<PmsBookingAddonItem> alreadyAdded = gson.fromJson(copy, type);
                        
                        for(PmsBookingAddonItem addonAlreadyBilled : alreadyAdded) {
                            PmsBookingAddonItem toCheck = addonAlreadyBilled;
                            if(toCheck == null || toCheck.price == null) {
                                continue;
                            }
                            
                            PmsBookingAddonItem addonOnRoom = null;
                            if(addonsToAdd.containsKey(addonAlreadyBilled.addonId)) {
                                addonOnRoom = addonsToAdd.get(addonAlreadyBilled.addonId);
                                if(order.isCreditNote) {
                                    addonAlreadyBilled.count *= -1;
                                }
                                removeFromAddon(addonOnRoom, addonAlreadyBilled);
                            } else {
                                if(order.isCreditNote) {
                                    toCheck.count *= -1;
                                }
                                addonsToAdd.put(toCheck.addonId, toCheck);
                            }
                        }
                    }
                }
            }
        }
        
        HashMap<String, List<PmsBookingAddonItem>> result = new HashMap();
        for(PmsBookingAddonItem item : addonsToAdd.values()) {
            List<PmsBookingAddonItem> items = new ArrayList();
            if(result.containsKey(item.productId)) {
                items = result.get(item.productId);
            }
            if((item.price * item.count) == 0.0) {
                continue;
            }
            items.add(item);
            result.put(item.productId, items);
        }
        
        
        return result;
    }

    private void removeFromAddon(PmsBookingAddonItem addonOnRoom, PmsBookingAddonItem addonAlreadyBilled) {
        if(addonAlreadyBilled == null || addonAlreadyBilled.count == null || addonAlreadyBilled.price == null) {
            return;
        }
        if(addonOnRoom == null || addonOnRoom.count == null || addonOnRoom.price == null) {
            return;
        }
        
        if(addonOnRoom.count >= addonAlreadyBilled.count && addonAlreadyBilled.price.equals(addonOnRoom.price)) {
            addonOnRoom.count -= addonAlreadyBilled.count;
        } else {
            double totalBilled = addonAlreadyBilled.count * addonAlreadyBilled.price;
            double totalOnRoom = addonOnRoom.count * addonOnRoom.price;
            double newPrice = (totalOnRoom - totalBilled) / addonOnRoom.count;
            addonOnRoom.price = newPrice;
        
        }
    }

    private boolean dateIsFiltered(Date dayToIterate) {
        if(currentFilter.endInvoiceAt != null) {
            if(dayToIterate.after(currentFilter.endInvoiceAt)) {
                return false;
            }
        }
        if(currentFilter.startInvoiceAt != null) {
            if(dayToIterate.before(currentFilter.startInvoiceAt)) {
                return false;
            }
        }
        return true;
    }

    private void makeSureEndStartDateIsCorrect() {
        Calendar cal = Calendar.getInstance();
        if(currentFilter.endInvoiceAt != null) {
            cal.setTime(currentFilter.endInvoiceAt);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            currentFilter.endInvoiceAt = cal.getTime();
        }
        if(currentFilter.startInvoiceAt != null) {
            cal.setTime(currentFilter.startInvoiceAt);
            cal.set(Calendar.HOUR_OF_DAY, 00);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);
            currentFilter.startInvoiceAt = cal.getTime();
        }
    }

    private void addPriceMatrix(CartItem item, HashMap<String, Double> priceMatrix) {
        HashMap<String, Double> newPriceMatrix = new HashMap();
        for(String offset : priceMatrix.keySet()) {
            if(daysInPriceMatrix.contains(offset)) {
                newPriceMatrix.put(offset, priceMatrix.get(offset));
            }
        }
        if(item != null) {
            item.priceMatrix = newPriceMatrix;
        }
        daysInPriceMatrix.clear();
    }


}

package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibm.icu.util.Calendar;
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
    
    private void addCartItem(CartItem cartItem) {
        if(cartItem != null) {
            generatedCartItems.add(cartItem);
        }
    }
    
    private void clearGenerateCart() {
        generatedCartItems.clear();
    }
    
    public String createCart(String bookingId, NewOrderFilter filter) {
        clearGenerateCart();
        
        currentBooking = pmsManager.getBooking(bookingId);
        currentFilter = filter;
        
        calculateRoomPrices();
        updateCart();
        return "";
    }

    private void calculateRoomPrices() {
        for(PmsBookingRooms room : currentBooking.getAllRoomsIncInactive()) {
            //Sleepover prices.
            HashMap<String, Double> priceMatrix = generatePriceMatrix(room);
            generateDailyPriceItems(priceMatrix, room);
            
            //Addon prices.
            HashMap<String, List<PmsBookingAddonItem>> items = getAddonsForRoom(room);
            for(String productId : items.keySet()) {
                generateAddonsCostForProduct(items.get(productId), room, true);
                generateAddonsCostForProduct(items.get(productId), room, false);
            }
            
        }
    }

    private HashMap<String, Double> generatePriceMatrix(PmsBookingRooms room) {
        LinkedHashMap<String, Double> currentMatrix = room.priceMatrix;
        
        if(room.deleted) {
            currentMatrix = new LinkedHashMap();
        }
        
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
        
        return currentMatrix;
    }


    private void generateDailyPriceItems(HashMap<String, Double> priceMatrix, PmsBookingRooms room) {
        TreeMap<Date, DailyPriceObject> dailyPrices = createDailyPriceObjects(priceMatrix);
        
        Date nextDay = null;
        Date lastDay = null;
        Date startDate = null;
        int count = 0;
        Double total = 0.0;
        
        for(Date dayToIterate : dailyPrices.keySet()) {
            Double priceOne = null;
            Double priceTwo = null;
            
            if(lastDay != null && dailyPrices.containsKey(lastDay)) { priceOne = dailyPrices.get(lastDay).price; }
            if(dailyPrices.containsKey(dayToIterate)) { priceTwo = dailyPrices.get(dayToIterate).price; }
            
            if(isNegated(priceOne,priceTwo)) {
                addCartItem(createCartItemForRoom(count, total, room, startDate, nextDay, null));
                count = 0;
                total = 0.0;
                startDate = null;
                nextDay = null;
            }
            
            if(nextDay != null && !room.isSameDay(nextDay, dayToIterate)) {
                addCartItem(createCartItemForRoom(count, total, room, startDate, nextDay, null));
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
            if(item != null) {
                item.priceMatrix = priceMatrix;
                addCartItem(item);
            }
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
            result.put(price.start, price);
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

    private HashMap<String, List<PmsBookingAddonItem>> getAddonsForRoom(PmsBookingRooms room) {
        
        HashMap<String, PmsBookingAddonItem> addonsToAdd = new HashMap();
        for(PmsBookingAddonItem item : room.addons) {
            addonsToAdd.put(item.addonId, item);
        }
        
        if(room.deleted) { 
            addonsToAdd = new HashMap();
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
                            PmsBookingAddonItem addonOnRoom = null;
                            if(addonsToAdd.containsKey(addonAlreadyBilled.addonId)) {
                                addonOnRoom = addonsToAdd.get(addonAlreadyBilled.addonId);
                                removeFromAddon(addonOnRoom, addonAlreadyBilled);
                            } else {
                                toCheck.price *= -1;
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
        double totalBilled = addonAlreadyBilled.count * addonAlreadyBilled.price;
        double totalOnRoom = addonOnRoom.count * addonOnRoom.price;
        double newPrice = (totalOnRoom - totalBilled) / addonOnRoom.count;
        addonOnRoom.price = newPrice;
        
    }


}

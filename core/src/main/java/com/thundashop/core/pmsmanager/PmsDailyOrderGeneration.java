package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
    CartManager cartManager;
    
    private void addCartItem(CartItem cartItem) {
        generatedCartItems.add(cartItem);
    }
    
    private void clearGenerateCart() {
        generatedCartItems.clear();
    }
    
    public String createOrder(String bookingId, NewOrderFilter filter) {
        clearGenerateCart();
        
        currentBooking = pmsManager.getBooking(bookingId);
        currentFilter = filter;
        
        calculateRoomPrices();
        updateCart();
        return "";
    }

    private void calculateRoomPrices() {
        for(PmsBookingRooms room : currentBooking.getActiveRooms()) {
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
            
            count++;
            DailyPriceObject priceobject = dailyPrices.get(dayToIterate);
            total += priceobject.price;
            if(startDate == null) {
                startDate = dayToIterate;
            }
            
            if(nextDay != null && !room.isSameDay(nextDay, dayToIterate)) {
                addCartItem(createCartItemForRoom(count, total, room, startDate, nextDay, null));
                startDate = null;
                nextDay = null;
                count = 0;
                total = 0.0;
            }
            
            nextDay = priceobject.end;
            lastDay = dayToIterate;
        }
        
        addCartItem(createCartItemForRoom(count, total, room,startDate, nextDay, null));
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
        for(PmsBookingAddonItem item : items) {
            if(positiveValues && item.price < 0.0) {
                continue;
            }
            if(!positiveValues && item.price > 0.0) {
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
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        end = cal.getTime();
        
        addCartItem(createCartItemForRoom(count, total, room,start, end, productId));
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
        item.getProduct().externalReferenceId = roomName;
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
        return result;
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
        HashMap<String, List<PmsBookingAddonItem>> result = new HashMap();
        for(PmsBookingAddonItem item : room.addons) {
            List<PmsBookingAddonItem> items = new ArrayList();
            if(result.containsKey(item.productId)) {
                items = result.get(item.productId);
            }
            items.add(item);
            result.put(item.productId, items);
        }
        return result;
    }


}

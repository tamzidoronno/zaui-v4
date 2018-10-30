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

    public String debugRoom = "";
    
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
    
    /* This is where it all begins */
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
        
        if (currentBooking != null) {
            cartManager.setReference(bookingId);
            cartManager.setCreateByGetShopModule("pms");
        }
        
        calculateRoomPrices();
        updateCart(filter);
        return "";
    }
    
    private void addCartItem(CartItem cartItem) {
        if(cartItem != null) {
            cartItem.addedByGetShopModule = "pms";
            generatedCartItems.add(cartItem);
        }
    }
    
    private void clearGenerateCart() {
        generatedCartItems.clear();
    }
    

    private void calculateRoomPrices() {
        for(PmsBookingRooms room : currentBooking.getAllRoomsIncInactive()) {
            calculatePricesOnRoom(room);
        }
        
        List<String> roomIds = getRoomsCompletedRemovedFromBooking();
        for(String roomRemoved : roomIds) {
            PmsBookingRooms room = new PmsBookingRooms();
            room.pmsBookingRoomId = roomRemoved;
            calculatePricesOnRoom(room);
        }
    }

    private HashMap<String, Double> generatePriceMatrix(PmsBookingRooms room) {
        LinkedHashMap<String, Double> currentMatrix = room.priceMatrix;
        
        if((room.deleted && !room.nonrefundable) || room.deletedByChannelManagerForModification) {
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
        
        if (!room.orderUnderConstructionId.isEmpty()) {
            if (orderManager.getOrder(room.orderUnderConstructionId) != null) {
                uniqueList.add(room.orderUnderConstructionId);
            }
        }
        
        for(String orderId : uniqueList) {
            Order order = orderManager.getOrderSecure(orderId);
            for(CartItem item : order.cart.getItems()) {
                if(item.getProduct().externalReferenceId.equals(room.pmsBookingRoomId)) {
                    if(room.bookingItemTypeId == null || room.bookingItemTypeId.isEmpty()) {
                        room.bookingItemTypeId = getRoomTypeFromItem(item);
                    }
                    
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
        
        //Include addons which is included in room price.
        if((!room.deleted || room.nonrefundable) && !room.deletedByChannelManagerForModification) {
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
        for(Date dayToIterate : dailyPrices.keySet()) {
            Double priceOne = null;
            Double priceTwo = null;
            
            if(!dateIsFiltered(dayToIterate)) {
                continue;
            }
            
            if(lastDay != null && dailyPrices.containsKey(lastDay)) { priceOne = dailyPrices.get(lastDay).price; }
            if(dailyPrices.containsKey(dayToIterate)) { priceTwo = dailyPrices.get(dayToIterate).price; }
            
            if(isNegated(priceOne,priceTwo)) {
                CartItem item = createCartItemForRoom(count, total, room, startDate, nextDay, null);
                if(item != null) {
                    addCartItem(item);
                    addPriceMatrix(item, priceMatrix);
                    count = 0;
                    total = 0.0;
                    startDate = null;
                    nextDay = null;
                }
            }
            
            if(nextDay != null && !room.isSameDay(nextDay, dayToIterate)) {
                CartItem item = createCartItemForRoom(count, total, room, startDate, nextDay, null);
                if(item != null) {
                    addCartItem(item);
                    addPriceMatrix(item, priceMatrix);
                    startDate = null;
                    nextDay = null;
                    count = 0;
                    total = 0.0;
                }
            }
            daysInPriceMatrix.add(PmsBookingRooms.convertOffsetToString(dayToIterate));
            
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
                addPriceMatrix(item, priceMatrix);
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
        Gson gson = new Gson();
        
        List<PmsBookingAddonItem> itemsAdded = new ArrayList();
        for(PmsBookingAddonItem item : items) {
            String json = gson.toJson(item);
            item = gson.fromJson(json, PmsBookingAddonItem.class);
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
            item.hideDates = true;
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
        
        if(guestName.isEmpty()) {
            guestName = tryFindFromOrder(room, "guest");
        }
        
        if(roomName.isEmpty()) {
            roomName = tryFindFromOrder(room, "room");
        }
        
        BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
        if(type.id.equals("gsconference") && (type.productId == null || type.productId.isEmpty())) {
            Product newProduct = productManager.createProduct();
            newProduct.name = "Conference";
            productManager.saveProduct(newProduct);
            type.productId = newProduct.id;
            bookingEngine.updateBookingItemType(type);
        }
        boolean useTypeName = false;
        if((productId == null || productId.isEmpty()) && type != null) {
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
        if(item != null) {
            item.startDate = start;
            item.endDate = end;
            item.pmsBookingId = pmsManager.getBookingFromRoom(room.pmsBookingRoomId).id;
            item.getProduct().externalReferenceId = room.pmsBookingRoomId;
            item.getProduct().metaData = guestName;
            item.getProduct().additionalMetaData = roomName;
            if(isAddon) {
                item.groupedById = room.pmsBookingRoomId;
            }
            if(useTypeName) {
                item.getProduct().name = type.name;
            }
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

    private void updateCart(NewOrderFilter filter) {
        if(!filter.avoidClearingCart) {
            cartManager.getCart().clear();
        }
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
        Gson gson = new Gson();
        for(PmsBookingAddonItem item : room.addons) {
            if(!dateIsFiltered(item.date)) {
                continue;
            }
            String json = gson.toJson(item);
            item = gson.fromJson(json, PmsBookingAddonItem.class);
            addonsToAdd.put(item.addonId, item);
        }
        
        if((room.deleted && !room.nonrefundable) || room.deletedByChannelManagerForModification) { 
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
                    if(room.pmsBookingRoomId.equals(debugRoom)) {
                        System.out.println("unpaidaddon;" + productManager.getProduct(item.productId).name + ";" + item.count + ";" + item.price);
                    }
                    addonsToAdd.put(item.addonId, item);
                }
            }
        }
        
        Type type = new TypeToken<HashMap<String, PmsBookingAddonItem>>(){}.getType();
      
        String copy = gson.toJson(addonsToAdd);
        addonsToAdd = gson.fromJson(copy, type);
      
        List<String> uniqueList = new ArrayList<String>(new HashSet<String>( currentBooking.orderIds ));
        
        if (!room.orderUnderConstructionId.isEmpty()) {
            if (orderManager.getOrder(room.orderUnderConstructionId) != null) {
                uniqueList.add(room.orderUnderConstructionId);
            }
        }
        
        for(String orderId : uniqueList) {
            Order order = orderManager.getOrderSecure(orderId);
            for(CartItem item : order.cart.getItems()) {
                if(item.getProduct().externalReferenceId.equals(room.pmsBookingRoomId)) {
                    if(item.itemsAdded != null) {
                        copy = gson.toJson(item.itemsAdded);
                        if(storeId.equals("b6949f70-5e41-4c5e-abcf-d595450f8048")) {
                            double diff = item.getDiffForFromMeta();
                            if(diff > 1.0 || diff < -1.0) {
                                System.out.println("This is wrong: " + diff);
                                item.dumpMetaData();
                                if(item.correctIncorrectCalculation()) {
                                    System.out.println("corrected");
                                }
                            }
                        }
                        
                        type = new TypeToken<List<PmsBookingAddonItem>>(){}.getType();
                        List<PmsBookingAddonItem> alreadyAdded = gson.fromJson(copy, type);
                        for(PmsBookingAddonItem toCheck : alreadyAdded) {
                            if(toCheck == null || toCheck.price == null) {
                                continue;
                            }
                            if(!dateIsFiltered(toCheck.date)) {
                                continue;
                            }
                            PmsBookingAddonItem addonOnRoom = null;
                            if(!addonsToAdd.containsKey(toCheck.addonId)) {
                                createEmptyAddonsToAdd(addonsToAdd, toCheck);
                            }
                            
                            addonOnRoom = addonsToAdd.get(toCheck.addonId);
                            
                            if(room.pmsBookingRoomId.equals(debugRoom)) {
                                System.out.println("removeaddon;" + order.incrementOrderId + ";" + productManager.getProduct(toCheck.productId).name + ";" + toCheck.count + ";" + toCheck.price);
                            }
                            
                            removeFromAddon(addonOnRoom, toCheck);
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
            if((addonAlreadyBilled.price*addonAlreadyBilled.count) > 0.0) {
                if((addonOnRoom.price*addonOnRoom.count) > 0.0) {
                    addonOnRoom.count -= addonAlreadyBilled.count;
                } else {
                    addonOnRoom.count += addonAlreadyBilled.count;
                }
            } else {
                if((addonOnRoom.price*addonOnRoom.count) < 0.0) {
                    addonOnRoom.count -= addonAlreadyBilled.count;
                } else {
                    addonOnRoom.count += addonAlreadyBilled.count;
                }
                
            }
        } else if(addonOnRoom.count != null && addonOnRoom.count.equals(0)) {
            addonOnRoom.count = addonAlreadyBilled.count;
            addonOnRoom.price = addonAlreadyBilled.price * -1;
        } else {
            double totalBilled = addonAlreadyBilled.count * addonAlreadyBilled.price;
            double totalOnRoom = addonOnRoom.count * addonOnRoom.price;
            double newPrice = 0;
            if (addonOnRoom.count != null && !addonOnRoom.count.equals(0)) {
                newPrice = (totalOnRoom - totalBilled) / addonOnRoom.count;
            }
            
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
        item.priceMatrix = newPriceMatrix;
        item.correctIncorrectCalculation();
        daysInPriceMatrix.clear();
    }

    private void createEmptyAddonsToAdd(HashMap<String, PmsBookingAddonItem> addonsToAdd, PmsBookingAddonItem toCheck) {
        Gson gson = new Gson();
        String copy = gson.toJson(toCheck);
        PmsBookingAddonItem empty = gson.fromJson(copy, PmsBookingAddonItem.class);
        empty.count = 0;
        addonsToAdd.put(toCheck.addonId, empty);
    }

    private void calculatePricesOnRoom(PmsBookingRooms room) {
        if(currentFilter.pmsRoomId != null && !currentFilter.pmsRoomId.isEmpty()) {
            if(!currentFilter.pmsRoomId.equals(room.pmsBookingRoomId)) {
                    return;
                }
            }
            
            if(currentFilter.pmsRoomIds != null && !currentFilter.pmsRoomIds.isEmpty()) {
                if(!currentFilter.pmsRoomIds.contains(room.pmsBookingRoomId)) {
                    return;
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

    private List<String> getRoomsCompletedRemovedFromBooking() {
        List<String> result = new ArrayList();
        HashMap<String, Integer> roomIdsFoundOnOrders = new HashMap();
        List<String> roomIdsFoundOnBooking = new ArrayList();
        for(String orderId : currentBooking.orderIds) {
            Order order = orderManager.getOrder(orderId);
            for(CartItem item : order.cart.getItems()) {
                roomIdsFoundOnOrders.put(item.getProduct().externalReferenceId, 1);
            }
        }
        
        for(PmsBookingRooms room : currentBooking.rooms) {
            roomIdsFoundOnBooking.add(room.pmsBookingRoomId);
        }
        
        for(String roomIdFromOrder : roomIdsFoundOnOrders.keySet()) {
            if(!roomIdsFoundOnBooking.contains(roomIdFromOrder)) {
                result.add(roomIdFromOrder);
            }
        }
        return result;
    }

    private String getRoomTypeFromItem(CartItem item) {
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        for(BookingItemType type : types) {
            if(type.productId.equals(item.getProduct().id)) {
                return type.id;
            }
        }
        return null;
    }

    private String tryFindFromOrder(PmsBookingRooms room, String type) {
        for(String orderId : currentBooking.orderIds) {
            Order order = orderManager.getOrder(orderId);
            for(CartItem item : order.cart.getItems()) {
                if(item.getProduct().externalReferenceId.equals(room.pmsBookingRoomId)) {
                    if(type.equals("guest") && item.getProduct().metaData != null && !item.getProduct().metaData.isEmpty()) {
                        return item.getProduct().metaData;
                    }
                    if(type.equals("room") && item.getProduct().additionalMetaData != null && !item.getProduct().additionalMetaData.isEmpty()) {
                        return item.getProduct().additionalMetaData;
                    }
                }
            }
        }
        return "";
    }

    private void dumpData(String bookingId) {
        PmsBooking booking = pmsManager.getBooking(bookingId);
        PmsBookingRooms room = booking.getRoom(debugRoom);
        if(room == null) {
            return;
        }
        System.out.println("Addons");
        for(PmsBookingAddonItem item : room.addons) {
            System.out.println(productManager.getProduct(item.productId).name + ";" + room.price + ";" + item.date);
        }
        System.out.println("Pricematrix");
        for(String day : room.priceMatrix.keySet()) {
            System.out.println("pricematrix;" + day + ";" + room.priceMatrix.get(day));
        }
        
        System.out.println("Cart");
        for(String orderId : booking.orderIds) {
            Order order = orderManager.getOrder(orderId);
            for(CartItem item : order.cart.getItems()) {
                if(item.getProduct().externalReferenceId.equals(debugRoom)) {
                    System.out.println(order.incrementOrderId + ";" + item.getProduct().name + ";" + item.getCount() + ";" + item.getProduct().price);
                }
            }
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pga;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pmsmanager.NewOrderFilter;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@GetShopSession
@Component
public class PgaManager extends GetShopSessionBeanNamed implements IPgaManager {
    
    public HashMap<String, LoginDetails> sessions = new HashMap();
   
    private PgaSettings pgaSettings;
    
    @Autowired
    private ProductManager productManager;
    
    @Autowired
    private PmsManager pmsManager;
    
    @Autowired
    private PmsInvoiceManager pmsInvoiceManager;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private BookingEngine bookingEngine;
    
    @Autowired
    private CartManager cartManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon common : data.data) {
            if (common instanceof LoginDetails) {
                LoginDetails detail = (LoginDetails)common;
                sessions.put(detail.id, detail);
            }
            if (common instanceof PgaSettings) {
                pgaSettings = (PgaSettings)common;
            }
        }
    }

    @Override
    public boolean checkLogin(String token) {
        LoginDetails details = getLoginDetailsByTokenId(token);
        
        if (details != null) {
            getSession().put("pga_token", token);
            return true;
        }
        
        return false;
    }

    private LoginDetails getLoginDetailsByTokenId(String token) {
        LoginDetails details = sessions.values()
                .stream()
                .filter(s -> s.tokenId.equals(token))
                .findFirst()
                .orElse(null);
        
        return details;
    }
    
    private PmsBookingRooms getRoom(String pmsBookingId, String pmsRoomId) {
        PmsBooking booking = pmsManager.getBookingUnsecure(pmsBookingId);
        if (booking == null)
            return null;
        
        for (PmsBookingRooms r : booking.rooms) {
            if (r.pmsBookingRoomId.equals(pmsRoomId)) {
                return r;
            }
        }
        
        return null;
    }

    @Override
    public boolean isLoggedIn() {
        return getLoginDetails() != null;
    }
    
    private LoginDetails getLoginDetails() {
       
        if ((String)getSession().get("pga_session") != null) {
            return sessions.get((String)getSession().get("pga_session"));
        }
        
        return getLoginDetailsByTokenId((String)getSession().get("pga_token"));
    }
    
    private boolean startImpersonation() {
        LoginDetails loginDetails = getLoginDetails();
        
        PmsBooking booking = pmsManager.getBookingUnsecure(loginDetails.bookingId);
        User user = userManager.getUserById(booking.userId);
        if (user == null)
            return false;
        
        userManager.startImpersonationUnsecure(booking.userId);
        getSession().currentUser = user;
        
        return true;
    }

    /**
     * 
     * Resultcode:
     *   0 = booking not found.
     *   1 = Failed to extend stay.
     *   2 = Success
     * @return 
     */
    @Override
    public PgaResult changeCheckoutDate(Date newDate) {
        
        PgaResult result = new PgaResult();
        
        checkLoginInternal();
        
        LoginDetails loginDetails = getLoginDetails();
        PmsBookingRooms room = getRoom(loginDetails.bookingId, loginDetails.pmsBookingRoomId);
        
        if (!startImpersonation()) {
            result.resultCode = 0;
            return result;
        }
        
        if (room == null) {
            result.resultCode = 0;
            return result;
        }
        
        newDate = getCorrectedTime(newDate, room.date.end);
        try {
            PmsBookingRooms changedRoom = pmsManager.changeDates(loginDetails.pmsBookingRoomId, loginDetails.bookingId, room.date.start, newDate);
            result.resultCode = changedRoom == null ? 1 : 2;
        } catch (Exception ex) {
            ex.printStackTrace();
            result.resultCode = 1;   
        } finally {
            cancelImpersonation();
        }
        
        return result;
    }
    
    private void checkLoginInternal() {
        if (getLoginDetails() == null) {
            throw new ErrorException(26);
        }
    }

    @Override
    public PgaRoom getMyRoom() {
        checkLoginInternal();
        
        LoginDetails loginDetails = getLoginDetails();
        PmsBookingRooms room = getRoom(loginDetails.bookingId, loginDetails.pmsBookingRoomId);
        
        if (room != null) {
            PgaRoom retRoom = new PgaRoom();
            retRoom.checkin = room.date.start;
            retRoom.checkout = room.date.end;
            retRoom.stayHasStarted = new Date().after(retRoom.checkin);
            
            if (!room.guests.isEmpty()) {
                retRoom.name = room.guests.get(0).name;
                retRoom.email = room.guests.get(0).email;
                retRoom.prefix = room.guests.get(0).prefix;
                retRoom.phonenumber = room.guests.get(0).phone;
            }
            
            retRoom.code = !isLoggedInOnPga() && room.isStarted() && !room.isEnded() ? room.code : "";
            
            retRoom.bookingItemName = getBookingItemName(room);
            
            retRoom.lateCheckoutPurchased = room.addons.stream()
                    .filter(addon -> addon.productId.equals("gs_pms_late_checkout"))
                    .count() > 0;
            
            retRoom.nextCleaningDate = pmsManager.getNextCleaningDate(loginDetails.bookingId, loginDetails.pmsBookingRoomId);
            return retRoom;
            
        }
        
        return null;
    }

    private Date getCorrectedTime(Date newDate, Date toUseFrom) {
        Calendar org = Calendar.getInstance();
        org.setTime(toUseFrom);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(newDate);
        cal.set(Calendar.HOUR_OF_DAY, org.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, org.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, org.get(Calendar.SECOND));
        return cal.getTime();
    }

    @Override
    public void saveSettings(PgaSettings pgaSettings) {
        if (this.pgaSettings != null) {
            pgaSettings.id = this.pgaSettings.id;
        }
        
        this.pgaSettings = pgaSettings;
        saveObject(pgaSettings);
    }

    @Override
    public PgaSettings getSettings() {
        if (this.pgaSettings == null) {
            return new PgaSettings();
        }
        
        return pgaSettings;
    }

    @Override
    public PgaResult buyLateCheckout() {
        PgaResult result = new PgaResult();
        
        checkLoginInternal();
        
        LoginDetails loginDetails = getLoginDetails();
        PmsBookingRooms room = getRoom(loginDetails.bookingId, loginDetails.pmsBookingRoomId);
        
        if (room == null) {
            return null;
        }
        
        if (!startImpersonation()) {
            result.resultCode = 0;
            return result;
        }
        
        Date newDate = addLateCheckoutHours(room.date.end);
        try {
            CartItem createCartItem = createCartItem("gs_pms_late_checkout", "Late checkout from PGA");
            PmsBookingRooms changedRoom = pmsManager.changeDates(loginDetails.pmsBookingRoomId, loginDetails.bookingId, room.date.start, newDate);
            result.resultCode = changedRoom == null ? 1 : 2;
            pmsManager.addCartItemToRoom(createCartItem, loginDetails.pmsBookingRoomId, getClass().getSimpleName());
        } catch (Exception ex) {
            ex.printStackTrace();
            result.resultCode = 1;   
        } finally {
            cancelImpersonation();
        }
        
        return result;
    }

    private void cancelImpersonation() throws ErrorException {
        userManager.cancelImpersonating();
        getSession().currentUser = null;
    }

    private Date addLateCheckoutHours(Date end) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.HOUR, getSettings().hoursExtraForLateCheckout);
        return cal.getTime();
    }

    private CartItem createCartItem(String productId, String productName) {
        Product product = productManager.getProduct(productId);
        if (product == null) {
            product = new Product();
            product.id = productId;
            product.name = productName;
            productManager.saveProduct(product);
        }
        
        long addonsCount = pmsManager.getAddonsAvailable()
                .stream()
                .filter(o -> o.productId.equals(productId))
                .count();
        
        if (addonsCount != 1) {
            pmsManager.createDefaultAddonConfig(productId);
        }
        
        product.price = getSettings().costForLateCheckout;
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setCount(1);
        return cartItem;
    }

    @Override
    public PgaResult buyExtraCleaning(Date date) {
        PgaResult result = new PgaResult();
        result.resultCode = 0;
        
        checkLoginInternal();
        startImpersonation();
        
        try {
            LoginDetails loginDetails = getLoginDetails();
            PmsBookingRooms room = getRoom(loginDetails.bookingId, loginDetails.pmsBookingRoomId);
            
            createCartItem("gs_pms_extra_cleaning", "Extra cleaning from PGA");
            PmsBookingAddonItem addonConfig = pmsManager.getAddonsAvailable().stream()
                    .filter(a -> a.productId.equals("gs_pms_extra_cleaning"))
                    .findAny()
                    .orElse(null);
            
            PmsBookingAddonItem addon = pmsManager.createAddonToAdd(addonConfig, date, room);
            addon.price = pgaSettings.extraCleaningCost;
            pmsManager.addAddonOnRoom(loginDetails.pmsBookingRoomId, addon);
            result.resultCode = 2;
        } catch (Exception ex) {
            ex.printStackTrace();
            result.resultCode = 1;
        } finally {
            cancelImpersonation();
        }
        
        return result;
    }

    public String createAccessToken(String pmsBookingId, String pmsBookingRoomId) {
        LoginDetails loginDetails = createLoginDetails(pmsBookingId, pmsBookingRoomId);
        return loginDetails.tokenId;
    }

    public void removeAccessToken(String pmsBookingId, String pmsBookingRoomId) {
        sessions.values()
                .stream()
                .filter(s -> s.bookingId.equals(pmsBookingId) && s.pmsBookingRoomId.equals(pmsBookingRoomId))
                .forEach(s -> {
                    deleteObject(s);
                });
        
        sessions.values()
                .removeIf(s -> s.bookingId.equals(pmsBookingId) && s.pmsBookingRoomId.equals(pmsBookingRoomId));
        
    }

    private String getBookingItemName(PmsBookingRooms room) {
        if (room.bookingItemId == null || room.bookingItemId.isEmpty()) {
            return null;
        }
        
        BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
        if (item != null) {
            return item.bookingItemName;
        }
        
        return null;
    }

    @Override
    public Cart getUnpaidCartItems() {
        checkLoginInternal();
        
        if (!startImpersonation()) {
            return null;
        }
        
        LoginDetails loginDetails = getLoginDetails();
        PmsBookingRooms room = getRoom(loginDetails.bookingId, loginDetails.pmsBookingRoomId);
        
        if (room == null) {
            return null;
        }
        
        PmsBooking booking = pmsManager.getBooking(loginDetails.bookingId);
        pmsInvoiceManager.clearOrdersOnBooking(booking);
        
        NewOrderFilter filter = new NewOrderFilter();
        filter.pmsRoomId = loginDetails.pmsBookingRoomId;
        filter.avoidOrderCreation = true;
        
        pmsInvoiceManager.createOrder(loginDetails.bookingId, filter);
        
        cancelImpersonation();
        
        return cartManager.getCart();
    }

    @Override
    public void sendPaymentLink(String email, String prefix, String phone) {
        logPrint("To implement: " + email + " " + prefix + " " + phone);
        return;
    }

    @Override
    public void logout() {
        // pga_session = login from pga.
        // pga_token = login from link.
        getSession().remove("pga_session");
        getSession().remove("pga_token");
    }

    @Override
    public boolean loginByItem(String bookingItemId, int pincode) {
        Booking booking = bookingEngine.getActiveBookingOnBookingItem(bookingItemId);
        
        if (booking == null) {
            return false;
        }
        
        PmsBooking pmsbooking = pmsManager.getBookingFromBookingEngineId(booking.id);
        if (pmsbooking == null)
            return false;
        
        for (PmsBookingRooms room : pmsbooking.rooms) {
            if (room.bookingItemId != null && room.bookingItemId.equals(bookingItemId) && room.code != null && room.code.equals(""+pincode)) {
                LoginDetails details = createLoginDetails(pmsbooking.id, room.pmsBookingRoomId);
                getSession().put("pga_session", details.id);
                return true;
            }
        }
        
        return false;
    }

    private LoginDetails createLoginDetails(String pmsBookingId, String pmsBookingRoomId) {
         LoginDetails loginDetails = sessions.values()
                .stream()
                .filter(s -> s.bookingId.equals(pmsBookingId) && s.pmsBookingRoomId.equals(pmsBookingRoomId))
                .findFirst()
                .orElse(null);
        
        if (loginDetails != null) {
            return loginDetails;
        }
        
        loginDetails = new LoginDetails();
        loginDetails.bookingId = pmsBookingId;
        loginDetails.pmsBookingRoomId = pmsBookingRoomId;
        saveObject(loginDetails);
        sessions.put(loginDetails.id, loginDetails);
        
        return loginDetails;
    }

    private boolean isLoggedInOnPga() {
        return getSession().get("pga_session") != null;
    }
}
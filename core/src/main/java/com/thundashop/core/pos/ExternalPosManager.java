/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.external.ExternalCartItem;
import com.thundashop.core.external.ExternalEndOfDayTransaction;
import com.thundashop.core.external.ExternalPosAccess;
import com.thundashop.core.pmsmanager.PmsBookingFilter;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.external.ExternalPosProduct;
import com.thundashop.core.external.ExternalRoom;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class ExternalPosManager extends ManagerBase implements IExternalPosManager {
    @Autowired
    public PosManager posManager;
    
    @Autowired
    private ProductManager productManager;
    
    @Autowired
    private GetShopSessionScope getShopSpringScope; 
    
    @Autowired
    private UserManager userManager;
    
    public HashMap<String, ExternalPosAccess> externalAccess = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon iData : data.data) {
            if (iData instanceof  ExternalPosAccess) {
                externalAccess.put(iData.id, (ExternalPosAccess)iData);
            }
        }
    }
    
    @Override
    public boolean hasAccess(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        return posManager.getCashPoints()
                .stream()
                .filter(o -> o.token != null && o.token.equals(token))
                .findAny()
                .orElse(null) != null;
    }

    @Override
    public List<ExternalPosProduct> getProducts(String token) {
        checkToken(token);
        
        return productManager.getAllProducts()
                .stream()
                .map(o -> {
                    ExternalPosProduct retProd = new ExternalPosProduct();
                    retProd.makeExternalPosProduct(o);
                    return retProd;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ExternalRoom> getRoomList(String token) {
        checkToken(token);
        
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.filterType = "active";
        filter.startDate = yesterday();
        filter.endDate = tomorrow();
        
        PmsManager pmsManager = getShopSpringScope.getNamedSessionBean("default", PmsManager.class);
        BookingEngine bookingEngine = getShopSpringScope.getNamedSessionBean("default", BookingEngine.class);
        
        boolean started = startImpersonationOfSystemScheduler();
        
        List<ExternalRoom> retList = pmsManager.getSimpleRooms(filter)
                .stream()
                .map(o -> {
                    BookingItem item = bookingEngine.getBookingItem(o.bookingItemId);
                    BookingItemType type = bookingEngine.getBookingItemType(o.bookingTypeId);
                    
                    String bookingItemName = item != null ? item.bookingItemName : "Floating";
                    
                    ExternalRoom room = new ExternalRoom();
                    room.makeRoom(o, bookingItemName, type.name);
                    
                    return room;
                }).collect(Collectors.toList());
        
        if (started) {
            stopImpersonation();
        }
        return retList;
    }

    private void checkToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new ErrorException(26);
        }
        
        if (!hasAccess(token)) {
            throw new ErrorException(26);
        }
    }
    
    
    private Date yesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
    
    private Date tomorrow() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }
    
    private boolean startImpersonationOfSystemScheduler() {
        boolean startedImpersonation = false;
        try {
            // Start impersonation
            if (getSession().currentUser == null) {
                User user = userManager.getInternalApiUser();
                userManager.startImpersonationUnsecure(user.id);
                getSession().currentUser = user;
                startedImpersonation = true;
            }
        } catch (Exception ex) {
            // This one should never happen, can be removed once securly made sure that it doesnt happen as its hard to test.
            ex.printStackTrace();
        }
        return startedImpersonation;
    }
    
    private void stopImpersonation() {
        try {
            userManager.cancelImpersonating();
            getSession().currentUser = null;
        } catch (Exception ex) {
            // This one should never happen, can be removed once securly made sure that it doesnt happen as its hard to test.
            ex.printStackTrace();
        }
    }

    @Override
    public String addToRoom(String token, String roomId, ExternalCartItem externalCartItem) {
        checkToken(token);
        
        CashPoint cashPoint = getCashPoint(token);
        
        Product product = productManager.getProduct(externalCartItem.productId);
        
        if (product == null) {
            return "failed:product_does_not_exists";
        }
        
        TaxGroup taxGroup = productManager.getTaxGroup(externalCartItem.taxGroupNumber);
        
        if (taxGroup == null) {
            return "failed:tax_group_does_not_exists";
        }
        
        boolean started = startImpersonationOfSystemScheduler();
        
        PmsManager pmsManager = getShopSpringScope.getNamedSessionBean("default", PmsManager.class);
        
        PmsBooking booking = pmsManager.getBookingFromRoom(roomId);
        
        if (booking == null) {
            return "failed:room_not_found";
        }
        
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setCount(externalCartItem.count);
        item.getProduct().price = externalCartItem.price;
        
        if (item.getProduct().taxgroup != taxGroup.groupNumber) {
            item.getProduct().changeToTaxGroup(taxGroup);
        }
        
        if (cashPoint.departmentId != null && !cashPoint.departmentId.isEmpty()) {
            item.getProduct().departmentId = cashPoint.departmentId;
        }
        
        String addonId = pmsManager.addCartItemToRoom(item, roomId, "EXTERNAL_CASH_POINT");
        
        if (externalCartItem.overrideProductName != null && !externalCartItem.overrideProductName.isEmpty()) {
            pmsManager.setOverrideNameOfAddon(roomId, addonId, externalCartItem.overrideProductName);
        }
        
        if (started) {
            stopImpersonation();
        }
        
        return addonId;
    }
    
    private CashPoint getCashPoint(String tokenId) {
        return posManager.getCashPoints().stream()
                .filter(o -> o.token != null && o.token.equals(tokenId))
                .findAny()
                .orElse(null);
    }

    @Override
    public void removeTransaction(String token, String addonId) {
        checkToken(token);
        
        PmsManager pmsManager = getShopSpringScope.getNamedSessionBean("default", PmsManager.class);
        
        boolean started = startImpersonationOfSystemScheduler();
        pmsManager.removeAddon(addonId);
        if (started) {
            stopImpersonation();
        }
    }

    @Override
    public void addEndOfDayTransaction(String token, String batchId, int accountNumber, double transactionAmount) {
        checkToken(token);
        
        ExternalEndOfDayTransaction transaction = new ExternalEndOfDayTransaction();
        transaction.accountNumber = accountNumber;
        transaction.batchId = batchId;
        transaction.amount = transactionAmount;
        transaction.cashPointId = getCashPoint(token).id;
        
        saveObject(transaction);
    }

}

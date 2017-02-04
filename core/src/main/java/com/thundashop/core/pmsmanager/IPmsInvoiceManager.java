/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.ordermanager.data.Payment;
import java.util.Date;
import java.util.List;

/**
 * Pms invoice system.
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IPmsInvoiceManager {
    @Administrator
    public void creditOrder(String bookingId, String orderId);
    
    @Administrator
    public void clearOrder(String bookingId, String orderId);

    @Administrator
    public void sendRecieptOrInvoice(String orderId, String email, String bookingId);
    
    @Administrator
    public String createOrder(String bookingId, NewOrderFilter filter);
    
    @Administrator
    public String createOrderOnUnsettledAmount(String bookingId);
    
    @Administrator
    public PmsUserDiscount getDiscountsForUser(String userId);
    
    @Administrator
    public void saveDiscounts(PmsUserDiscount discounts);
    
    @Administrator
    public void markOrderAsPaid(String bookingId, String orderId);
    
    @Administrator
    public Payment getPreferredPaymentMethod(String bookingId, NewOrderFilter filter);
    
    @Administrator
    public List<CartItem> removeOrderLinesOnOrdersForBooking(String id, List<String> roomIds);    
    
    @Administrator
    public PmsOrderStatistics generateStatistics(PmsOrderStatsFilter filter);
    
    @Administrator
    public List<String> validateAllInvoiceToDates();
    
    @Administrator
    public void createPeriodeInvoice(Date start, Date end, Double amount, String roomId);
    
    public boolean isRoomPaidFor(String pmsRoomId);
    
    public List<PmsSubscriptionOverview> getSubscriptionOverview(Date start, Date end); 
    
    
   public String createRegisterCardOrder(String item);
}

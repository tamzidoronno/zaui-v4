/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.FilterOptions;
import com.thundashop.core.common.FilteredData;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.ordermanager.data.Order;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IPosManager {
    @Editor
    public String createNewTab(String referenceName);
    
    @Editor
    public void deleteTab(String tabId);
    
    @Editor
    public List<PosTab> getAllTabs();
    
    @Editor
    public void addToTab(String tabId, CartItem cartItem);
    
    @Editor
    public PosTab getTab(String tabId);
    
    @Editor
    public Double getTotal(String tabId);
    
    @Editor
    public void removeFromTab(String cartItemId, String tabId);
    
    @Editor
    public Double getTotalForItems(List<CartItem> cartItems);
    
    @Editor
    public Order createOrder(List<CartItem> cartItems, String paymentId);
    
    @Editor
    public void completeTransaction(String tabId, String orderId, String cashPointDeviceId);
    
    @Editor
    public int getTabCount();
    
    @Editor
    public ZReport getZReport(String zReportId);
    
    @Editor
    public FilteredData getZReportsUnfinalized(FilterOptions filterOptions);
    
    @Editor
    public void removeItemsFromTab(String tabId, List<CartItem> cartItems);
    
    @Editor
    public void createZReport();
    
    @Editor
    public Double getTotalForCurrentZReport();
    
    @Editor
    public void printOverview(String tabId, String cashPointDeviceId);
}

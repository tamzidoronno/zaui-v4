/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.*;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.PmsConference;
import com.thundashop.core.productmanager.data.ProductList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Order createOrder(List<CartItem> cartItems, String paymentId, String tabId, String cashPointId);
    
    @Editor
    public void completeTransaction(String tabId, String orderId, String cashPointDeviceId, String kitchenDeviceId, HashMap<String,String> paymentMetaData);
    
    @Editor
    public int getTabCount();
    
    @Editor
    public ZReport getZReport(String zReportId, String cashPointId);
    
    @Editor
    public SalesPosResult getSalesReport(SalesReportFilter filter);
    
    @Editor
    public FilteredData getZReportsUnfinalized(FilterOptions filterOptions);
    
    @Editor
    public void removeItemsFromTab(String tabId, List<CartItem> cartItems);
    
    @Editor
    public void createZReport(String cashPointId);
    
    @Editor
    public Double getTotalForCurrentZReport(String cashPointId);
    
    @Editor
    public void printOverview(String tabId, String cashPointDeviceId);
    
    @Editor
    public void printRoomReceipt(String gdsDeviceId, String roomName, String guestName, List<CartItem> items);
    
    @Editor
    public void createCashPoint(String name);
    
    @Editor
    public List<CashPoint> getCashPoints();
    
    @Editor
    public CashPoint getCashPoint(String cashPointId);
    
    @Editor
    public List<ProductList> getProductList(String viewId);
    
    @Editor
    public void saveCashPoint(CashPoint cashPoint);
    
    @Editor
    public void moveList(String viewId, String listId, boolean down);
    
    @Editor
    public void addCashWithDrawalToTab(String tabId, double amount);
    
    @Editor
    public void addOrderIdToZReport(int incrementalOrderId, String zReportId, String password);
    
    @Editor
    public void createNewView(String viewName, String viewType);
    
    @Editor
    public void createNewTable(String tableName, int tableNumber);
    
    @Editor
    public void deleteTable(String tableId);
    
    @Editor
    public void deleteView(String viewId);
    
    @Administrator
    @ForceAsync
    public void deleteZReport(String zreportId, String password);
    
    @Editor
    public List<PosTable> getTables();
    
    @Editor
    public List<PosView> getViews();
    
    @Editor
    public PosView getView(String viewId);
    
    @Editor
    public PosTable getTable(String viewId);
    
    @Editor
    public void saveView(PosView view);
    
    @Editor
    public void saveTable(PosTable table);
    
    @Editor
    public boolean hasTables();
    
    @Editor
    public String getCurrentTabIdForTableId(String tableId);
    
    @Editor
    public void printKitchen(String tabId, String gdsDeviceId);
    
    @Editor
    public void changeTaxRate(String tabId, String taxGroupNumber);
    
    @Editor
    public CartItem setNewProductPrice(String tabId, String cartItemId, double newValue);
    
    @Editor
    public CartItem setDiscountToCartItem(String tabId, String cartItemId, double newValue);
    
    @Editor
    public void setTabDiscount(String tabId, double discount);
    
    @Editor
    public void addGiftCardToTab(String tabId, double value);
    
    @Editor
    public CanCloseZReport canCreateZReport(String pmsBookingMultilevelName, String cashPointId);
    
    @Editor
    public void changeListView(String viewId, String listId, boolean showAsGroupButton);
    
    @Editor
    public void setView(String cashPointId, String viewId);
    
    @Editor
    public ZReport getPrevZReport(String cashPointId);
    
    @Editor
    public Double getTotalForZreport(String zReportId);
    
    @Editor
    public boolean hasConferences();
    
    @Editor
    public List<PosConference> getPosConferences();
    
    @Editor
    public String canDeleteTab(String tabId);
    
    @Editor
    public void moveContentFromOneTabToAnother(String from, String to);
    
    @Editor
    public boolean isTabFromConference(String tabId);
    
    @Editor
    public PosConference getPosConference(String pmsConferenceId);

    @Editor
    public List<PmsConference> getConferencesThatHasUnsettledAmount(List<String> userIds);

    @Administrator
    public void toggleExternalAccess(String cashPointId);
    
    public void addToTabPga(String tabId, CartItem cartItem);
    
    public PosTab createTabForPga(String tabId, String name);
    
    public PosTab getTabForPga(String tabId);
    
    public int getProductCountForPgaTab(String tabId);
    
    @Administrator
    public boolean hasLockedPeriods();
    
    @Editor
    public List<ZReportConferenceSummary> getSummaryListForConferences();
    
    @Editor
    public Double getUnpaidAmountForConference(String conferenceId);

    @Administrator
    public List<ZReport> getReportNotTransferredToCentral();
    
    @Administrator
    public void markZReportAsTransferredToCentral(String zreportId);
    
    @Administrator
    public void markAllRoomsWithProblemsForPayAfterStay(String multilevelName, String cashPointId);
    
    @Administrator
    public void createZReportOfMissingOrders(String cashPointId);
}
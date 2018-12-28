/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.FilterOptions;
import com.thundashop.core.common.FilteredData;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.gsd.GdsManager;
import com.thundashop.core.gsd.KitchenPrintMessage;
import com.thundashop.core.gsd.RoomReceipt;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderFilter;
import com.thundashop.core.ordermanager.data.OrderResult;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.ProductList;
import com.thundashop.core.productmanager.data.TaxGroup;
import java.util.ArrayList;
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
@GetShopSession
@Component
public class PosManager extends ManagerBase implements IPosManager {
    public HashMap<String, PosTab> tabs = new HashMap();
    public HashMap<String, ZReport> zReports = new HashMap();
    public HashMap<String, CashPoint> cashPoints = new HashMap();
    public HashMap<String, PosView> views = new HashMap();
    public HashMap<String, PosTable> tables = new HashMap();
    
    @Autowired
    private CartManager cartManager;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private StoreApplicationPool storeApplicationPool;
    
    @Autowired
    private InvoiceManager invoiceManager;
    
    @Autowired
    private GdsManager gdsManager;

    @Autowired
    private ProductManager productManager;
    
    /**
     * Never access this variable directly! Always 
     * go trough the getSettings function!
     */
    private PosManagerSettings settings;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream()
                .forEach(dataCommon -> {
                    if (dataCommon instanceof PosTab) {
                        tabs.put(dataCommon.id, (PosTab)dataCommon);
                    }
                    if (dataCommon instanceof ZReport) {
                        zReports.put(dataCommon.id, (ZReport)dataCommon);
                    }
                    if (dataCommon instanceof CashPoint) {
                        cashPoints.put(dataCommon.id, (CashPoint)dataCommon);
                    }
                    if (dataCommon instanceof PosView) {
                        views.put(dataCommon.id, (PosView)dataCommon);
                    }
                    if (dataCommon instanceof PosTable) {
                        tables.put(dataCommon.id, (PosTable)dataCommon);
                    }
                    if (dataCommon instanceof PosManagerSettings) {
                        settings = (PosManagerSettings)dataCommon;
                    }
                });
    }
    
    private PosManagerSettings getSettings() {
        if (settings == null) {
            settings = new PosManagerSettings();
            saveObject(settings);
        }
        
        return settings;
    }
    
    private int getNextTabId() {
        PosManagerSettings settings = getSettings();
        settings.incrementalTabId++;
        saveObject(settings);
        return settings.incrementalTabId;
    }
    
    @Override
    public String createNewTab(String referenceName) {
        PosTab posTab = new PosTab();
        posTab.createdByUserId = getSession().currentUser.id;
        posTab.name = referenceName;
        posTab.incrementalTabId = getNextTabId();
        
        saveObject(posTab);
        tabs.put(posTab.id, posTab);
        return posTab.id;
    }   

    @Override
    public void deleteTab(String tabId) {
        PosTab tab = tabs.remove(tabId);
        if (tab != null) {
            removeTabFromTables(tab);
            deleteObject(tab);
        }
    }

    @Override
    public List<PosTab> getAllTabs() {
        return new ArrayList(tabs.values());
    }

    @Override
    public void addToTab(String tabId, CartItem cartItem) {
        PosTab tab = getTab(tabId);
        if (tab != null) {
            cartItem.addedBy = getSession().currentUser.id;
            cartItem.addedDate = new Date();
            if (cartItem.getCartItemId() != null) {
                tab.cartItems.removeIf(c -> c.getCartItemId().equals(cartItem.getCartItemId()));
            }
            
            if (tab.tabTaxGroupId != null) {
                cartItem.getProduct().changeToAdditionalTaxCode(""+tab.tabTaxGroupId);
            }
            
            tab.cartItems.add(cartItem);
        }
        saveObject(tab);
    }

    @Override
    public PosTab getTab(String tabId) {
        tabs.get(tabId).cartItems.stream()
                .forEach(i -> {
                    i.doFinalize();
                });
        
        return tabs.get(tabId);
    }

    @Override
    public Double getTotal(String tabId) {
        PosTab tab = getTab(tabId);
        Cart cart = new Cart();
        cart.addCartItems(tab.cartItems);
        return cartManager.getCartTotal(cart);
    }

    @Override
    public void removeFromTab(String cartItemId, String tabId) {
        PosTab tab = getTab(tabId);
        tab.cartItems.removeIf(c -> c.getCartItemId().equals(cartItemId));
        saveObject(tab);
    }

    @Override
    public Double getTotalForItems(List<CartItem> cartItems) {
        Cart cart = new Cart();
        cart.addCartItems(cartItems);
        return cartManager.getCartTotal(cart);
    }

    @Override
    public Order createOrder(List<CartItem> cartItems, String paymentId, String tabId) {
        PosTab tab = getTab(tabId);
        
        cartManager.clear();
        cartManager.getCart().addCartItems(cartItems);
        
        Order order = orderManager.createOrder(null);
        order.payment.paymentId = paymentId;
        
        Application paymentApplication = storeApplicationPool.getApplication(paymentId);
        order.payment.paymentType = "ns_" + paymentApplication.id.replace("-", "_") + "\\" + paymentApplication.appName;
        
        if (tab != null) {
            order.cashWithdrawal = tab.cashWithDrawal;
        }
        
        orderManager.saveOrder(order);
        
        return order;
    }

    @Override
    public void completeTransaction(String tabId, String orderId, String cashPointDeviceId) {
        Order order = orderManager.getOrder(orderId);
        
        if (!order.isFullyPaid() && !order.isSamleFaktura()) {
            orderManager.markAsPaid(orderId, new Date(), orderManager.getTotalAmount(order) + order.cashWithdrawal);
        }
        
        PosTab tab = getTab(tabId);
        
        order.cart.getItems().stream()
                .forEach(cartItem -> {
                    tab.removeCartItem(cartItem);
                });
        
        tab.cashWithDrawal = tab.cashWithDrawal - order.cashWithdrawal;
        
        saveObject(tab);
        
        if (cashPointDeviceId != null) {
            invoiceManager.sendReceiptToCashRegisterPoint(cashPointDeviceId, order.id);
        }
    }

    @Override
    public int getTabCount() {
        return tabs.size();
    }

    @Override
    public ZReport getZReport(String zReportId) {
        if (zReportId != null && !zReportId.isEmpty()) {
            return zReports.get(zReportId);
        }
        
        Date prevZReportDate = getPreviouseZReportDate();
        
        ZReport report = new ZReport();
        report.start = prevZReportDate;
        report.end = new Date();
        
        List<String> orderIds = orderManager.getOrdersByFilter(getOrderFilter())
                .stream()
                .filter(order -> order.paymentDate !=  null && order.paymentDate.after(prevZReportDate))
                .filter(order -> order.orderId != null && !order.orderId.isEmpty())
                .sorted((OrderResult o1, OrderResult o2) -> {
                    return o1.paymentDate.compareTo(o2.paymentDate);
                })
                .map(order -> order.orderId)
                .collect(Collectors.toList());
        
        report.orderIds = orderIds;
        
        return report;
    }
    
    @Override
    public FilteredData getZReportsUnfinalized(FilterOptions filterOptions) {
        List<ZReport> reports = zReports.values()
                .stream()
                .sorted((ZReport o1, ZReport o2) -> {
                    return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
                })
                .collect(Collectors.toList());
        
        return pageIt(reports, filterOptions);
    }

    private OrderFilter getOrderFilter() {
        OrderFilter filter = new OrderFilter();
        filter.end = new Date();
        filter.state = Order.Status.PAYMENT_COMPLETED;
        return filter;
    }

    private Date getPreviouseZReportDate() {
        Date start = new Date(0);
        for (ZReport rep : zReports.values()) {
            if (rep.rowCreatedDate.after(start)) {
                start = rep.rowCreatedDate;
            }
        }
        
        return start;
    }

    @Override
    public void createZReport() {
        ZReport report = getZReport("");
        report.createdByUserId = getSession().currentUser.id;
        report.totalAmount = getTotalAmountForZReport(report);
        saveObject(report);
        zReports.put(report.id, report);
    }
    
    /**
     * How to use this feature.
     * 
     * 1. navigate to z report you want to add order to
     * 2. do a javascript request: app.SalesPointReports.addOrderToZRepport('102107','Askdfjalksrdj23AMmasdkfasii23');
     * 
     * @param incrementalOrderId
     * @param zReportId
     * @param password 
     */
    @Override
    public void addOrderIdToZReport(int incrementalOrderId, String zReportId, String password) {
        if (password == null || !password.equals("Askdfjalksrdj23AMmasdkfasii23")) {
            return;
        }
        
        Order order = orderManager.getOrderByincrementOrderId(incrementalOrderId);
        ZReport zreport = getZReport(zReportId);
        zreport.orderIds.add(order.id);
        saveObject(zreport);
    }

    private double getTotalAmountForZReport(ZReport report) {
        return report.orderIds.stream()
                .map(orderId -> orderManager.getOrder(orderId))
                .mapToDouble(order -> orderManager.getTotalAmount(order))
                .sum();
    }

    @Override
    public void removeItemsFromTab(String tabId, List<CartItem> cartItems) {
        PosTab tab = getTab(tabId);
        if (tab != null) {
            cartItems.stream()
                    .forEach(item -> {
                        tab.removeCartItem(item);
                    });
            
            saveObject(tab);
        }
    }

    @Override
    public Double getTotalForCurrentZReport() {
        ZReport report = getZReport("");
        
        return report.orderIds.stream()
                .map(orderId -> orderManager.getOrder(orderId))
                .mapToDouble(order -> orderManager.getTotalAmount(order))
                .sum();
    }

    @Override
    public void printOverview(String tabId, String cashPointDeviceId) {
        PosTab tab = getTab(tabId);
        if (tab == null)
            return;
        
        Order order = new Order();
        order.cart = new Cart();
        order.cart.addCartItems(tab.cartItems);
        
        invoiceManager.sendOrderToGdsDevice(cashPointDeviceId, order);
    }

    @Override
    public void printRoomReceipt(String gdsDeviceId, String roomName, String guestName, List<CartItem> items) {
        RoomReceipt receipt = new RoomReceipt();
        receipt.roomName = roomName;
        receipt.guestName = guestName;
        receipt.cartItems = items;
        receipt.date = new Date();
        gdsManager.sendMessageToDevice(gdsDeviceId, receipt);
    }

    @Override
    public void createCashPoint(String name) {
        CashPoint cashPoint = new CashPoint();
        cashPoint.cashPointName = name;
        saveObject(cashPoint);
        cashPoints.put(cashPoint.id, cashPoint);
    }

    @Override
    public List<CashPoint> getCashPoints() {
        return new ArrayList(cashPoints.values());
    }
    
    @Override
    public List<ProductList> getProductList(String viewId) {
        PosView view = getView(viewId);
        if (view == null) {
            return productManager.getProductLists();
        }
     
        return view.productListsIds.stream()
                .map(id -> productManager.getProductList(id))
                .collect(Collectors.toList());
    }

    @Override
    public CashPoint getCashPoint(String cashPointId) {
        return cashPoints.get(cashPointId);
    }

    @Override
    public void saveCashPoint(CashPoint cashPoint) {
        saveObject(cashPoint);
        cashPoints.put(cashPoint.id, cashPoint);
    }

    @Override
    public void moveList(String viewId, String listId, boolean down) {
        int number = 0;
        PosView view = getView(viewId);
        
        if (view == null) {
            return;
        }
        
        for (String id : view.productListsIds) {
            if (id.equals(listId)) {
                break;
            }
            
            number++;
        }
        
        if (down) {
            number++;
            if (number > (view.productListsIds.size() - 1)) {
                number = view.productListsIds.size() - 1;
            }
        } else {
            number--;
            if (number < 0) {
                number = 0;
            }
        }
        
        int i = 0;
        List<String> newList = new ArrayList();
        for (String id : view.productListsIds) {
            if (i == number) {
                newList.add(listId);
            }
            
            i++;
            
            if (!id.equals(listId) && !newList.contains(id)) {
                newList.add(id);
            }
            
            if (view.productListsIds.size() < i && id.equals(listId) && down && !newList.contains(view.productListsIds.get(i))) {
                newList.add(view.productListsIds.get(i));
            }
        }
        
        view.productListsIds = newList;
        saveObject(view);
    }

    @Override
    public void addCashWithDrawalToTab(String tabId, double amount) {
        PosTab res = getTab(tabId);
        if (res != null) {
            res.cashWithDrawal = amount;
            saveObject(res);
        }
    }

    @Override
    public void createNewView(String viewName, String viewType) {
        PosView view = new PosView();
        view.name = viewName;
        view.type = viewType;
        saveObject(view);
        views.put(view.id, view);
    }

    @Override
    public void createNewTable(String tableName, int tableNumber) {
        PosTable table = new PosTable();
        table.name = tableName;
        table.tableNumber = tableNumber;
        saveObject(table);
        tables.put(table.id, table);
    }

    @Override
    public void deleteTable(String tableId) {
        PosTable table = tables.remove(tableId);
        if (table != null) {
            deleteObject(table);
        }
    }

    @Override
    public void deleteView(String viewId) {
        PosView viewToDelete = views.remove(viewId);
        if (viewToDelete != null) {
            deleteObject(viewToDelete);
        }
    }

    @Override
    public List<PosTable> getTables() {
        ArrayList<PosTable> retList = new ArrayList(tables.values());
        
        retList.sort((PosTable o1, PosTable o2) -> {
            Integer a = new Integer(o1.tableNumber);
            Integer b = new Integer(o2.tableNumber);
            return a.compareTo(b);
        });
        
        return retList;
    }

    @Override
    public List<PosView> getViews() {
        return new ArrayList(views.values());
    }

    @Override
    public PosView getView(String viewId) {
        return views.get(viewId);
    }

    @Override
    public PosTable getTable(String viewId) {
        return tables.get(viewId);
    }

    @Override
    public void saveView(PosView view) {
        saveObject(view);
        views.put(view.id, view);
    }

    @Override
    public void saveTable(PosTable table) {
        saveObject(table);
        tables.put(table.id, table);
    }

    @Override
    public boolean hasTables() {
        return !tables.isEmpty();
    }

    @Override
    public String getCurrentTabIdForTableId(String tableId) {
        PosTable table = getTable(tableId);
        if (table == null) {
            return null;
        }
        
        if (table.currentTabId != null && !table.currentTabId.isEmpty()) {
            PosTab tab = getTab(table.currentTabId);
            if (tab == null) {
                table.currentTabId = "";
            }
        }
        
        if (table.currentTabId == null || table.currentTabId.isEmpty()) {
            table.currentTabId = createNewTab("Table: " + table.tableNumber);
            saveObject(table);
        }
        
        return table.currentTabId;
    }

    private void removeTabFromTables(PosTab tab) {
        tables.values().stream()
                .filter(t -> t.currentTabId != null && t.currentTabId.equals(tab.id))
                .forEach(t -> {
                    t.currentTabId = "";
                    saveObject(t);
                });
    }
    
    @Override
    public void printKitchen(String tabId, String gdsDeviceId) {
        PosTab tab = getTab(tabId);
        
        if (tab == null)
            return;
        
        List<CartItem> itemsToPrint = tab.cartItems.stream()
                .filter(item -> item.getProduct() != null && item.getProduct().isFood)
                .collect(Collectors.toList());
        
        if (itemsToPrint.isEmpty()) {
            return;
        }
        
        tab.printedToKitchenTimes++;
        saveObject(tab);
        
        KitchenPrintMessage printMsg = new KitchenPrintMessage();
        printMsg.printedBy = getSession().currentUser.fullName;
        printMsg.tabName = tab.name;
        printMsg.cartItems = itemsToPrint;
        printMsg.header = String.format("%05d", tab.incrementalTabId)+" / "+tab.printedToKitchenTimes;
        
        TaxGroup group = productManager.getTaxGroupById(tab.tabTaxGroupId);
        if (group != null) {
            printMsg.header += "\n ( " + group.description + " )";
        }
       
        gdsManager.sendMessageToDevice(gdsDeviceId, printMsg);
    }

    @Override
    public void changeTaxRate(String tabId, String taxGroupNumber) {
        PosTab tab = getTab(tabId);
        
        if (tab == null)
            return;
        
        if (taxGroupNumber == null || taxGroupNumber.isEmpty()) {
            tab.cartItems.stream()
                .forEach(cartItem -> {
                    cartItem.getProduct().resetAdditionalTaxGroup();
                });
            tab.tabTaxGroupId = null;
            saveObject(tab);
            return;
        }
        
        tab.cartItems.stream()
                .forEach(cartItem -> {
                    cartItem.getProduct().changeToAdditionalTaxCode(""+taxGroupNumber);
                });
        
        tab.tabTaxGroupId = taxGroupNumber;
        
        saveObject(tab);
    }
}
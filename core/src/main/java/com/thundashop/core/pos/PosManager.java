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
import com.thundashop.core.gsd.RoomReceipt;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderFilter;
import com.thundashop.core.ordermanager.data.OrderResult;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.ProductList;
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
                });
    }
    
    
    @Override
    public String createNewTab(String referenceName) {
        PosTab posTab = new PosTab();
        posTab.createdByUserId = getSession().currentUser.id;
        posTab.name = referenceName;
        
        saveObject(posTab);
        tabs.put(posTab.id, posTab);
        return posTab.id;
    }   

    @Override
    public void deleteTab(String tabId) {
        PosTab tab = tabs.remove(tabId);
        if (tab != null) {
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
            tab.cartItems.add(cartItem);
        }
        saveObject(tab);
    }

    @Override
    public PosTab getTab(String tabId) {
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
        
        if (!order.isFullyPaid()) {
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
    public List<ProductList> getProductList(String cashPointId) {
        CashPoint cashPoint = getCashPoint(cashPointId);
        if (cashPoint == null) {
            return productManager.getProductLists();
        }
     
        return cashPoint.productListIds.stream()
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
    public void moveList(String cashPointId, String listId, boolean down) {
        int number = 0;
        CashPoint cashPoint = getCashPoint(cashPointId);
        
        for (String id : cashPoint.productListIds) {
            if (id.equals(listId)) {
                break;
            }
            
            number++;
        }
        
        if (down) {
            number++;
            if (number > (cashPoint.productListIds.size() - 1)) {
                number = cashPoint.productListIds.size() - 1;
            }
        } else {
            number--;
            if (number < 0) {
                number = 0;
            }
        }
        
        int i = 0;
        List<String> newList = new ArrayList();
        for (String id : cashPoint.productListIds) {
            if (i == number) {
                newList.add(listId);
            }
            
            i++;
            
            if (!id.equals(listId) && !newList.contains(id)) {
                newList.add(id);
            }
            
            if (cashPoint.productListIds.size() < i && id.equals(listId) && down && !newList.contains(cashPoint.productListIds.get(i))) {
                newList.add(cashPoint.productListIds.get(i));
            }
        }
        
        cashPoint.productListIds = newList;
        saveObject(cashPoint);
    }

    @Override
    public void addCashWithDrawalToTab(String tabId, double amount) {
        PosTab res = getTab(tabId);
        if (res != null) {
            res.cashWithDrawal = amount;
            saveObject(res);
        }
    }
}
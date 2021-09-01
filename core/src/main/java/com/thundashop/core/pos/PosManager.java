/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.central.GetShopCentral;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshopaccounting.DayIncome;
import com.thundashop.core.getshopaccounting.GetShopAccountingManager;
import com.thundashop.core.giftcard.GiftCardManager;
import com.thundashop.core.gsd.GdsManager;
import com.thundashop.core.gsd.GetShopCentralMessage;
import com.thundashop.core.gsd.KitchenPrintMessage;
import com.thundashop.core.gsd.RoomReceipt;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.*;
import com.thundashop.core.paymentmanager.PaymentManager;
import com.thundashop.core.paymentmanager.StorePaymentConfig;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
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
    public HashMap<String, PosConference> conferences = new HashMap();

    private PosConferenceCache posConferenceCache = null;

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

    @Autowired
    private GiftCardManager giftCardManager;

    @Autowired
    private GetShopSessionScope scope;

    @Autowired
    private PaymentManager paymentManager;

    @Autowired
    private GetShopAccountingManager getShopAccountingManager;

    @Autowired
    private PmsConferenceManager pmsConferenceManager;

    @Autowired
    private GetShopCentral central;

    /**
     * Never access this variable directly! Always go trough the getSettings
     * function!
     */
    private PosManagerSettings settings;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream()
                .forEach(dataCommon -> {
                    if (dataCommon instanceof PosTab) {
                        tabs.put(dataCommon.id, (PosTab) dataCommon);
                    }
                    if (dataCommon instanceof ZReport) {
                        zReports.put(dataCommon.id, (ZReport) dataCommon);
                    }
                    if (dataCommon instanceof CashPoint) {
                        cashPoints.put(dataCommon.id, (CashPoint) dataCommon);
                    }
                    if (dataCommon instanceof PosView) {
                        views.put(dataCommon.id, (PosView) dataCommon);
                    }
                    if (dataCommon instanceof PosConference) {
                        conferences.put(dataCommon.id, (PosConference) dataCommon);
                    }
                    if (dataCommon instanceof PosTable) {
                        tables.put(dataCommon.id, (PosTable) dataCommon);
                    }
                    if (dataCommon instanceof PosManagerSettings) {
                        settings = (PosManagerSettings) dataCommon;
                    }
                    if (dataCommon instanceof PosConferenceCache) {
                        posConferenceCache = (PosConferenceCache) dataCommon;
                    }
                });
       }

    private PosConferenceCache getPosConferenceCache() {
        if (posConferenceCache == null) {
            posConferenceCache = new PosConferenceCache();
            saveObject(posConferenceCache);
        }

        return posConferenceCache;
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
        PosTab posTab = createTab(referenceName);

        saveObject(posTab);
        tabs.put(posTab.id, posTab);
        return posTab.id;
    }

    private PosTab createTab(String referenceName) {
        PosTab posTab = new PosTab();

        if (getSession().currentUser != null) {
            posTab.createdByUserId = getSession().currentUser.id;
        }

        posTab.name = referenceName;
        posTab.incrementalTabId = getNextTabId();

        if (getSession() != null) {
            posTab.createdBySessionId = getSession().id;
        }

        return posTab;
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

            if (getSession().currentUser != null) {
                cartItem.addedBy = getSession().currentUser.id;
            }

            cartItem.addedDate = new Date();
            if (cartItem.getCartItemId() != null) {
                tab.cartItems.removeIf(c -> c.getCartItemId().equals(cartItem.getCartItemId()));
            }

            if (tab.tabTaxGroupId != null) {
                cartItem.getProduct().changeToAdditionalTaxCode("" + tab.tabTaxGroupId);
            }

            tab.cartItems.add(cartItem);

            if (tab.discount != null) {
                setDiscountToCartItem(tab.id, cartItem.getCartItemId(), tab.discount);
            }

            saveObject(tab);
        }
    }

    @Override
    public PosTab getTab(String tabId) {
        if (isTabFromConference(tabId) && tabs.get(tabId) == null) {
            PosTab tab = createTab(getPosConferenceByTabId(tabId).conferenceName);
            tab.id = tabId;
            saveObject(tab);
            tabs.put(tab.id, tab);
        }

        if (tabs.get(tabId) == null) {
            return null;
        }

        tabs.get(tabId).cartItems.stream()
                .forEach(i -> {
                    i.doFinalize();
                });

        return tabs.get(tabId);
    }

    @Override
    public Double getTotal(String tabId) {
        if (tabId == null) {
            return 0D;
        }

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
    public Order createOrder(List<CartItem> cartItems, String paymentId, String tabId, String cashPointId) {
        PosTab tab = getTab(tabId);

        cartManager.clear();
        cartManager.getCart().addCartItems(cartItems);

        if (cartManager.isCartConflictingWithClosedPeriode()) {
            cartManager.getCart().overrideDate = orderManager.getOrderManagerSettings().closedTilPeriode;
        }

        Order order = orderManager.createOrder(null);
        order.payment.paymentId = paymentId;

        CashPointTag tag = createOrderTag(cashPointId);

        order.addOrderTag(tag);
        order.getCartItems().stream()
                .forEach(item -> {
                    if (cashPoints.get(cashPointId) != null) {
                        item.wareHouseId = cashPoints.get(cashPointId).warehouseid;
                    }
                });

        Application paymentApplication = storeApplicationPool.getApplication(paymentId);
        order.payment.paymentType = "ns_" + paymentApplication.id.replace("-", "_") + "\\" + paymentApplication.appName;

        if (tab != null) {
            order.cashWithdrawal = tab.cashWithDrawal;
        }

        orderManager.saveOrder(order);

        return order;
    }

    @Override
    public void completeTransaction(String tabId, String orderId, String cashPointDeviceId, String kitchenDeviceId, HashMap<String, String> paymentMetaData) {
        Order order = orderManager.getOrder(orderId);

        if (!order.isFullyPaid() && !order.isSamleFaktura()) {
            order.payment.metaData = paymentMetaData;
            orderManager.markAsPaid(orderId, new Date(), orderManager.getTotalAmount(order) + order.cashWithdrawal);
        }

        finishTabAndOrder(tabId, order, kitchenDeviceId, cashPointDeviceId);
    }

    public void finishTabAndOrder(String tabId, Order order, String kitchenDeviceId, String cashPointDeviceId) throws ErrorException {
        PosTab tab = getTab(tabId);

        if (tab != null && kitchenDeviceId != null && !kitchenDeviceId.isEmpty()) {
            sendToKitchenInternal(kitchenDeviceId, tab, order.cart.getItems());
        }

        order.cart.getItems().stream()
                .forEach(cartItem -> {
                    tab.removeCartItem(cartItem);
                });

        tab.cashWithDrawal = tab.cashWithDrawal - order.cashWithdrawal;

        saveObject(tab);

        if (cashPointDeviceId != null) {
            invoiceManager.sendReceiptToCashRegisterPoint(cashPointDeviceId, order.id);

            giftCardManager.getGiftCardsCreatedByOrderId(order.id).stream()
                    .forEach(giftCard -> {
                        giftCardManager.printGiftCard(cashPointDeviceId, giftCard.id);
                    });
        }
    }

    @Override
    public int getTabCount() {
        return tabs.size();
    }

    /**
     * Gets all the data, orderIds that appear on XReport and ZReport. If a booking has no order, it will not appear on XReport.
     * Recalculates and sets room.unsettledAmountIncAccrued .
     * For some bookings like for ones that have "payAfterStay" it checks for which accruedPayments(==orders) will need to be
     * created (doesn't create them).
     */
    @Override
    public ZReport getZReport(String zReportId, String cashPointId) {
        if (zReportId != null && !zReportId.isEmpty()) {
            return zReports.get(zReportId);
        }

        Date prevZReportDate = getPreviouseZReportDate(cashPointId);

        ZReport report = new ZReport();
        report.start = prevZReportDate;
        report.end = new Date();

        List<String> orderIds = new ArrayList();
        if (central.hasBeenConnectedToCentral()) {
            Date fromWhenToTakeIntoAccount  = setDateToBeginningOfMonth(central.hasBeenConnectedToCentralSince());
            orderIds = orderManager.getAllOrders()
                    .stream()
                    .filter(o -> !o.isNullOrder())
                    .filter(o-> (o.hasPaymentDateAfter(fromWhenToTakeIntoAccount) && o.transferredToCentral == false || o.hasPaymentDateAfter(prevZReportDate)))
                    .filter(o -> o.isOrderFinanciallyRelatedToDatesIgnoreCreationDate(new Date(0), new Date()))
                    .map(o -> o.id)
                    .collect(Collectors.toList());

            removeOrdersPrePaidByOTAAndNotMarkedAsPaid(orderIds);
            report.invoicesWithNewPayments = getInvoicePayments();
        } else {
            orderIds = orderManager.getOrdersByFilter(getOrderFilter())
                    .stream()
                    .filter(order -> (order.getMarkedPaidDate() != null && order.getMarkedPaidDate().after(prevZReportDate)))
                    .filter(order -> order.orderId != null && !order.orderId.isEmpty())
                    .filter(order -> order.isConnectedToCashPointId(cashPointId) || (isMasterCashPoint(cashPointId) && order.isConnectedToCashPointId("")))
                    .sorted((OrderResult o1, OrderResult o2) -> {
                        return o1.paymentDate.compareTo(o2.paymentDate);
                    })
                    .map(order -> order.orderId)
                    .collect(Collectors.toList());
        }

        report.orderIds = orderIds;
        report.createdAfterConnectedToACentral = central.hasBeenConnectedToCentral();
        report.roomsThatWillBeAutomaticallyCreatedOrdersFor = getRoomsNeedToCreateOrdersFor();

        return report;
    }

    public void removeOrdersPrePaidByOTAAndNotMarkedAsPaid(List<String> orderIds) {
        // We remove all orders from OTA's that are not yet paid, we dont want them to be locked at this point.
        orderIds.removeIf(orderId -> {
            Order order = orderManager.getOrder(orderId);

            if (order.isPrepaidByOTA() && !order.isPaid()) {
                return true;
            }

            return false;
        });
    }

    public List<String> getInvoicePayments() {
        return orderManager.getAllOrders().stream()
                .filter(o -> o.isInvoice() && o.hasNewOrderTransactions())
                .map(o -> o.id)
                .collect(Collectors.toList());

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

    private Date getPreviouseZReportDate(String cashPointId) {
        Date start = new Date(0);
        for (ZReport rep : zReports.values()) {
            if (rep.rowCreatedDate.after(start) && (rep.cashPointId.equals(cashPointId) || rep.cashPointId.isEmpty())) {
                start = rep.rowCreatedDate;
            }
        }

        return start;
    }
    /**
     * Generates ZReport.
     * For some bookings like "PayAfterStay" it will create orders with accruedPayments
     */
    @Override
    public void createZReport(String cashPointId) {
        List<String> autoCreatedOrders = autoCreateOrders(cashPointId);
        List<String> orderdIdsFromConfernceSystem = autoCreateOrdersForConferenceTabs(cashPointId);

        List<String> orderIds = new ArrayList();
        orderIds.addAll(orderdIdsFromConfernceSystem);
        orderIds.addAll(autoCreatedOrders);

        createZReportInternal(cashPointId, orderIds);
    }

    private void createZReportInternal(String cashPointId, List<String> orderIds) throws ErrorException {
        ZReport report = getZReport("", cashPointId);
        report.createdByUserId = getSession().currentUser.id;
        report.cashPointId = cashPointId;
        report.orderIds.addAll(orderIds);

        orderManager.markAsTransferredToCentral(report.orderIds);

        report.totalAmount = getTotalAmountForZReport(report);

        saveObject(report);
        closeOrdersAndInvoicesByZReport(report);

        if (central.hasBeenConnectedToCentral()) {
            processExtraOrderIdsForCentral(cashPointId, report);
        }
        zReports.put(report.id, report);

        closeFinancialPeriodeIfNeeded(cashPointId);
        getShopAccountingManager.transferAllDaysThatCanBeTransferred();
        gdsManager.sendMessageToGetShopCentral(new GetShopCentralMessage("NEW_ZREPORT_CREATED"));
    }

    private void closeFinancialPeriodeIfNeeded(String cashPointId) {
        if (orderManager.getOrderManagerSettings().autoCloseFinancialDataWhenCreatingZReport && isMasterCashPoint(cashPointId)) {
            closeFinancialPeriode();
        }

        if (central.hasBeenConnectedToCentral()) {
            closeFinancialPeriode();
        }
    }

    private void processExtraOrderIdsForCentral(String cashPointId, ZReport report) {
        orderManager.creditOrdersThatHasDeletedConference();
        Date fromWhenToTakeIntoAccount  = setDateToBeginningOfMonth(central.hasBeenConnectedToCentralSince());
        Date prevZReportDate = getPreviouseZReportDate(cashPointId);

        List<String> extraOrderIds = orderManager.getOrdersNotConnectedToAnyZReports()
                .stream()
                .filter(o-> o.hasPaymentDateAfter(fromWhenToTakeIntoAccount) && o.transferredToCentral == false || o.hasPaymentDateAfter(prevZReportDate)) //after switching old customers to central, all old reports would get processed here
                .map(o -> o.id)
                .collect(Collectors.toList());
        extraOrderIds.forEach(orderId -> orderManager.closeOrderByZReport(orderId, report));
        report.orderIds.addAll(extraOrderIds);
        report.totalAmount = getTotalAmountForZReport(report);
        saveObject(report);
    }

    private void closeOrdersAndInvoicesByZReport(ZReport report) {
        report.orderIds.forEach(orderId -> orderManager.closeOrderByZReport(orderId, report));
        report.invoicesWithNewPayments.forEach(orderId -> orderManager.closeOrderByZReport(orderId, report));
    }

    /**
     * How to use this feature.
     * <p>
     * 1. navigate to z report you want to add order to 2. do a javascript
     * request:
     * app.SalesPointReports.addOrderToZRepport('102107','Askdfjalksrdj23AMmasdkfasii23');
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
        ZReport zreport = getZReport(zReportId, "");
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
    public Double getTotalForCurrentZReport(String cashPointId) {
        ZReport report = getZReport("", cashPointId);

        return report.orderIds.stream()
                .map(orderId -> orderManager.getOrder(orderId))
                .mapToDouble(order -> orderManager.getTotalAmount(order))
                .sum();
    }

    @Override
    public void printOverview(String tabId, String cashPointDeviceId) {
        PosTab tab = getTab(tabId);
        if (tab == null) {
            return;
        }

        List<CartItem> clonedItems = new ArrayList();

        tab.cartItems.stream().forEach(o -> {
            try {
                clonedItems.add((CartItem) o.clone());
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(PosManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Order order = new Order();
        order.cart = new Cart();
        order.cart.addCartItems(clonedItems);
        order.cashWithdrawal = tab.cashWithDrawal;
        order.setOverridePricesFromCartItem();

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

            if (id.equals(listId)) {
                i--;
            }

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

        if (tab == null) {
            return;
        }

        sendToKitchenInternal(gdsDeviceId, tab, tab.cartItems);
    }

    private void sendToKitchenInternal(String gdsDeviceId, PosTab tab, List<CartItem> cartItems) {
        List<CartItem> itemsToPrint = cartItems.stream()
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
        printMsg.header = String.format("%05d", tab.incrementalTabId) + " / " + tab.printedToKitchenTimes;

        TaxGroup group = productManager.getTaxGroupById(tab.tabTaxGroupId);
        if (group != null) {
            printMsg.header += "\n ( " + group.description + " )";
        }

        gdsManager.sendMessageToDevice(gdsDeviceId, printMsg);
    }

    @Override
    public void changeTaxRate(String tabId, String taxGroupNumber) {
        PosTab tab = getTab(tabId);

        if (tab == null) {
            return;
        }

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
                    cartItem.getProduct().changeToAdditionalTaxCode("" + taxGroupNumber);
                });

        tab.tabTaxGroupId = taxGroupNumber;

        saveObject(tab);
    }

    @Override
    public CartItem setNewProductPrice(String tabId, String cartItemId, double newValue) {
        String userId = getSession().currentUser.id;
        ProductPriceOverride override = new ProductPriceOverride(userId, "New fixed price set", newValue, ProductPriceOverrideType.fixedprice, "direct");
        addOverridePrice(tabId, cartItemId, override);

        CartItem cartItem = getCartItem(tabId, cartItemId);
        return cartItem;
    }

    @Override
    public CartItem setDiscountToCartItem(String tabId, String cartItemId, double newValue) {
        String userId = getSession().currentUser.id;
        ProductPriceOverride override = new ProductPriceOverride(userId, "New discount", newValue, ProductPriceOverrideType.discountpercent, "direct");
        addOverridePrice(tabId, cartItemId, override);

        CartItem cartItem = getCartItem(tabId, cartItemId);
        return cartItem;
    }

    private void addOverridePrice(String tabId, String cartItemId, ProductPriceOverride override) throws ErrorException {
        PosTab tab = getTab(tabId);
        if (tab == null) {
            return;
        }

        CartItem cartItem = getCartItem(tabId, cartItemId);

        String userId = getSession().currentUser.id;

        if (cartItem != null) {
            int seq = cartItem.getOverridePriceHistoryCount();
            override.setSequence(seq);
            cartItem.addOverridePriceHistory(override, userId);

            saveObject(tab);
        }
    }

    private CartItem getCartItem(String tabId, String cartItemId) {
        PosTab tab = getTab(tabId);
        if (tab == null) {
            return null;
        }

        return tab.cartItems.stream()
                .filter(cartItem -> cartItem.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void setTabDiscount(String tabId, double discount) {
        PosTab tab = getTab(tabId);
        tab.discount = discount;
        saveObject(tab);
        updateCartItemsWithTabDiscount(tab);
    }

    private void updateCartItemsWithTabDiscount(PosTab tab) {
        tab.cartItems.stream()
                .filter(item -> item.overridePriceIncTaxes == null || tab.discount == null || tab.discount == 0)
                .forEach(item -> {
                    setDiscountToCartItem(tab.id, item.getCartItemId(), tab.discount);
                });
    }

    @Override
    public void addGiftCardToTab(String tabId, double value) {
        PosTab tab = getTab(tabId);

        if (tab == null) {
            return;
        }

        Product product = productManager.getProduct("giftcard");

        if (product == null) {
            productManager.createGiftCardProduct();
            product = productManager.getProduct("giftcard");
        }

        CartItem cartItem = new CartItem();
        cartItem.setCount(1);
        cartItem.setProduct(product);
        cartItem.getProduct().price = value;
        addToTab(tabId, cartItem);
    }

    private void closeFinancialPeriode() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        orderManager.closeTransactionPeriode(cal.getTime());
    }

    @Override
    public void deleteZReport(String zreportId, String password) {
        System.out.println("Deleting a z-report here " + zreportId + " pass sent in was " + password );
        if (password != null && password.equals("as9d08f90213841nkajsdfi2u3h4kasjdf")) {
            System.out.println("password is correct.... find and delete the zreport");
            ZReport report = zReports.remove(zreportId);

            if (report != null) {
                System.out.println("found the report here... deleting it");
                deleteObject(report);
            }
        }
    }
    /**
     * It will create orders for some rooms which need them.
     */
    @Override
    public CanCloseZReport canCreateZReport(String pmsBookingMultilevelName, String cashPointId) {
        boolean autoClose = orderManager.getOrderManagerSettings().autoCloseFinancialDataWhenCreatingZReport;

        CashPoint cashPoint = getCashPoint(cashPointId);

        CanCloseZReport canClose = new CanCloseZReport();

        canClose.uncompletedOrders = orderManager.getAllOrders()
                .stream()
                .filter(o -> !o.isNullOrder() && !o.isAccruedPayment() && !o.isInvoice())
                .filter(o -> o.status != Order.Status.PAYMENT_COMPLETED)
                .filter(o -> createdCashPoint(o, cashPointId) || o.isIntegratedPaymentTerminal())
                .collect(Collectors.toList());

        canClose.finalize();

        if (!autoClose) {
            return canClose;
        }

        Date start = getDateWithOffset(getPreviouseZReportDate(cashPointId), -1);
        Date end = getDateWithOffset(new Date(), 0);

        List<DayIncome> incomes = orderManager.getDayIncomes(start, end);

        if (!central.hasBeenConnectedToCentral()) {
            canClose.fReportErrorCount = incomes.stream()
                    .filter(o -> o != null && o.errorMsg != null && !o.errorMsg.isEmpty())
                    .count();
        }

        if (cashPoint == null || !cashPoint.ignoreHotelErrors) {
            PmsManager pmsManager = scope.getNamedSessionBean(pmsBookingMultilevelName, PmsManager.class);

            List<PmsBooking> bookingsInPeriode = pmsManager.getBookingsWithUnsettledAmountBetween(start, end);
            List<PmsBookingRooms> roomsInPeriode = bookingsInPeriode
                    .stream()
                    .flatMap(o -> o.rooms.stream())
                    .filter(o -> o.date.within(start, end))
                    .filter(o -> o.unsettledAmount > 0.0001 || o.unsettledAmount < -0.0001)
                    .collect(Collectors.toList());

            canClose.roomsWithProblems = roomsInPeriode.stream()
                    .filter(o -> !o.createOrdersOnZReport)
                    .collect(Collectors.toList());

            boolean anyRoomsNeedAutoCreationOfOrders = roomsInPeriode.stream()
                    .filter(o -> o.createOrdersOnZReport)
                    .count() != 0;

            if (anyRoomsNeedAutoCreationOfOrders && !checkIfAccruedPaymentActivatedAndConfigured()) {
                canClose.canClose = false;
                canClose.accuredPaymentMethodNotActivatedOrConfiguredImproperly = true;
            }

            checkIfBookingsWithNoneSegments(canClose, pmsManager.getAllBookings(null));

            canClose.roomsWithProblems.removeIf(room -> noUnsettledAmountInPast(room, pmsManager));
        }

        canClose.finalize();
        return canClose;
    }

    public boolean noUnsettledAmountInPast(PmsBookingRooms room, PmsManager pmsManager) {
        PmsBooking booking = pmsManager.getBookingFromRoom(room.pmsBookingRoomId);
        PmsRoomPaymentSummary summary = pmsManager.getSummary(booking.id, room.pmsBookingRoomId);

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -2);

        Date yesterDay = cal.getTime();
        Date closeTilDate = orderManager.changeCloseDateToCorrectDate(yesterDay);

        long numberOfProblemsInPast = summary.rows.stream()
                .filter(o -> o.getDate().before(closeTilDate) && o.needToCreateOrderFor())
                .count();

        return numberOfProblemsInPast == 0;
    }

    private Date getDateWithOffset(Date date, int addDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, orderManager.getOrderManagerSettings().whatHourOfDayStartADay);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, addDays);
        return cal.getTime();
    }

    private CashPointTag createOrderTag(String cashPointId) {
        CashPointTag tag = new CashPointTag();
        tag.cashPointId = cashPointId;

        if (cashPointId != null && !cashPointId.isEmpty()) {
            tag.departmentId = getCashPoint(cashPointId).departmentId;
        }

        return tag;
    }

    private boolean createdCashPoint(Order o, String cashPointId) {

        for (OrderTag tag : o.getTags()) {

            if (tag instanceof CashPointTag) {
                if (((CashPointTag) tag).cashPointId.equals(cashPointId)) {
                    return true;
                }

            }
        }

        return false;
    }

    @Override
    public void changeListView(String viewId, String listId, boolean showAsGroupButton) {
        PosView view = getView(viewId);
        if (view != null) {
            view.changeListMode(listId, showAsGroupButton);
            saveObject(view);
        }
    }

    @Override
    public void setView(String cashPointId, String viewId) {
        CashPoint cpSkada = getCashPoint(cashPointId);
        if (cpSkada != null) {
            cpSkada.setUserView(getSession().currentUser.id, viewId);
            saveObject(cpSkada);
        }
    }

    private boolean isMasterCashPoint(String cashPointId) {
        if (cashPoints.size() < 2) {
            return true;
        }

        return getCashPoint(cashPointId).isMaster;
    }

    @Override
    public ZReport getPrevZReport(String cashPointId) {
        return zReports.values()
                .stream()
                .filter(o -> o.cashPointId.equals(cashPointId))
                .sorted((ZReport z1, ZReport z2) -> {
                    return z2.rowCreatedDate.compareTo(z1.rowCreatedDate);
                })
                .findFirst()
                .orElse(null);
    }

    @Override
    public Double getTotalForZreport(String zReportId) {
        return zReports.get(zReportId).orderIds.stream()
                .map(orderId -> orderManager.getOrder(orderId))
                .mapToDouble(order -> orderManager.getTotalAmount(order))
                .sum();
    }

    private List<String> autoCreateOrders(String cashPointId) {
        List<PmsBookingRooms> roomsNeedToCreateOrdersFor = getRoomsNeedToCreateOrdersFor();
        List<String> retList = new ArrayList();

        if (!roomsNeedToCreateOrdersFor.isEmpty()) {
            checkIfAccrudePaymentIsActivated();
        }

        roomsNeedToCreateOrdersFor.stream().forEach(o -> {
            String orderId = createOrder(o);
            retList.add(orderId);
        });

        return retList;
    }

    private String getEngineName() {
        if (storeId != null) {
            if (storeId.equals("a152b5bd-80b6-417b-b661-c7c522ccf305")) {
                return "demo";
            } //Fast Hotel Svolver
            if (storeId.equals("3b647c76-9b41-4c2a-80db-d96212af0789")) {
                return "demo";
            } //Fast Hotel Havna
            if (storeId.equals("e625c003-9754-4d66-8bab-d1452f4d5562")) {
                return "demo";
            } //Fast Hotel Lofoten
        }

        return "default";
    }

    private String createOrder(PmsBookingRooms room) {
        PmsManager pmsManager = scope.getNamedSessionBean(getEngineName(), PmsManager.class);
        PmsBooking booking = pmsManager.getBookingFromRoom(room.pmsBookingRoomId);
        return createOrderWithPaymentMethod(booking, room, "60f2f24e-ad41-4054-ba65-3a8a02ce0190");
    }

    private boolean checkIfAccruedPaymentActivatedAndConfigured() {
        Application activatedApp = storeApplicationPool.getApplication("60f2f24e-ad41-4054-ba65-3a8a02ce0190");

        if (activatedApp == null) {
            return false;
        }

        StorePaymentConfig config = paymentManager.getStorePaymentConfiguration("60f2f24e-ad41-4054-ba65-3a8a02ce0190");

        if (config == null) {
            return false;
        }

        if (config.offsetAccountingId_accrude == null || config.offsetAccountingId_accrude.isEmpty()) {
            return false;
        }

        if (config.offsetAccountingId_prepayment == null || config.offsetAccountingId_prepayment.isEmpty()) {
            return false;
        }

        if (config.userCustomerNumber == null || config.userCustomerNumber.isEmpty()) {
            return false;
        }

        return true;
    }

    private boolean hasAnyRoomThatIsNotDeleted(PmsBooking booking) {
        return booking.rooms.stream()
                .filter(o -> !o.deleted)
                .count() > 0;
    }

    private void checkIfBookingsWithNoneSegments(CanCloseZReport canClose, List<PmsBooking> bookingsInPeriode) {
        List<String> bookingsWithNoneSegments = bookingsInPeriode.stream()
                .filter(o -> o.segmentId == null || o.segmentId.isEmpty())
                .filter(o -> !o.isDeleted)
                .filter(o -> o.isCompletedBooking())
                .filter(o -> o.confirmed)
                .filter(o -> hasAnyRoomThatIsNotDeleted(o))
                .filter(o -> o != null)
                .map(o -> o.id)
                .collect(Collectors.toList());

        if (!bookingsWithNoneSegments.isEmpty()) {
            canClose.canClose = false;
            canClose.bookingsWithNoneSegments = bookingsWithNoneSegments;
        }
    }

    @Override
    public SalesPosResult getSalesReport(SalesReportFilter filter) {
        List<Order> orders = orderManager.getAllOrders();
        Calendar day = Calendar.getInstance();
        Calendar checker = Calendar.getInstance();
        day.setTime(filter.start);
        SalesPosResult toReturn = new SalesPosResult();
        List<CartItem> itemsToIncludeInReport = new ArrayList();
        while (true) {
            for (Order ord : orders) {
                if (ord.status != Order.Status.PAYMENT_COMPLETED) {
                    continue;
                }
                checker.setTime(ord.rowCreatedDate);
                if (checker.get(Calendar.YEAR) != day.get(Calendar.YEAR) || checker.get(Calendar.DAY_OF_YEAR) != day.get(Calendar.DAY_OF_YEAR)) {
                    continue;
                }
                itemsToIncludeInReport.addAll(ord.getCartItems());
            }
            day.add(Calendar.DAY_OF_YEAR, +1);
            if (day.getTime().after(filter.end)) {
                break;
            }
        }
        SalesPosReportEntry entry = createEntries(day, itemsToIncludeInReport);
        toReturn.entries.add(entry);

        return toReturn;
    }

    private SalesPosReportEntry createEntries(Calendar day, List<CartItem> itemsToIncludeInReport) {
        SalesPosReportEntry result = new SalesPosReportEntry();
        result.day = day.getTime();
        for (CartItem item : itemsToIncludeInReport) {
            String productId = item.getProduct().id;
            if (!result.productCounter.containsKey(productId)) {
                result.productCounter.put(productId, 0);
                result.productTaxes.put(productId, 0.0);
                result.productValue.put(productId, 0.0);
            }

            Integer curCounter = result.productCounter.get(productId);
            Double curTaxes = result.productTaxes.get(productId);
            Double curValue = result.productValue.get(productId);

            curCounter += item.getCount();
            curTaxes += (item.getTotalAmount() - item.getTotalEx());
            curValue += item.getTotalAmount();

            result.productCounter.put(productId, curCounter);
            result.productTaxes.put(productId, curTaxes);
            result.productValue.put(productId, curValue);
        }

        return result;
    }

    @Override
    public boolean hasConferences() {
        return pmsConferenceManager.anyConferences();
    }

    @Override
    public List<PosConference> getPosConferences() {
        syncConferences();
        return new ArrayList(conferences.values());
    }

    public void syncConferences() {
        List<String> confIds = pmsConferenceManager.getConferencesIds();

        List<PosConference> toDelete = conferences.values()
                .stream()
                .filter(o -> !confIds.contains(o.pmsConferenceId))
                .collect(Collectors.toList());

        toDelete.stream().forEach(o -> {
            deletePosConference(o);
        });

        confIds.stream().forEach(confId -> {
            PosConference conf = getPosConferenceByConfId(confId);

            if (conf == null) {
                updatePosConference(confId);
            }
        });
    }

    private void deletePosConference(PosConference o) throws ErrorException {
        deleteTab(o.tabId);
        conferences.remove(o.id);
        deleteObject(o);
    }

    private PosConference getPosConferenceByConfId(String confId) {
        PosConference conf = conferences.values()
                .stream()
                .filter(o -> o.pmsConferenceId.equals(confId))
                .findAny()
                .orElse(null);
        return conf;
    }

    private boolean isTabEmpty(String tabId) {
        PosTab tab = tabs.get(tabId);
        if (tab == null) {
            return true;
        }

        return tab.cartItems.isEmpty();
    }

    public void updatePosConference(String confId) {
        PosConference conf = getPosConferenceByConfId(confId);
        if (conf == null) {
            conf = new PosConference();
        }

        PmsConference pmsConference = pmsConferenceManager.getConference(confId);

        if (pmsConference == null) {
            deletePosConference(conf);
            return;
        }

        conf.conferenceName = pmsConference.meetingTitle;
        conf.pmsConferenceId = confId;
        conf.expiryDate = pmsConferenceManager.getExpiryDate(confId);

        if (conf.tabId == null || conf.tabId.isEmpty()) {
            conf.tabId = createNewTab(pmsConference.meetingTitle);
        }

        List<String> eventIdsInConference = pmsConferenceManager.getConferenceEvents(confId)
                .stream()
                .map(o -> o.id)
                .collect(Collectors.toList());

        eventIdsInConference.add("");
        eventIdsInConference.add("overview");

        PosTab tab = getTab(conf.tabId);
        tab.cartItems.removeIf(item -> item.conferenceEventId != null && !eventIdsInConference.contains(item.conferenceEventId));

        saveObject(conf);
        conferences.put(conf.id, conf);
    }

    @Override
    public String canDeleteTab(String tabId) {
        boolean isTabFromConference = isTabFromConference(tabId);

        if (isTabFromConference && !isTabEmpty(tabId)) {
            return "This tab belongs to a conference and cant be deleted unless it has been cleared out.";
        }

        return "";
    }

    @Override
    public boolean isTabFromConference(String tabId) {
        return getPosConferenceByTabId(tabId) != null;
    }

    private PosConference getPosConferenceByTabId(String tabId) {
        return conferences.values()
                .stream()
                .filter(o -> o.tabId.equals(tabId))
                .findAny()
                .orElse(null);
    }

    @Override
    public void moveContentFromOneTabToAnother(String fromTabId, String toTabId) {
        if (fromTabId.equals(toTabId)) {
            return;
        }

        PosTab fromTab = getTab(fromTabId);
        PosTab toTab = getTab(toTabId);
        toTab.cartItems.addAll(fromTab.cartItems);
        deleteTab(fromTabId);

        saveObject(toTab);
    }

    @Override
    public PosConference getPosConference(String pmsConferenceId) {
        return getPosConferenceByConfId(pmsConferenceId);
    }

    @Override
    public List<PmsConference> getConferencesThatHasUnsettledAmount(List<String> userIds) {
        PmsConferenceFilter filter = new PmsConferenceFilter();
        filter.userIds = userIds;

        List<PmsConference> conferencesWithUserIds = pmsConferenceManager.getAllConferences(filter);

        // Remove conferences that does not have anything to pay.
        conferencesWithUserIds.removeIf(o -> {
            PosConference conf = getPosConference(o.id);
            return getTab(conf.tabId).cartItems.isEmpty();
        });

        return conferencesWithUserIds;
    }

    private PmsOrderCreateRow getCreateOrderForRoom(PmsBooking booking, PmsBookingRooms room, PmsManager pmsManager) {
        PmsOrderCreateRow createOrderForRoom = new PmsOrderCreateRow();

        PmsRoomPaymentSummary summary = pmsManager.getSummary(booking.id, room.pmsBookingRoomId);
        createOrderForRoom.roomId = room.pmsBookingRoomId;
        createOrderForRoom.items = summary.getCheckoutRows();

        return createOrderForRoom;
    }

    private String createOrderWithPaymentMethod(PmsBooking booking, PmsBookingRooms room, String roomId) {
        PmsManager pmsManager = scope.getNamedSessionBean(getEngineName(), PmsManager.class);

        PmsOrderCreateRow createOrderForRoom = getCreateOrderForRoom(booking, room, pmsManager);

        List<PmsOrderCreateRow> createOrder = new ArrayList();
        createOrder.add(createOrderForRoom);

        String userId = booking.userId != null && !booking.userId.isEmpty() ? booking.userId : getSession().currentUser.id;
        return pmsManager.createOrderFromCheckout(createOrder, roomId, userId);
    }

    private List<String> autoCreateOrdersForConferenceTabs(String cashPointId) {
        String accuredPayment = "60f2f24e-ad41-4054-ba65-3a8a02ce0190";

        boolean isActive = storeApplicationPool.isActivated(accuredPayment);

        // We need to have a payment method in order to autocreate orders
        if (!isActive && central.hasBeenConnectedToCentral()) {
            storeApplicationPool.activateApplication("60f2f24e-ad41-4054-ba65-3a8a02ce0190");
            isActive = true;
        }

        if (!isActive) {
            return new ArrayList();
        }

        ArrayList<String> orderIds = new ArrayList();

        Map<String, List<CartItem>> retMap = getOrderSummaryToCreateForSummary();

        for (String pmsConferenceId : retMap.keySet()) {
            List<CartItem> cartItemsInDifference = retMap.get(pmsConferenceId);
            Order order = createOrder(cartItemsInDifference, accuredPayment, null, cashPointId);
            order.autoCreatedOrderForConferenceId = pmsConferenceId;
            orderManager.saveOrder(order);
            boolean shouldInclude = order.isOrderFinanciallyRelatedToDatesIgnoreCreationDate(new Date(0), new Date());

            if (shouldInclude) {
                orderIds.add(order.id);
            }
        }

        PosConferenceCache cache = getPosConferenceCache();
        cache.clear();
        saveObject(cache);

        return orderIds;
    }

    private Map<String, List<CartItem>> getOrderSummaryToCreateForSummary() {
        Map<String, List<CartItem>> retMap = new HashMap();
        PosConferenceCache cache = getPosConferenceCache();
        for (PosConferenceCacheDirty dirtyCache : cache.getDirtyConferences()) {
            List<CartItem> cartItems = getConferenceDiffCartItems(dirtyCache.pmsConferenceId, dirtyCache.tabId);
            if (cartItems.size() > 0) {
                retMap.put(dirtyCache.pmsConferenceId, cartItems);
            }
        }
        return retMap;
    }

    public Double getUnpaidAmountForConference(String conferenceId) {
        PosConference conf = getPosConference(conferenceId);
        if (conf != null) {
            PosTab tab = getTab(conf.tabId);
            return tab.getTotalAmount();
        }
        return 0.0;
    }

    private List<CartItem> getConferenceDiffCartItems(String pmsConferenceId, String tabId) {
        List<Order> autoCreatedOrders = orderManager.getAutoCreatedOrdersForConference(pmsConferenceId);
        List<CartItem> cartItemsInDifference = getDiff(autoCreatedOrders, getTab(tabId), pmsConferenceId);
        return cartItemsInDifference;
    }

    private String createKey(CartItem o, String pmsConferenceId) {
        String key = o.conferenceEventId;

        if (o.conferenceEventId == null || o.conferenceEventId.trim().isEmpty()) {
            key = "overview";
        }

        key += "_____";
        key += getDateForConfernceFormatted(pmsConferenceId, o.conferenceEventId, o);
        key += "_____";
        key += o.departmentRemoteId;

        return key;
    }

    private List<CartItem> getDiff(List<Order> autoCreatedOrders, PosTab tab, String pmsConferenceId) {
        if (tab == null) {
            tab = new PosTab();
        }

        Map<String, List<CartItem>> cartItemsFromOrdersGroupedByDay = autoCreatedOrders.stream()
                .flatMap(o -> o.getCartItems().stream())
                .collect(Collectors.groupingBy(o -> createKey(o, pmsConferenceId)));

        Map<String, List<CartItem>> allProductIdsFromTabGroupedByDay = tab.cartItems
                .stream()
                .distinct()
                .collect(Collectors.groupingBy(o -> createKey(o, pmsConferenceId)));

        List<String> allGroups = new ArrayList(cartItemsFromOrdersGroupedByDay.keySet());
        allGroups.addAll(allProductIdsFromTabGroupedByDay.keySet());

        allGroups = allGroups.stream().distinct().collect(Collectors.toList());

        List<CartItem> retList = new ArrayList();

        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("dd.MM.yyyy");

        for (String eventIdKey : allGroups) {
            System.out.println(eventIdKey);

            String[] splittedEventKey = eventIdKey.split("_____");
            String eventId = splittedEventKey[0];
            String dateKey = splittedEventKey[1];
            String departmentId = "";

            if (splittedEventKey.length > 2) {
                departmentId = splittedEventKey[2];
            }

            Map<Integer, List<CartItem>> cartItemsFromOrdersGroupedByTaxGroup = new HashMap();
            Map<Integer, List<CartItem>> allCartItemsFromTabGroupedByTaxGroup = new HashMap();

            if (cartItemsFromOrdersGroupedByDay.get(eventIdKey) != null) {
                cartItemsFromOrdersGroupedByTaxGroup = cartItemsFromOrdersGroupedByDay.get(eventIdKey)
                        .stream()
                        .collect(Collectors.groupingBy(o -> {
                                    return new Integer(o.getProduct().taxgroup);
                                }
                        ));
            }

            if (allProductIdsFromTabGroupedByDay.get(eventIdKey) != null) {
                allCartItemsFromTabGroupedByTaxGroup = allProductIdsFromTabGroupedByDay.get(eventIdKey)
                        .stream()
                        .collect(Collectors.groupingBy(o -> {
                                    return new Integer(o.getProduct().taxgroup);
                                }
                        ));
            }

            List<Integer> allTaxGroups = new ArrayList(cartItemsFromOrdersGroupedByTaxGroup.keySet());
            allTaxGroups.addAll(allCartItemsFromTabGroupedByTaxGroup.keySet());
            allTaxGroups = allTaxGroups.stream().distinct().collect(Collectors.toList());

            for (Integer taxGroup : allTaxGroups) {

                List<String> allProductIdsFromOrder = new ArrayList();
                List<CartItem> cartItemsFromOrders = new ArrayList();
                if (cartItemsFromOrdersGroupedByTaxGroup.get(taxGroup) != null) {
                    cartItemsFromOrders = cartItemsFromOrdersGroupedByTaxGroup.get(taxGroup);
                    allProductIdsFromOrder = cartItemsFromOrders.stream()
                            .filter(o -> o.getProduct().taxgroup == taxGroup)
                            .map(o -> o.getProductId())
                            .distinct()
                            .collect(Collectors.toList());
                }

                List<String> allProductIdsFromTab = new ArrayList();

                List<CartItem> allCartItemsFromTab = new ArrayList();

                if (allCartItemsFromTabGroupedByTaxGroup.get(taxGroup) != null) {
                    allCartItemsFromTab = allCartItemsFromTabGroupedByTaxGroup.get(taxGroup);
                    allProductIdsFromTab = allCartItemsFromTab.stream()
                            .filter(o -> o.getProduct().taxgroup == taxGroup)
                            .map(o -> o.getProductId())
                            .distinct()
                            .collect(Collectors.toList());
                }

                List<String> allProductsIds = new ArrayList();
                allProductsIds.addAll(allProductIdsFromOrder);
                allProductsIds.addAll(allProductIdsFromTab);
                allProductsIds = allProductsIds.stream().distinct().collect(Collectors.toList());

                for (String productId : allProductsIds) {
                    int countInTab = getCountInCartItems(allCartItemsFromTab, productId, eventId, departmentId);
                    int countInOrders = getCountInCartItems(cartItemsFromOrders, productId, eventId, departmentId);

                    BigDecimal totalFromTab = getTotalInCartItems(allCartItemsFromTab, productId, eventId, departmentId);
                    BigDecimal totalFromOrder = getTotalInCartItems(cartItemsFromOrders, productId, eventId, departmentId);
                    BigDecimal toCreateOrderFor = totalFromTab.subtract(totalFromOrder);

                    System.out.println("Total from tab: " + totalFromTab + " | " + totalFromOrder + " | " + toCreateOrderFor + " | " + taxGroup + " | " + productId);
                    int countToCreateFor = countInTab - countInOrders;
                    if (!toCreateOrderFor.equals(BigDecimal.ZERO)) {
                        if (countToCreateFor == 0) {
                            countToCreateFor = 1;
                        }

                        CartItem item = new CartItem();
                        item.setProduct(productManager.getProduct(productId).clone());
                        item.setCount(countToCreateFor);
                        item.getProduct().price = toCreateOrderFor.doubleValue() / (double) countToCreateFor;
                        try {
                            item.accountingDate = simpleDateFormatter.parse(dateKey);
                        } catch (ParseException ex) {
                            throw new NullPointerException("There should always be a date available for items added to a conference.");
                        }

                        item.getProduct().taxgroup = taxGroup;
                        item.getProduct().taxGroupObject = productManager.getTaxGroup(item.getProduct().taxgroup);
                        item.conferenceEventId = eventId;
                        item.departmentRemoteId = departmentId;
                        retList.add(item);
                    }
                }

            }
        }

        return retList;
    }

    private BigDecimal getTotalInCartItems(List<CartItem> cartItemsFromOrders, String productId, String eventId, String departmentId) {
        return cartItemsFromOrders.stream()
                .filter(o -> eventId.equals(o.conferenceEventId))
                .filter(o -> o.getProductId().equals(productId))
                .filter(o -> departmentId.equals(o.departmentRemoteId))
                .map(o -> {
                    if (o.overridePriceIncTaxes != null && o.overridePriceIncTaxes != 0D) {
                        return o.getTotalAmountRoundedWithTwoDecimalsOverride(2);
                    }

                    return o.getTotalAmountRoundedWithTwoDecimals(2);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int getCountInCartItems(List<CartItem> cartItems, String productId, String eventId, String departmentId) {
        return cartItems.stream()
                .filter(o -> eventId.equals(o.conferenceEventId))
                .filter(o -> departmentId.equals(o.departmentRemoteId))
                .filter(o -> o.getProductId().equals(productId))
                .mapToInt(o -> o.getCount())
                .sum();
    }

    @Override
    public void toggleExternalAccess(String cashPointId) {
        CashPoint point = getCashPoint(cashPointId);

        if (point == null) {
            return;
        }

        if (point.token == null) {
            point.token = UUID.randomUUID().toString();
        } else {
            point.token = null;
        }

        saveObject(point);

    }

    @Override
    public void addToTabPga(String tabId, CartItem cartItem) {
        PosTab tab = getTab(tabId);
        if (tab == null || !tab.createdBySessionId.equals(getSession().id)) {
            return;
        }

        addToTab(tab.id, cartItem);
    }

    @Override
    public PosTab createTabForPga(String tabId, String name) {
        if (getTab(tabId) != null) {
            return null;
        }

        PosTab tab = createTab(name);
        tab.id = tabId;
        saveObject(tab);
        tabs.put(tab.id, tab);

        return tab;
    }

    @Override
    public PosTab getTabForPga(String tabId) {
        PosTab tab = getTab(tabId);
        if (tab != null && tab.createdBySessionId != null && tab.createdBySessionId.equals(getSession().id)) {
            return tab;
        }

        return null;
    }

    @Override
    public int getProductCountForPgaTab(String tabId) {
        PosTab tab = getTab(tabId);
        if (tab == null || !tab.createdBySessionId.equals(getSession().id)) {
            return 0;
        }

        return tab.cartItems.stream()
                .map(o -> o.getCount())
                .collect(Collectors.summingInt(Integer::intValue));
    }

    @Override
    public boolean hasLockedPeriods() {
        return !zReports.isEmpty();
    }

    @Override
    public List<ZReportConferenceSummary> getSummaryListForConferences() {
        Map<String, List<CartItem>> retMap = getOrderSummaryToCreateForSummary();

        ArrayList<ZReportConferenceSummary> retList = new ArrayList();

        for (String pmsConferenceId : retMap.keySet()) {
            ZReportConferenceSummary rep = new ZReportConferenceSummary();
            rep.pmsConference = pmsConferenceManager.getConferenceDirectFromDB(pmsConferenceId);
            rep.cartItems = retMap.get(pmsConferenceId);
            rep.total = rep.cartItems.stream()
                    .mapToDouble(a -> a.getTotalAmount())
                    .sum();

            retList.add(rep);
        }

        return retList;

    }

    @Override
    public void deleteObject(DataCommon data) throws ErrorException {
        dirtyTabs(data);
        super.deleteObject(data);
    }

    @Override
    public void saveObject(DataCommon data) throws ErrorException {
        dirtyTabs(data);
        super.saveObject(data);
    }

    private void dirtyTabs(DataCommon data) {
        if (data instanceof PosConference) {
            PosConference posConference = (PosConference) data;
            PosConferenceCache cache = getPosConferenceCache();
            cache.markDirty(posConference.pmsConferenceId, posConference.tabId);
            saveObject(cache);
        }

        if (data instanceof PosTab) {
            PosConference posConference = getPosConferenceByTabId(data.id);
            if (posConference != null) {
                PosConferenceCache cache = getPosConferenceCache();
                cache.markDirty(posConference.pmsConferenceId, posConference.tabId);
                saveObject(cache);
            }
        }
    }

    private Date getDateForConfernce(String pmsConferenceId, String eventId) {
        PmsConferenceEvent event = pmsConferenceManager.getConferenceEventDirectFromDB(eventId);

        if (event == null || event.from == null || eventId == null || eventId.isEmpty() || eventId.equals("overview")) {
            PmsConference conference = pmsConferenceManager.getConferenceDirectFromDB(pmsConferenceId);
            if (conference != null && conference.conferenceDate != null) {
                return conference.conferenceDate;
            }

            return new Date();
        }

        return event.from;
    }

    private String getDateForConfernceFormatted(String pmsConferenceId, String conferenceEventId, CartItem cartItem) {
        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("dd.MM.yyyy");

        if (cartItem != null && cartItem.accountingDate != null) {
            return simpleDateFormatter.format(cartItem.accountingDate);
        }

        return simpleDateFormatter.format(getDateForConfernce(pmsConferenceId, conferenceEventId));
    }

    @Override
    public List<ZReport> getReportNotTransferredToCentral() {
        return zReports.values()
                .stream()
                .filter(o -> !o.transferredToCentral && o.createdAfterConnectedToACentral)
                .collect(Collectors.toList());
    }

    @Override
    public void markZReportAsTransferredToCentral(String zreportId) {
        ZReport report = zReports.get(zreportId);
        if (report != null) {
            report.transferredToCentral = true;
            saveObject(report);
        }
    }

    @Override
    public void markAllRoomsWithProblemsForPayAfterStay(String multilevelName, String cashPointId) {
        CanCloseZReport res = canCreateZReport(multilevelName, cashPointId);
        res.roomsWithProblems.stream()
                .forEach(room -> {
                    if (room.createOrdersOnZReport) {
                        return;
                    }

                    PmsManager pmsManager = scope.getNamedSessionBean(multilevelName, PmsManager.class);
                    PmsBooking booking = pmsManager.getBookingFromRoom(room.pmsBookingRoomId);
                    if (booking != null) {
                        pmsManager.toggleAutoCreateOrders(booking.id, room.pmsBookingRoomId);
                    }
                });
    }

    private void checkIfAccrudePaymentIsActivated() {
        Application application = storeApplicationPool.getApplication("60f2f24e-ad41-4054-ba65-3a8a02ce0190");
        if (application == null) {
            storeApplicationPool.activateApplication("60f2f24e-ad41-4054-ba65-3a8a02ce0190");
        }
    }

    private List<PmsBookingRooms> getRoomsNeedToCreateOrdersFor() {
        Date end = getDateWithOffset(new Date(), 0);

        /**
         * When its connected to the getshop central we also do accrude payments for future booking to make a forcast.
         */
        boolean connectedToCentral = central.hasBeenConnectedToCentral();
        Date fromWhenToTakeIntoAccount  = (connectedToCentral) ? setDateToBeginningOfMonth(central.hasBeenConnectedToCentralSince()) : null;

        PmsManager pmsManager = scope.getNamedSessionBean(getEngineName(), PmsManager.class);

        List<PmsBookingRooms> roomsNeedToCreateOrdersFor = pmsManager.getAllBookingsFlat()
                .stream()
                .flatMap(b -> b.rooms.stream())
                .filter(r -> (connectedToCentral && r.date.end.after(fromWhenToTakeIntoAccount)) || r.createOrdersOnZReport)
                .filter(r -> r.hasUnsettledAmountIncAccrued())
                .filter(r -> r.date.start.before(end) || r.date.start.equals(end))
                .collect(Collectors.toList());

        updateAccruedAmountForRoomBookings(roomsNeedToCreateOrdersFor, pmsManager);

        return roomsNeedToCreateOrdersFor.stream()
                .filter(room -> room.hasUnsettledAmountIncAccrued()).collect(Collectors.toList());

    }

    private Date setDateToBeginningOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public void updateAccruedAmountForRoomBookings(List<PmsBookingRooms> roomsToBeRecalculated, PmsManager pmsManager) {
        
        for(PmsBookingRooms room : roomsToBeRecalculated) {
            PmsBooking booking = pmsManager.getBookingFromRoom(room.pmsBookingRoomId);
            room.unsettledAmountIncAccrued = recalculateAccruedAmountForRoomBooking(pmsManager, booking.id, room.pmsBookingRoomId);
        }
    }

    private Double recalculateAccruedAmountForRoomBooking(PmsManager pmsManager, String bookingId, String pmsBookingRoomId) {
        PmsRoomPaymentSummary summary = pmsManager.getSummary(bookingId, pmsBookingRoomId);
        if (summary == null) {
            return 0D;
        } else {
            return summary.getCheckoutRows()
                    .stream()
                    .mapToDouble(o -> o.count * o.price)
                    .sum();
        }
    }

    public boolean hasZreport(Order order) {
        return zReports.values()
                .stream()
                .filter(o -> o.orderIds.contains(order.id))
                .count() > 0;
    }

    @Override
    public void createZReportOfMissingOrders(String cashPointId) {
        List<String> orderIds = orderManager.getOrdersNotConnectedToAnyZReports()
                .stream()
                .map(o -> o.id)
                .collect(Collectors.toList());

        createZReportInternal(cashPointId, orderIds);
    }
}

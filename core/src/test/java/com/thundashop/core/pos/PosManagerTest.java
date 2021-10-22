package com.thundashop.core.pos;

import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderTransaction;
import com.thundashop.core.productmanager.data.Product;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.mockito.Mockito.*;

public class PosManagerTest {

    PosManager posManager;

    public PosManagerTest() {
        posManager =  Mockito.mock( PosManager.class);
        posManager.zReports = new HashMap<>();

        doReturn(true).when(posManager).isConnectedToCentral();
        doReturn(get10daysAgo()).when(posManager).isConnectedToCentralSince();
    }

    @Test
    public void unpaidInvoiceWillGetOnZreport() {
        Order unpaidInvoice = makeOrderUnpaidInvoice(2222,"2222");
        when( posManager.filterOrdersForZreport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenCallRealMethod();
        List<String> orderIds = posManager.filterOrdersForZreport( "cashpointId", getTwodaysAgo(), getBaseOfZreport(posManager), getAllOrders(unpaidInvoice));
        Assert.assertTrue(orderIds.contains("2222"));
    }

    @Test
    public void invoiceGetsPaidAndAgainNeedsToBeOnZReport() {
    }

    @Test
    public void invoiceWithFuturePaymentDateNotOnZreport() {
        Order orderFuture = makeOrderThatHasFuturePaymentDate(1111, "1111");

        when( posManager.filterOrdersForZreport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenCallRealMethod();
        List<String> orderIds = posManager.filterOrdersForZreport( "cashpointId", getTwodaysAgo(), getBaseOfZreport(posManager), getAllOrders(orderFuture));
        Assert.assertFalse(orderIds.contains("1111"));
    }

    private Order makeOrderThatHasFuturePaymentDate(int incrementOrderId, String orderId) {
        Order order = new Order();
        OrderTransaction t1 = new OrderTransaction();
        t1.addedToZreport = "";
        t1.transferredToAccounting = false;
        order.orderTransactions.add(t1);

        order.paymentDate = getTomorrow();
        order.rowCreatedDate = new Date();
        order.incrementOrderId = incrementOrderId;
        order.id = orderId;
        order.cart = new Cart();
        order.cart.createCartItem(new Product(), 1);
        return order;
    }
    private Order makeOrderUnpaidInvoice(int incrementOrderId, String orderId) {
        Order order = new Order();
        OrderTransaction t1 = new OrderTransaction();
        t1.addedToZreport = "";
        t1.transferredToAccounting = false;
        order.orderTransactions.add(t1);

        order.paymentDate = null;
        order.rowCreatedDate = new Date();
        order.incrementOrderId = incrementOrderId;
        order.id = orderId;
        order.cart = new Cart();
        order.cart.createCartItem(new Product(), 1);
        return order;
    }

    private ZReport getBaseOfZreport(PosManager posManager) {
        ZReport zReport = new ZReport();
        zReport.start = getTwodaysAgo();
        zReport.end = new Date();
        zReport.cashPointId = "cashpointId";
        zReport.rowCreatedDate = getTwodaysAgo();
        posManager.zReports.put("ZREPORT0", zReport);
        return zReport;
    }

    private Date getTwodaysAgo() {
        Date twodaysAgo= new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(twodaysAgo);
        c.add(Calendar.DATE, -2);
        twodaysAgo = c.getTime();
        return twodaysAgo;
    }

    private Date get10daysAgo() {
        Date twodaysAgo= new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(twodaysAgo);
        c.add(Calendar.DATE, -10);
        twodaysAgo = c.getTime();
        return twodaysAgo;
    }
    private Date getTomorrow() {
        Date twodaysAgo= new Date();//-2
        Calendar c = Calendar.getInstance();
        c.setTime(twodaysAgo);
        c.add(Calendar.DATE, 1);
        twodaysAgo = c.getTime();
        return twodaysAgo;
    }

    private List<Order> getAllOrders(Order order){
        HashMap<String, Order> orders = new HashMap();
        orders.put("1111", order);
        ArrayList list =  new ArrayList<>(orders.values());
        return list;
    }

}
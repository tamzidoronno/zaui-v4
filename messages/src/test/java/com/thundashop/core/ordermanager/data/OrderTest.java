package com.thundashop.core.ordermanager.data;

import org.junit.Assert;
import org.junit.Test;

public class OrderTest {

    Order order;
    OrderTransaction t1;

    public OrderTest() {
        order = new Order();
        t1 = new OrderTransaction();
        t1.addedToZreport = "";
        t1.transferredToAccounting = false;

        order.orderTransactions.add(t1);
    }

    @Test
    public void anOrderTransactionAddedToAZReportHasNoUntransferredPayements() {
        t1.addedToZreport = "ZREPORT1";

        Assert.assertFalse(order.hasUntransferredPayments());
    }

    @Test
    public void anOrderTransactionTransferredToAccountingHasNoUntransferredPayements() {
        t1.transferredToAccounting = true;

        Assert.assertEquals(order.hasUntransferredPayments(), false);
    }
}
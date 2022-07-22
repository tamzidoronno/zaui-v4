package com.thundashop.core.paymentmanager;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.paymentmanager.EasyByNets.EasyByNetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Naim Murad (naim)
 * @since 7/5/22
 */
@Slf4j
//@Service
public class PaymentStatusCheckService {

    @Autowired private BookingEngine bookingEngine;
    @Autowired private OrderManager orderManager;
    @Autowired private EasyByNetService easyByNetService;

    private static final String NUM = "";

    public boolean isPaymentSuccess() {
        List<Order> paymentPendingOrders = getPendingPaymentstatusOrders();
        for (Order order : paymentPendingOrders) {
            boolean anyUpdated = false;
            for (CartItem item : order.getCartItems()) {
            }
        }
        return false;
    }
    public List<Order> getPendingPaymentstatusOrders() {
        return orderManager.getAllOrders().stream()
                .filter(o ->  o.markedPaidDate == null || o.warnedNotPaid)
                .filter(o -> o.payment != null &&  o.payment.paymentInitiated && o.payment.paymentInitiatedDate != null && isToday(o.payment.paymentInitiatedDate.getTime()))
                .collect(Collectors.toList());
    }
    private static boolean isToday(long timestamp) {
        Calendar now = Calendar.getInstance();
        Calendar timeToCheck = Calendar.getInstance();
        timeToCheck.setTimeInMillis(timestamp);
        return (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR));
    }
}

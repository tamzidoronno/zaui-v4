package com.thundashop.core.paymentmanager;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Naim Murad (naim)
 * @since 7/5/22
 */
@Slf4j
//@Service
public class PaymentStatusCheckService {

    @Autowired private BookingEngine bookingEngine;
    @Autowired private OrderManager orderManager;

    private static final String NUM = "";

    public boolean isPaymentSuccess() {
        List<Order> allOrders = orderManager.getAllOrders();
        for (Order order : allOrders) {
            boolean anyUpdated = false;
            for (CartItem item : order.getCartItems()) {
                //item.accountingDate();
            }
        }
        return false;
    }
}

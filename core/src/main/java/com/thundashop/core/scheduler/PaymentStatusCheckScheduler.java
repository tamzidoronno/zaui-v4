package com.thundashop.core.scheduler;

import com.thundashop.core.paymentmanager.PaymentStatusCheckService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Naim Murad (naim)
 * @since 6/28/22
 */
public class PaymentStatusCheckScheduler {

    @Autowired private PaymentStatusCheckService service;


}

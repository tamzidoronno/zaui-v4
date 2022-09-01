package com.thundashop.core.ordermanager.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Naim Murad (naim)
 * @since 8/3/22
 */
public class PaymentLog implements Serializable {
    public String orderId;
    public String transactionPaymentId;
    public boolean isPaymentInitiated;
    public String paymentTypeId;
    public String paymentResponse;
}


package com.thundashop.core.pmsbookingprocess;

/**
 *
 * @author boggi
 */
public class StartPaymentProcessResult {
    public String name;
    public Double amount;
    public String orderId;
    boolean goToCompleted = false;
    boolean paidFor = false;
    public String pmsBookingId = "";
}

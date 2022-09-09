
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Payment {

    @SerializedName("paymentId")
    @Expose
    private String paymentId;
    @SerializedName("summary")
    @Expose
    private Summary summary;
    @SerializedName("consumer")
    @Expose
    private Consumer consumer;
    @SerializedName("paymentDetails")
    @Expose
    private PaymentDetails paymentDetails;
    @SerializedName("orderDetails")
    @Expose
    private OrderDetails orderDetails;
    @SerializedName("checkout")
    @Expose
    private Checkout checkout;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("refunds")
    @Expose
    private List<Refund> refunds = new ArrayList<Refund>();
    @SerializedName("charges")
    @Expose
    private List<Charge> charges = new ArrayList<Charge>();
    @SerializedName("terminated")
    @Expose
    private String terminated;
    @SerializedName("subscription")
    @Expose
    private Subscription subscription;
    @SerializedName("unscheduledSubscription")
    @Expose
    private UnscheduledSubscription unscheduledSubscription;
    @SerializedName("myReference")
    @Expose
    private String myReference;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Checkout getCheckout() {
        return checkout;
    }

    public void setCheckout(Checkout checkout) {
        this.checkout = checkout;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public List<Refund> getRefunds() {
        return refunds;
    }

    public void setRefunds(List<Refund> refunds) {
        this.refunds = refunds;
    }

    public List<Charge> getCharges() {
        return charges;
    }

    public void setCharges(List<Charge> charges) {
        this.charges = charges;
    }

    public String getTerminated() {
        return terminated;
    }

    public void setTerminated(String terminated) {
        this.terminated = terminated;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public UnscheduledSubscription getUnscheduledSubscription() {
        return unscheduledSubscription;
    }

    public void setUnscheduledSubscription(UnscheduledSubscription unscheduledSubscription) {
        this.unscheduledSubscription = unscheduledSubscription;
    }

    public String getMyReference() {
        return myReference;
    }

    public void setMyReference(String myReference) {
        this.myReference = myReference;
    }

}

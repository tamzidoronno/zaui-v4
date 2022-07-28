package com.thundashop.core.paymentmanager.EasyByNets;

import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.paymentmanager.EasyByNets.DTO.RetrievePayment;
import com.thundashop.core.paymentmanager.EasyByNets.DTO.Summary;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Date;
import java.util.List;

/**
 * @author Naim Murad (naim)
 * @since 7/21/22
 */
@Slf4j
@Service
public class EasyByNetService {
    @Autowired private StoreApplicationPool storeApplicationPool;
    @Autowired private FrameworkConfig frameworkConfig;
    @Autowired private OrderManager orderManager;


    public RetrievePayment retrievePayment(String paymentId) {

        Application app = storeApplicationPool.getApplication("be004408_e969_4dba_9b23_5922b8f1d7e2");
        if(app == null || app.getSetting("apikey") == null) {
            log.error("Easy by Net application not found {}" , "be004408_e969_4dba_9b23_5922b8f1d7e2");
            return null;
        }
        String token = app.getSetting("apikey");
        if(StringUtils.isBlank(token)) {
            log.error("Token required");
            return null;
        }
        if(StringUtils.isBlank(paymentId)) {
            log.error("Payment ID required");
            return null;
        }
        String url = "https://test.api.dibspayment.eu/v1/payments/";
        if(frameworkConfig.productionMode) {
            url = "https://api.dibspayment.eu/v1/payments/";
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();

        PaymentAPI service = retrofit.create(PaymentAPI.class);
        Call<RetrievePayment> callSync = service.getPaymentDetails(token, paymentId);
        try {
            Response<RetrievePayment> response = callSync.execute();
            return response.body();
        } catch (Exception ex) {
            log.error("Failed {} {}", ex.getMessage(), ex);
            return null;
        }
    }

    public void checkAndUpdatePaymentStatus(List<Order> orders) {
        if(orders == null || orders.isEmpty()) return;
        log.info("Pending payment order size {}", orders.size());
        orders.stream()
                .filter(this::isOrderPendingForEasyByNet)
                .forEach(o -> {
                    Double val = paidValue(o.payment.transactionPaymentId);
                    log.info("Pending payment order no: {}, paid value {}", o.incrementOrderId, val);
                    if(val == null) return;
                    orderManager.markAsPaidWithTransactionType(o.id, new Date(), val, Order.OrderTransactionType.SCHEDULER, "", "Automatically marking as paid by Easy by net scheduler");
                });
    }
    private Double paidValue(String paymentId) {
        RetrievePayment pay =  retrievePayment(paymentId);
        if(pay == null || pay.getPayment() == null) {
            log.error("Payment not found with payment id {}", paymentId);
            return null;
        }
        Summary summary = pay.getPayment().getSummary();
        if(summary == null) {
            log.error("Payment summary not found with payment id {}", paymentId);
            return null;
        }
        if(summary.getRefundedAmount() > 0) {
            log.error("Payment has refunded {} with payment id {}", summary.getRefundedAmount(), paymentId);
            return null;
        }
        if(summary.getCancelledAmount() > 0) {
            log.error("Payment has cancelled {} with payment id {}", summary.getCancelledAmount(), paymentId);
            return null;
        }
        if(summary.getChargedAmount() > 0 && pay.getPayment().getCharges() != null && !pay.getPayment().getCharges().isEmpty()) {
            log.info("Payment has total {} no of charges with amount {} with payment id {}", pay.getPayment().getCharges().size(), summary.getChargedAmount(), paymentId);
            return summary.getChargedAmount();
        }
        return null;
    }

    private boolean isOrderPendingForEasyByNet(Order o) {
        Payment payment = o.payment;
        if(payment == null || StringUtils.isNotBlank(payment.paymentType)) return false;
        if(!payment.paymentType.endsWith("EasyByNets") || StringUtils.isBlank(payment.transactionPaymentId)) return false;
        return true;
    }
}

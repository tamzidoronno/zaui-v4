package com.thundashop.core.paymentmanager.EasyByNets;

import com.thundashop.core.common.FrameworkConfig;
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

/**
 * @author Naim Murad (naim)
 * @since 7/21/22
 */
@Slf4j
@Service
public class EasyByNetService {
    @Autowired private FrameworkConfig frameworkConfig;
    public RetrievePayment retrievePayment(String token, String paymentId) {
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

    public double paidValue(String token, String paymentId) {
        RetrievePayment pay =  retrievePayment(token, paymentId);
        double val = 0;
        if(pay == null || pay.getPayment() == null) {
            log.error("Payment not found with payment id {}", paymentId);
            return val;
        }
        Summary summary = pay.getPayment().getSummary();
        if(summary == null) {
            log.error("Payment summary not found with payment id {}", paymentId);
            return val;
        }
        if(summary.getRefundedAmount() > 0) {
            log.error("Payment has refunded {} with payment id {}", summary.getRefundedAmount(), paymentId);
            return val;
        }
        if(summary.getCancelledAmount() > 0) {
            log.error("Payment has cancelled {} with payment id {}", summary.getCancelledAmount(), paymentId);
            return val;
        }
        if(summary.getChargedAmount() > 0 && pay.getPayment().getCharges() != null && !pay.getPayment().getCharges().isEmpty()) {
            log.info("Payment has total {} no of charges with amount {} with payment id {}", pay.getPayment().getCharges().size(), summary.getChargedAmount(), paymentId);
            return summary.getChargedAmount();
        }
        return val;
    }

}

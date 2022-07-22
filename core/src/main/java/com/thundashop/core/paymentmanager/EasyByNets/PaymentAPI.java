package com.thundashop.core.paymentmanager.EasyByNets;

import com.thundashop.core.paymentmanager.EasyByNets.DTO.RetrievePayment;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * @author Naim Murad (naim)
 * @since 7/15/22
 */
public interface PaymentAPI {
    // Request method and URL specified in the annotation
    @GET("{paymentId}")
    Call<RetrievePayment> getPaymentDetails(@Header("Authorization") String authorization, @Path("paymentId") String paymentId);
}

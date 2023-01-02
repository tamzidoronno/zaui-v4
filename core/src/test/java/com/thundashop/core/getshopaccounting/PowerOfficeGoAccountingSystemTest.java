package com.thundashop.core.getshopaccounting;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import com.thundashop.core.webmanager.WebManager;
import com.thundashop.services.core.httpservice.ZauiHttpService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;

public class PowerOfficeGoAccountingSystemTest {

    @Autowired
    ZauiHttpService httpService;

    String data = "{\n" +
            "  \"code\":\"191455\",\n" +
            "  \"name\":\"MOELVEN MODUS AS\",\n" +
            "  \"vatNumber\":\"951269778\",\n" +
            "  \"emailAddress\":\"Per.Finstad@moelven.no\",\n" +
            "  \"id\":\"27994498\",\n" +
            "  \"mailAddress\":{\n" +
            "    \"city\":\"JESSHEIM\",\n" +
            "    \"zipCode\":\"2069\",\n" +
            "    \"address1\":\"Asfaltvegen 1\",\n" +
            "    \"countryCode\":\"NO\",\n" +
            "    \"id\":1,\n" +
            "    \"isPrimary\":true\n" +
            "  }\n" +
            "}";

    String endpoint = "https://api.poweroffice.net/customer/";
    //        String endpoint = "http://localhost:8080";
    String token = "IrSsVsUdT5XIYKcoAkegMYu-UcGPVDh47TKlfAUUedfcIi0XyvIIt735kDnWuoTFJSbDTqcSZ7IbgZtla-qrEI4SdFzs3KApZcW9RF3bWjWDzYM-LRHroc5z60yTklugJ_tRDlW54_uIXcGXasAQTCFl0SpFI0LeaQ4UgEuUTkjef5Z59--wcADzT6vPZUDmvjf7uTPu6xrCt0rzo0fzl8qDnu9WiPmCgDooejAVrgzXZqH9jvW6NzgIC0E1wpREaBhWbz8q1qk_RjiQ2ZNrGNme4QndqKfMB7PpM40VYyWzS_iXa2kYuhW1_hmlgs98Wom9mJ5wl0UQtWEm6TRDso8ygtto9rZOo1OYgMPV98BemA4C0Ua1gBWBMpyUZvdt5Wq9olwUEQbETWWLa4Gtk0ojf9q-T3-hYh4ut2El7GbSexSPxeovxXxgZ8UKVUOCH6uS3A";
    String htmlType = "POST";

    @Test
    @Ignore
    public void testPowerOfficeApi() throws Exception {
        WebManager webManager = new WebManager();

        String result = webManager.htmlPostBasicAuth(endpoint, data, true, null, token,
                "Bearer", false, htmlType);

        System.out.println("Result --> " + result);
    }

    @Test
    @Ignore
    public void okHttp() throws IOException {
        OkHttpClient httpClient = new OkHttpClient();

        Response response = httpClient.newCall(new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .url(endpoint)
                .method("POST", RequestBody.create(MediaType.parse("application/json"), data))
                .build()).execute();

        String res = response.body().string();
        int code = response.code();

        System.out.println(code);
        System.out.println(res);

    }

    @Test
    @Ignore
    public void okHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .build();
        PowerOfficeGoHttpClientManager manager = new PowerOfficeGoHttpClientManager(httpService);

        String response = manager.post(data, token, endpoint);

        System.out.println(response);
    }
}
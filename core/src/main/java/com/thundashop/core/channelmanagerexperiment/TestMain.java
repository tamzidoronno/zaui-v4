package com.thundashop.core.channelmanagerexperiment;

import com.thundashop.core.channelmanagerexperiment.gotohub.GotoSimpleResponse;
import okhttp3.*;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import static com.thundashop.core.utils.Constants.GOTO_CLIENT_URL;


public class TestMain {
    public static void main(String []args) throws Exception{
        RestTemplate restTemplate = new RestTemplate();
        GotoSimpleResponse simpleResponse = restTemplate.postForObject(GOTO_CLIENT_URL, null, GotoSimpleResponse.class);
        System.out.println("the message is: "+simpleResponse.getMessage());

//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("text/plain");
//        RequestBody body = RequestBody.create(mediaType, "");
//        Request request = new Request.Builder()
//                .url(GOTO_CLIENT_URL)
//                .method("POST", body)
//                .build();
//
//        Response response = client.newCall(request).execute();

    }
}

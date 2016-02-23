/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.databasemanager.Database;
import java.io.InputStream;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author ktonder
 */
public class PlivoSmsHandler extends SmsHandlerAbstract implements SmsHandler, Runnable {

    public PlivoSmsHandler(String storeId, Database database, String prefix, String from, String to, String message, boolean productionMode) {
        super(storeId, database, prefix, from, to, message, productionMode);
    }

    @Override
    public String getName() {
        return "plivo";
    }
    
    @Override
    public void postSms() throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        client = wrapClient(client);
        HttpResponse httpResponse = null;
        
        String encoding = Base64.encode(("MAMGUYNGZMMWMWOWM2MD:ODJjZmQyYjExZDZhMjdmMTg4YWI2MjJiZWQ3NDky").getBytes());

        HttpPost request = new HttpPost("https://api.plivo.com/v1/Account/MAMGUYNGZMMWMWOWM2MD/Message/");
        request.addHeader("Authorization", "Basic " + encoding);

        HttpEntity reqEntity = createHttpEntity();
        request.setEntity(reqEntity);
        httpResponse = client.execute(request);

        HttpEntity entity = httpResponse.getEntity();

        if (entity != null) {
            InputStream instream = entity.getContent();
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = instream.read()) != -1) {
                sb.append((char) ch);
            }
            String result = sb.toString();
            if (result.contains("error")) {
                logMessage("failed", result);
            } else {
                logMessage("delivered", result);
            }
        } 

        logMessage("failed", "");
    }

    private HttpEntity createHttpEntity() {
        String dst = getTo().replace("+","");
        dst = getPrefix() + dst;
        StringBody srcbody = new StringBody(getFrom(), ContentType.TEXT_PLAIN);
        StringBody dstbody = new StringBody(dst, ContentType.TEXT_PLAIN);
        StringBody textbody = new StringBody(getMessage(), ContentType.TEXT_PLAIN);
        HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("src", srcbody)
                .addPart("dst", dstbody)
                .addPart("text", textbody)
                .build();
        
        return reqEntity;
    }
}
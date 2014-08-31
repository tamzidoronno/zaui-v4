package com.thundashop.core.start;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class TestClass {

    public static void main(String[] args) {
        TestClass test = new TestClass();
        test.httpLoginRequest("https://192.168.1.103:5002/arx/import", "master", "master", "/home/boggi/import_person");
    }

    public String httpLoginRequest(String address, String username, String password, String filepath) {
        String loginToken = null;
        String loginUrl = address;
        //
        DefaultHttpClient client = new DefaultHttpClient();
        client = wrapClient(client);
        HttpResponse httpResponse;

        HttpEntity entity;
        HttpPost request = new HttpPost(loginUrl);
        byte[] bytes = (username + ":" + password).getBytes();
        String encoding = Base64.encode(bytes);

        request.addHeader("Authorization", "Basic " + encoding);

        FileBody bin = new FileBody(new File(filepath));
        StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

        HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("upfile", bin)
                .addPart("comment", comment)
                .build();

        request.setEntity(reqEntity);

        try {
            httpResponse = client.execute(request);
            entity = httpResponse.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                int ch;
                StringBuilder sb = new StringBuilder();
                while ((ch = instream.read()) != -1) {
                    sb.append((char) ch);
                }
                String result = sb.toString();
                System.out.println(result);
            }
        } catch (ClientProtocolException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
        return loginToken;
    }

    public static DefaultHttpClient wrapClient(DefaultHttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = base.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            return new DefaultHttpClient(ccm, base.getParams());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}

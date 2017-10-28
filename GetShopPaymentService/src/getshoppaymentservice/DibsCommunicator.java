package getshoppaymentservice;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.HashMap;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author boggi
 */
public class DibsCommunicator {
    public static void main(String[] args) throws Exception {
        DibsCommunicator com = new DibsCommunicator();
        com.testPostData();
    }

    private void testPostData() throws Exception {
//        chargeCard("4711100000000000", "684", "06", "24", (long)1, "90069173", "10", true);
        chargeCard("4299412252118913", "558", "06", "19", (long)5, "90217560", "200", false);
    }

    public HashMap<String, String> chargeCard(String cardno, String cvc, String expmon, String expyear, Long orderId, String merchantId, String amount, boolean testMode) throws Exception {
        String capturenow = "yes";
        String currency = "NOK";
        String textreply = "no";
        Integer test = 1;
        if(!testMode) {
            test = 0;
        }
        
        String rawData = "amount="+amount+"&cardno="+cardno+"&";
        rawData += "cvc="+cvc+"&";
        rawData += "expmon="+expmon+"&";
        rawData += "expyear="+expyear+"&";
        rawData += "postype=SSL&";
        rawData += "orderid="+orderId+"&";
        rawData += "merchant="+merchantId+"&";
        rawData += "capturenow="+capturenow+"&";
        rawData += "currency="+currency+"&";
        rawData += "test="+test+"&";
        rawData += "orderid="+orderId+"&";
        rawData += "textreply="+textreply;
        LogPrinter.println(rawData);
        
        String url = "https://payment.architrade.com/cgi-ssl/auth.cgi";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setDoOutput(true);
        con.connect();
        
        Certificate[] certs = con.getServerCertificates();
        boolean verified = false;
        for (Certificate cert : certs) {
            if(cert.getType().contains("O=DIBS Payment Services AB (publ)") && cert.getType().contains("CN=payment.architrade.com")) {
                verified = true;
            }
        }

        if(verified) {
            LogPrinter.println("Dibs sertificate not valid");
            return null;
        }
        
        // Send post request
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(rawData);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        LogPrinter.println("\nSending 'POST' request to URL : " + url);
        LogPrinter.println("Post parameters : " + rawData);
        LogPrinter.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        in.close();

        String responseToParse = response.toString();
        String[] responseList = responseToParse.split("&");
        HashMap<String, String> responseMap = new HashMap();
        for(String res : responseList) {
            String[] variable = res.split("=");
            responseMap.put(variable[0], variable[1]);
        }
        return responseMap;
    }
    
    
    public String readFullyAsString(InputStream inputStream, String encoding) throws IOException {
        return readFully(inputStream).toString(encoding); 
    }

    private ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }

}

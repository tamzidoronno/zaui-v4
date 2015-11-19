package com.thundashop.core.dibs;

import com.braintreegateway.org.apache.commons.codec.DecoderException;
import com.braintreegateway.org.apache.commons.codec.binary.Hex;
import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.data.Order;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import static java.lang.System.in;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class DibsManager extends ManagerBase implements IDibsManager {

    @Autowired
    MessageManager messageManager;
    
    /**
     * postToDIBS Sends a post to the specified DIBS function
     *
     * @param parameters A Map<String, String> holding the parameters to be
     * parsed to DIBS
     * @param func A String holding the name of the function to be used
     * @return A Map<String, String> holding the parameters returned by DIBS
     */
    public Map<String, String> postToDIBS(Map<String, String> parameters, String func) {
        boolean debug = true;
        try {
            //Set the endpoint for the chosen function
            String endpoint = null;
            if (func.equals("AuthorizeCard")) {
                endpoint = "https://api.dibspayment.com/merchant/v1/JSON/Transaction/AuthorizeCard";
            } else if (func.equals("AuthorizeTicket")) {
                endpoint = "https://api.dibspayment.com/merchant/v1/JSON/Transaction/AuthorizeTicket";
            } else if (func.equals("CancelTransaction")) {
                endpoint = "https://api.dibspayment.com/merchant/v1/JSON/Transaction/CancelTransaction";
            } else if (func.equals("CaptureTransaction")) {
                endpoint = "https://api.dibspayment.com/merchant/v1/JSON/Transaction/CaptureTransaction";
            } else if (func.equals("CreateTicket")) {
                endpoint = "https://api.dibspayment.com/merchant/v1/JSON/Transaction/CreateTicket";
            } else if (func.equals("RefundTransaction")) {
                endpoint = "https://api.dibspayment.com/merchant/v1/JSON/Transaction/RefundTransaction";
            } else if (func.equals("Ping")) {
                endpoint = "https://api.dibspayment.com/merchant/v1/JSON/Transaction/Ping";
            } else {
                System.out.println("Error in postToDIBS(): Given function does not exist");
                return null;
            }

            // Set properties for connection. (bengt karlson)
            // Connection is not initialized until parameters are posted
            System.setProperty("jsse.enableSNIExtension", "false");
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            // Initiate output stream for connection and post message
            OutputStream out = connection.getOutputStream();
            out.write("request=".getBytes(Charset.forName("UTF-8")));
            Gson gson = new Gson();
            String json = gson.toJson(parameters);
            out.write(json.getBytes(Charset.forName("UTF-8")));
            out.flush();
            out.close();

            if(debug) {
                System.out.println("-------------- Debug ----------");
                System.out.println("Endpoint: " + endpoint);
                for(String key : parameters.keySet()) {
                    System.out.println("\t" + key + " : " + parameters.get(key));
                }

                System.out.println("Writing to stream: " + json);
            }
            
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader is = new InputStreamReader(connection.getInputStream());
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(is);
                String read = br.readLine();

                while (read != null) {
                    sb.append(read);
                    read = br.readLine();
                }

                String result = sb.toString();

                Map<String, String> response = gson.fromJson(result, Map.class);
                if(debug) {
                    System.out.println("Response code: " + connection.getResponseCode());
                    System.out.println("Response message: " + result);
                    System.out.println("-------------- Debug ----------");
                }
                return response;
            } else {
                // Server returned HTTP error code.
                System.out.println("HTTP error!" + connection.getResponseCode());
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * CaptureTransaction Captures a previously authorized transaction using the
     * CaptureTransaction JSON service
     *
     * @param amount The amount of the purchase in smallest unit
     * @param merchantId DIBS Merchant ID / customer number
     * @param transactionId The ticket number on which the authorization should
     * be done
     * @param macKeyHex The secret HMAC key from DIBS Admin
 *
     */
    public Map<String, String> captureTransaction(int amount, String merchantId, String transactionId, String macKeyHex) {
        // Create JSON object and add parameters
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("amount", String.valueOf(amount));
        parameters.put("merchantId", merchantId);
        parameters.put("transactionId", transactionId);

        // Add MAC value for request
        parameters.put("MAC", calculateMac(parameters, macKeyHex));

        // Post to the DIBS system and receive response
        Map<String, String> resp = postToDIBS(parameters, "CaptureTransaction");
        return resp;
    }
    
        /**
     * calculateMac Calculates the MAC from a given Map holding the post
     * parameters and the secret key from DIBS Admin
     *
     * @param parameters Map<String, String> holding all post parameters
     * @param macKeyHex Hex encoded secret key from DIBS Admin
     * @return The MAC string calculated from the message and secret key using
     * the hmac-sha256 algorithm
     */
    public String calculateMac(Map<String, String> parameters, String macKeyHex) {
        try {
            // Sort the parameters ASCII-betically
            SortedMap<String, String> sortedParameters = new TreeMap<String, String>(parameters);

            // Build the string to calculate HMAC from
            StringBuilder macMsg = new StringBuilder();
            for (Map.Entry<String, String> param : sortedParameters.entrySet()) {
                if (macMsg.length() != 0) {
                    macMsg.append('&');
                }
                if(!param.equals("MAC")) {
                    macMsg.append(param.getKey()).append('=').append(param.getValue());
                }
            }

            // Decode the hex-encoded secret key
            byte[] macKey = Hex.decodeHex(macKeyHex.toCharArray());

            // Calculate the hmac from message and secret key
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            hmacSha256.init(new SecretKeySpec(macKey, "HmacSHA256"));
            byte[] mac = hmacSha256.doFinal(macMsg.toString().getBytes(Charset.forName("UTF-8")));

            // Return the hex-encoded hmac
            return Hex.encodeHexString(mac);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void captureOrder(Order order, int amount) throws Exception {
        String merchantId = order.payment.callBackParameters.get("merchant");
        String secretMacKey = "need to fetch from configuration";
        Map<String, String> response = captureTransaction(amount, merchantId , order.payment.callBackParameters.get("transaction"), secretMacKey);
        
        if(!order.payment.callBackParameters.get("currency").equals("NOK")) {
            throw new Exception("Incorrect currency set");
        }
        
        if(response == null) {
            throw new Exception("Invalid response from dibs");
        }
        
        Gson gson = new Gson();
        String respresult = gson.toJson(response);
        
        if (response.get("status").equals("ACCEPT")) {
            order.captured = true;
            order.payment.captured = true;
            order.status = Order.Status.PAYMENT_COMPLETED;
        } else if (response.get("status").equals("DECLINE")) {
            messageManager.sendMail("post@getshop.com", "post@getshop.com", "Declined to capture order (" + order.incrementOrderId + ")", respresult, "post@getshop.com", "post@getshop.com");
            order.status = Order.Status.COLLECTION_FAILED;
        } else {
            messageManager.sendMail("post@getshop.com", "post@getshop.com", "Failed to capture order (" + order.incrementOrderId + ")", respresult, "post@getshop.com", "post@getshop.com");
            order.status = Order.Status.COLLECTION_FAILED;
        }
        order.payment.transactionLog.put(System.currentTimeMillis(), respresult);
    }

}

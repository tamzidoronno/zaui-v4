package com.thundashop.core.dibs;

import com.braintreegateway.org.apache.commons.codec.DecoderException;
import com.braintreegateway.org.apache.commons.codec.binary.Hex;
import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.getshop.GetShopPullService;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.getshop.pullserver.PullMessage;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserCard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
        
    @Autowired
    private GetShopPullService getShopPullService; 

    @Autowired
    private OrderManager orderManager;

    @Autowired
    private FrameworkConfig frameworkConfig;
    
    @Autowired
    private CartManager cartManager;
    
    @Autowired
    private UserManager userManager;
    
    private boolean sentPollFailed = false;
    

    @Override
    public void dataFromDatabase(DataRetreived data) {
    }
    
    
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
                logPrint("Error in postToDIBS(): Given function does not exist");
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
            try (OutputStream out = connection.getOutputStream()) {
                out.write("request=".getBytes(Charset.forName("UTF-8")));
                Gson gson = new Gson();
                String json = gson.toJson(parameters);
                out.write(json.getBytes(Charset.forName("UTF-8")));
                out.flush();
                out.close();

                if(debug) {
                    logPrint("-------------- Debug ----------");
                    logPrint("Endpoint: " + endpoint);
                    for(String key : parameters.keySet()) {
                        logPrint("\t" + key + " : " + parameters.get(key));
                    }

                    logPrint("Writing to stream: " + json);
                }

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStreamReader is = new InputStreamReader(connection.getInputStream())) {
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
                            logPrint("Response code: " + connection.getResponseCode());
                            logPrint("Response message: " + result);
                            logPrint("-------------- Debug ----------");
                        }
                        return response;
                    }
                } else {
                    // Server returned HTTP error code.
                    logPrint("HTTP error!" + connection.getResponseCode());
                    return null;
                }
            } finally {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
        String merchantId = storeApplicationPool.getApplication("d02f8b7a-7395-455d-b754-888d7d701db8").getSetting("merchantid");

        
        String secretMacKey = storeApplicationPool.getApplication("d02f8b7a-7395-455d-b754-888d7d701db8").getSetting("hmac");
       
        if(!frameworkConfig.productionMode) {
            merchantId = "90069173";
            secretMacKey = "7e546521244050365a355b2376305248695e3f2e7d467b754c7074635d563a366f302e256338473569756547456f732856376c412b3225454b5a4d6a6d5a5277";
        }
        
        String transaction = order.payment.callBackParameters.get("transaction");
        order.payment.transactionLog.put(System.currentTimeMillis(), "Starting capture of transaction (DIBS) : " + transaction);
        orderManager.saveOrder(order);
        
        Map<String, String> response = captureTransaction(amount, merchantId , transaction, secretMacKey);
        
        Application ecommerceSettings = storeApplicationPool.getApplication("9de54ce1-f7a0-4729-b128-b062dc70dcce");
        String currency = ecommerceSettings.getSetting("currency");
        if(currency == null || currency.isEmpty()) {
            currency = "NOK";
        }
        
        if(order.payment.callBackParameters.containsKey("currency") && !order.payment.callBackParameters.get("currency").equals(currency)) {
            messageManager.sendMail("post@getshop.com", "post@getshop.com", "Fraud attempt on order (" + order.incrementOrderId + ")", "", "post@getshop.com", "post@getshop.com");
            throw new Exception("Incorrect currency set");
        }
        
        if(response == null) {
            messageManager.sendMail("post@getshop.com", "post@getshop.com", "Invalid response on order (" + order.incrementOrderId + ")", "", "post@getshop.com", "post@getshop.com");
            throw new Exception("Invalid response from dibs");
        }
        
        Gson gson = new Gson();
        String respresult = gson.toJson(response);
        
        if (response.get("status").equals("ACCEPT")) {
            order.captured = true;
            order.payment.captured = true;
            orderManager.markAsPaid(order.id, new Date(), new Double(amount) / 100);
        } else if (response.get("status").equals("DECLINE")) {
            messageManager.sendMail("post@getshop.com", "post@getshop.com", "Declined to capture order (" + order.incrementOrderId + ")", respresult, "post@getshop.com", "post@getshop.com");
            order.status = Order.Status.COLLECTION_FAILED;
        } else {
            messageManager.sendErrorNotification("Failed to capture order (" + order.incrementOrderId + ")" + respresult, null);
            order.status = Order.Status.COLLECTION_FAILED;
        }
        order.payment.transactionLog.put(System.currentTimeMillis(), respresult);
    }

    
    @Override
    public void checkForOrdersToCapture() {
        Gson gson = new Gson();
        
        Application dibsApp = storeApplicationPool.getApplication("d02f8b7a-7395-455d-b754-888d7d701db8");
        if (dibsApp == null) {
            return;
        }
        
        
        String pollKey = dibsApp.getSetting("merchantid");
        if(frameworkConfig.productionMode) {
            pollKey += "-prod";
        } else {
            pollKey = "90069171-debug";
        }
        
        String useFlexWin = dibsApp.getSetting("useflexwin");
        
        try {
            //First check for polls.
            long start = System.currentTimeMillis();
            List<PullMessage> messages = getShopPullService.getMessages(pollKey, storeId);
            long end = System.currentTimeMillis();
            logPrint(start - end);
            for(PullMessage msg : messages) {
                try {
                    LinkedHashMap<String,String> polledResult = gson.fromJson(msg.postVariables, LinkedHashMap.class);
                    if(useFlexWin != null && useFlexWin.equals("true")) {
                        processFlexWinResponse(polledResult, msg);
                    } else {
                        processOrderPolledResult(polledResult,msg);
                    }
                }catch(Exception ex) {
                    messageManager.sendMail("post@getshop.com", "post@getshop.com", "Failed message message from pull server: " + ex.getMessage(), gson.toJson(msg), "post@getshop.com", "post@getshop.com");
                    logPrintException(ex);
                }
                getShopPullService.markMessageAsReceived(msg.id, storeId);
            }
            if (sentPollFailed) {
                messageManager.sendMail("post@getshop.com", "post@getshop.com", "Pullserver", "Back online again", "post@getshop.com", "post@getshop.com");
            }
            sentPollFailed = false;
        } catch (Exception ex) {
            if (!sentPollFailed) {
                messageManager.sendMail("post@getshop.com", "post@getshop.com", "Failed to fetch from pull server", "Is pull server down: " + ex.getMessage(), "post@getshop.com", "post@getshop.com");
                sentPollFailed = true;
                logPrintException(ex);
            }
        }
    }

    private void saveCardOnUser(Order order) {
        User user = userManager.getUserById(order.userId);
        if(user == null) {
            logPrint("Failed to find user?" + order.userId);
            return;
        }
        UserCard card = new UserCard();
        card.card = order.payment.callBackParameters.get("ticket");
        card.expireYear = new Integer(order.payment.callBackParameters.get("expYear"));
        card.expireMonth = new Integer(order.payment.callBackParameters.get("expMonth"));
        card.savedByVendor = "DIBS";
        card.mask = order.payment.callBackParameters.get("cardNumberMasked");
        user.savedCards.add(card);
        userManager.saveUser(user);
    }

    public boolean payWithCard(Order order, UserCard card) throws Exception {
        order.payment.transactionLog.put(System.currentTimeMillis(), "Trying to extract with saved card: " + card.card + " expire: " + card.expireMonth + "/" + card.expireYear);
        order.payment.triedAutoPay.add(new Date());
        orderManager.saveOrder(order);
        
        String merchantId = storeApplicationPool.getApplication("d02f8b7a-7395-455d-b754-888d7d701db8").getSetting("merchantid");
        Application ecommerceSettings = storeApplicationPool.getApplication("9de54ce1-f7a0-4729-b128-b062dc70dcce");
        String currency = ecommerceSettings.getSetting("currency");
        
        if(currency == null || currency.isEmpty()) {
            currency = "NOK";
        }
        
        Double amount = order.cart.getTotal(false);
        int toTicket = new Double(amount*100).intValue();
        
        String secretMacKey = storeApplicationPool.getApplication("d02f8b7a-7395-455d-b754-888d7d701db8").getSetting("hmac");
        Map<String, String> result = AuthorizeTicket(toTicket, currency, merchantId, order.incrementOrderId+ "", card.card, secretMacKey);
        boolean res = false;
        if (result.get("status").equals("ACCEPT")) {
            String transactionId = result.get("transactionId");
            order.payment.callBackParameters.put("transaction", transactionId);
            captureOrder(order, toTicket);
            res = true;
        } else if(order.payment.triedAutoPay.size() >= 31) {
            order.status = Order.Status.PAYMENT_FAILED;
       }
        
        Gson gson = new Gson();
        String toLog = gson.toJson(result);
        order.payment.transactionLog.put(System.currentTimeMillis(), toLog);
        orderManager.saveObject(order);
        return res;
    }
    
    
    /**
    * Dependencies:
    * Apache-Commons/Commons-Codec for Hex encoding/decoding
    * Codehaus/Jackson for JSON mapping
    */
   /**
    * AuthorizeTicket Makes a new authorization on an existing ticket using the AuthorizeTicket JSON service
    *
    * @param amount The amount of the purchase in smallest unit
    * @param clientIp The customers IP address
    * @param currency The currency either in numeric or string format (e.g. 208/DKK)
    * @param merchantId DIBS Merchant ID / customer number
    * @param orderId The shops order ID for the purchase
    * @param ticketId The ticket number on which the authorization should be done
    * @param macKeyHex The secret HMAC key from DIBS Admin
    */
   public Map<String, String> AuthorizeTicket(int amount, String currency, String merchantId, String orderId, String ticketId, String macKeyHex) {

     // Create JSON object and add parameters
     Map<String, String> parameters = new HashMap<String, String>();
     parameters.put("amount", String.valueOf(amount));
     parameters.put("currency", currency);
     parameters.put("merchantId", merchantId);
     parameters.put("orderId", orderId);
     parameters.put("ticketId", ticketId);

     // Add MAC value for request
     parameters.put("MAC", calculateMac(parameters, macKeyHex));

     // Post to the DIBS system and receive response
     Map<String, String> resp = postToDIBS(parameters, "AuthorizeTicket");
     
     if (resp.get("status").equals("ACCEPT")) {
       // Authorization accepted. Check resp.get("transactionId") for transaction ID
       // ...
       logPrint("Ticket Auth accepted. Response:");
       logPrint(resp.toString());
     } else if (resp.get("status").equals("DECLINE")) {
       // Authorization declined. Check resp.get("declineReason") for more information
       // ...
       logPrint("Ticket Auth declined. Response:");
       logPrint(resp.toString());
     } else {
       // An error happened. Check Check resp.get("declineReason") for more information
       // ...
       logPrint("An error happened during Auth. Response:");
       logPrint(resp.toString());
     }
     
     return resp;
   }

    private void saveTicket(Order order) {
        String status =  order.payment.callBackParameters.get("ticketStatus");
        if(!status.equals("ACCEPTED")) {
            messageManager.sendMail("post@getshop.com", "post@getshop.com", "Ticket status failure", "for order: " + order.incrementOrderId, "post@getshop.com", "post@getshop.com");
        } else {
            logPrint("This is a save card procedure...");
            saveCardOnUser(order);
            order.status = Order.Status.WAITING_FOR_PAYMENT;
            orderManager.saveOrder(order);
        }
    }

    private void processOrderPolledResult(LinkedHashMap<String, String> polledResult, PullMessage msg) {
        Gson gson = new Gson();
        if(!polledResult.containsKey("orderId")) {
            messageManager.sendMail("post@getshop.com", "post@getshop.com", "Failed prosessing message from pull server", gson.toJson(msg), "post@getshop.com", "post@getshop.com");
        } else {
            Order order = orderManager.getOrderByincrementOrderId(new Integer(polledResult.get("orderId")));
            try {
                order.payment.callBackParameters = polledResult;

                Double amount = orderManager.getTotalAmount(order);
                int toCapture = new Double(amount*100).intValue();
                order.payment.transactionLog.put(System.currentTimeMillis(), "Trying to capture this order");

                String createTicket = order.payment.callBackParameters.get("createTicket");
                if(createTicket != null && !createTicket.isEmpty()) {
                    saveTicket(order);
                } else {
                    captureOrder(order, toCapture);
                }
                if(order.status == Order.Status.PAYMENT_COMPLETED) {
                    orderManager.markOrderForAutoSending(order.id);
                }

                orderManager.saveOrder(order);
            }catch(Exception d) {
                messageManager.sendMail("post@getshop.com", "post@getshop.com", "Failed message message from pull server: " + d.getMessage(), gson.toJson(msg), "post@getshop.com", "post@getshop.com");
                order.payment.transactionLog.put(System.currentTimeMillis(), "Failed capturing order: " + d.getMessage());
                logPrintException(d);
            }
        }    
    }

    private void processFlexWinResponse(LinkedHashMap<String, String> polledResult, PullMessage msg) {
        Gson gson = new Gson();
        if(!polledResult.containsKey("orderid")) {
            messageManager.sendMail("post@getshop.com", "post@getshop.com", "Failed prosessing message from pull server", gson.toJson(msg), "post@getshop.com", "post@getshop.com");
        } else {
            Order order = orderManager.getOrderByincrementOrderId(new Integer(polledResult.get("orderid")));
            try {
                order.payment.callBackParameters = polledResult;
                
                if(!polledResult.containsKey("statuscode")) {
                    throw new Exception("Status code field not configured correctly in dibs, please make sure the statuscode is active as the return field in the dibs administration window");
                }

                Double amount = orderManager.getTotalAmount(order);
                int result = new Integer(polledResult.get("statuscode"));
                int toCapture = new Double(amount*100).intValue();
                int diff = toCapture-new Integer(polledResult.get("amount"));
                if(diff > 5 || diff < -5) {
                    throw new Exception("Invalid amount amount: " + toCapture + " - " + (amount*100));
                }
                if(result != 5 && result != 12) {
                    throw new Exception("Invalid response code from dibs code: " + result);
                }
                if(!polledResult.get("currency").equals("NOK")) {
                    throw new Exception("Invalid currency");
                }
                if(!validmd5(polledResult, amount.intValue())) {
                    throw new Exception("Invalid MD5");
                }

                String createTicket = order.payment.callBackParameters.get("createTicket");
                if(createTicket != null && !createTicket.isEmpty()) {
                    saveTicket(order);
                } else {
                    order.status = Order.Status.PAYMENT_COMPLETED;
                    orderManager.saveOrder(order);
                }
                
                if(order.status == Order.Status.PAYMENT_COMPLETED) {
                    orderManager.markOrderForAutoSending(order.id);
                }

                orderManager.saveOrder(order);
            }catch(Exception d) {
                messageManager.sendMail("post@getshop.com", "post@getshop.com", "Failed message message from pull server" + d.getMessage(), gson.toJson(msg), "post@getshop.com", "post@getshop.com");
                order.payment.transactionLog.put(System.currentTimeMillis(), "Failed capturing order: " + d.getMessage());
                logPrintException(d);
            }
        }
    }

    private boolean validmd5(LinkedHashMap<String, String> msg, Integer amount) {
        
        Application dibsApp = storeApplicationPool.getApplication("d02f8b7a-7395-455d-b754-888d7d701db8");
        
        String key1 = dibsApp.getSetting("md5k1");
        String key2 = dibsApp.getSetting("md5k2");
        
        String toCheck = "";
        toCheck += "transact=" + msg.get("transact") + "&";
        toCheck += "amount=" + msg.get("amount")  + "&";
        toCheck += "currency=578";
        
        
        String firstMd5 = key1 + toCheck;
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(firstMd5.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            String hashtext = bigInt.toString(16);
            
            while(hashtext.length() < 32 ){
              hashtext = "0"+hashtext;
            }

            
            String secondMd5 = key2 + hashtext;
            m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(secondMd5.getBytes());
            digest = m.digest();
            bigInt = new BigInteger(1,digest);
            hashtext = bigInt.toString(16);
            
            while(hashtext.length() < 32 ){
              hashtext = "0"+hashtext;
            }
            
            return msg.get("authkey").equals(hashtext);
        }catch(Exception e) {
            return false;
        }
    }
    
    public static void main(String[] args) {
        DibsManager testing = new DibsManager();
        String k1 = "bBEp;-+intW?wM:eXesp0-i~}gUYj]NS";
        String k2 = "~eV$82QYm%~-{[_2j-Cg;vCoT1^p2m_Z";
        testing.validmd5(new LinkedHashMap(), 208);
    }
}

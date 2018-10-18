package com.thundashop.core.epay;

import com.getshop.pullserver.PullMessage;
import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.getshop.GetShopPullService;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserCard;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Component
@GetShopSession
public class EpayManager extends ManagerBase implements IEpayManager {
        
    @Autowired
    FrameworkConfig frameworkConfig;
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
        
    @Autowired
    private GetShopPullService getShopPullService; 
      
    @Autowired
    MessageManager messageManager;
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    UserManager userManager;
    
    @Override
    public void checkForOrdersToCapture() {
        String pollKey = getCallBackId();
        if(pollKey == null) { return; }
        if(frameworkConfig.productionMode) {
            pollKey += "-prod";
        } else {
            pollKey += "-debug";
        }
        try {
            //First check for polls.
            long start = System.currentTimeMillis();
            List<PullMessage> messages = getShopPullService.getMessages(pollKey, storeId);
            Gson gson = new Gson();
            for(PullMessage msg : messages) {
                try {
                    LinkedHashMap<String,String> polledResult = gson.fromJson(msg.getVariables, LinkedHashMap.class);
                    EpayResponse resp = gson.fromJson(msg.getVariables, EpayResponse.class);
                    Order order = orderManager.getOrderByincrementOrderId(resp.gsordnr);
                    order.payment.callBackParameters = polledResult;
                    
                    boolean valid = true;
                    if(!validCallBack(msg.getVariables)) {
                        order.payment.transactionLog.put(System.currentTimeMillis(), "Invalid callback from epay callback: " + msg.id + " : " + msg.getVariables);
                        messageManager.sendErrorNotification("Invalid callback from epay callback: " + msg.id + " : " + msg.getVariables, null);
                        valid = false;
                    }
                    
                    
                    saveCardOnUser(order);
                    
                    Double amount = new Double(resp.amount) / 100;
                    Double total = orderManager.getTotalAmount(order);
                    if(!amount.equals(total)) {
                        order.payment.transactionLog.put(System.currentTimeMillis(), "Amount does not match the order (security problem): " + msg.id + " : " + msg.getVariables);
                        messageManager.sendErrorNotification("Amount does not match the order (security problem): " + msg.id + " : " + msg.getVariables, null);
                        valid = false;
                    }
                    if(valid) {
                        order.payment.transactionLog.put(System.currentTimeMillis(), "Payment completion (EPAY) : " + resp.txnid);
                        order.captured = true;
                        orderManager.markAsPaid(order.id, new Date(), amount);
                    }
                    orderManager.markOrderForAutoSending(order.id);
                    orderManager.saveObject(order);
                }catch(Exception e) {
                    messageManager.sendErrorNotification("Failed to parse json for epay callback: " + msg.id + " : " + msg.getVariables, e);
//                    getShopPullService.markMessageAsReceived(msg.id, storeId);
                    e.printStackTrace();
                }
                getShopPullService.markMessageAsReceived(msg.id, storeId);
            }
        }catch(Exception e) {
            logPrintException(e);
        }
    }
    
    private void saveCardOnUser(Order order) {
        User user = userManager.getUserById(order.userId);
        if(user == null) {
            logPrint("Failed to find user?" + order.userId);
            return;
        }

        String subid = order.payment.callBackParameters.get("subscriptionid");
        if(subid == null || subid.isEmpty()) {
            return;
        }
        
        UserCard card = new UserCard();
        card.card = subid;
        card.savedByVendor = "EPAY";
        card.mask = order.payment.callBackParameters.get("cardno");
        user.savedCards.add(card);
        userManager.saveUser(user);
    }
    
    private String getCallBackId() {
        Application epayApp = storeApplicationPool.getApplicationWithSecuredSettings("8f5d04ca-11d1-4b9d-9642-85aebf774fee");
        if(epayApp == null) {
            return null;
        }
        Setting setting = epayApp.settings.get("merchantid");
        if(setting == null) {
            return null;
        }
        return setting.value;
    }
    
    
    private boolean validCallBack(String variables) {
        Gson gson = new Gson();
        Type typeOfHashMap = new TypeToken<Map<String, String>>() { }.getType();
        Map<String, String> newMap = gson.fromJson(variables, typeOfHashMap);
        String toCheck = "";
        String hash = "";
        for(String key : newMap.keySet()) {
            if(key.equals("hash")) {
                hash = newMap.get(key);
                continue;
            }
            toCheck += newMap.get(key);
        }
        toCheck += getPassword();
        
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(toCheck.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            String hashtext = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashtext.length() < 32 ){
              hashtext = "0"+hashtext;
            }
            
            if(hashtext.equals(hash)) {
                return true;
            }
        }catch(Exception e) {
            return false;
        }
        return false;
    }
    
    private String getPassword() {
        Application epayApp = storeApplicationPool.getApplicationWithSecuredSettings("8f5d04ca-11d1-4b9d-9642-85aebf774fee");
        Setting setting = epayApp.settings.get("md5secret");
        return setting.value;
    }
    
    private String getApiPassword() {
        Application epayApp = storeApplicationPool.getApplicationWithSecuredSettings("8f5d04ca-11d1-4b9d-9642-85aebf774fee");
        Setting setting = epayApp.settings.get("apipassword");
        return setting.value;
    }

    public boolean payWithCard(Order order, UserCard card) {
        System.out.println("Trying to pay with card: " + card.mask);
        return capture(card, order);
    }
    
    public boolean capture(UserCard card, Order order) {
        boolean result = false;
         try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            String url = "https://ssl.ditonlinebetalingssystem.dk/remote/subscription.asmx";
            
            //This needs to be fetched from settings.
            String password = getApiPassword();
            String merchant = getCallBackId();
            
            SOAPMessage capturerequest = createCaptureRequest(card, merchant, order.incrementOrderId, orderManager.getTotalAmount(order), password);
            SOAPMessage soapResponse = soapConnection.call(capturerequest, url);
            
            
            // Process the SOAP Response
            if(successfulCapture(soapResponse)) {
                order.status = Order.Status.PAYMENT_COMPLETED;
                order.payment.transactionLog.put(System.currentTimeMillis(), "Successfully paid card.");
                order.captured = true;
                result= true;
            } else {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    soapResponse.writeTo(out);
                    order.payment.transactionLog.put(System.currentTimeMillis(), out.toString());
                }
            }
            orderManager.saveOrder(order);
            soapConnection.close();
        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request to Server");
            e.printStackTrace();
        }        
        return result;
    }
    
    
    private static SOAPMessage createCaptureRequest(UserCard card, String merchantNumber, Long orderId, Double amount, String password) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        int amountToAdd = (int)(amount * 100);
        
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody soapBody = envelope.getBody();
        SOAPElement captured = soapBody.addChildElement("authorize", "", "https://ssl.ditonlinebetalingssystem.dk/remote/subscription");
        captured.addChildElement("merchantnumber").setTextContent(merchantNumber);
        captured.addChildElement("subscriptionid").setTextContent(card.card);
        captured.addChildElement("orderid").setTextContent(orderId + "");
        captured.addChildElement("amount").setTextContent(amountToAdd + "");
        captured.addChildElement("instantcapture").setTextContent("1");
        captured.addChildElement("currency").setTextContent("578");
        captured.addChildElement("pwd").setTextContent(password);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "https://ssl.ditonlinebetalingssystem.dk/remote/subscription/authorize");

        soapMessage.saveChanges();

        return soapMessage;
    }

    /**
     * Method used to print the SOAP Response
     */
    private boolean successfulCapture(SOAPMessage soapResponse) throws Exception {
        SOAPBody body = soapResponse.getSOAPBody();
        Iterator childs = body.getChildElements();
        while (childs.hasNext()) {
            Node node = (Node) childs.next();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element ele = (Element) node;
                switch (ele.getNodeName()) {
                    case "authorizeResponse":
                        NodeList childs2 = ele.getChildNodes();
                        for(int i = 0; i < childs2.getLength(); i++) {
                            Node node2 = (Node) childs2.item(i);
                            if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                Element ele2 = (Element) node2;
                                if (ele2.getNodeName().equals("authorizeResult")) {
                                    if (ele2.getTextContent().equals("true")) {
                                        return true;
                                    }
                                }
                            }
                            
                        }
                }
            }
        }

        return false;

    }

    
}

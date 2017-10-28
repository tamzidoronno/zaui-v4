/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshoppaymentservice;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.common.ApplicationInstance;
import com.thundashop.core.getshop.data.DibsAutoCollectData;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

/**
 *
 * @author boggi
 */
public class PaymentProcessor {

    private final PasswordDatabase db;
    private String token = "";
    private int tokenCount = 0;
    private XmlRpcClient client;

    PaymentProcessor(PasswordDatabase db) {
        this.db = db;
    }
    
    public static void main(String[] args) throws Exception {
        String rcode = "1509024761";
        PaymentProcessor processor = new PaymentProcessor(null);
        processor.fetchCreditCard(rcode, "1498895137", "GM187", "ms9uhp40", "432fdsvcxvxcvdsfg455DD213");
    }

    void run() throws Exception {
        int i = 0;
        while(true) {
            db.checkIfNeedRefresh();
            i++;
            if(i > 60) {
                i = 0;
                LogPrinter.println("wtf");
                checkIfOrdersToRequirePayments();
            }
            Thread.sleep(1000);
        }
    }

    private void checkIfOrdersToRequirePayments() throws Exception {
        String sessid = UUID.randomUUID().toString();
        GetShopApi api = new GetShopApi(4224, "www.getshop.com", sessid, "www.getshop.com");
        api.getUserManager().logOn("post@getshop.com", "g4kkg4kk");
        List<DibsAutoCollectData> toCollect = api.getGetShop().getOrdersToAutoPayFromDibs();
        for(DibsAutoCollectData collect : toCollect) {
            PasswordUser user = db.getUser(collect.storeId);
            if(user == null) {
                String text = "Payment server has no user set up for order: " + collect.orderId + " for storeid: " + collect.storeId;
                api.getMessageManager().sendMail("post@getshop.com", "post@getshop.com", "User not set up", text, "post@getshop.com", "post@getshop.com");
                LogPrinter.println(text);
            } else {
                String sessid2 = UUID.randomUUID().toString();
                GetShopApi api2 = new GetShopApi(4224, user.hostname, sessid2, user.hostname);
                api.getUserManager().logOn(user.getshopUsername, user.getshopPassword);
                Order ord = api2.getOrderManager().getOrder(collect.orderId);
                if(chargeOrder(ord, api2, user)) {
                    api.getOrderManager().markAsPaid(ord.id, new Date());
                } else {
                    ord.payment.paymentType = "ns_d02f8b7a_7395_455d_b754_888d7d701db8\\Dibs";
                    api.getOrderManager().saveOrder(ord);
                }
            }
        }
        api.transport.close();
    }


    private boolean chargeOrder(Order ord, GetShopApi api, PasswordUser user) throws Exception {
        PmsConfiguration config = api.getPmsManager().getConfiguration(user.engine);
        List<ApplicationInstance> dibsAutoCollectApps = api.getStoreApplicationInstancePool().getApplicationInstances("688c382f-5995-456e-9385-9a724b9b9351");
        ApplicationInstance app = null;
        if(dibsAutoCollectApps.isEmpty()) {
            ord.payment.transactionLog.put(System.currentTimeMillis(), "Dibs autocollect not set up properly");
            api.getOrderManager().saveOrder(ord);
            return false;
        } else {
            app = dibsAutoCollectApps.get(0);
        }
        String merchantId = app.getSetting("merchantid");
        
        CreditCard card = fetchCreditCard(ord.wubookid, config.wubooklcode, config.wubookusername, config.wubookpassword, user.wubookCreditCardPassword);
        if(card == null) {
            ord.payment.transactionLog.put(System.currentTimeMillis(), "Credit card not found, switching to dibs.");
            api.getOrderManager().saveOrder(ord);
            return false;
        }
        DibsCommunicator dibs = new DibsCommunicator();
        double amount = api.getOrderManager().getTotalAmount(ord);
        HashMap<String, String> responseMap = dibs.chargeCard(card.cardNumber, card.cvv, card.expMonth, card.expYear, ord.incrementOrderId, merchantId, amount + "", false);

        ord.payment.callBackParameters = responseMap;
        
        boolean result = false;
        if(responseMap.get("status") == null) {
            ord.payment.transactionLog.put(System.currentTimeMillis(), "Invalid response when chargin order: " + ord.incrementOrderId);
        }
        if(responseMap.get("status").equals("accept")) {
            api.getOrderManager().markAsPaid(ord.id, new Date());
            result = true;
        } else {
            ord.payment.transactionLog.put(System.currentTimeMillis(), "Failed to handle order");
        }

        return result;
    }

    public CreditCard fetchCreditCard(String wubookid, String wubooklcode, String wubookusername, String wubookpassword, String ccPassword) throws Exception {
        connectToApi(wubookusername, wubookpassword);
        Vector params = new Vector();
        params.addElement(token); 
        params.addElement(wubooklcode);
        params.addElement(wubookid);
        params.addElement(ccPassword);
        Vector result = executeClient("fetch_ccard", params);
        if((int)result.get(0) == 0) {
            Hashtable table = (Hashtable) result.get(1);
            CreditCard card = new CreditCard();
            card.cardNumber = (String) table.get("cc_number");
            card.cvv = (String) table.get("cc_cvv");
            String expire = (String) table.get("cc_expiring");
            String[] expiring = expire.split("/");
            card.expMonth = expiring[0];
            card.expYear = expiring[1];
            return card;
        }
        LogPrinter.println("RESUlT");
        return null;
    }

    private boolean connectToApi(String wubookusername, String wubookpassword) throws Exception {
        
        if(tokenCount < 30 && token != null && !token.isEmpty()) {
            tokenCount++;
            return true;
        }
        
        client = new XmlRpcClient("https://wubook.net/xrws/");

        Vector<String> params = new Vector<String>();
        params.addElement(wubookusername);
        params.addElement(wubookpassword);
        params.addElement("823y8vcuzntzo_o201");
        Vector result = executeClient("acquire_token", params);
        
        Integer response = (Integer) result.get(0);
        token = (String) result.get(1);
        tokenCount = 0;
        if (response == 0) {
            return true;
        }
        try {
            LogPrinter.println("Failed to connect to api, " + response);
            LogPrinter.println("Failed to connect to api, " + result.get(1));
        }catch(Exception e) {
            
        }

        return false;
    }
    
    
    private Vector executeClient(String apicall, Vector<String> params) throws XmlRpcException, IOException {
        LogPrinter.println("Executing api call: " + apicall);
        try {
            Vector res = (Vector) client.execute(apicall, params);
            return res;
        }catch(ConnectException e) {
            LogPrinter.println("Could not connect to wubook on api call: " + apicall + " message: " + e.getMessage());
        }
        return null;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopstripe;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stripe.exception.CardException;
import com.stripe.model.Card;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

/**
 *
 * @author boggi
 */
public class GetShopPaymentProcessor {
    private List<AccountDetails> accounts = new ArrayList();
    private AccountDetails currentAccount;
    private boolean isDevMode = false;
    private boolean printDebug = true;
    private XmlRpcClient client;
    private String token = "";
    private int tokenCount = 0;
    
    public static void main(String args[]) {
        try {
            System.out.println("Starting process");
            GetShopPaymentProcessor processor = new GetShopPaymentProcessor();
            processor.start();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void start() throws IOException {
        while(true) {
            readAccounts();
            Date d = new Date();
            logPrint("start", d.toString() + " : Running autocarding for " + accounts.size() + " accounts");
            for(AccountDetails detail : accounts) {
                token=null;
                currentAccount = detail; 
               if(isDevMode) {
                    currentAccount.address = currentAccount.address.replace(".getshop", ".3.0.local.getshop");
                }
                try {
                    checkForCardsToSave();
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
            if(isDevMode) {
                break;
            }
            try { Thread.sleep(60000);} catch(Exception e) {
                System.out.println("sleep failed");
                break;
            }
        }
    }

    private void readAccounts() throws IOException {
        accounts = new ArrayList();
        String content = new String(Files.readAllBytes(Paths.get("accounts.txt")));
        String[] lines = content.split("\n");
        for(String line : lines) {
            if(line.startsWith("//")) {
                continue;
            }
            String[] args = line.split(";-;");
            AccountDetails detail = new AccountDetails();
            detail.StripeLiveKey = args[0];
            detail.address = args[1];
            detail.apiUsername = args[2];
            detail.apiPassword = args[3];
            detail.wubookCCardPassword = args[4];
            detail.wubookUsername = args[5];
            detail.wubookPassword = args[6];
            detail.wubookLCode = args[7];
            detail.multilevelName = args[8];
            accounts.add(detail);
        }
    }

    private void checkForCardsToSave() throws IOException, Exception {
        //First find new bookings
        LinkedList<PmsWubookCCardData> bookings = fetchBookings();
        if(bookings.size() > 0) {
            logPrint("checkForCardsToSave", "Found " + bookings.size() + " bookings at " + currentAccount.address);
        }
        
        //Fetch credit card
        for(PmsWubookCCardData booking : bookings) {
            CreditCard ccard = fetchCreditCard(booking.reservationCode);
            if(ccard == null) {
                logPrint("checkForCardsToSave", "Did not find credit card: for booking : " + booking.bookingId);
                continue;
            }
            logPrint("checkForCardsToSave", "Found credit card: " + ccard.expMonth + " / " + ccard.expYear + " for booking : " + booking.bookingId);
            
            String key = currentAccount.StripeLiveKey;
            if(isDevMode) {
                key = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
            }
            
            //Save credit card
            GetShopStripe stripe = new GetShopStripe();
            logPrint("checkForCardsToSave", "Trying to save card into stripe " + booking.bookingId);
            try {
                UserCard savedCard = stripe.saveCard(ccard.cardNumber, new Integer(ccard.expMonth), new Integer(ccard.expYear), key, booking.email);
                saveCardInGetShop(savedCard, booking);
                notifyChargeBooking(booking);
            }catch(CardException e) {
                //Notify invalid card.
                notifyInvalidCard(booking, e.getMessage());
            }
        }
    }

    
    public CreditCard fetchCreditCard(String wubookid) throws Exception {
        
        if(isDevMode) {
            CreditCard tmpCard = new CreditCard();
            tmpCard.cardNumber = "4242424242424242";
            tmpCard.cvv = "123";
            tmpCard.expMonth = "02";
            tmpCard.expYear = "35";
            return tmpCard;
        }
        int i = 0;
        while(true) {
            boolean connected = connectToApi(currentAccount.wubookUsername, currentAccount.wubookPassword);
            i++;
            if(connected) {
                break;
            } else {
                logPrint("fetchCreditCard", "Failed to connect to wubook, waiting and retrying");
            }
            try { Thread.sleep(60000); }catch(Exception e) {}
        }
        
        Vector params = new Vector();
        params.addElement(token); 
        params.addElement(currentAccount.wubookLCode);
        params.addElement(wubookid);
        params.addElement(currentAccount.wubookCCardPassword);
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
        } else {
            logPrint("fetchCreditCard", "Failed to fetch credit card");
        }
        return null;
    }
    private boolean connectToApi(String wubookusername, String wubookpassword) throws Exception {
        
        if(tokenCount < 10 && token != null && !token.isEmpty()) {
            tokenCount++;
            return true;
        }
        
        client = new XmlRpcClient("https://wired.wubook.net/xrws/");

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
            logPrint("connectToApi", "Failed to connect to api, " + response);
            logPrint("connectToApi", "Failed to connect to api, " + result.get(1));
        }catch(Exception e) {
            
        }

        return false;
    }
    
    private Vector executeClient(String apicall, Vector<String> params) throws XmlRpcException, IOException {
        logPrint("executeClient", "Executing api call: " + apicall);
        try {
            Vector res = (Vector) client.execute(apicall, params);
            return res;
        }catch(ConnectException e) {
            logPrint("executeClient", "Could not connect to wubook on api call: " + apicall + " message: " + e.getMessage());
        }
        return null;
    }
    
    private String doApiCall(String method, String interfaceAddr, HashMap<String, String> args) throws UnsupportedEncodingException, IOException {
        HttpClient httpclient = HttpClients.createDefault();
        String protocol = "https";
        if(isDevMode) {
            protocol = "http";
        }
        String addr = protocol+"://"+currentAccount.address+"/scripts/api.php";
        HttpPost httppost = new HttpPost(addr);

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("username", "\"" + currentAccount.apiUsername + "\""));
        params.add(new BasicNameValuePair("password","\"" +  currentAccount.apiPassword+"\""));
        params.add(new BasicNameValuePair("method",method));
        params.add(new BasicNameValuePair("multiLevelName",currentAccount.multilevelName));
        params.add(new BasicNameValuePair("interfaceName",interfaceAddr));
        if(args != null && !args.isEmpty()) {
            Gson gson = new Gson();
            params.add(new BasicNameValuePair("getshop_fdsaf3234234fdsafsadbfdargsdfsahjoiuwenflksadnfueirhnb",gson.toJson(args)));
        }
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        
        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        String content = "";
        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                // do something useful
                BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                while(true) {
                    String line = br.readLine();
                    if(line == null) {
                        break;
                    }
                    content += line;
                }
            }
        }
        return content;
    }

    private void logPrint(String method, String string) {
        if(printDebug) {
            System.out.println(string + " (" + method + ")");
        }
    }

    private LinkedList<PmsWubookCCardData> fetchBookings() throws IOException {
        HashMap<String, String> args = new HashMap();
        String content = doApiCall("getCardsToSave", "core.pmsmanager.IPmsManager", args);
        if(content == null || content.equals("null")) {
            return new LinkedList();
        }
        Type listType = new TypeToken<LinkedList<PmsWubookCCardData>>() {}.getType();

        LinkedList<PmsWubookCCardData> res = new LinkedList();
        Gson gson = new Gson();
        res = gson.fromJson(content, listType);
        return res;
    }

    private void saveCardInGetShop(UserCard savedCard, PmsWubookCCardData booking) throws IOException {
        logPrint("saveCardInGetShop", "Saving stripe card on getshopuser for booking " + booking.bookingId + " card: " + savedCard.id);
        Gson gson = new Gson();
        LinkedHashMap<String, String> args = new LinkedHashMap();
        args.put("userId", gson.toJson(booking.userId));
        args.put("card", gson.toJson(savedCard));
        doApiCall("addCardToUser", "core.usermanager.IUserManager", args);
    }

    private void notifyChargeBooking(PmsWubookCCardData booking) throws IOException {
        logPrint("saveCardInGetShop", "Notifying getshop about try to charge card on user " + booking.bookingId);
        Gson gson = new Gson();
        LinkedHashMap<String, String> args = new LinkedHashMap();
        args.put("bookingId", booking.bookingId);
        doApiCall("doChargeCardFromAutoBooking", "core.pmsmanager.IPmsManager", args);
    }

    private void notifyInvalidCard(PmsWubookCCardData booking, String message) throws IOException {
        logPrint("saveCardInGetShop", "Notifying about invalid credit card " + booking.bookingId);
        Gson gson = new Gson();
        LinkedHashMap<String, String> args = new LinkedHashMap();
        
        if(message== null) {
            message = "uknown reason";
        }
        
        args.put("bookingId", booking.bookingId);
        args.put("reason", gson.toJson(message));
        doApiCall("wubookCreditCardIsInvalid", "core.pmsmanager.IPmsManager", args);
    }
}

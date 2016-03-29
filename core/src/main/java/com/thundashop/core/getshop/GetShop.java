package com.thundashop.core.getshop;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSessionScope;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.data.GetshopStore;
import com.thundashop.core.getshop.data.Partner;
import com.thundashop.core.getshop.data.PartnerData;
import com.thundashop.core.getshop.data.SmsResponse;
import com.thundashop.core.getshop.data.StartData;
import com.thundashop.core.getshop.data.WebPageData;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class GetShop extends ManagerBase implements IGetShop {

    private HashMap<String, List<Partner>> partners;
    private HashMap<String, PartnerData> partnerData = new HashMap();
    private ConcurrentHashMap<String, SmsResponse> smsResponses = new ConcurrentHashMap();

    
    @Autowired
    public Database database;

    @Autowired
    public StorePool storePool;
    
    @Autowired
    public FrameworkConfig frameworkConfig;
    
    @Autowired
    private MailFactory mailFactory;
    
    @Override
    public List<GetshopStore> getStores(String code) {
        if (!code.equals("laskdjf034j523lknjksdjnfklqnwgflkjangfasnlkjqnwtwpdsfguoq32905htkasjdfklsdfnaksldth342o5hiy4n4vnyesfjn")) {
            return new ArrayList();
        }

        throw new RuntimeException("This function is not available yet in 2.0.0");
    }

    @PostConstruct
    public void init() {
        isSingleton = true;
        storeId = "all";
        partners = new HashMap();
        initialize();
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon obj : data.data) {
            if (obj instanceof Partner) {
                Partner toAdd = (Partner) obj;
                List<Partner> partnerList = getPartnerList(toAdd.partnerId);
                partnerList.add(toAdd);
            }
            if(obj instanceof PartnerData) {
                PartnerData pdata = (PartnerData)obj;
                partnerData.put(pdata.partnerId, pdata);
            }
            if(obj instanceof SmsResponse) {
                SmsResponse pdata = (SmsResponse)obj;
                smsResponses.put(pdata.id, pdata);
            }
        }
    }

    private void addUserInformation(GetshopStore getshopstore, Store store) {
        Credentials credentials = new Credentials(UserManager.class);
        credentials.manangerName = "UserManager";
        credentials.storeid = store.id;
        getshopstore.userAgents = new ArrayList();


        List<DataCommon> users = database.retreiveData(credentials);
        for (DataCommon duser : users) {
            if (duser instanceof User) {
                User user = (User) duser;
                if (user.type == User.Type.ADMINISTRATOR) {
                    getshopstore.email = user.emailAddress;
                    getshopstore.lastLoggedIn = user.lastLoggedIn;
                    getshopstore.username = user.username;
                }
                if (user.type == User.Type.ADMINISTRATOR) {
                    if (user.hasChrome) {
                        getshopstore.hasChrome = true;
                    }
                    getshopstore.userAgents.add(user.userAgent);
                }
            }
        }
    }

    @Override
    public void addUserToPartner(String userId, String partner, String password) throws ErrorException {

        if (!password.equals("feerfdafeerfdsaf433453bvcbhgftrhyer56346453465346")) {
            return;
        }

        removeFromPartnersIfExists(userId);
        List<Partner> partnerList = getPartnerList(partner);

        Partner newPartner = new Partner();
        newPartner.partnerId = partner;
        newPartner.userId = userId;

        partnerList.add(newPartner);
        newPartner.storeId = storeId;
        databaseSaver.saveObject(newPartner, credentials);
    }

    private void removeFromPartnersIfExists(String userId) {
        HashMap<String, List<Partner>> partners = getPartnerList();
        Partner toRemove = null;
        for (List<Partner> userList : partners.values()) {
            for (Partner partner : userList) {
                if (partner.userId.equals(userId)) {
                    toRemove = partner;
                }
            }

            if (toRemove != null) {
                userList.remove(toRemove);
            }
        }
    }

    private HashMap<String, List<Partner>> getPartnerList() {
        if (partners == null) {
            partners = new HashMap();
        }

        return partners;
    }

    private List<Partner> getPartnerList(String partner) {
        List<Partner> partnerList = partners.get(partner);
        if (partnerList == null) {
            partnerList = new ArrayList();
            partners.put(partner, partnerList);
        }
        return partnerList;
    }

    public boolean isPartner(String partnerId, String userId) {
        for (Partner partner : getPartnerList(partnerId)) {
            if (partner.userId.equals(userId)) {
                return true;
            }
        }

        return false;
    }

    private Partner findPartnerForUser(String userId) {
        HashMap<String, List<Partner>> partnerList = getPartnerList();
        for (List<Partner> partners : partnerList.values()) {
            for (Partner partner : partners) {
                if (partner.userId.equals(userId)) {
                    return partner;
                }
            }
        }
        
        return null;
    }

    @Override
    public String findAddressForUUID(String uuid) throws ErrorException {
        DataCommon obj = database.findObject(uuid, null);
        Store store = (Store) database.findObject(obj.storeId, "StoreManager");
        return store.webAddress;
    }

    @Override
    public String findAddressForApplication(String uuid) throws ErrorException {
        DataCommon obj = database.findObject(uuid, "PageManager");
        DataCommon store = database.findObject(obj.storeId, "StoreManager");
        Store toreturn = (Store) store;
        return toreturn.webAddress;
    }
    

    @Override
    public void setApplicationList(List<String> ids, String partnerId, String password) throws ErrorException {
        if(!password.equals("vcxubidnsituenguidnskgjnsdg")) {
            throw new ErrorException(26);
        }
        
        PartnerData data = getPartnerDataInternal(partnerId);
        data.applications = ids;
        savePartnerData(data);
    }

    @Override
    public PartnerData getPartnerData(String partnerId, String password) throws ErrorException {
        if(!password.equals("vcxubidnsituenguidnskgjnsdg")) {
            return null;
        }
        PartnerData data = getPartnerDataInternal(partnerId);
        return data;
    }

    public List<String> getPartnerApplicationList(String partnerid) {
        PartnerData partner = getPartnerDataInternal(partnerid);
        return partner.applications;
    }

    private PartnerData getPartnerDataInternal(String partnerid) {
        PartnerData result = partnerData.get(partnerid);
        if(result == null) {
            result = new PartnerData();
            result.partnerId = partnerid;
        }
        return result;
    }

    private void savePartnerData(PartnerData data) throws ErrorException {
        partnerData.put(data.partnerId, data);
        data.storeId = storeId;
        databaseSaver.saveObject(data, credentials);
    }

    public String getPartnerId(String userId) {
        Partner partner = findPartnerForUser(userId);
        if(partner != null) {
            return partner.partnerId;
        }
        return null;
    }
    
    @Override
    public synchronized Store createWebPage(WebPageData data) throws ErrorException {
        String local = frameworkConfig.productionMode ? "" : ".local";
        if(storePool == null) {
            System.out.println("Storepool null?");
        }
        
        User user = new User();
        user.fullName = data.fullName;
        user.password = data.password;
        user.emailAddress = data.emailAddress;
        user.username = data.emailAddress;
        
        String address = storePool.incrementStoreCounter()+local+".getshop.com";
        Store store = storePool.createStoreObject(address, data.emailAddress, data.password, true);
        store.registrationUser = user;
        return store;
    }

    @Override
    public String startStoreFromStore(StartData startData) {
        Store store = storePool.getStore(startData.storeId);
        if (store == null) {
            return "";
        }
        
        if (!store.isTemplate) {
            throw new ErrorException(26);
        }
        
        
        int nextStoreId = storePool.incrementStoreCounter();
        
        String newAddress = nextStoreId + ".getshop.com";
        if (!frameworkConfig.productionMode) {
            newAddress = nextStoreId + ".2.0.local.getshop.com";
        }
        
        
        try {
            String newStoreId = copyStore(startData.storeId, newAddress, startData);
            storePool.loadStore(newStoreId, newAddress);
            
            GetShopSessionScope scope = AppContext.appContext.getBean(GetShopSessionScope.class);
            User user = createUser(startData, newStoreId, newAddress);
            
            scope.setStoreId(newStoreId, "", null);
            UserManager userManager = AppContext.appContext.getBean(UserManager.class);
            userManager.saveUser(user);
            
            OrderManager orderManager = AppContext.appContext.getBean(OrderManager.class);
            orderManager.clear();
            
            saveCustomerToGetShop(user, scope);
            if (startData.language.equals("nb_NO")) {
                sendWelcomeEmailNorwegian(startData, newAddress);
            } else {
                sendWelcomeEmailEnglish(startData, newAddress);
            }
           
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            return "";
        } catch (ErrorException ex) {
            throw ex;
        }
       
        
        
        return newAddress;
    }
    
    public String copyStore(String originalStoreId, String newAddress, StartData start) throws UnknownHostException {
        Database database = AppContext.appContext.getBean(Database.class);
        String newStoreId = UUID.randomUUID().toString();
        
        Mongo m = new MongoClient("localhost", Database.mongoPort);

        for (String databaseName : m.getDatabaseNames()) {
            if (databaseName.equals("LoggerManager") || databaseName.equals("UserManager")) {
                continue;
            }

            String storeId = databaseName.equals("StoreManager") ? "all" : originalStoreId;
            String newStoreIdi = databaseName.equals("StoreManager") ? "all" : newStoreId;
            DB db = m.getDB(databaseName);
            DBCollection collection = db.getCollection("col_" + storeId);
            DBCursor stores = collection.find();

            Credentials cred = new Credentials(null);
            cred.manangerName = databaseName;
            cred.password =  newStoreId;
            cred.storeid = newStoreId;
            
            while (stores.hasNext()) {
                DBObject data = stores.next();
                Morphia morphia = new Morphia();
                morphia.map(DataCommon.class);
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, data);
                dataCommon.storeId = newStoreId;
                
                if (dataCommon instanceof Store) {
                    Store store = (Store)dataCommon;
                    
                    if (!store.id.equals(originalStoreId)) {
                        continue;
                    }
                    
                    store.webAddressPrimary = newAddress;
                    store.webAddress = null;
                    store.storeId = newStoreIdi;
                    store.id = newStoreId;
                    store.isTemplate = false;
                    store.configuration.emailAdress = start.email;
                    store.configuration.shopName = start.shopName;
                    
                    if (start.color != null && !start.color.equals("")) {
                        store.configuration.selectedColorTemplate = start.color;
                    }
                    
                    Calendar cal = Calendar.getInstance(); 
                    cal.add(Calendar.MONTH, 1);
                    store.expiryDate = cal.getTime();
                    
                    store.additionalDomainNames = new ArrayList();
                    database.save(store, cred);
                } else {
                    database.save(dataCommon, cred);
                }
            }
        }
        
        m.close();
        
        return newStoreId;
    }

    private User createUser(StartData startData, String storeId, String webshopAddress) {
        User user = new User();
        user.id = storeId;
        user.fullName = startData.name;
        user.emailAddress = startData.email;
        user.cellPhone = startData.phoneNumber;
        user.password = startData.password;
        user.address = new Address();
        user.address.fullName = startData.name;
        user.address.emailAddress = startData.email;
        user.address.address = webshopAddress;
        user.address.phone = user.cellPhone;
        return user;
    }

    private void saveCustomerToGetShop(User user, GetShopSessionScope scope) {
        String getshopStoreId = "efad5b1f-b679-4c2b-8774-8c2475c20137";
        User customer = user.jsonClone();
        customer.type = User.Type.CUSTOMER;

        scope.setStoreId(getshopStoreId, "", null);
        UserManager getShopUserManager = AppContext.appContext.getBean(UserManager.class);
        getShopUserManager.saveCustomerDirect(customer);
    }

    private void sendWelcomeEmailNorwegian(StartData startData, String newAddress) {
        String emailTemplate = "";
        emailTemplate += "<html >";
        emailTemplate += "    <head>";
        emailTemplate += "        <meta http-equiv='content-type' content='text/html; charset=UTF-8'>";
        emailTemplate += "        <meta charset='UTF-8'>";
        emailTemplate += "        ";
        emailTemplate += "        <link rel='stylesheet' type='text/css' href='http://fonts.googleapis.com/css?family=Ubuntu:regular,bold&subset=Latin'>";
        emailTemplate += "    </head>";
        emailTemplate += "    <body style='margin: 0px; padding: 0px;'>";
        emailTemplate += "        <div style='min-width: 100%; min-height: 100%; background-color: #419bcd; text-align: center; font-family: Ubuntu, sans-serif;'>";
        emailTemplate += "        <br/>";
        emailTemplate += "        <center>";
        emailTemplate += "            <img src='data:image/png;base64,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'/>";
        emailTemplate += "        </center>";
        emailTemplate += "        <br/>";
        emailTemplate += "        ";
        emailTemplate += "        <div style='background-color: #FFF; border-radius: 10px; width: 500px; text-align: center; padding: 20px; margin: 0 auto; text-align: left; color: #333;'>";
        emailTemplate += "            <h1 style='text-align: center;'>Velkommen til GetShop </h1>";
        emailTemplate += "            <br/> Vi i GetShop ønsker deg velkommen til din nye plattform, GetShop har som mål at du alltid skal være fornøyd!";
        emailTemplate += "            <br/> ";
        emailTemplate += "            <br/> <b>Ditt nettsted er nå klart</b>";
        emailTemplate += "            <br/> Addresse: http://"+newAddress;
        emailTemplate += "            <br/> Admin login : http://"+newAddress + "/login.php";
        emailTemplate += "            <br/> Brukernavn: " + startData.email;
        emailTemplate += "            <br/> Passord: " + startData.password;
        emailTemplate += "            <br/> ";
        emailTemplate += "            <br/> Skriv ut denne eposten og lager den på et trygt sted, emailboksen bør ikke brukes som en lagringsplass for eposter av sikkerhetsmessige grunner.";
        emailTemplate += "            <br/> ";
        emailTemplate += "            <br/> <b>Trenger du hjelp til å komme i gang?</b>";
        emailTemplate += "            <br/> Slapp av, vi hjelper deg i gang og det koster deg ikke noe ekstra. Dette gjør vi fordi det ikke er så vanskelig som du skulle tro. Ta kontakt så gir vi deg noen hint om hvordan du kan komme i mål med dine ønsker og behov.";
        emailTemplate += "            <br/> ";
        emailTemplate += "            <br/> <b>Dokumentasjon:</b>";
        emailTemplate += "            <br/> Dette finner du ved å klikke på <a target='_blank' href='https://no.getshop.com/dokumentasjon.html'>linken her</a>";
        emailTemplate += "            <br/> ";
        emailTemplate += "            <br/> <b>Hilsen oss i GetShop</b>";
        emailTemplate += "            <br/> Epost: post@getshop.com";
        emailTemplate += "            <br/> Tlf: +47 940 10 704";
        emailTemplate += "            <br/> Org nr: 912 574 784 MVA";
        emailTemplate += "            <br/>";
        emailTemplate += "            <br/> Addresse:";
        emailTemplate += "            <br/> Strandgaten 21";
        emailTemplate += "            <br/> 4380 Hauge I Dalane";
        emailTemplate += "        </div>";
        emailTemplate += "        <br/><div style='color: #173d56'> A piece of GetShop @ <a href='http://www.getshop.com'>www.getshop.com</a></div><br/><br/>";
        emailTemplate += "        </div>";
        emailTemplate += "    </body>";
        emailTemplate += "</html>";

        
        mailFactory.send("post@getshop.com", "post@getshop.com", "Din GetShop side er nå klar", emailTemplate);
        mailFactory.send("post@getshop.com", startData.email, "Din GetShop side er nå klar", emailTemplate);
    }
    
    private void sendWelcomeEmailEnglish(StartData startData, String newAddress) {
        String emailTemplate = "";
        emailTemplate += "<html >";
        emailTemplate += "    <head>";
        emailTemplate += "        <meta http-equiv='content-type' content='text/html; charset=UTF-8'>";
        emailTemplate += "        <meta charset='UTF-8'>";
        emailTemplate += "        ";
        emailTemplate += "        <link rel='stylesheet' type='text/css' href='http://fonts.googleapis.com/css?family=Ubuntu:regular,bold&subset=Latin'>";
        emailTemplate += "    </head>";
        emailTemplate += "    <body style='margin: 0px; padding: 0px;'>";
        emailTemplate += "        <div style='min-width: 100%; min-height: 100%; background-color: #419bcd; text-align: center; font-family: Ubuntu, sans-serif;'>";
        emailTemplate += "        <br/>";
        emailTemplate += "        <center>";
        emailTemplate += "            <img src='data:image/png;base64,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'/>";
        emailTemplate += "        </center>";
        emailTemplate += "        <br/>";
        emailTemplate += "        ";
        emailTemplate += "        <div style='background-color: #FFF; border-radius: 10px; width: 500px; text-align: center; padding: 20px; margin: 0 auto; text-align: left; color: #333;'>";
        emailTemplate += "            <h1 style='text-align: center;'>Welcome to GetShop </h1>";
        emailTemplate += "            <br/> GetShop welcomes you to your new e-commerce platform!";
        emailTemplate += "            <br/> ";
        emailTemplate += "            <br/> <b>Your ecommerce is ready</b>";
        emailTemplate += "            <br/> Address: http://"+newAddress;
        emailTemplate += "            <br/> Admin login : http://"+newAddress + "/login.php";
        emailTemplate += "            <br/> Username: " + startData.email;
        emailTemplate += "            <br/> Password: " + startData.password;
        emailTemplate += "            <br/> ";
        emailTemplate += "            <br/> Please print this document, your email account should not be used as a secure password box.";
        emailTemplate += "            <br/> ";
        emailTemplate += "            <br/> <b>Need help to get started?</b>";
        emailTemplate += "            <br/> Relax, we will help you and it will not cost you any extra. This is beacuse its not as hard as you might think. Contact us and we can help you to get the best out of your solution.";
        emailTemplate += "            <br/> ";
        emailTemplate += "            <br/> <b>Documentation:</b>";
        emailTemplate += "            <br/> Click on the following link to access our documentation <a target='_blank' href='https://www.getshop.com/documentation.htmll'>Link here</a>";
        emailTemplate += "            <br/> ";
        emailTemplate += "            <br/> <b>Best regards GetShop</b>";
        emailTemplate += "            <br/> Epost: canada@getshop.com";
        emailTemplate += "            <br/> Tlf: +47 940 10 704";
        emailTemplate += "            <br/>";
        emailTemplate += "            <br/> Address:";
        emailTemplate += "            <br/> 106 spruceside cres";
        emailTemplate += "            <br/> fonthill, ontario canada los 1E1";
        emailTemplate += "            <br/> Canada";
        emailTemplate += "        </div>";
        emailTemplate += "        <br/><div style='color: #173d56'> A piece of GetShop @ <a href='http://www.getshop.com'>www.getshop.com</a></div><br/><br/>";
        emailTemplate += "        </div>";
        emailTemplate += "    </body>";
        emailTemplate += "</html>";

        
        mailFactory.send("post@getshop.com", "canada@getshop.com", "Your GetShop account is ready", emailTemplate);
        mailFactory.send("post@getshop.com", startData.email, "Your GetShop account is ready", emailTemplate);
    }

    @Override
    public void saveSmsCallback(SmsResponse response) {
        smsResponses.put(response.id, response);
        response.storeId = "all";
        saveObject(response);
    }

    public SmsResponse getSmsResponse(String msgId) {
        return smsResponses.get(msgId);
    }

    @Override
    public String getBase64EncodedPDFWebPage(String urlToPage) {
        urlToPage = urlToPage.replaceAll("&amp;", "&");
        String tmpPdfName = "/tmp/"+UUID.randomUUID().toString() + ".pdf";
        boolean executed = executeCommand("/usr/local/bin/wkhtmltopdf.sh " + urlToPage + " " + tmpPdfName);
        
        if (!executed) {
            executed = executeCommand("wkhtmltopdf -B 0 -L 0 -R 0 -T 0  " + urlToPage + " " + tmpPdfName);
        }
        
        if (!executed) {
            throw new ErrorException(1033);
        }
        
        File file = new File(tmpPdfName);
        byte[] bytes;
        try {
            bytes = InvoiceManager.loadFile(file);
            byte[] encoded = Base64.encodeBase64(bytes);
            String encodedString = new String(encoded);
            file.delete();
            return encodedString;
        } catch (IOException ex) {
            throw new ErrorException(1033);
        }
    }
    
    
    private boolean executeCommand(String command) {
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String d = "";
            
            boolean ok = true;
            while ((d = stdError.readLine()) != null) {
                if (d.contains("error"))
                    ok = false;
            }
            
            return ok;
        } catch (IOException e) {
            return false;
        }
    }
}
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.appmanager.data.SavedApplicationSettings;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.GetShop;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
public class PmsGetShopOverView extends ManagerBase implements IPmsGetShopOverView {

    @Autowired
    public GetShop GetShop;

    @Autowired
    public Database database;
    
    private List<Application> applist;
    private PmsConfiguration pmsConfiguration;
    
    private HashMap<String, CustomerSetupObject> customers = new HashMap();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof CustomerSetupObject) {
                CustomerSetupObject res = (CustomerSetupObject)dataCommon;
                customers.put(res.customerStoreId, res);
            }
            
        }
    }
    
    @Override
    public List<CustomerSetupObject> getCustomerToSetup() {
        if(!storeId.equals("13442b34-31e5-424c-bb23-a396b7aeb8ca")) { return null; }
            List<Store> allStores = storePool.getAllStores();
        
        List<CustomerSetupObject> objects = new ArrayList();
        Calendar cal = Calendar.getInstance();
        for(Store s : allStores) {
            cal.setTime(s.rowCreatedDate);
            if(cal.get(Calendar.YEAR) < 2018) {
                continue;
            }
            CustomerSetupObject obj = createCustomerObject(s);
            if(!obj.completed) {
                objects.add(obj);
            }
        }
        
        Collections.sort(objects, new Comparator<CustomerSetupObject>(){
            public int compare(CustomerSetupObject o1, CustomerSetupObject o2){
                return o2.storeDate.compareTo(o1.storeDate);
            }
       });
        
        return objects;
    }

    private Store findStore(PmsBookingRooms room, List<Store> allStores) {
        for(Store s : allStores) {
            for(PmsGuests guest : room.guests) {
                
                if(s.webAddress != null && s.webAddress.equals(guest.name)) {
                    return s;
                }
                if(s.webAddressPrimary != null && s.webAddressPrimary.equals(guest.name)) {
                    return s;
                }
                if(s.additionalDomainNames != null && s.additionalDomainNames.contains(guest.name)) {
                    return s;
                }
            }
        }
        return null;
    }

    private CustomerSetupObject createCustomerObject(Store s) {
        pmsConfiguration = null;
        CustomerSetupObject obj = getCustomerObject(s.id);
        obj.customerStoreId = s.id;
        obj.storeDate = s.rowCreatedDate;
        obj.acceptedGdpr = s.acceptedGDPR;
        obj.address = s.webAddress;
        if(obj.address == null || obj.address.isEmpty()) {
            obj.address = s.webAddressPrimary;
        }
        obj.paymentMethodsSetup = getNumberOfPaymentMethods(s);
        obj.numberOfMessagesSetup = getMessageCount(s);
        obj.addedTermsAndConditions = getHasTermsAndConditions(s);
        obj.numberOfCategoriesAdded = getCategories(s).size();
        obj.numberOfRoomsAdded = getRooms(s).size();
        obj.numberOfProductsCreated = getProducts(s).size();
        obj.wubookSetUpState = getWubookState(s);
        obj.numberOfFieldsSetupInInvoice = getNumberOfFieldsInInvoice(s);
        obj.pricesCompleted = checkIfPricesAreOk(s);
        saveCustomerObject(obj);
        return obj;
    }

    private Integer getNumberOfPaymentMethods(Store store) {
        
        List<Application> papps = getPaymentApps();
        List<SavedApplicationSettings> activatedSettings = getActivatedApplications(store);
        List<String> ids = new ArrayList();
        for(Application app : papps) { ids.add(app.id); }
        int counter = 0;
        for(SavedApplicationSettings setting : activatedSettings) {
            if(ids.contains(setting.applicationId)) {
                counter++;
            }
        }
        return counter;
    }

    private List<Application> getApps() {
        if(applist != null) {
            return applist;
        }
        List<DataCommon> appinstances = database.retreiveData(credentials);
        credentials.manangerName = "ApplicationPool";
        credentials.storeid = "all";
        List<DataCommon> apps = database.retreiveData(credentials);
        List<Application> applist = new ArrayList();
        for(DataCommon d : apps) {
            if(d instanceof Application) {
                applist.add((Application) d);
            }
        }
        this.applist = applist;
        return applist;
    }
    
    private List<Application> getPaymentApps() {
        List<Application> list = getApps();
        List<Application> returnlist = new ArrayList();
        for(Application app : list) {
            if(app.type.equals(Application.Type.Payment)) {
                returnlist.add(app);
            }
        }
        return returnlist;
    }

    private List<SavedApplicationSettings> getActivatedApplications(Store store) {
        Credentials credentials = new Credentials(UserManager.class);
        credentials.manangerName = "StoreApplicationPool";
        credentials.storeid = store.id;
        List<DataCommon> apps = database.retreiveData(credentials);
        List<SavedApplicationSettings> pmethods = new ArrayList();
        for(DataCommon data : apps) {
            if(data instanceof SavedApplicationSettings) {
                SavedApplicationSettings activated = (SavedApplicationSettings) data;
                pmethods.add(activated);
            }
        }
        return pmethods;
    }

    private PmsConfiguration getPmsConfiguration(Store s) {
        if(this.pmsConfiguration != null) {
            return this.pmsConfiguration;
        }
        HashMap<String, String> searchCriteria = new HashMap();
        searchCriteria.put("className", "com.thundashop.core.pmsmanager.PmsConfiguration");
        List<DataCommon> apps = database.find("col_" + s.id, null, null, "PmsManager_default", searchCriteria);
        for(DataCommon d : apps) {
            if(d instanceof PmsConfiguration) {
                this.pmsConfiguration = (PmsConfiguration) d;
                return (PmsConfiguration) d;
            }
        }
        return new PmsConfiguration();
    }

    private Integer getMessageCount(Store s) {
        PmsConfiguration config = getPmsConfiguration(s);
        int counter = 0;
        for(String msg : config.emails.values()) {
            if(msg != null && !msg.trim().isEmpty()) {
                counter++;
            }
        }
        for(String msg : config.smses.values()) {
            if(msg != null && !msg.trim().isEmpty()) {
                counter++;
            }
        }
        return counter;
    }

    private boolean getHasTermsAndConditions(Store s) {
        PmsConfiguration config = getPmsConfiguration(s);
        for(String contract : config.contracts.values()) {
            if(contract != null && !contract.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private List<BookingItemType> getCategories(Store s) {
        HashMap<String, String> searchCriteria = new HashMap();
        searchCriteria.put("className", "com.thundashop.core.bookingengine.data.BookingItemType");
        List<DataCommon> apps = database.find("col_" + s.id, null, null, "BookingEngineAbstract_default", searchCriteria);
        List<BookingItemType> result = new ArrayList();
        for(DataCommon d : apps) {
            if(d instanceof BookingItemType) {
                result.add((BookingItemType) d);
            }
        }
        return result;
    }

    private List<BookingItem> getRooms(Store s) {
        HashMap<String, String> searchCriteria = new HashMap();
        searchCriteria.put("className", "com.thundashop.core.bookingengine.data.BookingItem");
        List<DataCommon> apps = database.find("col_" + s.id, null, null, "BookingEngineAbstract_default", searchCriteria);
        List<BookingItem> result = new ArrayList();
        for(DataCommon d : apps) {
            if(d instanceof BookingItem) {
                result.add((BookingItem) d);
            }
        }
        return result;
    }

    private List<Product> getProducts(Store s) {
        HashMap<String, String> searchCriteria = new HashMap();
        searchCriteria.put("className", "com.thundashop.core.productmanager.data.Product");
        List<DataCommon> apps = database.find("col_" + s.id, null, null, "ProductManager", searchCriteria);
        List<Product> result = new ArrayList();
        for(DataCommon d : apps) {
            if(d instanceof Product) {
                result.add((Product) d);
            }
        }
        return result;
    }

    private Integer getWubookState(Store s) {
        PmsConfiguration config = getPmsConfiguration(s);
        int state = 0;
        if((config.wubooklcode != null && !config.wubooklcode.isEmpty()) &&
        (config.wubookusername != null && !config.wubookusername.isEmpty()) &&
        (config.wubookpassword != null && !config.wubookpassword.isEmpty())) {
            state = 1;
        }
        
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -20);
        
        Date date = new Date();
        Date date2 = cal.getTime();
        
        HashMap<String, String> searchCriteria = new HashMap();
        searchCriteria.put("className", "com.thundashop.core.pmsmanager.PmsBooking");
        List<DataCommon> apps = database.find("col_" + s.id, date2, date, "PmsManager_default", searchCriteria);
        for(DataCommon d : apps) {
            if(d instanceof PmsBooking) {
                PmsBooking booking = (PmsBooking)d;
                if(booking.isWubook()) {
                    state = 2;
                    break;
                }
            }
        }
        
        return state;
    }

    private Integer getNumberOfFieldsInInvoice(Store s) {
        int number = 0;
        List<SavedApplicationSettings> activatedSettings = getActivatedApplications(s);
        for(SavedApplicationSettings appsetting : activatedSettings) {
            if(appsetting.applicationId.equals("70ace3f0-3981-11e3-aa6e-0800200c9a66")) {
                for(Setting setting : appsetting.settings.values()) {
                    if(setting.value != null && !setting.value.isEmpty()) {
                        number++;
                    }
                }
                System.out.println("Found");
            }
        }
        return number;
    }

    @Override
    public void saveCustomerObject(CustomerSetupObject object) {
        if(!storeId.equals("13442b34-31e5-424c-bb23-a396b7aeb8ca")) { return; }
        saveObject(object);
        customers.put(object.customerStoreId, object);
    }

    @Override
    public CustomerSetupObject getCustomerObject(String customerStoreId) {
        if(!storeId.equals("13442b34-31e5-424c-bb23-a396b7aeb8ca")) { return null; }
        if(customers.containsKey(customerStoreId)) {
            CustomerSetupObject res = customers.get(customerStoreId);
            if(res != null) {
                return res;
            }
        }
        return new CustomerSetupObject();
    }

    private boolean checkIfPricesAreOk(Store s) {
        HashMap<String, String> searchCriteria = new HashMap();
        searchCriteria.put("className", "com.thundashop.core.pmsmanager.PmsPricing");
        List<DataCommon> apps = database.find("col_" + s.id, null, null, "PmsManager_default", searchCriteria);
        for(DataCommon d : apps) {
            boolean failed = false;
            if(d instanceof PmsPricing) {
                PmsPricing price = (PmsPricing)d;
                if(price.code== null || !price.code.equals("default")) {
                   continue; 
                }
                for(String cat : price.dailyPrices.keySet()) {
                    HashMap<String, Double> catPrice = price.dailyPrices.get(cat);
                    if(catPrice != null) {
                        Double defaultPrice = catPrice.get("default");
                        if(defaultPrice == null || defaultPrice <= 0.0) {
                            failed = true;
                        }
                    }
                }
                return !failed;
            }
        }
        
        return false;
    }

    
}

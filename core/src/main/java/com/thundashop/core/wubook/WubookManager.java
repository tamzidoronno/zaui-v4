package com.thundashop.core.wubook;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.core.productmanager.data.Product;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@GetShopSession
public class WubookManager extends GetShopSessionBeanNamed implements IWubookManager {
    
    private XmlRpcClient client;
    String token = "";
    private HashMap<String, WubookRoomData> wubookdata = new HashMap();
    
    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof WubookRoomData) {
                wubookdata.put(dataCommon.id, (WubookRoomData) dataCommon);
            }
        }
    }
    
    @Override
    public String updateAvailability() throws Exception {
        if(!connectToApi()) { return "Faield to connect to api"; }

        Vector<Hashtable> tosend = new Vector();
        
        for (WubookRoomData rdata : wubookdata.values()) {
            if(!rdata.addedToWuBook) {
                continue;
            }
            
            Hashtable roomToUpdate = new Hashtable();
            roomToUpdate.put("id", rdata.wubookroomid);

            Calendar startcal = Calendar.getInstance();
            startcal.set(Calendar.HOUR_OF_DAY, 18);
            Vector days = new Vector();
            for (int i = 0; i < 365; i++) {
                Date start = startcal.getTime();
                startcal.add(Calendar.DAY_OF_YEAR, 1);
                Date end = startcal.getTime();
                int count = bookingEngine.getNumberOfAvailable(rdata.bookingEngineTypeId, start, end);

                Hashtable result = new Hashtable();
                result.put("avail", count);
                result.put("no_ota", 0);
                days.add(result);
            }
            roomToUpdate.put("days", days);
            tosend.add(roomToUpdate);
        }

        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String todayString = format.format(new Date());

        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(todayString);
        params.addElement(tosend);

        Vector result = (Vector) client.execute("update_rooms_values", params);

        if ((Integer)result.get(0) != 0) {
            System.out.println("Failed to update availability, send mail about it.");
            System.out.println("0:" + result.get(0));
            System.out.println("1:" + result.get(1));
            return (String) result.get(1);
        }
        return "";
    }
    
    private boolean isWubookActive() {
        if(pmsManager.getConfigurationSecure().wubookusername == null || pmsManager.getConfigurationSecure().wubookusername.isEmpty()) {
            return false;
        }
        return true;
    }
    
    
    private boolean connectToApi() throws Exception {
        if(!isWubookActive()) { return false; }
        client = new XmlRpcClient("https://wubook.net/xrws/");
        
        Vector<String> params = new Vector<String>();
        params.addElement(pmsManager.getConfigurationSecure().wubookusername);
        params.addElement(pmsManager.getConfigurationSecure().wubookpassword);
        params.addElement("823y8vcuzntzo_o201");
        Vector result = (Vector) client.execute("acquire_token", params);
        Integer response = (Integer) result.get(0);
        token = (String) result.get(1);
        if (response == 0) {
            return true;
        }
        return false;
    }
    
    @Override
    public boolean testConnection() throws Exception {
        return connectToApi();
    }

    @Override
    public List<String> fetchAllBookings() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> fetchNewBookings() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean fetchBooking(String rcode) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void markNoShow(String rcode) throws Exception {
        if(!isWubookActive()) { return; }
        System.out.println("Setting no show on rcode: " + rcode);
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(rcode);
        client.execute("bcom_notify_noshow", params);
    }

    @Override
    public String updatePrices() throws Exception {
        if(!connectToApi()) {
            return "failed to connect to api";
        }
        Hashtable table = new Hashtable();
        
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String dfrom = format.format(new Date());
        
        for (WubookRoomData rdata : wubookdata.values()) {
            if(!rdata.addedToWuBook) {
                continue;
            }
            Calendar cal = Calendar.getInstance();
            Vector list = new Vector();
            Date now = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, 365*2);
            Date end = cal.getTime();
            PmsPricing prices = pmsManager.getPrices(now, end);
            Calendar calStart = Calendar.getInstance();
            
            HashMap<String, Double> pricesForType = prices.dailyPrices.get(rdata.bookingEngineTypeId);
            Double price = pricesForType.get("default");
            
            for(int i = 0;i < (365*2); i++) {
                int year = calStart.get(Calendar.YEAR);
                int month = calStart.get(Calendar.MONTH)+1;
                int day = calStart.get(Calendar.DAY_OF_MONTH);
                String dateString = "";
                
                if(day < 10) { dateString += "0" + day; } else { dateString += day; }
                dateString += "-";
                if(month < 10) { dateString += "0" + month; } else { dateString += month; }
                dateString += "-" + year;
                
                if(pricesForType.containsKey(dateString)) {
                    price = pricesForType.get(dateString);
                }
                list.add(price.intValue());
                calStart.add(Calendar.DAY_OF_YEAR, 1);
            }
            table.put(rdata.wubookroomid + "", list);
        }
        
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(0);
        params.addElement(dfrom);
        params.addElement(table);

        Vector result = (Vector) client.execute("update_plan_prices", params);
        if((Integer)result.get(0) != 0) {
            return "Failed to update price, " + result.get(1);
        }
        return "";
    }

    @Override
    public void markCCInvalid(String rcode) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> insertAllRooms() throws Exception {
        if(!connectToApi()) {
            return new ArrayList();
        }
        
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        List<String> errors = new ArrayList();
        for (BookingItemType type : types) {
            String added = "";
            WubookRoomData data = getWubookRoomData(type.id);
            if(data.addedToWuBook) {
                added = updateRoom(type);
            } else {
                added = insertRoom(type);
            }
            
            if(!added.isEmpty()) {
                errors.add(added);
            }
        }
        return errors;
    }

    private WubookRoomData getWubookRoomData(String typeid) {
        WubookRoomData res = null;
        for(WubookRoomData rdata : wubookdata.values()) {
            if(rdata.bookingEngineTypeId.equals(typeid)) {
                res = rdata;
            }
        }
        if(res == null) {
            WubookRoomData newData = new WubookRoomData();
            newData.bookingEngineTypeId = typeid;
            for(int i = 10; i < 1000; i++) {
                if(!codeInUse(i)) {
                    newData.code = i;
                    saveObject(newData);
                    wubookdata.put(newData.id, newData);
                    return newData;
                }
            }
        }
        return res;
    }

    private boolean codeInUse(int i) {
        for(WubookRoomData rdata : wubookdata.values()) {
            if(rdata.code == i) {
                return true;
            }
        }
        return false;
    }

    private String insertRoom(BookingItemType type) throws XmlRpcException, IOException {
        List<BookingItem> items = bookingEngine.getBookingItemsByType(type.id);
        WubookRoomData rdata = getWubookRoomData(type.id);
        Vector<String> params = new Vector<String>();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement("0");
        params.addElement(type.name);
        params.addElement(type.capacity + "");
        params.addElement("9999");
        params.addElement(items.size() + "");
        params.addElement("r" + rdata.code);
        params.addElement("nb");
        Vector result = (Vector) client.execute("new_room", params);
        Integer response = (Integer) result.get(0);
        String res = "";
        if(response == 0) {
            rdata.addedToWuBook = true;
            rdata.wubookroomid = (Integer)result.get(1);
            saveObject(rdata);
            System.out.println("Succesfully added room");
        } else {
            res = result.toString();
        }
        return res;
    }

    private String updateRoom(BookingItemType type) throws XmlRpcException, IOException {
        List<BookingItem> items = bookingEngine.getBookingItemsByType(type.id);
        WubookRoomData rdata = getWubookRoomData(type.id);
        Vector<String> params = new Vector<String>();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(rdata.wubookroomid + "");
        params.addElement(type.name);
        params.addElement(type.capacity + "");
        params.addElement("9999");
        params.addElement(items.size() + "");
        params.addElement("r" + rdata.code);
        params.addElement("nb");
        Vector result = (Vector) client.execute("mod_room", params);
        String res = "";
        Integer response = (Integer) result.get(0);
        if(response == 0) {
            System.out.println("Succesfully updated room");
        } else {
            res = result.toString();
        }
        return res;
    }
}

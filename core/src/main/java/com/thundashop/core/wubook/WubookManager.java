package com.thundashop.core.wubook;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingComment;
import com.thundashop.core.pmsmanager.PmsBookingDateRange;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsGuests;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pmsmanager.PmsPricing;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
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
    public List<WubookBooking> fetchAllBookings(Integer daysBack) throws Exception {
        if(!connectToApi()) {
            return new ArrayList();
        }
        
        if(daysBack == null) {
            daysBack = 10;
        }
        
        System.out.println("Verifying all bookings");
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, daysBack*-1);
        
        String to = format.format(new Date());
        String from = format.format(cal.getTime());
        
        params.addElement(from);
        params.addElement(to);
        System.out.println("Finding bookings from: " + from + " to -> " + to);
        Vector result = (Vector) client.execute("fetch_bookings", params);
        List<WubookBooking> toReturn = new ArrayList();
        if((Integer)result.get(0) != 0) {
            System.out.println("Failed to fetch all reservations: " + result.get(1));
        } else {
            Vector getAllBookings = (Vector) result.get(1);
            
            for(int bookcount = 0; bookcount < getAllBookings.size(); bookcount++) {
                Hashtable reservation = (Hashtable) getAllBookings.get(bookcount);
                WubookBooking wubooking = buildBookingResult(reservation);
                toReturn.add(wubooking);
            }
        }
        return toReturn;
    }
    
    private WubookBooking buildBookingResult(Hashtable table) {
        WubookBooking booking = new WubookBooking();

        String arrival = (String) table.get("date_arrival");
        String departure = (String) table.get("date_departure");
        booking.channelId = table.get("id_channel") + "";
        booking.reservationCode = table.get("reservation_code") + "";
        booking.channel_reservation_code = (String) table.get("channel_reservation_code");
        boolean delete = false;
        Integer status = (Integer) table.get("status");
        if(status == 5) {
            delete = true;
        }
        booking.delete = delete;

        Vector rooms_occupancies = (Vector) table.get("rooms_occupancies");
        booking.name = table.get("customer_name") + " " + table.get("customer_surname");
        booking.address = (String) table.get("customer_address");
        booking.postCode = (String) table.get("customer_zip");
        booking.city = (String) table.get("customer_city");
        booking.email = (String) table.get("customer_mail");
        booking.wasModified = (Integer)table.get("was_modified");
        booking.phone = (String) table.get("customer_phone");
        booking.customerNotes = getCustomerNotes(table);
        booking.breakfast = checkForBreakfast(table);

        String countryCode = (String) table.get("customer_country");
        if (countryCode == null || countryCode.equals("--") || countryCode.isEmpty()) {
            countryCode = "NO";
        }
        booking.countryCode = countryCode;

        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        Date arrivalDate = null;
        Date depDate = null;
        try {
            arrivalDate = format.parse(arrival);
            depDate = format.parse(departure);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        booking.arrivalDate = arrivalDate;
        booking.depDate = depDate;

        Vector bookedRooms = (Vector) table.get("booked_rooms");

        Iterator roomIterator = bookedRooms.iterator();
        int roomNumber = 0;
        while (roomIterator.hasNext()) {
            WubookBookedRoom room = new WubookBookedRoom();
            int guest = 1;
            if (rooms_occupancies.size() > roomNumber) {
                Hashtable occupancyForRoom = (Hashtable) rooms_occupancies.get(roomNumber);
                guest = (int) occupancyForRoom.get("occupancy");
            }

            Hashtable roomtable = (Hashtable) roomIterator.next();
            Integer roomId = (Integer) roomtable.get("room_id");
            room.guest = guest;
            room.roomId = roomId;
            booking.rooms.add(room);
            roomNumber++;
        }
        return booking;
    }
    

    @Override
    public List<WubookBooking> fetchNewBookings() throws Exception {
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(1);
        params.addElement(1);

        Vector result = (Vector) client.execute("fetch_new_bookings", params);
        List<WubookBooking> toReturn = new ArrayList();
        if (!result.get(0).equals(0)) {
            System.out.println("0:" + result.get(0));
            System.out.println("1:" + result.get(1));
            throw new Exception();
        } else {
            Vector bookings = (Vector) result.get(1);
            for(int bookcount = 0; bookcount < bookings.size(); bookcount++) {
                Hashtable reservation = (Hashtable) bookings.get(bookcount);
                WubookBooking wubooking = buildBookingResult(reservation);
                toReturn.add(wubooking);
                addBookingToPms(wubooking);
            }
        }
        return toReturn;
    }

    @Override
    public WubookBooking fetchBooking(String rcode) throws Exception {
        if(!connectToApi()) {
            return null;
        }
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(rcode);
        params.addElement(true);

        Vector result = (Vector) client.execute("fetch_booking", params);
        Vector res = (Vector) result.get(1);
        return buildBookingResult((Hashtable) res.get(0));
    }

    @Override
    public String markNoShow(String rcode) throws Exception {
        if(!connectToApi()) { return "Failed to connect to ap"; }
        System.out.println("Setting no show on rcode: " + rcode);
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(rcode);
        Vector noshow = (Vector) client.execute("bcom_notify_noshow", params);
        if(!noshow.get(0).equals(0)) {
            return noshow.toString();
        }
        return "";
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
    public String markCCInvalid(String rcode) throws Exception {
        if(!connectToApi()) { return "Failed to connect to ap"; }
        System.out.println("Setting no show on rcode: " + rcode);
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(rcode);
        Vector noshow = (Vector) client.execute("bcom_notify_invalid_cc", params);
        if(!noshow.get(0).equals(0)) {
            return noshow.toString();
        }
        return "";
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

    
    private boolean checkForBreakfast(Hashtable table) {
        Vector addons = (Vector) table.get("addons_list");
        Iterator itr = addons.iterator();
        while(itr.hasNext()) {
            Hashtable addontable = (Hashtable) itr.next();
            if(addontable.containsKey("name")) {
                String name = (String) addontable.get("name");
                if(name.toLowerCase().contains("breakfast")) {
                    return true;
                }
            }
        }
        
        if((int)table.get("id_channel") == 1 && (int)table.get("booked_rate") == -1) {
            return true;
        }
        
        return false;
    }
    
    private String getCustomerNotes(Hashtable table) {
        String customerNotes = (String) table.get("customer_notes");
        if (customerNotes == null) {
            customerNotes = "";
        }

        customerNotes = customerNotes.replace("--", "");
        customerNotes = customerNotes.replace("--- No-CC reservation (no credit card needed, none provided) ---", "");
        customerNotes = customerNotes.replace("Upper-storey room request: this booker requests upper-storey room(s) - based on availability", "");
        customerNotes = customerNotes.replace("You have a booker that would prefer a quiet room. (based on availability)", "");
        customerNotes = customerNotes.replace("You have a booker that would prefer a quiet room.", "");
        customerNotes = customerNotes.replace("Non-Smoking, 1 double bed", "");
        customerNotes = customerNotes.replace("Non-Smoking, 2 double beds", "");
        customerNotes = customerNotes.replace("*** Genius booker ***", "");
        customerNotes = customerNotes.replace("Multi-room booking. Primary traveler", "");
        customerNotes = customerNotes.replace("1 double bed, Non-Smoking", "");
        customerNotes = customerNotes.replace("Ground-level room request: this booker requests ground-level room(s) - based on availability", "");
        customerNotes = customerNotes.trim();

        return customerNotes;
    }

    @Override
    public void addBooking(String rcode) throws Exception {
        WubookBooking booking = fetchBooking(rcode);
        addBookingToPms(booking);
    }

    private void addBookingToPms(WubookBooking booking) throws Exception {
        PmsBooking newbooking = pmsManager.startBooking();
        newbooking.wubookchannelid = booking.channelId;
        newbooking.wubookchannelreservationcode = booking.channel_reservation_code;
        newbooking.wubookreservationid = booking.reservationCode;
        newbooking.countryCode = booking.countryCode;
        if(booking.customerNotes != null && !booking.customerNotes.isEmpty()) {
            PmsBookingComment comment = new PmsBookingComment();
            comment.userId = "";
            comment.comment = booking.customerNotes;
            comment.added = new Date();
            newbooking.comments.put(System.currentTimeMillis(), comment);
        }
        
        newbooking.registrationData.resultAdded.put("user_fullName", booking.name);
        newbooking.registrationData.resultAdded.put("user_cellPhone", booking.phone);
        newbooking.registrationData.resultAdded.put("user_address_address", booking.address);
        newbooking.registrationData.resultAdded.put("user_address_city", booking.city);
        newbooking.registrationData.resultAdded.put("user_emailAddress", booking.email);
        newbooking.registrationData.resultAdded.put("user_address_postCode", booking.postCode);
        
        for(WubookBookedRoom r : booking.rooms) {
            PmsBookingRooms room = new PmsBookingRooms();
            room.date = new PmsBookingDateRange();
            room.date.start = setCorrectTime(booking.arrivalDate, true);
            room.date.end = setCorrectTime(booking.depDate, false);
            room.numberOfGuests = r.guest;
            room.bookingItemTypeId = getTypeFromWubookRoomId(r.roomId);
            
            PmsGuests guest = new PmsGuests();
            guest.email = booking.email;
            guest.name = booking.name;
            guest.phone = booking.phone;
            room.guests.add(guest);
            newbooking.rooms.add(room);
        }
        
        pmsManager.setBooking(newbooking);
        
        pmsManager.completeCurrentBooking();
    }

    private String getTypeFromWubookRoomId(int roomId) {
        for(WubookRoomData rdata : wubookdata.values()) {
            if(rdata.wubookroomid == roomId) {
                return rdata.bookingEngineTypeId;
            }
        }
        return null;
    }

    private Date setCorrectTime(Date arrivalDate, boolean start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(arrivalDate);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String starttime = pmsManager.getConfigurationSecure().defaultStart;
        if(!start) {
            starttime = pmsManager.getConfigurationSecure().defaultEnd;
        } 
        
        String[] starting = starttime.split(":");
        cal.set(Calendar.HOUR_OF_DAY, new Integer(starting[0]));
        cal.set(Calendar.MINUTE, new Integer(starting[1]));
        
        return cal.getTime();
    }
}

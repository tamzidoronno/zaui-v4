package com.thundashop.core.wubook;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.gson.Gson;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.pmsmanager.NewOrderFilter;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import com.thundashop.core.pmsmanager.PmsBookingComment;
import com.thundashop.core.pmsmanager.PmsBookingDateRange;
import com.thundashop.core.pmsmanager.PmsBookingFilter;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsGuests;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pmsmanager.PmsPricing;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
    private HashMap<String, WubookAvailabilityRestrictions> restrictions = new HashMap();
    private Date availabilityHasBeenChanged = null;
    private Date availabilityLastUpdated = null;
    SavedLastAvailibilityUpdate lastAvailability = new SavedLastAvailibilityUpdate();
    
    private WubookLog log = new WubookLog();

    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    PmsInvoiceManager pmsInvoiceManager;
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    FrameworkConfig frameworkConfig;
    
    @Autowired
    MessageManager messageManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof WubookRoomData) {
                wubookdata.put(dataCommon.id, (WubookRoomData) dataCommon);
            }
            if(dataCommon instanceof WubookAvailabilityRestrictions) {
                restrictions.put(dataCommon.id, (WubookAvailabilityRestrictions) dataCommon);
            }
            if(dataCommon instanceof WubookLog) {
                log = (WubookLog) dataCommon;
            }
            if(dataCommon instanceof SavedLastAvailibilityUpdate) {
                lastAvailability = (SavedLastAvailibilityUpdate) dataCommon;
            }
        }
        
        createScheduler("wubookprocessor", "* * * * *", WuBookManagerProcessor.class);
        createScheduler("wubookprocessor2", "1 1,5,7,9,12,18,22 * * *", WuBookHourlyProcessor.class);
    }
    
    @Override
    public String updateAvailability() throws Exception {
        if(storeId.equals("87cdfab5-db67-4716-bef8-fcd1f55b770b")) {
            return sparseUpdateAvailabilityInternal();
        }
        return updateAvailabilityInternal(730);
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
        try {
            logText("Failed to connect to api, " + response);
            logText("Failed to connect to api, " + result.get(1));
        }catch(Exception e) {
            
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
        
        daysBack *= -1;
        
        logPrint("Verifying all bookings");
        List<WubookBooking> toReturn = fetchBookings(daysBack, true);
        return toReturn;
    }

    @Override
    public List<Integer> fetchBookingCodes(Integer daysBack) throws Exception {
        if(!connectToApi()) {
            return new ArrayList();
        }
        
        if(daysBack == null) {
            daysBack = 10;
        }
        
        logPrint("Verifying all bookings");
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, daysBack*-1);
        
        Calendar stop = Calendar.getInstance();
        stop.add(Calendar.DAY_OF_YEAR, 1);
        
        String to = format.format(stop.getTime());
        String from = format.format(cal.getTime());
        
        params.addElement(from);
        params.addElement(to);
        logPrint("Finding bookings from: " + from + " to -> " + to);
        Vector result = (Vector) client.execute("fetch_bookings_codes", params);
        List<Integer> toReturn = new ArrayList();
        if((Integer)result.get(0) != 0) {
            logText("Failed to fetch all reservations: " + result.get(1));
        } else {
            Vector getAllBookings = (Vector) result.get(1);
            
            for(int bookcount = 0; bookcount < getAllBookings.size(); bookcount++) {
                Hashtable reservation = (Hashtable) getAllBookings.get(bookcount);
                toReturn.add((Integer) reservation.get("reservation_code"));
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
        booking.status = new Integer(table.get("status") + "");
        booking.isExpediaCollect = checkExpediaCollect(table);
        booking.isNonRefundable = checkNonRefundable(table);
        Vector modifications = (Vector) table.get("modified_reservations");
        if(modifications != null) {
            for(int i = 0; i < modifications.size(); i++) {
                booking.modifiedReservation.add((Integer) modifications.get(i));
            }
        }
        boolean delete = false;
        Integer status = (Integer) table.get("status");
        Integer wasModifield = (Integer)table.get("was_modified");
        if(status == 5 && wasModifield == 0) {
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
            room.guestName = getGuestName(roomNumber, table);
            room.roomId = roomId;
            room.breakfasts = checkForBreakfast(roomtable, table, guest);
            booking.rooms.add(room);
            
            if(pmsManager.getConfigurationSecure().usePricesFromChannelManager) {
                Vector roomdays = (Vector) roomtable.get("roomdays");
                Iterator roomDaysIterator = roomdays.iterator();
                SimpleDateFormat intoDate = new SimpleDateFormat("dd-MM-yyyy");
                while(roomDaysIterator.hasNext()) {
                    Hashtable roomday = (Hashtable) roomDaysIterator.next();
                    Double dayprice = (Double) roomday.get("price");
                    String day = (String) roomday.get("day");
                    try {
                        Date date = sdf.parse(day);
                        room.priceMatrix.put(date, dayprice);
                    }catch(Exception e) {
                        logPrintException(e);
                    }
                }
            }
            roomNumber++;
        }
        return booking;
    }
    

    @Override
    public List<WubookBooking> fetchNewBookings() throws Exception {
        if(pmsManager.getConfigurationSecure().wubooklcode == null ||
                pmsManager.getConfigurationSecure().wubooklcode.isEmpty()) {
            return new ArrayList();
        }
        connectToApi();
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(1);
        params.addElement(1);
        
        Vector result = (Vector) client.execute("fetch_new_bookings", params);
        List<WubookBooking> toReturn = new ArrayList();
        if (!result.get(0).equals(0)) {
            logText("0:" + result.get(0));
            logText("1:" + result.get(1));
            Exception ex = new Exception();
            logPrintException(ex);
        } else {
            Vector bookings = (Vector) result.get(1);
            for(int bookcount = 0; bookcount < bookings.size(); bookcount++) {
                Hashtable reservation = (Hashtable) bookings.get(bookcount);
                WubookBooking wubooking = buildBookingResult(reservation);
                if(wubooking.status == 5) {
                    if(wubooking.wasModified > 0) {
                        //This is a modified reservation. its not a new booking.
                        //This happends if the booking has been modified since last time we checked for new bookings.
                        PmsBooking correlatedBooking = findCorrelatedBooking(wubooking);
                        if(correlatedBooking != null) {
                            correlatedBooking.wubookModifiedResId.add(wubooking.reservationCode);
                            pmsManager.saveBooking(correlatedBooking);
                        } else {
                            sendErrorForReservation(wubooking.reservationCode, "Where not able to find correlated booking for modified booking while fetching new bookings.");
                        }
                        continue;
                    }
                }
                try {
                    Gson gson = new Gson();
                    logPrint(gson.toJson(reservation));
                }catch(Exception e) {
                    logPrintException(e);
                }
                if(!bookingAlreadyExists(wubooking) || wubooking.delete) {
                    toReturn.add(wubooking);
                    addBookingToPms(wubooking);
                }
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

        if(numberOfBookingsHavingWuBookId(rcode) != 1) {
            sendErrorForReservation(rcode, "Failed to mark booking as no show since there are multiple bookings related to this one... should not happen, number of bookings found: " + numberOfBookingsHavingWuBookId(rcode));
            return "";
        }

        logPrint("Setting no show on rcode: " + rcode);
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
        if(!frameworkConfig.productionMode) { return ""; }
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
                if(price != null) {
                    list.add(price.intValue());
                }
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
        logPrint("Setting no show on rcode: " + rcode);
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
            wubookdata.put(newData.id, newData);
            res = newData;
            saveObject(newData);
        }
        
        if(res.code < 0) {
            for(int i = 10; i < 1000; i++) {
                if(!codeInUse(i)) {
                    res.code = i;
                    saveObject(res);
                    break;
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

    private String insertRoom(BookingItemType type) throws XmlRpcException, IOException, Exception {
        if(!connectToApi()) { return "Faield to connect to api"; }
        List<BookingItem> items = bookingEngine.getBookingItemsByType(type.id);
        WubookRoomData rdata = getWubookRoomData(type.id);
        Vector<String> params = new Vector<String>();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement("0");
        params.addElement(type.name);
        params.addElement(type.size + "");
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
            logPrint("Succesfully added room");
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
            logPrint("Succesfully updated room");
        } else {
            res = result.toString();
        }
        return res;
    }

    
    private int checkForBreakfast(Hashtable table, Hashtable bookingtable, int guests) {
        Hashtable addons = (Hashtable) table.get("ancillary");
        Vector addonsList = (Vector) addons.get("addons");
        if(addonsList != null) {
            Iterator roomIterator = addonsList.iterator();
            while (roomIterator.hasNext()) {
                Hashtable addon = (Hashtable) roomIterator.next();
                String name = (String) addon.get("name");
                String type = (String) addon.get("type");
                
                if(type != null && (type.toLowerCase().contains("breakfast") || type.toLowerCase().contains("frokost"))) {
                    return (int) addon.get("persons");
                }
                
                if(name != null && (name.toLowerCase().contains("breakfast") || name.toLowerCase().contains("frokost"))) {
                    return (int) addon.get("persons");
                }
            }
        }
        
        if(addons != null) {
            String channel = (String) addons.get("channel_rate_name");
            if(channel != null) {
                if(channel.toLowerCase().contains("breakfast included") || channel.toLowerCase().contains("frokost inkludert")) {
                    return guests;
                }
            }
        }
        
        return 0;
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

    private String addBookingToPms(WubookBooking booking) throws Exception {
        PmsBooking newbooking = null; 

        long start = System.currentTimeMillis();
        boolean isUpdate = false;
        if(booking.modifiedReservation.size() > 0 && !booking.delete) {
            newbooking = findCorrelatedBooking(booking);
            if(newbooking == null) {
                sendErrorForReservation(booking.reservationCode, "Could not find existing booking for a modification on reservation");
            } else {
                pmsManager.logEntry("Modified by channel manager", newbooking.id, null);
                for(PmsBookingRooms room : newbooking.getActiveRooms()) {
                    pmsManager.removeFromBooking(newbooking.id, room.pmsBookingRoomId);
                }
                isUpdate = true;
            }
        } else if(booking.delete) {
            newbooking = findCorrelatedBooking(booking);
            if(newbooking == null) {
                sendErrorForReservation(booking.reservationCode, "Could not find deleted booking for a modification on reservation");
                return "Did not find booking to delete.";
            } else {
                pmsManager.deleteBooking(newbooking.id);
            }
            
            NewOrderFilter filter = new NewOrderFilter();
            filter.avoidOrderCreation = false;
            filter.createNewOrder = false;
            filter.prepayment = true;
            filter.endInvoiceAt = newbooking.getEndDate();
            pmsInvoiceManager.createOrder(newbooking.id,filter);
            
            return "";
        }
        
        if(newbooking == null) {
            newbooking = pmsManager.startBooking();
        }
        
        newbooking.channel = "wubook_" + booking.channelId;
        newbooking.wubookchannelreservationcode = booking.channel_reservation_code;
        if(!isUpdate) {
            newbooking.wubookreservationid = booking.reservationCode;
        }
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
        newbooking.nonrefundable = booking.isNonRefundable;
        
        Calendar calStart = Calendar.getInstance();
        
        HashMap<String,HashMap<Date, Double>> pricestoset = new HashMap();
        
        for(WubookBookedRoom r : booking.rooms) {
            PmsBookingRooms room = new PmsBookingRooms();
            room.date = new PmsBookingDateRange();
            room.date.start = setCorrectTime(booking.arrivalDate, true);
            room.date.end = setCorrectTime(booking.depDate, false);
            room.numberOfGuests = r.guest;
            room.bookingItemTypeId = getTypeFromWubookRoomId(r.roomId);
            pricestoset.put(room.pmsBookingRoomId, r.priceMatrix);
            PmsGuests guest = new PmsGuests();
            guest.email = booking.email;
            guest.name = booking.name;
            guest.phone = booking.phone;
            
            if(r.guestName != null && !r.guestName.isEmpty()) {
                guest.name = r.guestName;
            }
            room.guests.add(guest);
            newbooking.addRoom(room);
        }
        if(booking.isExpediaCollect) {
            newbooking.paymentType = "92bd796f-758e-4e03-bece-7d2dbfa40d7a";
        }
        pmsManager.setBooking(newbooking);
        int i = 0;
        for(PmsBookingRooms room : newbooking.getActiveRooms()) {
            WubookBookedRoom r = booking.rooms.get(i);
            if(r.breakfasts > 0) {
                pmsManager.addAddonsToBookingWithCount(PmsBookingAddonItem.AddonTypes.BREAKFAST, room.pmsBookingRoomId, false, r.breakfasts);
            }
            i++;
        }
        pmsInvoiceManager.clearOrdersOnBooking(newbooking);
        newbooking = pmsManager.doCompleteBooking(newbooking);
        
        boolean doNormalPricing = true;
        if(newbooking.channel != null && newbooking.channel.equals("wubook_1")) {
           if(pmsManager.getConfigurationSecure().useGetShopPricesOnExpedia) {
               doNormalPricing = false;
           }
        }
        
        if(pmsManager.getConfigurationSecure().usePricesFromChannelManager && newbooking != null && doNormalPricing) {
            Date end = new Date();
            for(String pmsId : pricestoset.keySet()) {
                PmsBookingRooms pmsroom = newbooking.findRoom(pmsId);
                if(pmsroom.date.end.after(end)) {
                    end = pmsroom.date.end;
                }
                HashMap<Date, Double> priceMatrix = pricestoset.get(pmsId);
                double total = 0.0;
                int count = 0;
                for(Date daydate : priceMatrix.keySet()) {
                    calStart.setTime(daydate);
                    String offset = PmsBookingRooms.getOffsetKey(calStart, PmsBooking.PriceType.daily);
                    pmsroom.priceMatrix.put(offset, priceMatrix.get(daydate));
                    total += priceMatrix.get(daydate);
                    count++;
                }
                pmsroom.price = (total / (double)count);
            }
            pmsManager.saveBooking(newbooking);
            NewOrderFilter filter = new NewOrderFilter();
            filter.createNewOrder = false;
            filter.prepayment = true;
            filter.endInvoiceAt = end;
            pmsInvoiceManager.clearOrdersOnBooking(newbooking);
            pmsInvoiceManager.createOrder(newbooking.id, filter);
        }
        
        logPrint("Time takes to complete one booking: " + (System.currentTimeMillis() - start));
        return "";
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

    @Override
    public String deleteBooking(String rcode) throws Exception {
        WubookBooking booking = fetchBooking(rcode);
        booking.delete = true;
        return addBookingToPms(booking);
    }

    @Override
    public HashMap<String, WubookRoomData> getWubookRoomData() {
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        for(BookingItemType type : types) {
            boolean found = false;
            for(WubookRoomData rdata : wubookdata.values()) {
                if(rdata.bookingEngineTypeId.equals(type.id)) {
                    found = true;
                }
            }
            
            if(!found) {
                WubookRoomData newRdata = new WubookRoomData();
                newRdata.bookingEngineTypeId = type.id;
                saveObject(newRdata);
                wubookdata.put(newRdata.id, newRdata);
            }
        }
        
        
        return wubookdata;
    }

    @Override
    public void saveWubookRoomData(HashMap<String, WubookRoomData> res) {
        for(WubookRoomData data : res.values()) {
            if(data.wubookroomid != null && data.wubookroomid > 0 && !data.addedToWuBook) {
                data.addedToWuBook = true;
            }
            saveObject(data);
            wubookdata.put(data.id, data);
        }
    }

    @Override
    public List<WubookBooking> addNewBookingsPastDays(Integer daysback) throws Exception {
        List<Integer> codes = fetchBookingCodes(daysback);
        List<WubookBooking> bookingsAdded = new ArrayList();
        List<PmsBooking> bookings = pmsManager.getAllBookingsFlat();
        for(Integer code : codes) {
            boolean found = false;
            for(PmsBooking booking : bookings) {
                String codeToCheck = code+"";
                if(booking.wubookreservationid.equals(codeToCheck)) {
                    found = true;
                }
                if(booking.wubookModifiedResId.contains(codeToCheck)) {
                    found = true;
                }
            }
            
            if(!found) {
                WubookBooking booking = fetchBooking(code + "");
                addBookingToPms(booking);
                bookingsAdded.add(booking);
            }
        }
        
        return bookingsAdded;
        
    }

    @Override
    public void checkForNoShowsAndMark() throws Exception {
        Calendar cal = Calendar.getInstance();
        
        if(!frameworkConfig.productionMode) { return; }
        if(pmsManager.getConfigurationSecure().ignoreNoShow) { return; }
        PmsBookingFilter filter = new PmsBookingFilter();
        
        if(cal.get(Calendar.HOUR_OF_DAY) < 5) {
            return;
        }
        filter.filterType = "checkin";
        
        cal.add(Calendar.DAY_OF_YEAR, -1);
        
        filter.startDate = cal.getTime();
        filter.endDate = cal.getTime();
        
        List<PmsBooking> booking = pmsManager.getAllBookings(filter);
        for(PmsBooking book : booking) {
            if(book.payedFor) {
                continue;
            }
            boolean arrived = false;
            for(PmsBookingRooms room : book.getActiveRooms()) {
                if(room.checkedin) {
                    arrived = true;
                }
            }
            if(arrived) {
                continue;
            }
            if(book.channel != null && book.channel.contains("wubook")) {
                Long idToMark = new Long(book.wubookreservationid);
                List<String> ids = book.wubookModifiedResId;
                for(String id : ids) {
                    long tmpid = new Long(id);
                    if(tmpid > idToMark) {
                        idToMark = tmpid;
                    }
                }
                markNoShow(idToMark + "");
                book.wubookNoShow = true;
                pmsManager.saveBooking(book);
            }
        }
    }

    @Override
    public void deleteAllRooms() throws Exception {
        for(WubookRoomData room : wubookdata.values()) {
            deleteObject(room);
        }
        wubookdata = new HashMap();
    }

    @Override
    public void doubleCheckDeletedBookings() throws Exception {
        if(!connectToApi()) {
            return;
        }
        
        List<WubookBooking> nextBookings = fetchBookings(3, false);
        for(WubookBooking booking : nextBookings) {
            if(booking.delete) {
                PmsBooking pmsbooking = getPmsBooking(booking.reservationCode);
                if(pmsbooking == null) {
                    continue;
                }
                if(!pmsbooking.isDeleted && !pmsbooking.getActiveRooms().isEmpty()) {
                    boolean needSave = false;
                    for (PmsBookingRooms room : pmsbooking.getActiveRooms()) {
                        if (room.bookingId != null && !room.bookingId.isEmpty() && room.undeletedDate == null) {
                            bookingEngine.deleteBooking(room.bookingId);
                            room.delete();
                            pmsManager.logEntry("Autodeleted room by channel manager:  ", pmsbooking.id, room.bookingId);
                            needSave = true;
                        }
                    }
                    if(needSave) {
                        pmsManager.saveBooking(pmsbooking);
                    }
                }
            }
        }
    }

    private List<WubookBooking> fetchBookings(Integer daysBack, boolean registrations) throws XmlRpcException, IOException {
         Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, daysBack);
        
        String to = format.format(new Date());
        String from = format.format(cal.getTime());
        if(daysBack > 0) {
            params.addElement(to);
            params.addElement(from);
        } else {
            params.addElement(from);
            params.addElement(to);
        }
        
        if(registrations) {
            params.addElement(1);
        } else {
            params.addElement(0);
        }
        logPrint("Finding bookings from: " + from + " to -> " + to);
        Vector result = (Vector) client.execute("fetch_bookings", params);
        List<WubookBooking> toReturn = new ArrayList();
        if((Integer)result.get(0) != 0) {
            logPrint("Failed to fetch all reservations: " + result.get(1));
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

    private PmsBooking getPmsBooking(String reservationCode) {
        List<PmsBooking> allBookings = pmsManager.getAllBookings(null);
        for(PmsBooking book : allBookings) {
            if(book.wubookreservationid != null && book.wubookreservationid.equals(reservationCode)) {
                return book;
            }
        }
        return null;
    }

    private String getGuestName(int roomNumber, Hashtable table) {
        try {
            Hashtable addons = (Hashtable) table.get("ancillary");
            if(addons == null) {
                return "";
            }


            for(Object key : addons.keySet()) {
                if(key == null) {
                    continue;
                }
                if(key.toString().contains("Room (" + roomNumber + ")")) {
                    Hashtable roomAnc = (Hashtable) addons.get(key);
                    if(roomAnc != null) {
                        String guest = (String) roomAnc.get("Guest");
                        if(guest != null) {
                            return guest;
                        }
                    }
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        return "";
    }

    private List<Integer> getAllResCodesForPmsBooking(PmsBooking pmsbook) {
        List<Integer> result = new ArrayList();
        if(pmsbook.wubookreservationid != null && !pmsbook.wubookreservationid.isEmpty()) {
            result.add(new Integer(pmsbook.wubookreservationid));
        }
        for(String modified : pmsbook.wubookModifiedResId) {
            if(modified != null && !modified.isEmpty()) {
                result.add(new Integer(modified));
            }
        }
        return result;
    }

    @Override
    public void addRestriction(WubookAvailabilityRestrictions restriction) {
        saveObject(restriction);
        restrictions.put(restriction.id, restriction);
    }

    @Override
    public void deleteRestriction(String id) {
        WubookAvailabilityRestrictions object = restrictions.get(id);
        restrictions.remove(id);
        deleteObject(object);
    }

    @Override
    public List<WubookAvailabilityRestrictions> getAllRestriction() {
        return new ArrayList(restrictions.values()); 
    }

    private boolean isRestricted(String bookingEngineTypeId, Date start, Date end) {
        for(WubookAvailabilityRestrictions restriction : restrictions.values()) {
            if(restriction.types == null || restriction.types.isEmpty()) {
                continue;
            }
            if(end.before(restriction.start)) {
                continue;
            }
            if(start.after(restriction.end)) {
                continue;
            }
            
            if(restriction.types.contains(bookingEngineTypeId)) {
                System.out.println("Is restricted in time: " + start + " - " +end);
                return true;
            }
        }
        return false;
    }

    private boolean checkNonRefundable(Hashtable table) {
        try {
            Gson gson = new Gson();
            String text = gson.toJson(table);
            text = text.toLowerCase();
            return (text.contains("non refundable") || text.contains("non-refundable"));
        }catch(Exception e) {
        }
        return false;
    }
    
    private boolean checkExpediaCollect(Hashtable table) {
        try {
            Gson gson = new Gson();
            String text = gson.toJson(table);
            text = text.toLowerCase();
            return text.contains("expedia collect");
        }catch(Exception e) {
        }
        return false;
        
        
    }

    private boolean bookingAlreadyExists(WubookBooking wubooking) {
        List<PmsBooking> allBookings = pmsManager.getAllBookings(null);
        for(PmsBooking booking : allBookings){
            if(booking.wubookreservationid.equals(wubooking.reservationCode)) {
                return true;
            }
            if(booking.wubookModifiedResId.contains(wubooking.reservationCode)) {
                return true;
            }
        }
        return false;
    }

    private PmsBooking findCorrelatedBooking(WubookBooking booking) throws Exception {
        PmsBooking newbooking = null;
        List<PmsBooking> allbookings = pmsManager.getAllBookings(null);
        List<Integer> allCodesInNewBooking = getAllResCodesForPmsBooking(booking);

        for(PmsBooking pmsbook : allbookings) {
            List<Integer> allCodesOnOldBooking = getAllResCodesForPmsBooking(pmsbook);
            for(Integer resCode : allCodesOnOldBooking) {
                if(allCodesInNewBooking.contains(resCode)) {
                    newbooking = pmsManager.getBooking(pmsbook.id);
                    newbooking.wubookModifiedResId.add(booking.reservationCode);
                    return newbooking;
                }
            }
        }
        return newbooking;
    }

    public void setAvailabilityChanged() {
        if(availabilityHasBeenChanged == null) {
            availabilityHasBeenChanged = new Date();
        }
    }

    private String updateAvailabilityInternal(int numberOfDays) throws Exception {
        if(availabilityLastUpdated != null && numberOfDays < 700) {
            long diff = System.currentTimeMillis() - availabilityLastUpdated.getTime();
            if(diff < (10*60*1000)) {
                return "";
            }
        }
        availabilityLastUpdated = new Date();

        if(!frameworkConfig.productionMode) { return ""; }
        
        if(!connectToApi()) {
            return "Faield to connect to api"; 
        }
        Vector<Hashtable> tosend = new Vector();
        int toRemove = pmsManager.getConfigurationSecure().numberOfRoomsToRemoveFromBookingCom;
        
        for (WubookRoomData rdata : wubookdata.values()) {
            if(!rdata.addedToWuBook) {
                continue;
            }
            
            Hashtable roomToUpdate = new Hashtable();
            roomToUpdate.put("id", rdata.wubookroomid);

            Calendar startcal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            startcal.set(Calendar.HOUR_OF_DAY, 16);
            Vector days = new Vector();
            for (int i = 0; i < numberOfDays; i++) {
                Date start = startcal.getTime();
                endCal.setTime(startcal.getTime());
                endCal.add(Calendar.HOUR_OF_DAY, 16);
                Date end = startcal.getTime();
                int count = pmsManager.getNumberOfAvailable(rdata.bookingEngineTypeId, start, endCal.getTime());
                if(count > 0) {
                    count -= toRemove;
                }
                if(isRestricted(rdata.bookingEngineTypeId, start, end)) {
                    count = 0;
                }
                
                if(i > 305) {
                    count = 0;
                }
                
                Hashtable result = new Hashtable();
                result.put("avail", count);
                result.put("no_ota", 0);
                days.add(result);
                startcal.add(Calendar.DAY_OF_YEAR, 1);
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

        WubookManagerUpdateThread updateThread = new WubookManagerUpdateThread("update_rooms_values", client, this, params);
        updateThread.start();
        availabilityHasBeenChanged = null;

        return "";    
    }

    private String sparseUpdateAvailabilityInternal() throws Exception {

        if(!frameworkConfig.productionMode) { return ""; }
        
        if(!connectToApi()) {
            return "Faield to connect to api"; 
        }
        Vector<Hashtable> tosend = new Vector();
        int toRemove = pmsManager.getConfigurationSecure().numberOfRoomsToRemoveFromBookingCom;
        List<WubookAvailabilityField> fieldsUpdated = new ArrayList();
        for (WubookRoomData rdata : wubookdata.values()) {
            if(!rdata.addedToWuBook) {
                continue;
            }
            

            Calendar startcal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            startcal.set(Calendar.HOUR_OF_DAY, 16);
            for (int i = 0; i < 720; i++) {
                Date start = startcal.getTime();
                endCal.setTime(startcal.getTime());
                endCal.add(Calendar.HOUR_OF_DAY, 16);
                Date end = startcal.getTime();
                int count = pmsManager.getNumberOfAvailable(rdata.bookingEngineTypeId, start, endCal.getTime());
                if(count > 0) {
                    count -= toRemove;
                }
                if(isRestricted(rdata.bookingEngineTypeId, start, end)) {
                    count = 0;
                }
                
                if(i > 305) {
                    count = 0;
                }
                
                WubookAvailabilityField field = new WubookAvailabilityField();
                field.roomId = rdata.wubookroomid;
                field.availability = count;
                field.date = start;
                field.dateAsString = convertToDayString(start);
                fieldsUpdated.add(field);
                startcal.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        boolean found = false;
        for (WubookRoomData rdata : wubookdata.values()) {
            if(!rdata.addedToWuBook) {
                continue;
            }
            Vector days = new Vector();
            for(WubookAvailabilityField field : fieldsUpdated) {
                if(!field.roomId.equals(rdata.wubookroomid)) {
                    continue;
                }
                if(hasChanged(field)) {
                    Hashtable result = new Hashtable();
                    result.put("avail", field.availability);
                    result.put("date", field.dateAsString);
                    days.add(result);
                }
            }
            if(!days.isEmpty()) {
                Hashtable roomToUpdate = new Hashtable();
                roomToUpdate.put("id", rdata.wubookroomid);
                roomToUpdate.put("days", days);
                tosend.add(roomToUpdate);
                found = true;
            }
        }
        
        if(found) {
            Gson gson = new Gson();
            String toPrintToLog = gson.toJson(tosend);
            logText(toPrintToLog);
            Vector params = new Vector();
            params.addElement(token);
            params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
            params.addElement(tosend);
            
            Vector result = (Vector) client.execute("update_sparse_rooms_values", params);
            if ((Integer)result.get(0) != 0) {
                logText("Failed to update availability " + "(" + result.get(0) + ")" + result.get(1));
            } else {
                lastAvailability.lastAvailabilityUpdated = fieldsUpdated;
                saveObject(lastAvailability);
                logText("Availability successfully updated.");
            }
            
        }

        return "";    
    }

    @Override
    public String updateShortAvailability() throws Exception {
        if(availabilityHasBeenChanged == null) {
            return "";
        }
        
        if(storeId.equals("87cdfab5-db67-4716-bef8-fcd1f55b770b")) {
            return sparseUpdateAvailabilityInternal();
        }
        return updateAvailabilityInternal(300);
    }

    public void logText(String string) {
        log.logEntries.put(System.currentTimeMillis(), string);
        saveObject(log);
    }

    @Override
    public HashMap<Long, String> getLogEntries() {
        return log.logEntries;
    }

    private Integer numberOfBookingsHavingWuBookId(String idToMark) {
        int count = 0;
        
        for(PmsBooking booking : pmsManager.getAllBookingsFlat()) {
            if(booking.wubookreservationid != null && booking.wubookreservationid.equals(idToMark)) {
                count++;
                continue;
            }
            for(String modified : booking.wubookModifiedResId) {
                if(modified.equals(idToMark)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private void sendErrorForReservation(String wubookId, String message) {
        if(!pmsManager.hasSentErrorNotificationForWubookId(wubookId)) {
            messageManager.sendErrorNotification("Error for wubookreservation: " + wubookId + " : "  + message, null);
            pmsManager.markSentErrorMessageForWubookId(wubookId);
        }
    }

    private List<Integer> getAllResCodesForPmsBooking(WubookBooking booking) {
        List<Integer> result = new ArrayList();
        result.add(new Integer(booking.reservationCode));
        result.addAll(booking.modifiedReservation);
        return result;
    }

    private boolean hasChanged(WubookAvailabilityField field) {
        for(WubookAvailabilityField oldField : lastAvailability.lastAvailabilityUpdated) {
            if(field.roomId.equals(oldField.roomId) && oldField.dateAsString.equals(field.dateAsString)) {
                return !field.availability.equals(oldField.availability);
            }
        }
        return true;
    }

    private String convertToDayString(Date date) {
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    private void sortList(List<WubookBooking> list) {
        Collections.sort(list, new Comparator<WubookBooking>(){
            public int compare(WubookBooking o1, WubookBooking o2){
                return o1.reservationCode.compareTo(o2.reservationCode);
            }
       });
    }

}

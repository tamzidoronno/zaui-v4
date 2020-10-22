package com.thundashop.core.wubook;

import com.thundashop.core.storemanager.StoreManager;
import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.gson.Gson;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.NewOrderFilter;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import com.thundashop.core.pmsmanager.PmsBookingComment;
import com.thundashop.core.pmsmanager.PmsBookingDateRange;
import com.thundashop.core.pmsmanager.PmsBookingFilter;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.core.pmsmanager.PmsGuests;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.core.pmsmanager.TimeRepeater;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import com.thundashop.core.pmsmanager.TimeRepeaterDateRange;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.IOException;
import java.net.ConnectException;
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
import java.util.LinkedList;
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
    private Date lastUpdateLongTime = null;
    SavedLastAvailibilityUpdate lastAvailability = new SavedLastAvailibilityUpdate();
    
    private WubookLog log = new WubookLog();

    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    ProductManager productManager;
    
    @Autowired
    PmsInvoiceManager pmsInvoiceManager;
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    FrameworkConfig frameworkConfig;
    
    @Autowired
    MessageManager messageManager;
    
    @Autowired
    StoreManager storeManager;
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
    
    @Autowired
    UserManager userManager;
    
    @Autowired
    OrderManager orderManager;
    private int tokenCount;
    private Date availabiltyyHasBeenChangedEnd;
    private Date availabiltyyHasBeenChangedStart;
    private Date latestCheckForNewBookings = null;
    private boolean forceUpdate = false;
    public Date disableWubook = null;
    public Vector bookingsToAdd = null;
    boolean fetchBookingThreadIsRunning = false;
    Date fetchBookingThreadStarted = null;
    private List<WubookBooking> nextBookings;
    private List<String> bookingCodesToAdd = new ArrayList();
    private boolean errorNotificationSent = false;
    private Date lastPulledWubook = new Date();
    private boolean isRunningFetchNewBookings = false;

    
    public void checkIfLastPulledIsOk() {
        if(!isWubookActive()) {
            return;
        }
        
        if(errorNotificationSent) {
            return;
        }
        
        Date now = new Date();
        long diff = now.getTime() - lastPulledWubook.getTime();
        long minutes = diff / 1000 / 60;
        if(minutes > 5) {
            messageManager.sendErrorNotificationToEmail("post@getshop.com", "Fetch new booking not run i 5 minutes.", null);
            errorNotificationSent = true;
        }
    }
    
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
        createScheduler("wubookprocessor2", "1 5,12,22 * * *", WuBookHourlyProcessor.class);
    }
    
    public boolean updateAvailability() throws Exception {
        int numberOfDays = pmsManager.getConfigurationSecure().daysAllowedInTheFuture;
        if(lastUpdateLongTime == null) {
            numberOfDays = 720;
            lastUpdateLongTime = new Date();
        } else {
            long diff = System.currentTimeMillis() - lastUpdateLongTime.getTime();
            if(diff > (60*1000*60*24)) {
                numberOfDays = 720;
                lastUpdateLongTime = new Date();
            }
        }
        
        return updateAvailabilityInternal(numberOfDays);
    }
    
    @Override
    public boolean updateAvailabilityFromButton() throws Exception {
        forceUpdate = true;
        return updateAvailabilityInternal(720);
    }
    
    private boolean isWubookActive() { 
        if(pmsManager.getConfigurationSecure().wubookusername == null || pmsManager.getConfigurationSecure().wubookusername.isEmpty()) {
            return false;
        } 
       return true;
    }
    
    
    private boolean connectToApi() throws Exception {
        
        if(!isWubookActive()) { return false; }
        
        if(tokenCount < 30 && token != null && !token.isEmpty()) {
            tokenCount++;
            
            return true;
        }
        
        //Old
//        client = new XmlRpcClient("https://wubook.net/xrws/");
        //New
        client = new XmlRpcClient("https://wired.wubook.net/xrws/");
        logText("Reloading token");
        Vector<String> params = new Vector<String>();
        params.addElement(pmsManager.getConfigurationSecure().wubookusername);
        params.addElement(pmsManager.getConfigurationSecure().wubookpassword);
        params.addElement("823y8vcuzntzo_o201");
        Vector result = executeClient("acquire_token", params);
        
        if(result == null) {
            return false;
        }
        
        Integer response = (Integer) result.get(0);
        token = (String) result.get(1);
        tokenCount = 0;
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
        
        for(WubookBooking book : toReturn) {
            PmsBooking correlated = findCorrelatedBooking(book);
            if(correlated != null) {
                book.isAddedToPms = true;
            }
        }
        
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
        Vector result = executeClient("fetch_bookings_codes", params);
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
        if(table.containsKey("customer_language_iso")) {
            booking.language = table.get("customer_language_iso") + "";
        }
        booking.reservationCode = table.get("reservation_code") + "";
        booking.channel_reservation_code = (String) table.get("channel_reservation_code");
        booking.status = new Integer(table.get("status") + "");
        booking.isExpediaCollect = checkExpediaCollect(table);
        booking.isBookingComVirtual = checkBcomVirtualCard(table);
        booking.isPrePaid = checkIfPrepaid(table);
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
            
            try {
                if(table.containsKey("men") && (int)table.get("id_channel") == 37) {
                    guest = (int) table.get("men");
                }
            }catch(Exception e) {
                
            }
            
            room.guest = guest;
            room.guestName = getGuestName(roomNumber, table);
            room.roomId = roomId; 
            room.breakfasts = checkForBreakfast(roomtable, table, guest);
            try {
                ArrayList<PmsBookingAddonItem> addons = new ArrayList(pmsManager.getConfigurationSecure().addonConfiguration.values());
                for(PmsBookingAddonItem addon : addons) {
                    addAddonsToRoom(room, table, addon, guest);
                }
            }catch(Exception e) {
                messageManager.sendErrorNotification("Stack failure in new code change for wubook", e);
            }
            booking.rooms.add(room);
            
            if(pmsManager.getConfigurationSecure().usePricesFromChannelManager || storeManager.isPikStore()) {
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
                room.needToAddTaxes = checkIfNeedToAddTaxes(roomtable);
                if(room.needToAddTaxes) {
                    addTaxesToRoom(room);
                }
            }
            roomNumber++;
        }
        return booking;
    }
    

    @Override
    public void fetchNewBookings() {
       if(lastPulledWubook != null) {
           Date now = new Date();
            long diff = now.getTime() - lastPulledWubook.getTime();
            long seconds = diff / 1000;
            if(seconds < 20) {
                logPrint("Avoid pulling wubook more than once a minute.");
                return;
            }
       }
       isRunningFetchNewBookings = true;
       try {
            if(disableWubook != null) {
                long diff = new Date().getTime() - disableWubook.getTime();
                if(diff < (10*60*1000)) {
                    logText("Fetch new booking disabled from : " + disableWubook);
                    lastPulledWubook = new Date();
                    return;
                }
            }

            if(nextBookings != null) {
                try {
                    logText("Next bookings found:" + nextBookings.size());
                    checkBookingsToDelete(nextBookings);
                }catch(Exception e) {
                    messageManager.sendErrorNotification("Failed to double delete bookings", e);
                    logPrintException(e);
                }
                nextBookings = null; 
            }

            try {
                if(!bookingCodesToAdd.isEmpty()) {
                    logText("BookingsCodesToAdd is not empty:" + bookingCodesToAdd.size());
                    for(String code : bookingCodesToAdd) {
                        WubookBooking booking = fetchBooking(code + "");
                        addBookingToPms(booking);
                    }
                }
            }catch(Exception e) {
                logPrintException(e);
                messageManager.sendErrorNotification("Failed to add booking codes", e);
            }
            bookingCodesToAdd.clear();

            if(!connectToApi()) {
                return;
            }
            
            lastPulledWubook = new Date();
            
            PmsConfiguration config = pmsManager.getConfigurationSecure();
            if(config.wubooklcode == null || config.wubooklcode.isEmpty()) {
                return;
            }
            List<WubookBooking> toReturn = new ArrayList();
            if(bookingsToAdd != null) {
                Vector bookings = bookingsToAdd;
                for(int bookcount = 0; bookcount < bookings.size(); bookcount++) {
                    Hashtable reservation = (Hashtable) bookings.get(bookcount);
                    WubookBooking wubooking = buildBookingResult(reservation);
                    logText("Adding reservation: " + wubooking.reservationCode);
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

            if(!fetchBookingThreadIsRunning) {
                WubookThreadRipper checkNewBookingsThread = new WubookThreadRipper(this, 1);
                checkNewBookingsThread.setWubookSettings(token, pmsManager.getConfigurationSecure().wubooklcode, client);
                checkNewBookingsThread.setStoreId(storeId);
                checkNewBookingsThread.setName("Checking for new bookings wubook: " + storeId);
                checkNewBookingsThread.start();
            } else {
                logPrint("Not starting thread fetch new bookings since it is already running since:" + fetchBookingThreadStarted);
            }

            return;
       }catch(Exception e) {
           if(!errorNotificationSent) {
            messageManager.sendErrorNotification("Error in fetchnewbooking.", e);
            errorNotificationSent = true;
           }
           logPrintException(e);
       }
       isRunningFetchNewBookings = false;
    }

    private boolean doNotCheckBookings() {
        if(latestCheckForNewBookings == null) {
            latestCheckForNewBookings = new Date();
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        Date toCheck = cal.getTime();
        if(toCheck.after(latestCheckForNewBookings)) {
            latestCheckForNewBookings = new Date();
            return false;
        }
        return true;
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

        Vector result = executeClient("fetch_booking", params);
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
        Vector noshow = executeClient("bcom_notify_noshow", params);
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
        
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, pmsManager.getConfigurationSecure().daysAllowedInTheFuture+20);
        Date end = cal.getTime();

        return updatePricesBetweenDates(now, end);
    }

    @Override
    public String updateMinStay() throws Exception {
        if(!connectToApi()) {
            return "failed to connect to api";
        }
        
        if(!needUpdateMinStay()) {
            logText("Min stay not updated since no min stay is needed to be updated");
            return "Min stay update not needed";
        }
        
        logText("Updating minstay");
        
        Hashtable table = new Hashtable();
        
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String dfrom = format.format(new Date());
        
        PmsPricing prices = pmsManager.getPrices(new Date(), new Date());
        boolean found = false;
        for (WubookRoomData rdata : wubookdata.values()) {
            if(!rdata.addedToWuBook) {
                continue;
            }
            BookingItemType type = bookingEngine.getBookingItemType(rdata.bookingEngineTypeId);
            if(type == null) {
                //Type has been deleted.
                continue;
            }
            String[] roomIds = new String[1];
            roomIds[0] = rdata.wubookroomid + "";
            if(rdata.newRoomPriceSystem) {
                roomIds = rdata.virtualWubookRoomIds.split(";");
            }
            

            HashMap<String, Vector> results = new HashMap();
            for(String roomId : roomIds) {
                Vector vector = new Vector();
                results.put(roomId, vector);
            }

            
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY,16);
            for(int i = 0;i < (365*2); i++) {
                Double minstay = getSpecialRestriction(cal.getTime(), rdata.bookingEngineTypeId, TimeRepeaterData.TimePeriodeType.min_stay);
                Double nocheckin = getSpecialRestriction(cal.getTime(), rdata.bookingEngineTypeId, TimeRepeaterData.TimePeriodeType.noCheckIn);
                Double nocheckout = getSpecialRestriction(cal.getTime(), rdata.bookingEngineTypeId, TimeRepeaterData.TimePeriodeType.noCheckOut);
                
                if(minstay == null) { minstay = 1.0; }
                if(nocheckin == null) { nocheckin = 0.0; }
                if(nocheckout == null) { nocheckout = 0.0; }
                
                for(String roomId : roomIds) {
                    Vector list = results.get(roomId);
                    Hashtable dayEntry = new Hashtable();
                    dayEntry.put("min_stay", minstay);
                    dayEntry.put("closed_arrival", nocheckin);
                    dayEntry.put("closed_departure", nocheckout);
                    list.add(dayEntry);
                    found = true;
                }
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
            for(String roomId : results.keySet()) {
                if(!roomId.equals("-1") && !roomId.isEmpty()) {
                    table.put(roomId + "", results.get(roomId));
                }
            }
        }
        if(found) {
            Vector params = new Vector();
            params.addElement(token);
            params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
            params.addElement(0);
            params.addElement(dfrom);
            params.addElement(table);

            Vector result = executeClient("rplan_update_rplan_values", params);
            if((Integer)result.get(0) != 0) {
                logText("Failed to update daily min stay, " + result.get(1));
                return "Failed to update daily min stay, " + result.get(1);
            }
            lastAvailability.needUpdateMinStay = false;
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
        Vector noshow = executeClient("bcom_notify_invalid_cc", params);
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
        
        List<BookingItemType> types = bookingEngine.getBookingItemTypesWithSystemType(null);
        List<String> errors = new ArrayList();
        for (BookingItemType type : types) {
            String added = "";
            WubookRoomData data = getWubookRoomData(type.id);
            if(data.addedToWuBook && data.wubookroomid != -1) {
                added = updateRoom(type);
            } else {
                added = insertRoom(type);
            }
            
            insertVirtualRooms(data, type);
            
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
            res = newData;
            saveObject(newData);
            wubookdata.put(newData.id, newData);
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
        if(type.size == 0) {
            return "Invalid size for room: " + type.name;
        }
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
        params.addElement("0");
        params.addElement("r" + rdata.code);
        params.addElement("nb");
        Vector result = executeClient("new_room", params);
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

    private Integer insertVirtualRoom(BookingItemType type, int guests, WubookRoomData data) throws XmlRpcException, IOException, Exception {
        if(!connectToApi()) { return -1; }
        List<BookingItem> items = bookingEngine.getBookingItemsByType(type.id);
        Vector<Object> params = new Vector<Object>();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(new Integer(data.wubookroomid));
        params.addElement(1);
        params.addElement(type.name + "(" + guests + " guests)");
        params.addElement(type.size);
        params.addElement(0);
        params.addElement(9999);
        params.addElement("r" + data.code + "" + guests);
        params.addElement("nb");
        Vector result = executeClient("new_virtual_room", params);
        Integer response = (Integer) result.get(0);
        String res = "";
        if(response == 0) {
            logPrint("Succesfully added virtual room");
            return (Integer)result.get(1);
        } else {
            return -1;
        }
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
        Vector result = executeClient("mod_room", params);
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
        if(addons == null) {
            return 0;
        }
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
                if(channel.toLowerCase().contains("breakfast is included") || channel.toLowerCase().contains("frokost er inkludert")) {
                    return guests;
                }
            }
        }
        
        if(bookingtable != null) {
            Gson gson = new Gson();
            String test = gson.toJson(bookingtable);
            if(test.toLowerCase().contains("breakfast is included in the room rate")) {
                return guests;
            }
            if(test.toLowerCase().contains("breakfast included") || test.toLowerCase().contains("frokost inkludert")) {
                return guests;
            }
            if(test.toLowerCase().contains("breakfast is included") || test.toLowerCase().contains("frokost er inkludert")) {
                return guests;
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
        customerNotes = customerNotes.replace("** DO NOT CHARGE CREDIT CARD **", "");
        customerNotes = customerNotes.replace("<br>", "");
        customerNotes = customerNotes.replace("You have a booker that prefers communication by phone", "");
        customerNotes = customerNotes.replace("I am travelling for business and I may be using a business credit card.", "");
        customerNotes = customerNotes.replace("This guest would like the rooms in this booking to be close together if possible.", "");
        customerNotes = customerNotes.replace("You have a booker that would like free parking. (based on availability)", "");
        customerNotes = customerNotes.replace("You have a booker that prefers communication by email", "");
        customerNotes = customerNotes.replace("--- No-CC reservation (no credit card needed, none provided) ---", "");
        customerNotes = customerNotes.replace("Upper-storey room request: this booker requests upper-storey room(s) - based on availability", "");
        customerNotes = customerNotes.replace("You have a booker that would prefer a quiet room. (based on availability)", "");
        customerNotes = customerNotes.replace("You have a booker that would prefer a quiet room.", "");
        customerNotes = customerNotes.replace("Non-Smoking, 1 double bed", "");
        customerNotes = customerNotes.replace("Non-Smoking, 2 double beds", "");
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
        try {
            PmsBooking newbooking = null; 

            long start = System.currentTimeMillis();
            boolean isUpdate = false;
            if(booking.modifiedReservation.size() > 0 && !booking.delete) {
                newbooking = findCorrelatedBooking(booking);
                if(newbooking == null) {
//                    sendErrorForReservation(booking.reservationCode, "Could not find existing booking for a modification on reservation");
                } else {
                    if(newbooking.ignoreWubook) {
                        pmsManager.logEntry("Booking modified, but ignored due to force ignore.", newbooking.id, null);
                        return "";
                    }
                    
                    for(PmsBookingRooms room : newbooking.getActiveRooms()) {
                        if(room.isStarted()) {
                            newbooking.wubookModifiedResId.add(booking.reservationCode);
                            pmsManager.logEntry("Failed to update from channel manager, stay already started.", newbooking.id, null);
                            pmsManager.saveBooking(newbooking);
                            return "";
                        }
                        room.deletedByChannelManagerForModification = true;
                        pmsManager.removeFromBooking(newbooking.id, room.pmsBookingRoomId);
                    }
                    pmsManager.logEntry("Modified by channel manager", newbooking.id, null);
                    isUpdate = true;
                }
            } else if(booking.delete) {
                newbooking = findCorrelatedBooking(booking);
                if(newbooking == null) {
                    return "Did not find booking to delete.";
                } else { 
                    if(newbooking.ignoreWubook) {
                        pmsManager.logEntry("Booking modified, but ignored due to force ignore (delete).", newbooking.id, null);
                        return "";
                    }

                    pmsManager.logEntry("Deleted by channel manager", newbooking.id, null);
                    pmsManager.deleteBooking(newbooking.id);
                }
                
                newbooking = pmsManager.getBooking(newbooking.id);
                List<String> orderIds = new ArrayList(newbooking.orderIds);
                for(String orderId : orderIds) {
                    Order order = orderManager.getOrderSecure(orderId);
                    if(order.isCreditNote || !order.creditOrderId.isEmpty()) {
                        continue;
                    }
                    List<PmsBooking> bookings = pmsManager.getBookingsFromOrderId(orderId);
                    if(bookings.size() > 1) {
                        continue;
                    }
                    pmsInvoiceManager.creditOrder(newbooking.id, orderId);
                }

                return "";
            }

            if(newbooking == null) {
                newbooking = pmsManager.startBooking();
            }
            
            if(newbooking.ignoreWubook) {
                pmsManager.logEntry("Booking modified, but ignored due to force ignore (standard process).", newbooking.id, null);
                return "";
            }
            
            for(PmsBookingRooms room : newbooking.getAllRooms()) {
                room.unmarkOverBooking();
            }

            if(booking.language != null && !booking.language.isEmpty()) {
                newbooking.language = booking.language;
            }
            newbooking.channel = "wubook_" + booking.channelId;
            newbooking.wubookchannelreservationcode = booking.channel_reservation_code;
            if(!isUpdate) {
                newbooking.wubookreservationid = booking.reservationCode;
            }
            newbooking.latestwubookreservationid = booking.reservationCode;
            newbooking.countryCode = booking.countryCode;
            
            newbooking.isBookingComVirtual = booking.isBookingComVirtual;
            newbooking.isExpediaCollect = booking.isExpediaCollect;
            newbooking.isPrePaid = booking.isPrePaid;
            
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
            if(booking.isNonRefundable) {
                newbooking.setAllRoomsNonRefundable();
            }

            Calendar calStart = Calendar.getInstance();

            HashMap<String,HashMap<Date, Double>> pricestoset = new HashMap();

            for(WubookBookedRoom r : booking.rooms) {
                PmsBookingRooms room = new PmsBookingRooms();
                room.date = new PmsBookingDateRange();
                room.date.start = setCorrectTime(booking.arrivalDate, true);
                room.date.end = setCorrectTime(booking.depDate, false);
                room.numberOfGuests = r.guest;
                room.bookingItemTypeId = getTypeFromWubookRoomId(r.roomId);
                if(room.bookingItemTypeId == null) {
                    logText("Failed to find room type for booking: " + booking.reservationCode);
                    sendErrorForReservation(booking.reservationCode, "Failed to find room type for reservation");
                }
                
                if(!doesTypeExists(room.bookingItemTypeId)) {
                    logText("The booking type for this room does not exists (channel manager), this needs to be remapped. Category in GetShop has been deleted.");
                    continue;
                }
                
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
            
            if(newbooking.rooms.isEmpty()) {
                logText("Returning since there are no rooms to add id: " + booking.reservationCode);
                return null;
            }
            
            boolean isPrepaidByOta = false;
            if(booking.isExpediaCollect) {
                checkIfPaymentMethodIsActive("92bd796f-758e-4e03-bece-7d2dbfa40d7a");
                newbooking.paymentType = "92bd796f-758e-4e03-bece-7d2dbfa40d7a";
                newbooking.isPrePaid = true;
                isPrepaidByOta = true;
            }
            if(booking.isBookingComVirtual) {
                checkIfPaymentMethodIsActive("d79569c6-ff6a-4ab5-8820-add42ae71170");
                newbooking.paymentType = "d79569c6-ff6a-4ab5-8820-add42ae71170";
                newbooking.isPrePaid = true;
                isPrepaidByOta = true;
            }
            if(newbooking.channel.equals("wubook_43")) {
                checkIfPaymentMethodIsActive("639164bc-37f2-11e6-ac61-9e71128cae77");
                newbooking.paymentType = "639164bc-37f2-11e6-ac61-9e71128cae77";
                newbooking.isPrePaid = true;
                isPrepaidByOta = true;
            }
            
            if(useNewPrePaidMethod()) {
                checkIfPaymentMethodIsActive("4aa4888a-4685-4373-bffe-aa6a3005eff1");
                newbooking.paymentType = "4aa4888a-4685-4373-bffe-aa6a3005eff1";
                newbooking.isPrePaid = true;
            }
            
            pmsManager.setBooking(newbooking);
            int i = 0;
            for(PmsBookingRooms room : newbooking.getActiveRooms()) {
                WubookBookedRoom r = booking.rooms.get(i);
                if(r.breakfasts > 0) {
                    boolean add = true;
                    for(PmsBookingAddonItem item : pmsManager.getConfigurationSecure().addonConfiguration.values()) {
                        if(item.addonType == PmsBookingAddonItem.AddonTypes.BREAKFAST && item.includedInBookingItemTypes.contains(room.bookingItemTypeId)) {
                            //If this is a default addon, it will be included anyway.
                            add = false;
                        }
                    }
                    
                    if(add) {
                        pmsManager.addAddonsToBookingWithCount(PmsBookingAddonItem.AddonTypes.BREAKFAST, room.pmsBookingRoomId, false, r.breakfasts);
                    }
                }
                i++;
                try {
                    for(String productId : r.addonsToAdd.keySet()) {
                        boolean add = true;
                        for(PmsBookingAddonItem item : pmsManager.getConfigurationSecure().addonConfiguration.values()) {
                            if(item.productId != null && item.productId.equals(productId) && item.includedInBookingItemTypes.contains(room.bookingItemTypeId)) {
                                //If this is a default addon, it will be included anyway.
                                add = false;
                            }
                        }
                        if(add) {
                            pmsManager.addProductToRoom(productId, room.pmsBookingRoomId, r.addonsToAdd.get(productId));
                        }
                    }
                }catch(Exception e) {
                    messageManager.sendErrorNotification("Stack failure in new code change for wubook (when adding)", e);
                }
            }
            pmsInvoiceManager.clearOrdersOnBooking(newbooking);
            newbooking.tryAutoCharge = pmsManager.getConfigurationSecure().wubookAutoCharging;
            newbooking = pmsManager.doCompleteBooking(newbooking);
            boolean doNormalPricing = true;
            if(newbooking == null) {
                messageManager.sendErrorNotification("Failed to add new booking in wubook: " + booking.reservationCode, null);
            } else {
                if(newbooking.channel != null && newbooking.channel.equals("wubook_1")) {
                   if(pmsManager.getConfigurationSecure().useGetShopPricesOnExpedia) {
                       doNormalPricing = false;
                        if(newbooking.paymentType != null && !newbooking.paymentType.isEmpty()) {
                            pmsInvoiceManager.autoCreateOrderForBookingAndRoom(newbooking.id, newbooking.paymentType);
                        }
                   }
                }
            }

            if((pmsManager.getConfigurationSecure().usePricesFromChannelManager || storeManager.isPikStore()) && newbooking != null && doNormalPricing) {
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
                if(!newbooking.hasOverBooking()) {
                    if(newbooking.paymentType != null && !newbooking.paymentType.isEmpty()) {
                        pmsInvoiceManager.autoCreateOrderForBookingAndRoom(newbooking.id, newbooking.paymentType);
                    }
                } else {
                    newbooking.rowCreatedDate = new Date();
                    
                    for(PmsBookingRooms room : newbooking.rooms) {
                        if(room.isOverBooking()) {
                            try {
                                pmsManager.removeFromBooking(newbooking.id, room.pmsBookingRoomId);
                            }catch(Exception e) {
                                //Okay, it failed, that's okay.
                            }
                        }
                    }
                    
                    String text = "An overbooking occured go to your booking admin panel handle it.<br><bR><br>booking dump:<br>" + pmsManager.dumpBooking(newbooking, true);
                    text += "<br><br>";
                    text += "For more information about overbooking, see: https://getshop.com/double_booking_error.html"; 
                    String email = getStoreEmailAddress();
                    String content = "Possible overbooking happened:<br>" + text;
                    messageManager.sendMail(email, email, "Warning: possible overbooking happened", content, email, email);
//                    messageManager.sendMail("pal@getshop.com","pal@getshop.com", "Warning: possible overbooking happened", content,"pal@getshop.com","pal@getshop.com");

                }
            }
            
            boolean hasPaidOrders = checkForPaidOrders(newbooking);
            if(hasPaidOrders) {
                String orderId = pmsInvoiceManager.autoCreateOrderForBookingAndRoom(newbooking.id, newbooking.paymentType);
                Order ord = orderManager.getOrder(orderId);
                Double amount = orderManager.getTotalAmount(ord);
                if(amount == 0.0) {
                    pmsInvoiceManager.markOrderAsPaid(newbooking.id, orderId);
                }
            }
            
            logPrint("Time takes to complete one booking: " + (System.currentTimeMillis() - start));
            }catch(Exception e) {
                e.printStackTrace();
                messageManager.sendErrorNotification("Outer wubook catch, booking failed to be added: " +booking.reservationCode, e);
            }
            return "";
    }

    private String getTypeFromWubookRoomId(int roomId) {
        for(WubookRoomData rdata : wubookdata.values()) {
            if(rdata.wubookroomid == roomId) {
                return rdata.bookingEngineTypeId;
            }
            if(rdata.virtualWubookRoomIds != null && !rdata.virtualWubookRoomIds.isEmpty()) {
                String[] splitted = rdata.virtualWubookRoomIds.split(";");
                for(String tmp : splitted) {
                    Integer tmpRoomId = new Integer(tmp);
                    if(tmpRoomId == roomId) {
                        return rdata.bookingEngineTypeId;
                    }
                }
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
        String starttime = pmsManager.getConfigurationSecure().getDefaultStart();
        if(!start) {
            starttime = pmsManager.getConfigurationSecure().getDefaultEnd();
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
                bookingCodesToAdd.add(code + "");
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
        filter.normalizeStartEndDateByBeginningEndOfDay();
        
        cal.add(Calendar.DAY_OF_YEAR, -1);
        
        filter.startDate = cal.getTime();
        filter.endDate = cal.getTime();
        
        List<PmsBooking> booking = pmsManager.getAllBookings(filter);
        for(PmsBooking book : booking) {
            if(book.payedFor) {
                continue;
            }
            if(book.hasForcedAccessedRooms()) {
                continue;
            }
            boolean arrived = false;
            
            for(PmsBookingRooms room : book.getAllRooms()) {
                if(room.checkedin || room.addedToArx) {
                    arrived = true;
                }
            }
            if(arrived) {
                continue;
            }
            

            if(book.orderIds.isEmpty()) {
                continue;
            }
            
            try {
                if(book.ignoreNoShow) {
                    continue;
                }

                for(String orderId : book.orderIds) {
                    Order order = orderManager.getOrderSecure(orderId);
                    if(order.status == Order.Status.PAYMENT_COMPLETED) {
                        book.ignoreNoShow = true;
                        pmsManager.saveBooking(book);
                    }
                }

                if(book.ignoreNoShow) {
                    continue;
                }
            }catch(Exception e) {
                messageManager.sendErrorNotification("Wubook noshow problem", e);
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
        
        nextBookings = fetchBookings(3, false);
    }

    private void checkBookingsToDelete(List<WubookBooking> nextBookings) throws ErrorException {
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
                            pmsManager.deleteRoom(room);
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
        nextBookings = null;
    }

    @Override
    public List<WubookBooking> fetchBookings(Integer daysBack, boolean registrations) throws Exception {
        if(!connectToApi()) {
            return new ArrayList();
        }
        
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
        Vector result = executeClient("fetch_bookings", params);
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
        if(restriction.start.after(restriction.end)) {
            return;
        }
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
        List<WubookAvailabilityRestrictions> res = new ArrayList(restrictions.values()); 
        res.sort(Comparator.comparing(WubookAvailabilityRestrictions::getStartDate));
        Collections.reverse(res);
        return res;
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
            return (text.contains("non refundable") || 
                    text.contains("non-refundable")) || 
                    text.contains("ikke refunderbar");
            
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
        
        Integer lowest = null;
        if(newbooking == null) {
            for(Integer id : booking.modifiedReservation) {
                if(lowest == null || lowest > id) {
                    lowest = id;
                }
            }
            if(lowest != null && !booking.reservationCode.equals(lowest+"")) {
                addBooking(lowest + "");
                newbooking = findCorrelatedBooking(booking);
            }
        }
        
        return newbooking;
    }

    @Override
    public List<WubookOta> getOtas() throws Exception {
        if(!connectToApi()) {
            return new ArrayList();
        }
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        
        Vector result = executeClient("get_otas", params);
        Integer responseCode = (Integer) result.get(0);
        List<WubookOta> returnChannels = new ArrayList();
        if(responseCode == 0) {
            Vector channels = (Vector) result.get(1);
            for(Integer offset = 0; offset < channels.size(); offset++) {
                Hashtable table = (Hashtable) channels.get(offset);
                WubookOta ota = new WubookOta();
                ota.channel = new Integer((int) table.get("ctype")) + "";
                ota.tag = (String) table.get("tag");
                ota.id = (int)table.get("id");
                ota.running = (int) table.get("running");
                returnChannels.add(ota);
            }
        }
        return returnChannels;
    }
    
    @Override
    public boolean newOta(String type) {
        return false;
    }
    
    public void setAvailabilityChanged(Date start, Date end) {
        if(availabiltyyHasBeenChangedStart == null || (start != null && start.before(availabiltyyHasBeenChangedStart))) {
            availabiltyyHasBeenChangedStart = start;
        }
        if(availabiltyyHasBeenChangedEnd == null || (end != null && end.after(availabiltyyHasBeenChangedEnd))) {
            availabiltyyHasBeenChangedEnd = end;
        }
        logPrint("Avialability changed at : " + start + " - " + end);
        availabilityHasBeenChanged = new Date();
    }

    private boolean updateAvailabilityInternal(int numberOfDays) throws Exception {
        if(availabilityLastUpdated != null && numberOfDays < 700 && !forceUpdate) {
            long diff = System.currentTimeMillis() - availabilityLastUpdated.getTime();
            if(diff < (120*60*1000)) {
                return false;
            }
        }
        
        if(!frameworkConfig.productionMode && !forceUpdate) { return false; }
        
        forceUpdate = false;
        
        availabilityLastUpdated = new Date();
        
        
        if(!connectToApi()) {
            return false; 
        }
        Vector<Hashtable> tosend = new Vector();
        int toRemove = pmsManager.getConfigurationSecure().numberOfRoomsToRemoveFromBookingCom;
        List<WubookAvailabilityField> fieldsUpdated = new ArrayList();
        gsTiming("Start iterating");
        for (WubookRoomData rdata : wubookdata.values()) {
            if(!rdata.addedToWuBook) {
                continue;
            }
            BookingItemType type = bookingEngine.getBookingItemType(rdata.bookingEngineTypeId);
            if(type == null) {
                //Type has been deleted.
                continue;
            }            
            Hashtable roomToUpdate = new Hashtable();
            roomToUpdate.put("id", rdata.wubookroomid);

            Calendar startcal = getCalendar(true);
            Calendar endCal = getCalendar(false);
            
            Vector days = new Vector();
            for (int i = 0; i < numberOfDays; i++) {
                Date start = startcal.getTime();
                endCal.add(Calendar.DAY_OF_YEAR, 1);
                Date end = endCal.getTime();
                int count = 0;
                try {
                    count = pmsManager.getNumberOfAvailable(rdata.bookingEngineTypeId, start, end, false, false);
                }catch(BookingEngineException e) {

                }
                int totalForType = bookingEngine.getBookingItemsByType(rdata.bookingEngineTypeId).size();
                if(count > 0 && totalForType > 2) {
                    count -= toRemove;
                }
                if(isRestricted(rdata.bookingEngineTypeId, start, end)) {
                    count = 0;
                }
                
                if(i > pmsManager.getConfigurationSecure().daysAllowedInTheFuture) {
                    count = 0;
                }
                
                Integer maxToAdd = getMaxForCategory(rdata.bookingEngineTypeId);
                if(maxToAdd >= 0 && maxToAdd < count) {
                    count = maxToAdd;
                }
                
                Hashtable result = new Hashtable();
                result.put("avail", count);
                result.put("no_ota", 0);
                days.add(result);
                startcal.add(Calendar.DAY_OF_YEAR, 1);
                
                WubookAvailabilityField field = new WubookAvailabilityField();
                field.roomId = rdata.wubookroomid;
                field.availability = count;
                field.date = start;
                field.dateAsString = convertToDayString(start);
                fieldsUpdated.add(field);
                
            }
            roomToUpdate.put("days", days);
            tosend.add(roomToUpdate);
        }
        gsTiming("done iterating");

        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String todayString = format.format(new Date());

        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(todayString);
        params.addElement(tosend);
        logText("Doing update of " + numberOfDays + " days");
        WubookManagerUpdateThread updateThread = new WubookManagerUpdateThread("update_rooms_values", client, this, params);
        updateThread.setName("Wubook update thread, storeid: " + storeId);
        updateThread.start();
        updateThread.setName("Updating WuBookThread for store: " + storeId);
        availabilityHasBeenChanged = null;
        lastAvailability.lastAvailabilityUpdated = fieldsUpdated;
        
        return true;    
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
            BookingItemType type = bookingEngine.getBookingItemType(rdata.bookingEngineTypeId);
            if(type == null) {
                //Type has been deleted.
                continue;
            }

            Calendar startcal = getCalendar(true);
            Calendar endCal = getCalendar(false);
            for (int i = 0; i < pmsManager.getConfigurationSecure().daysAllowedInTheFuture; i++) {
                Date start = startcal.getTime();
                endCal.add(Calendar.DAY_OF_YEAR, 1);
                Date end = endCal.getTime();
                
                if(availabiltyyHasBeenChangedEnd != null && (end.after(availabiltyyHasBeenChangedEnd) &&
                        !PmsBookingRooms.isSameDayStatic(availabiltyyHasBeenChangedEnd, end))) {
                    startcal.add(Calendar.DAY_OF_YEAR, 1);
                    continue;
                }
                
                if(availabiltyyHasBeenChangedStart != null && (start.before(availabiltyyHasBeenChangedStart) &&
                        !PmsBookingRooms.isSameDayStatic(availabiltyyHasBeenChangedStart, start))) {
                    startcal.add(Calendar.DAY_OF_YEAR, 1);
                    continue;
                }
                
                int count = 0;
                try {
                    count = pmsManager.getNumberOfAvailable(rdata.bookingEngineTypeId, start, end, false, false);
                }catch(BookingEngineException e) {
                    
                }
                int totalForType = bookingEngine.getBookingItemsByType(rdata.bookingEngineTypeId).size();
                if(count > 0 && totalForType > 2) {
                    count -= toRemove;
                }
                if(isRestricted(rdata.bookingEngineTypeId, start, end)) {
                    count = 0;
                }
                
                if(i > 305) {
                    count = 0;
                }
                
                Integer maxToAdd = getMaxForCategory(rdata.bookingEngineTypeId);
                if(maxToAdd >= 0 && maxToAdd < count) {
                    count = maxToAdd;
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
            BookingItemType type = bookingEngine.getBookingItemType(rdata.bookingEngineTypeId);
            if(type == null) {
                //Type has been deleted.
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
            
            Vector result = executeClient("update_sparse_rooms_values", params);
            if ((Integer)result.get(0) != 0) {
                logText("Failed to update availability " + "(" + result.get(0) + ")" + result.get(1));
            } else {
                lastAvailability.lastAvailabilityUpdated = fieldsUpdated;
                saveObject(lastAvailability);
                logText("Availability successfully updated.");
            }
            
        }
        availabilityHasBeenChanged = null;
        availabiltyyHasBeenChangedEnd = null;
        availabiltyyHasBeenChangedStart = null;
        return "";    
    }

    @Override
    public String updateShortAvailability() {
        try {
            if(updateAvailability()) {
                return "";
            }
            if(availabilityHasBeenChanged == null) {
                return "";
            }

            if(availabiltyyHasBeenChangedEnd != null && availabiltyyHasBeenChangedStart != null) {
                updatePricesBetweenDates(availabiltyyHasBeenChangedStart, availabiltyyHasBeenChangedEnd);
            }

            return sparseUpdateAvailabilityInternal();
        }catch(Exception e) {
            if(!errorNotificationSent) {
                messageManager.sendErrorNotification("Failed to handle update new bookings", e);
                logPrintException(e);
                errorNotificationSent = true;
            }
        }
        return "";
    }

    public void logText(String string) {
        log.logEntries.put(System.currentTimeMillis(), string);
        long old = System.currentTimeMillis();
        old = old - (1000*60*24*3);
        List<Long> toRemove = new ArrayList();
        for(long key : log.logEntries.keySet()) {
            if(key < old) {
                toRemove.add(key);
            }
        }
        for(Long key : toRemove) {
            log.logEntries.remove(key);
        }
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

    /* TEST */
    private Calendar getCalendar(boolean start) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 11);
        
        String time = pmsManager.getConfigurationSecure().getDefaultEnd();
        if(start) {
            time = pmsManager.getConfigurationSecure().getDefaultStart();
        }
        if(time != null && time.contains(":")) {
            String[] times = time.split(":");
            try {
                cal.set(Calendar.HOUR_OF_DAY, new Integer(times[0]));
                cal.set(Calendar.MINUTE, new Integer(times[1]));
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            }catch(Exception e) {
                logPrintException(e);
            }
        }
        return cal;
    }

    private void addAddonsToRoom(WubookBookedRoom room, Hashtable table, PmsBookingAddonItem item, int guests) {
        if(item.channelManagerAddonText== null || item.channelManagerAddonText.isEmpty()) {
            return;
        }
        String[] toCheckFor = item.channelManagerAddonText.split(";");
        
        Hashtable addons = (Hashtable) table.get("ancillary");
        int addoncount = 0;
        if(addons != null) {
            Vector addonsList = (Vector) addons.get("addons");
            if(addonsList != null) {
                Iterator roomIterator = addonsList.iterator();
                while (roomIterator.hasNext()) {
                    Hashtable addon = (Hashtable) roomIterator.next();
                    String name = (String) addon.get("name");
                    String type = (String) addon.get("type");
                    for(String check : toCheckFor) {
                        if(type != null && (type.toLowerCase().contains(check))) {
                            addoncount = (int) addon.get("persons");
                        } else if(name != null && (name.toLowerCase().contains(check))) {
                            addoncount = (int) addon.get("persons");
                        }
                    }
                }
            }
        }
        
        if(addoncount == 0) {
            Gson gson = new Gson();
            String toCheckIn = gson.toJson(table);
            for(String check : toCheckFor) {
                if(toCheckIn.toLowerCase().contains(check.toLowerCase())) {
                    addoncount = guests;
                }
            }
        }
        
        if(addoncount > 0) {
            room.addonsToAdd.put(item.productId, addoncount);
        }
    }

    public boolean forceUpdateOnAvailability(PmsBookingRooms room) {
        Integer rid = -1;
        for(WubookRoomData rdata : wubookdata.values()) {
            if(rdata.bookingEngineTypeId.equals(room.bookingItemTypeId)) {
                rid = rdata.wubookroomid;
            }
        }
        
        boolean forceUpdateDone = false;
        if(rid == null || rid == -1) {
            logPrint("Rid not found for room type: " + room.bookingItemTypeId);
            return forceUpdateDone;
        }
        Calendar start = Calendar.getInstance();
        start.setTime(room.date.start);
        while(true) {
            String roomDateString = convertToDayString(start.getTime());
            
            for(WubookAvailabilityField field : lastAvailability.lastAvailabilityUpdated) {
                if(field.roomId.equals(rid) && field.dateAsString.equals(roomDateString)) {
                    logPrint("Update availability for room: " + field.dateAsString + " for room : " + rid);
                    field.availability = -1;
                    setAvailabilityChanged(room.date.start, room.date.end);
                    forceUpdateDone = true;
                }
            }

            
            start.add(Calendar.DAY_OF_YEAR, 1);
            if(start.getTime().after(room.date.end)) {
                break;
            }
        }
        
        return forceUpdateDone;
    }

    private boolean checkBcomVirtualCard(Hashtable table) {
        try {
            if(!table.get("id_channel").equals(2)) {
                return false;
            }
            Gson test = new Gson();
            String toCheck = test.toJson(table);
            if(toCheck.toLowerCase().contains("virtuelt kredittkort")) {
                return true;
            }
            if(toCheck.toLowerCase().contains("** this reservation has been pre-paid")) {
                return true;
            }
            if(toCheck.toLowerCase().contains("virtual credit card")) {
                return true;
            }
            if(toCheck.toLowerCase().contains("booking note : payment charge is")) {
                return true;
            }
        }catch(Exception e) {
            logPrintException(e);
        }
        
        return false;
    }

    private Vector executeClient(String apicall, Vector params) throws XmlRpcException, IOException {
        
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
        new javax.net.ssl.HostnameVerifier(){

            public boolean verify(String hostname,
                    javax.net.ssl.SSLSession sslSession) {
                return true;
            }
        });
        
        logText("Executing api call: " + apicall);
        try {
            long start = System.currentTimeMillis();
            Vector res = (Vector) client.execute(apicall, params);
            long end = System.currentTimeMillis();
            long diff = end - start;
            if(diff > 2000) {
                logPrint("Excecuted api call: " + apicall + ", time: " + diff);
            }
            
            return res;
        }catch(Exception d) {
            logPrint("Could not connect to wubook on api call: " + apicall + " message: " + d.getMessage());
            disableWubook = new Date();
        }
        return null;
    }

    private Double getSpecialRestriction(Date time, String bookingEngineTypeId, Integer type) {
        List<TimeRepeaterData> minstayours = bookingEngine.getOpeningHoursWithType(bookingEngineTypeId, type);
        
        if(minstayours == null || minstayours.isEmpty()) {
            return null;
        }
        
        TimeRepeater repeater = new TimeRepeater();
        double minstay = 1.0;
        if(type.equals(TimeRepeaterData.TimePeriodeType.noCheckIn)) { minstay = 0.0; }
        if(type.equals(TimeRepeaterData.TimePeriodeType.noCheckOut)) { minstay = 0.0; }
        
        for(TimeRepeaterData res : minstayours) {
            LinkedList<TimeRepeaterDateRange> ranges = repeater.generateRange(res);
            for(TimeRepeaterDateRange range : ranges) {
                if(range.isBetweenTime(time)) {
                    try {
                        minstay = new Integer(res.timePeriodeTypeAttribute);
                    }catch(Exception e) {

                    }
                }
            }
        }
        return minstay;
    }

    private boolean checkIfPrepaid(Hashtable table) {
        try {
            if(!table.get("id_channel").equals(2)) {
                return false;
            }
            Gson test = new Gson();
            String toCheck = test.toJson(table);
            if(toCheck.toLowerCase().contains("** this reservation has been pre-paid")) {
                return true;
            }
            if(toCheck.toLowerCase().contains("booking note : payment charge is")) {
                return true;
            }
        }catch(Exception e) {
            logPrintException(e);
        }
        
        return false;
    }

    private void insertVirtualRooms(WubookRoomData data, BookingItemType type) {
        if(type.size > 10) {
            return;
        }
        logPrint("Virtual rooms, number of guests: " + type.size + ";roomid: " + data.rid + ", data virtualroom ids:" + data.virtualWubookRoomIds + ", name: " + type.name);
        String[] virtualRooms = data.virtualWubookRoomIds.split(";");
        String virtualRoomIds = data.wubookroomid + "";
        for(int i = 2; i <= type.size; i++) {
            logPrint("Need to add virtual room for guest: " + i);
            try {
                int roomId = -1;
                if(virtualRooms.length >= i) {
                    roomId = new Integer(virtualRooms[i-1]);
                    if(roomId == -1) {
                        roomId = insertVirtualRoom(type, i, data);
                    } else {
                        updateVirtualRoom(type,i,data, roomId);
                    }
                } else {
                    roomId = insertVirtualRoom(type, i, data);
                }
                    virtualRoomIds += ";" + roomId;
            }catch(Exception e) {
                
            }
        }
        data.virtualWubookRoomIds = virtualRoomIds;
        data.newRoomPriceSystem = true;
        saveObject(data);
    }

    private Vector createRoomPriceList(WubookRoomData rdata, HashMap<String, Double> pricesForType, Calendar calStart, Vector list, int guests, Date endAtDate) {
        Double defaultPrice = pricesForType.get("default");
        PmsConfiguration config = pmsManager.getConfigurationSecure();
            
        for(int i = 0;i < (365*3); i++) {
            int year = calStart.get(Calendar.YEAR);
            int month = calStart.get(Calendar.MONTH)+1;
            int day = calStart.get(Calendar.DAY_OF_MONTH);
            String dateString = "";

            if(day < 10) { dateString += "0" + day; } else { dateString += day; }
            dateString += "-";
            if(month < 10) { dateString += "0" + month; } else { dateString += month; }
            dateString += "-" + year; 
            Double priceToAdd = null;
            if(pricesForType.containsKey(dateString)) {
                priceToAdd = pricesForType.get(dateString);
            }
            if((priceToAdd == null || priceToAdd == 0.0) && defaultPrice != null) {
                priceToAdd = defaultPrice;
            }
            if(priceToAdd == null) {
                priceToAdd = 999999.0;
            } else if(rdata.newRoomPriceSystem) {
                PmsBookingRooms room = new PmsBookingRooms();
                room.numberOfGuests = guests;
                room.bookingItemTypeId = rdata.bookingEngineTypeId;
                room.date.start = calStart.getTime();
                room.date.end = new Date();
                room.date.end.setTime(calStart.getTimeInMillis()+57600000);
                PmsBooking booking = new PmsBooking();
                pmsManager.setPriceOnRoom(room, true, booking);
                priceToAdd = room.price;
            } else if(pmsManager.getConfigurationSecure().enableCoveragePrices) {
                PmsBooking booking = new PmsBooking();
                priceToAdd = pmsInvoiceManager.calculatePrice(rdata.bookingEngineTypeId, calStart.getTime(), calStart.getTime(), true, booking);
            }
            
            if(priceToAdd == 0.0) {
                priceToAdd = 1.0;
            }
            
            if(config.increaseByPercentage > 0) {
                double factor = 1.15;
                if(config.increaseByPercentage > 0) {
                    factor = 1.0 + ((double)config.increaseByPercentage/100);
                }
                priceToAdd *= factor;
                priceToAdd = (double)Math.round(priceToAdd);
            }

            list.add(priceToAdd);
            if(endAtDate.before(calStart.getTime())) {
                break;
            }
            calStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return list;
    }

    private Integer updateVirtualRoom(BookingItemType type, int guests, WubookRoomData data, Integer roomid) throws Exception {
        if(!connectToApi()) { return -1; }
        List<BookingItem> items = bookingEngine.getBookingItemsByType(type.id);
        Vector<Object> params = new Vector<Object>();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(roomid);
        params.addElement(type.name + "(" + guests + " guests)");
        params.addElement(type.size);
        params.addElement(0);
        params.addElement(9999);
        params.addElement("r" + data.code + "" + guests);
        params.addElement("nb");
        Vector result = executeClient("mod_virtual_room", params);
        Integer response = (Integer) result.get(0);
        String res = "";
        if(response == 0) {
            logPrint("Succesfully added virtual room");
            return (Integer)result.get(1);
        } else {
            return -1;
        }
    }

    @Override
    public List<WubookRoomRateMap> getRoomRates(Integer channelId, Integer channelType) throws Exception {
        if(!connectToApi()) {
            return new ArrayList();
        }
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(channelId);
        
        Vector result = null;
        if(channelType == 2) {
            result = executeClient("bcom_rooms_rates", params);
        } else if(channelType == 1) {
            result = executeClient("exp_rooms_rates", params);
        } else {
            return new ArrayList();
        }
        Integer responseCode = (Integer) result.get(0);
        Hashtable mapping = (Hashtable) result.get(1);
        Hashtable rooms = (Hashtable) mapping.get("rooms");
        List<WubookRoomRateMap> returnChannels = new ArrayList();
        for (Iterator it = rooms.keySet().iterator(); it.hasNext();) {
            String offset = (String) it.next();
            WubookRoomRateMap rate = new WubookRoomRateMap();
            Hashtable rateObject = (Hashtable) rooms.get(offset);
            rate.name = (String) rateObject.get("name");
            rate.wubookRoomId = (Integer) rateObject.get("wbroom");
            rate.id = new Integer(offset);
            returnChannels.add(rate);
        }
        
        return returnChannels;    
    }

    @Override
    public void setRoomRates(Integer channelId, List<WubookRoomRateMap> rates, Integer channelType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean checkIfNeedToAddTaxes(Hashtable roomtable) {
        try {
            Hashtable anc = (Hashtable) roomtable.get("ancillary");
            boolean tax_inclusive = (boolean) anc.get("tax_inclusive");
            return !tax_inclusive;
        }catch(Exception e) {
            //Ignore failure here.
        }
        return false;
    }

    private void addTaxesToRoom(WubookBookedRoom room) {
        WubookRoomData rdata = getWubookRoomData(room.roomId);
        BookingItemType type = bookingEngine.getBookingItemType(rdata.bookingEngineTypeId);
        if(type == null) {
            return;
        }
        Product prod = productManager.getProduct(type.productId);
        for(Date key : room.priceMatrix.keySet()) {
            TaxGroup tax = productManager.getTaxGroup(prod.id, prod.taxgroup, key);
            Double factor = (100 + tax.taxRate) / 100;
            Double current = room.priceMatrix.get(key);
            current *= factor;
            room.priceMatrix.put(key, current);
        }
    }

    private WubookRoomData getWubookRoomData(int roomId) {
        for(WubookRoomData rdata : wubookdata.values()) {
            if(rdata.wubookroomid == roomId) {
                return rdata;
            }
        }
        
        return null;
    }

    private void checkIfPaymentMethodIsActive(String pmethod) {
        if(!storeApplicationPool.isActivated(pmethod)) {
            storeApplicationPool.activateApplication(pmethod);
        }
    }

    @Override
    public void fetchBookingFromCallback(String rcode) throws Exception {
//        try {
//            User user = userManager.getInternalApiUser();
//            userManager.startImpersonationUnsecure("gs_system_scheduler_user");
//            getSession().currentUser = user;
//            
//            logPrint("Fetching wubook booking from callback using rcode: " + rcode);
//            List<PmsBooking> allbookings = pmsManager.getAllBookings(null);
//            Integer toCheck = new Integer(rcode);
//
//            for(PmsBooking pmsbook : allbookings) {
//                List<Integer> allCodesOnOldBooking = getAllResCodesForPmsBooking(pmsbook);
//                for(Integer resCode : allCodesOnOldBooking) {
//                    if(resCode == toCheck) {
//                        logPrint("Booking with rcode: " + toCheck + " already exist");
//                        return;
//                    }
//                }
//            }
//            WubookBooking booking = fetchBooking(rcode);
//            if(booking != null) {
//                logPrint("Booking with rcode: " + toCheck + " found in wubook");
//                addBookingToPms(booking);
//                PmsConfiguration config = pmsManager.getConfigurationSecure();
//                if(!config.wubookcallbackactivated) {
//                    config.wubookcallbackactivated = true;
//                    pmsManager.saveConfiguration(config);
//                }
//            } else {
//                logPrint("Booking with rcode: " + toCheck + " could not be found in wubook");
//            }
//            pmsManager.processor();
//        }catch(Exception e) {
//            logPrintException(e);
//        }
//        userManager.cancelImpersonating();
    }

    @Override
    public void activateWubookCallback() throws Exception {
        String address = getCallbackUrl();
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(address);
        Vector res = executeClient("push_activation", params);
        Integer responseCode = (Integer) res.get(0);
    }

    @Override
    public String getCallbackUrl() {
        String address = storeManager.getMyStore().getDefaultWebAddress() + "/scripts/wubookcallback.php";
        if(!address.startsWith("http")) {
            address = "https://" + address;
        } 
        return address;
    }

    private boolean checkForPaidOrders(PmsBooking newbooking) {
        boolean hasPaidOrders = false;
        
        if(newbooking.orderIds == null) {
            newbooking.orderIds = new ArrayList();
        }
        
        for(String orderId : newbooking.orderIds) {
            Order ord = orderManager.getOrder(orderId);
            if(ord.status == Order.Status.PAYMENT_COMPLETED) {
                hasPaidOrders = true;
            }
        }
        return hasPaidOrders;
    }

    private boolean needUpdateMinStay() {
        return lastAvailability.needUpdateMinStay;
    }

    @Override
    public void doUpdateMinStay() {
        lastAvailability.needUpdateMinStay = true;
        saveObject(lastAvailability);
    }

    public String updatePricesBetweenDates(Date now, Date end) throws Exception {
        if(!connectToApi()) {
            return "Not able to connect";
        }
        
        Hashtable table = new Hashtable();
        
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String dfrom = format.format(now);
        pmsInvoiceManager.startCacheCoverage();
        
        for (WubookRoomData rdata : wubookdata.values()) {
            if(!rdata.addedToWuBook) {
                continue;
            }
            BookingItemType type = bookingEngine.getBookingItemType(rdata.bookingEngineTypeId);
            if(type == null) {
                //Type has been deleted.
                continue;
            }
            
            PmsPricing prices = pmsManager.getPrices(now, end);
            Calendar calStart = Calendar.getInstance();
            calStart.setTime(pmsManager.getConfigurationSecure().getDefaultStart(now));
            
            HashMap<String, Double> pricesForType = prices.dailyPrices.get(rdata.bookingEngineTypeId);
            if(pricesForType == null) {
                logPrint("Invalid price daily prices for : " + rdata.bookingEngineTypeId);
                continue;
            }
            
            String[] roomIds = new String[1];
            roomIds[0] = rdata.wubookroomid + "";
            if(rdata.newRoomPriceSystem) {
                roomIds = rdata.virtualWubookRoomIds.split(";");
            }
            int guests = 1;
            for(String roomId : roomIds) {
                Vector list = new Vector(); 
                Calendar copy = Calendar.getInstance();
                copy.setTime(calStart.getTime());
                list = createRoomPriceList(rdata, pricesForType,copy,list,guests, end);
                if(!list.isEmpty()) {
                    if(!roomId.equals("-1") && !roomId.isEmpty()) {
                        table.put(roomId, list);
                    }
                }
                guests++;
            }
        } 
        
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(0);
        params.addElement(dfrom);
        params.addElement(table);
        
        Vector result = executeClient("update_plan_prices", params);
        if((Integer)result.get(0) != 0) {
            logText("Where not able to update prices:" + result.get(1));
            return "Failed to update price, " + result.get(1);
        } else {
            logText("Prices updated between " + now + " - " + end);
        }
        
        updateMinStay(); 
        
        return "";
    }

    private Integer getMaxForCategory(String bookingEngineTypeId) {
        Integer count = -1;
        if(pmsManager.getConfigurationSecure().maxNumberForEachCategory.containsKey(bookingEngineTypeId)) {
            count = pmsManager.getConfigurationSecure().maxNumberForEachCategory.get(bookingEngineTypeId);
        }
        return count;
    }

    private boolean useNewPrePaidMethod() {
        Date storeDate = storeManager.getMyStore().rowCreatedDate;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        if(cal.getTime().before(storeDate)) {
            return true;
        }
        return false;
    }

    private boolean doesTypeExists(String bookingItemTypeId) {
        BookingItemType type = bookingEngine.getBookingItemType(bookingItemTypeId);
        if(type == null) {
            return false;
        }
        return true;
    }

    public void markRoomsWithNewPricingModel() {
        for(WubookRoomData data : wubookdata.values()) {
            data.newRoomPriceSystem = true;
        }
        saveWubookRoomData(wubookdata);
    }


}

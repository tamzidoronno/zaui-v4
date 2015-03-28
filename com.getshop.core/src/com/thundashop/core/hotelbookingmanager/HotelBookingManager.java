package com.thundashop.core.hotelbookingmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class HotelBookingManager extends ManagerBase implements IHotelBookingManager {

    public GlobalBookingSettings settings = new GlobalBookingSettings();
    public ArxSettings arxSettings = new ArxSettings();
    public VismaSettings vismaSettings = new VismaSettings();

    public HashMap<String, Room> rooms = new HashMap();
    public HashMap<String, RoomType> roomTypes = new HashMap();
    private VismaUsers transferredUsers = new VismaUsers();
    private Date lastPulled = null;
    private boolean warnedAboutArxDown = false;
    private List<TempAccess> tempAccessList = new ArrayList();
    private List<UsersBookingData> usersBookingData = new ArrayList();

    public List<ArxLogEntry> logEntries = new ArrayList();
    
    @Autowired
    private StoreManager storeManager;
	
    @Autowired
    private MessageManager messageManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private OrderManager orderManager;

    @Autowired
    private PageManager pageManager;

    @Autowired
    private CartManager cartManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dbobj : data.data) {
            if (dbobj instanceof Room) {
                Room room = (Room) dbobj;
                rooms.put(dbobj.id, room);
            }
            if (dbobj instanceof UsersBookingData) {
                UsersBookingData reference = (UsersBookingData) dbobj;
                usersBookingData.add(reference);
            }
            if (dbobj instanceof ArxSettings) {
                arxSettings = (ArxSettings) dbobj;
            }            
            if (dbobj instanceof GlobalBookingSettings) {
                if(settings.id != null && !settings.id.isEmpty()) {
                    deleteObject(dbobj);
                } else {
                    settings = (GlobalBookingSettings) dbobj;
                }
            }            
        }
    }
    
    public List<UsersBookingData> getAllUsersBookingData() {
        return usersBookingData;
    }
    
    @Override
    public Integer checkAvailable(long startDate, long endDate, String productId, AdditionalBookingInformation additional) throws ErrorException {
        List<String> takenRooms = new ArrayList();
        for(UsersBookingData bookingData : getAllUsersBookingData()) {
            if(!bookingData.active) {
                continue;
            }
            if(bookingData.sessionId.equals(getSession().id)) {
                continue;
            }
            
            for(BookingReference reference :bookingData.references) {
                if(reference.isBetweenDates(startDate*1000, endDate*1000)) {
                    for(RoomInformation info : reference.roomsReserved) {
                        takenRooms.add(info.roomId);
                    }
                }
            }
        }
        
        List<Room> allRoomsOnProduct = findAllRoomsOnProduct(productId);
        
        Integer count = 0;
        for(Room room : allRoomsOnProduct) {
            if(!takenRooms.contains(room.id)) {
                if(additional.needHandicap) {
                    if(room.isHandicap) {
                        count++;
                    }
                } else {
                    count++;
                }
            }
        }
        
        return count;
    }

    @Override
    public void reserveRoom(String roomProductId, long startDate, long endDate, Integer count, AdditionalBookingInformation additionalInfo) throws ErrorException {
        List<Room> availableRoom = getAvailableRooms(roomProductId, startDate, endDate, additionalInfo);
        List<Room> roomsToBook = new ArrayList();
        for(Room room : availableRoom) {
            roomsToBook.add(room);
            if(roomsToBook.size() == count) {
                break;
            }
        }
        
        if(roomsToBook.size() < count) {
            throw new ErrorException(1032);
        }
        
        Date start = new Date(startDate * 1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 0);
        start = cal.getTime();

        Date end = new Date(endDate * 1000);
        cal.setTime(end);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        end = cal.getTime();

        UsersBookingData bookingData = getCurrentUserBookingData();
        bookingData.references = new ArrayList();
        bookingData.additonalInformation = additionalInfo;
        bookingData.roomProductId = roomProductId;
        
        BookingReference reference = new BookingReference();
        reference.bookingReference = genereateReferenceId();
        reference.startDate = start;
        reference.endDate = end;
        reference.roomsReserved = new ArrayList();
        for(Room room : roomsToBook) {
            RoomInformation info = new RoomInformation();
            info.roomId = room.id;
            reference.roomsReserved.add(info);
        }
        bookingData.references.add(reference);
        saveObject(bookingData);
    }

    private int genereateReferenceId() throws ErrorException {
        settings.referenceCount++;
        int count = settings.referenceCount;
        saveObject(settings);
        return count;
    }
    
    @Override
    public void saveRoom(Room room) throws ErrorException {
        saveObject(room);
        rooms.put(room.id, room);
    }

    @Override
    public void removeRoom(String id) throws ErrorException {
        Room room = getRoom(id);
        rooms.remove(id);
        deleteObject(room);
    }

    @Override
    public Room getRoom(String id) throws ErrorException {
        return rooms.get(id);
    }

    @Override
    public String getEmailMessage(String language) throws ErrorException {
        if (arxSettings == null) {
            return "";
        }
        if (language != null && language.equals("nb_NO")) {
            return arxSettings.emailWelcomeNO;
        }
        if (arxSettings.emailWelcome == null || arxSettings.emailWelcome.isEmpty()) {
            return "";
        }
        return arxSettings.emailWelcome;        
    }

    @Override
    public void moveRoomOnReference(Integer reference, String oldRoom, String newRoomId) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Room> getAllRooms() throws ErrorException {
        
        List<Room> rooms = new ArrayList(this.rooms.values());
        Collections.sort(rooms,new Comparator<Room>() { 
            public int compare(Room a, Room b) {
                return a.roomName.compareTo(b.roomName);
            }
        });
        
        return rooms;
    }

    @Override
    public List<BookingReference> getAllReservations() throws ErrorException {
        List<BookingReference> reservations = new ArrayList();
        for(UsersBookingData bdata : getAllUsersBookingData()) {
            finalize(bdata);
            reservations.addAll(bdata.references);
        }
        return reservations;
    }

    @Override
    public void deleteReference(int reference) throws ErrorException {
        for(UsersBookingData bdata : getAllUsersBookingData()) {
            BookingReference toremove = null;
            for(BookingReference referenceObject : bdata.references) {
                if(referenceObject.bookingReference == reference) {
                    toremove = referenceObject;
                }
            }
            if(toremove != null) {
                bdata.references.remove(toremove);
                saveObject(bdata);
                break;
            }
        }
    }

    @Override
    public void setArxConfiguration(ArxSettings settings) throws ErrorException {
        if (arxSettings == null) {
            arxSettings = new ArxSettings();
        }
        settings.id = arxSettings.id;
        arxSettings = settings;
        saveObject(arxSettings);
    }

    @Override
    public void setVismaConfiguration(VismaSettings settings) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void markRoomAsReady(String roomId) throws ErrorException {
        Room room = getRoom(roomId);
        room.isClean = true;
        saveObject(room);
    }

    @Override
    public BookingReference getReservationByReferenceId(Integer referenceId) throws ErrorException {
        for(UsersBookingData bdata : getAllUsersBookingData()) {
             for(BookingReference referenceObject : bdata.references) {
                if(referenceObject.bookingReference == referenceId) {
                    return referenceObject;
                }
            }
        }
        return null;
    }

    @Override
    public List<ArxLogEntry> getArxLog() throws ErrorException {
        sortLogEntries(logEntries);
        return logEntries;        
    }

    private void sortLogEntries(List<ArxLogEntry> logEntries) {
        Collections.sort(logEntries, new Comparator<ArxLogEntry>() {
            public int compare(ArxLogEntry f1, ArxLogEntry f2) {
                return f2.rowCreatedDate.compareTo(f1.rowCreatedDate);
            }
        });
    }

    @Override
    public void checkForVismaTransfer() throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void checkForArxTransfer() throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void checkForWelcomeMessagesToSend() throws ErrorException {
        
        if(lastPulled != null) {
            if (new Date().getTime() - lastPulled.getTime() >= 5*60*1000) {
                warnAboutArxDown();
            } else {
                if(warnedAboutArxDown) {
                    notifyArxUp();
                }
            }
        }
        
        
        for(UsersBookingData bdata : getAllUsersBookingData()) {
            
            for(BookingReference reference : bdata.references) {
                if(!bdata.payedFor) {
                    continue;
                }
                if(!reference.sentWelcomeMessages) {
                    System.out.println("Welcome message needs to be sent for: " + reference.bookingReference);
                    for(RoomInformation room : reference.roomsReserved) {
                        Visitors visitor = room.visitors.get(0);
                        String title = formatMessage(reference, arxSettings.emailWelcomeTitleNO, getRoom(room.roomId).roomName, 0, visitor.name);
                        String message = formatMessage(reference, arxSettings.emailWelcomeNO, getRoom(room.roomId).roomName, 0, visitor.name);
                        String sms = formatMessage(reference, arxSettings.smsWelcomeNO, getRoom(room.roomId).roomName, 0, visitor.name);
                        sendEmail(visitor, title, message);
                        sendSms(visitor,sms);
                    }
                    reference.sentWelcomeMessages = true;
                    saveObject(bdata);
                }
            }
        }
    }

    @Override
    public ArrayList getRoomProductIds() throws ErrorException {
        HashMap<String, Integer> selection = new HashMap();
        for(Room room : rooms.values()) {
             selection.put(room.productId, 1);
        }
        
        return new ArrayList(selection.keySet());
    }
    
    private Calendar getTodayCalendar() {
        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(new Date(System.currentTimeMillis()));
        todayCal.set(Calendar.HOUR_OF_DAY, 11);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        return todayCal;
    }
    

    private Date getStartDate(long startDate) {
        Date start = new Date(startDate * 1000);
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        startCal.set(Calendar.HOUR_OF_DAY, 13);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        start = startCal.getTime();
        return start;
    }

    private Date getEndDate(long endDate) {
        Date end = new Date(endDate * 1000);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        endCal.set(Calendar.HOUR_OF_DAY, 11);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        end = endCal.getTime();
        return end;
    }

    @Override
    public Integer checkAvailableParkingSpots(long startDate, long endDate) throws ErrorException {

        Calendar todayCal = getTodayCalendar();
        Date start = getStartDate(startDate);
        Date end = getEndDate(endDate);

        if (start.before(todayCal.getTime())) {
            return -1;
        }

        if (end.before(start)) {
            return -2;
        }

        int totalCount = settings.parkingSpots;
        for (BookingReference reference : getAllReservations()) {
            if (reference.isBetween(start, end)) {
                totalCount -= reference.parkingSpots;
            }
        }
        if (totalCount < 0) {
            return 0;
        }

        return totalCount;

    }
    
    
    private List<Room> findAllRoomsOnProduct(String productId) {
        List<Room> allRooms = new ArrayList();
        for(Room room : rooms.values()) {
            if(room.productId.equals(productId)) {
                allRooms.add(room);
            }
        }
        return allRooms;
    }

    @Override
    public void setBookingConfiguration(GlobalBookingSettings settings) throws ErrorException {
        this.settings = settings;
        saveObject(settings);
    }
    
    @Override
    public GlobalBookingSettings getBookingConfiguration() throws ErrorException {
        return settings;
    }


    private boolean isAvailable(Room room, long startDate, long endDate) {
        for(UsersBookingData bdata : getAllUsersBookingData()) {
            if(!bdata.active) {
                continue;
            }
            
            if(bdata.sessionId.equals(getSession().id)) {
                continue;
            }
            
            for(BookingReference reference : bdata.references) {
                if(reference.isBetweenDates(startDate*1000, endDate*1000) && reference.getAllRooms().contains(room.id)) {
                    return false;
                } 
            }
        }
        
        return true;
    }

    private String formatMessage(BookingReference reference, String message, String roomName, Integer code, String name) throws ErrorException {
       if (code != null) {
            message = message.replaceAll("\\{code\\}", code + "");
        }
        if (roomName != null) {
            message = message.replaceAll("\\{room\\}", roomName);
        }
        String startMinute = new SimpleDateFormat("m").format(reference.startDate).toString();
        if (startMinute.length() < 2) {
            startMinute = "0" + startMinute;
        }
        String endMinute = new SimpleDateFormat("m").format(reference.endDate).toString();
        if (endMinute.length() < 2) {
            endMinute = "0" + endMinute;
        }
        
        String startday = new SimpleDateFormat("d").format(reference.startDate);
        String startmonth = new SimpleDateFormat("M").format(reference.startDate);
        String startyear = new SimpleDateFormat("y").format(reference.startDate);
        String endday = new SimpleDateFormat("d").format(reference.endDate);
        String endmonth = new SimpleDateFormat("M").format(reference.endDate);
        String endyear = new SimpleDateFormat("y").format(reference.endDate);
        
        if(startday.length() == 1) { startday = "0" + startday; }
        if(startmonth.length() == 1) { startmonth = "0" + startmonth; }
        if(startyear.length() == 1) { startyear = "0" + startyear; }
        if(endday.length() == 1) { endday = "0" + endday; }
        if(endmonth.length() == 1) { endmonth = "0" + endmonth; }
        if(endyear.length() == 1) { endyear = "0" + endyear; }
        
        message = message.replaceAll("\\{checkin_time\\}", new SimpleDateFormat("H:").format(reference.startDate) + startMinute);
        message = message.replaceAll("\\{checkin_date\\}", startday + "-" + startmonth + "-" + startyear);
        message = message.replaceAll("\\{checkout_date\\}",  endday + "-" + endmonth + "-" + endyear);
        message = message.replaceAll("\\{checkout_time\\}", new SimpleDateFormat("H:").format(reference.endDate) + endMinute);
        message = message.replaceAll("\\{name\\}", name);
        message = message.replaceAll("\\{referenceNumber\\}", reference.bookingReference + "");
        String contacts = "";
        for(RoomInformation roomReserved : reference.roomsReserved) {
            for(Visitors visitor : roomReserved.visitors) {
                contacts += visitor.name + "<br>";
                contacts += visitor.phone + "<br>";
                contacts += visitor.email + "<br>";
            }
        }
        
        message = message.replaceAll("\\{contacts\\}", contacts);
        Order order = orderManager.getOrderByReference(reference.bookingReference + "");
        if (order != null) {
            message = message.replaceAll("\\{roomName\\}", order.cart.getItems().get(0).getProduct().name + "");
            User user = userManager.getUserById(order.userId);
            if (user != null) {
                message = message.replaceAll("\\{email\\}", user.emailAddress);
                message = message.replaceAll("\\{address\\}", user.address.address);
                message = message.replaceAll("\\{postCode\\}", user.address.postCode);
                message = message.replaceAll("\\{city\\}", user.address.city);
            }
        }
        return message;
    }
    
    
    @Override
    public void notifyUserAboutRoom(BookingReference reference, RoomInformation roomInfo, Integer code) throws ErrorException {
        String origMessage = arxSettings.smsReady;
        Room room = getRoom(roomInfo.roomId);
        Visitors visitor = roomInfo.visitors.get(0);
        String message = formatMessage(reference, origMessage, room.roomName, code, visitor.name);
        messageManager.sendSms(visitor.phone, message);
    }
    
    private List<Room> getAvailableRooms(String roomProductId, long startDate, long endDate, AdditionalBookingInformation additionalInfo) {
        List<Room> allRooms = findAllRoomsOnProduct(roomProductId);
        List<Room> allRoomsToBook = new ArrayList();
        List<Room> shortTerm = new ArrayList();
        List<Room> longTerm = new ArrayList();

        
        for(Room room : allRooms) {
            if(isAvailable(room, startDate, endDate)) {
                if(room.suitedForLongTerm) {
                    longTerm.add(room);
                } else {
                    shortTerm.add(room);
                }
            }
        }
        
        long diff = (endDate-startDate)/1000/86400;
        if(diff > settings.longTermRentalDays) {
            allRoomsToBook.addAll(longTerm);
            allRoomsToBook.addAll(shortTerm);
        } else {
            allRoomsToBook.addAll(shortTerm);
            allRoomsToBook.addAll(longTerm);
        }
        
        return allRoomsToBook;
    }

    private void finalize(UsersBookingData reservation) {
        if(reservation == null) {
            System.out.println("Tried to finalize null resverence");
            return;
        }
        reservation.payedFor = false;
        if(!reservation.active) {
            return;
        }
        if(reservation.partnerReference) {
            reservation.payedFor = true;
        } else {
            if(!reservation.orderIds.isEmpty()) {
                Order order = orderManager.getOrder(reservation.orderIds.get(0));
                if(order.status == Order.Status.PAYMENT_COMPLETED) {
                    reservation.payedFor = true;
                }
            }
        }
        
    }

    public String getUserIdForRoom(String roomNumber) {
        for(UsersBookingData bdata : getAllUsersBookingData()) {
            for (BookingReference ref : bdata.references) {
                if (!ref.isActive()) {
                    continue;
                }

                if (!ref.containsRoom(roomNumber, rooms)) {
                    continue;
                }
                return bdata.additonalInformation.userId;
            }
        }
        
        return "";
    }
    
    private void sendEmail(Visitors visitor, String title, String message) {
        String msg = "Sending mail to " + visitor.email + " title: " + title + " message: " + message;
            
         String copyadress = "toreplaced@test.no";       
//        String copyadress = getSettings("Settings").get("mainemailaddress").value;
        
        messageManager.sendMail(visitor.email, visitor.name, title, message, copyadress, copyadress);
        ArxLogEntry newEntry = new ArxLogEntry();
        newEntry.message = msg;
        saveObject(newEntry);
        logEntries.add(newEntry);
    }

    private void sendSms(Visitors visitor, String sms) {
        String msg = "";
        try {
            msg = "Sending sms to " + visitor.phone + " title: " + visitor.name + " message: " + sms;
            messageManager.sendSms(visitor.phone, sms);
            ArxLogEntry newEntry = new ArxLogEntry();
            newEntry.message = msg;
            saveObject(newEntry);
        }catch(Exception e) {
            msg = "Failed sending sms to " + visitor.phone + " title: " + visitor.name + " message: " + sms;
        }
        
        ArxLogEntry newEntry = new ArxLogEntry();
        newEntry.message = msg;
        saveObject(newEntry);
        logEntries.add(newEntry);
        
    }

    @Override
    public List<BookingReference> getAllReservationsArx() throws ErrorException {
        lastPulled = new Date();
        List<BookingReference> references = new ArrayList();
        for(UsersBookingData bdata : getAllUsersBookingData()) {
            if(!bdata.payedFor) {
                continue;
            }
            if(!bdata.active) {
                continue;
            }
            references.addAll(bdata.references);
        }
        return references;
    }

    private void warnAboutArxDown() {
        if(!warnedAboutArxDown) {
            warnedAboutArxDown = true;
            messageManager.sendMail("post@getshop.com", "GetShop Support", "arx down", "Not getting pull messages from arx anymore. seems to be down.", "post@getshop.com", "post@getshop.com");
        }
    }

    private void notifyArxUp() {
        warnedAboutArxDown = false;
        messageManager.sendMail("post@getshop.com", "GetShop Support", "arx back up", "Yez.", "post@getshop.com", "post@getshop.com");
    }

    @Override
    public boolean isRoomAvailable(String roomId, long startDate, long endDate) throws ErrorException {
        Room room = getRoom(roomId);
        return isAvailable(room, startDate, endDate);
    }

    @Override
    public void checkForOrdersToGenerate() throws ErrorException {
        List<BookingReference> reservations = getAllReservations();
        Calendar nowCal = Calendar.getInstance();
        Date nowDate = new Date();
        nowCal.setTime(nowDate);
        String now = nowCal.get(Calendar.MONTH) + "-" + nowCal.get(Calendar.YEAR);
        
        //This needs to be recalculated.
    }


    @Override
    public void tempGrantAccess(Integer reference, String roomId) throws ErrorException {
        TempAccess toRecover = new TempAccess();
        toRecover.referenceNumber = reference;
        toRecover.roomName = getRoom(roomId).roomName;
        tempAccessList.add(toRecover);
    }

    @Override
    public List<TempAccess> getAllTempAccesses() throws ErrorException {
        return tempAccessList;
    }

    @Override
    public UsersBookingData getCurrentUserBookingData() {
        UsersBookingData tempData = null;
        for(UsersBookingData bdata : getAllUsersBookingData()) {
            if(bdata.sessionId.equals(getSession().id)) {
                tempData = bdata;
            }
        }
        if(tempData == null) {
            tempData = new UsersBookingData();
            tempData.sessionId = getSession().id;
            usersBookingData.add(tempData);
        }
        
        return tempData;
    }

    @Override
    public void updateCart() {
        UsersBookingData bookingData = getCurrentUserBookingData();
        cartManager.clear();
        for(BookingReference reference : bookingData.references) {
            for(int i = 0; i < reference.roomsReserved.size(); i++) {
                int count = reference.getNumberOfNights();
                cartManager.addProductItem(bookingData.roomProductId, count);
            }
        }
    }

    @Override
    public void setVistorData(HashMap<Integer, List<Visitors>> data) throws ErrorException {
        UsersBookingData bookingdata = getCurrentUserBookingData();
        for(Integer roomIndex : data.keySet()) {
            for(BookingReference reference : bookingdata.references) {
                reference.roomsReserved.get(roomIndex).visitors.clear();
                reference.roomsReserved.get(roomIndex).visitors.addAll(data.get(roomIndex));
            }
        }
        saveObject(bookingdata);
    }

    @Override
    public String completeOrder(String referenceKey, boolean partner) {
        UsersBookingData bookingData = getCurrentUserBookingData();
        User user = userManager.getUserByReference(referenceKey);
        bookingData.additonalInformation.userId = user.id;
        updateCart();
        Order order = orderManager.createOrderForUser(user.id);
        bookingData.sessionId = "";
        bookingData.orderIds.add(order.id);
        saveObject(bookingData);
        return order.id;
    }
}

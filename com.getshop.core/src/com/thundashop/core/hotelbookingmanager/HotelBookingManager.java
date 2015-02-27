package com.thundashop.core.hotelbookingmanager;

import com.getshop.scope.GetShopSession;
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
import jdk.nashorn.internal.runtime.regexp.joni.Config;
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
    public HashMap<Integer, BookingReference> bookingReferences = new HashMap();
    private VismaUsers transferredUsers = new VismaUsers();

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
    

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dbobj : data.data) {
            if (dbobj instanceof Room) {
                Room room = (Room) dbobj;
                rooms.put(dbobj.id, room);
            }
            if (dbobj instanceof BookingReference) {
                BookingReference reference = (BookingReference) dbobj;
                bookingReferences.put(reference.bookingReference, reference);
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
    
    @Override
    public Integer checkAvailable(long startDate, long endDate, String productId, AdditionalBookingInformation additional) throws ErrorException {
        List<String> takenRooms = new ArrayList();
        for(BookingReference reference :bookingReferences.values()) {
            if(reference.isBetweenDates(startDate*1000, endDate*1000)) {
                for(RoomInformation info : reference.roomsReserved) {
                    takenRooms.add(info.roomId);
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
    public String reserveRoom(String roomProductId, long startDate, long endDate, List<RoomInformation> roomInfo, AdditionalBookingInformation additionalInfo) throws ErrorException {
        startDate *= 1000;
        endDate *= 1000;
        List<Room> availableRoom = getAvailableRooms(roomProductId, startDate, endDate, additionalInfo);
        List<Room> roomsToBook = new ArrayList();
        for(Room room : availableRoom) {
            roomsToBook.add(room);
            if(roomsToBook.size() == roomInfo.size()) {
                break;
            }
        }
        
        if(roomsToBook.size() < roomInfo.size()) {
            throw new ErrorException(1032);
        }
        
        for(int i = 0; i < roomInfo.size(); i++) {
            roomInfo.get(i).roomId = roomsToBook.get(i).id;
        }
        
        BookingReference reference = new BookingReference();
        reference.startDate = new Date();
        reference.startDate.setTime(startDate);
        reference.bookingReference = genereateReferenceId();
        reference.endDate = new Date();
        reference.endDate.setTime(endDate);
        
        reference.roomsReserved = roomInfo;
        
        saveObject(reference);
        bookingReferences.put(reference.bookingReference, reference);
        return new Integer(reference.bookingReference).toString();
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
        return new ArrayList(rooms.values());
    }

    @Override
    public List<BookingReference> getAllReservations() throws ErrorException {
        List<BookingReference> reservations = new ArrayList(bookingReferences.values());
        for(BookingReference reservation : reservations) {
            finalize(reservation);
        }
        return reservations;
    }

    @Override
    public void deleteReference(int reference) throws ErrorException {
        BookingReference todelete = getReservationByReferenceId(reference);
        databaseSaver.deleteObject(todelete, credentials);
        bookingReferences.remove(reference);
    }

    @Override
    public void updateReservation(BookingReference reference) throws ErrorException {
        saveObject(reference);
        bookingReferences.put(reference.bookingReference, reference);
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
        return bookingReferences.get(referenceId);
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
        List<BookingReference> reservations = getAllReservations();
        
        for(BookingReference reference : reservations) {
            if(reference.payedFor && !reference.sentWelcomeMessages) {
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
                saveObject(reference);
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
        for(BookingReference reference : bookingReferences.values()) {
            if(reference.isBetweenDates(startDate, endDate) && reference.getAllRooms().contains(room.id)) {
                return false;
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
        message = message.replaceAll("\\{checkin_time\\}", new SimpleDateFormat("dd-MM-yyyy H:").format(reference.startDate) + startMinute);
        message = message.replaceAll("\\{checkin_date\\}", new SimpleDateFormat("dd-MM-yyyy").format(reference.startDate));
        message = message.replaceAll("\\{checkout_time\\}", new SimpleDateFormat("dd-MM-yyyy H:").format(reference.endDate) + endMinute);
        message = message.replaceAll("\\{name\\}", name);
        message = message.replaceAll("\\{referenceNumber\\}", reference.bookingReference + "");
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

    private void finalize(BookingReference reservation) {
        Order order = orderManager.getOrderByReference(reservation.bookingReference+"");
        reservation.orderId = order.incrementOrderId;
        if(order.status == Order.Status.PAYMENT_COMPLETED) {
            reservation.payedFor = true;
        } else {
            reservation.payedFor = false;
        }
    }

    public String getUserIdForRoom(String roomNumber) {
        for (BookingReference ref : bookingReferences.values()) {
            if (!ref.isBetweenCheckinAndCheckout()) {
                continue;
            }
            
            if (!ref.containsRoom(roomNumber, rooms)) {
                continue;
            }
            
            System.out.println("Ref: " + ref.bookingReference);
            Order order = orderManager.getOrderByReference(""+ref.bookingReference);
            if (order != null) {
                return order.userId;
            }
        }
        
        return "";
    }
    
    private void sendEmail(Visitors visitor, String title, String message) {
        String msg = "Sending mail to " + visitor.email + " title: " + title + " message: " + message;
        messageManager.sendMail(visitor.email, visitor.name, title, message, arxSettings.smsFrom, "");
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
}

package com.thundashop.core.hotelbookingmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pagemanager.PageManager;
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

@Component
@GetShopSession
public class HotelBookingManager extends ManagerBase implements IHotelBookingManager {

    public BookingSettings booksettings = new BookingSettings();
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
        }
    }
    
    @Override
    public Integer checkAvailable(long startDate, long endDate, String productId) throws ErrorException {
        List<String> takenRooms = new ArrayList();
        for(BookingReference reference :bookingReferences.values()) {
            if(reference.isBetweenDates(startDate, endDate)) {
                for(RoomInformation info : reference.roomsReserved) {
                    takenRooms.add(info.roomId);
                }
            }
        }
        
        List<Room> allRoomsOnProduct = findAllRoomsOnProduct(productId);
        
        Integer count = 0;
        for(Room room : allRoomsOnProduct) {
            if(!takenRooms.contains(room.id)) {
                count++;
            }
        }
        
        return count;
    }

    @Override
    public String reserveRoom(String roomProductId, long startDate, long endDate, List<RoomInformation> roomInfo, AdditionalBookingInformation additionalInfo) throws ErrorException {
        startDate *= 1000;
        endDate *= 1000;
        List<Room> allRooms = findAllRoomsOnProduct(roomProductId);
        List<Room> roomsToBook = new ArrayList();
        for(Room room : allRooms) {
            if(isAvailable(room, startDate, endDate)) {
                roomsToBook.add(room);
            }
            
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
        booksettings.referenceCount++;
        int count = booksettings.referenceCount;
        saveObject(booksettings);
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
        return new ArrayList(bookingReferences.values());
    }

    @Override
    public void deleteReference(int reference) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateReservation(BookingReference reference) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        settings.storeId = storeId;
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
}

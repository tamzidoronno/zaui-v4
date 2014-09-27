package com.thundashop.core.hotelbookingmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.storemanager.StoreManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class HotelBookingManager extends ManagerBase implements IHotelBookingManager {

    public BookingSettings booksettings = new BookingSettings();
    public ArxSettings arxSettings;

    public HashMap<String, Room> rooms = new HashMap();
    public HashMap<String, RoomType> roomTypes = new HashMap();
    public HashMap<Integer, BookingReference> bookingReferences = new HashMap();

    public List<ArxLogEntry> logEntries = new ArrayList();
    
    @Autowired
    private ArxAccessCommunicator communicator;
    
    @Autowired
    private StoreManager storeManager;

    @PostConstruct
    public void addManager() {
        communicator.addManager(this);
    }

    @Autowired
    MessageManager msgmgr;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        List<ArxLogEntry> tmpLogEntries = new ArrayList();
        for (DataCommon dbobj : data.data) {
            if (dbobj instanceof ArxSettings) {
                arxSettings = (ArxSettings) dbobj;
            }
            if (dbobj instanceof ArxLogEntry) {
                tmpLogEntries.add((ArxLogEntry) dbobj);
            }
            if (dbobj instanceof RoomType) {
                roomTypes.put(dbobj.id, (RoomType) dbobj);
            }
            if (dbobj instanceof BookingSettings) {
                booksettings = (BookingSettings) dbobj;
            }

            if (dbobj instanceof Room) {
                Room room = (Room) dbobj;
                room.bookedDates = new ArrayList();
                rooms.put(dbobj.id, room);
            }
            if (dbobj instanceof BookingReference) {
                BookingReference reference = (BookingReference) dbobj;
                bookingReferences.put(reference.bookingReference, reference);
            }
        }
        
        sortLogEntries(tmpLogEntries);
        int i =0;
        for(ArxLogEntry entry : tmpLogEntries) {
            logEntries.add(entry);
            i++;
            if(i > 100) {
                break;
            }
        }
    }

    @Override
    public Integer checkAvailable(long startDate, long endDate, String typeName) throws ErrorException {
        Date start = new Date(startDate * 1000);
        Date end = new Date(endDate * 1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(System.currentTimeMillis()));
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);

        if (start.before(cal.getTime())) {
            return -1;
        }

        if (end.before(start)) {
            return -2;
        }

        RoomType rtype = null;
        for (RoomType type : roomTypes.values()) {
            if (type.name.trim().equalsIgnoreCase(typeName.trim())) {
                rtype = type;
            }
        }

        if (rtype == null) {
            msgmgr.mailFactory.send("post@getshop.com", "post@getshop.com", "Booking failed for " + storeId + " room type is fail type : " + typeName, storeManager.getMyStore().webAddress + " : " + storeManager.getMyStore().webAddressPrimary + " : ");
            throw new ErrorException(1023);
        }

        int count = 0;
        for (Room room : rooms.values()) {
            if (room.roomType != null && room.roomType.equals(rtype.id) && room.isAvilable(start, end)) {
                count++;
            }
        }

        return count;
    }

    @Override
    public String reserveRoom(String roomType, long startDate, long endDate, int count, ContactData contact, boolean markRoomInactive) throws ErrorException {
        //First make sure there is enough rooms available.
        RoomType roomtype = getRoomType(roomType);
        Integer availableRooms = checkAvailable(startDate, startDate, roomtype.name);
        if (availableRooms < count) {
            return "-1";
        }

        Date start = new Date(startDate * 1000);
        Date end = new Date(endDate * 1000);

        BookingReference reference = new BookingReference();
        reference.bookingReference = genereateReferenceId();
        reference.startDate = start;
        reference.endDate = end;

        for (int i = 0; i < count; i++) {
            Room room = getAvailableRoom(roomtype.id, start, end);
            reference.codes.add(room.reserveDates(start, end, reference.bookingReference));
            reference.roomIds.add(room.id);
            room.storeId = storeId;
            if (markRoomInactive) {
                room.isActive = false;
            }
            databaseSaver.saveObject(room, credentials);
        }
        reference.storeId = storeId;
        reference.contact = contact;

        databaseSaver.saveObject(reference, credentials);
        bookingReferences.put(reference.bookingReference, reference);
        return new Integer(reference.bookingReference).toString();
    }

    @Override
    public void saveRoom(Room room) throws ErrorException {
        room.storeId = storeId;
        if (!room.isActive) {
            System.out.println("Room is inactive");
        } else {
            System.out.println("Is active");
        }
        databaseSaver.saveObject(room, credentials);
        rooms.put(room.id, room);
    }

    @Override
    public void removeRoom(String id) throws ErrorException {
        Room room = rooms.get(id);
        databaseSaver.deleteObject(room, credentials);
        rooms.remove(id);
    }


    @Override
    public List<Room> getAllRooms() throws ErrorException {
        List<Room> room = new ArrayList(rooms.values());
        Collections.sort(room,
                new Comparator<Room>() {
                    public int compare(Room f1, Room f2) {
                        return f1.roomName.compareTo(f2.roomName);
                    }
                });
        return room;
    }

    @Override
    public List<RoomType> getRoomTypes() throws ErrorException {
        ArrayList retval = new ArrayList(roomTypes.values());

        Collections.sort(retval,
                new Comparator<RoomType>() {
                    public int compare(RoomType f1, RoomType f2) {
                        return f1.name.compareTo(f2.name);
                    }
                });

        return retval;
    }

    @Override
    public void saveRoomType(RoomType type) throws ErrorException {
        type.storeId = storeId;
        databaseSaver.saveObject(type, credentials);
        roomTypes.put(type.id, type);
    }

    @Override
    public void removeRoomType(String id) throws ErrorException {
        RoomType type = roomTypes.get(id);
        List<Room> allRooms = getAllRooms();
        for (Room room : allRooms) {
            if (room.roomType.equals(type.id)) {
                throw new ErrorException(1020);
            }
        }
        databaseSaver.deleteObject(type, credentials);
        roomTypes.remove(id);
    }

    private Room getAvailableRoom(String roomTypeId, Date startDate, Date endDate) {
        for (Room room : rooms.values()) {
            if (room.roomType == null || !room.roomType.equals(roomTypeId)) {
                continue;
            }
            if (room.isAvilable(startDate, endDate)) {
                return room;
            }
        }

        return null;
    }

    private int genereateReferenceId() throws ErrorException {
        booksettings.referenceCount++;
        int count = booksettings.referenceCount;
        saveObject(booksettings);
        return count;
    }

    private RoomType getRoomType(String roomTypeName) {
        for (RoomType roomtype : roomTypes.values()) {
            if (roomtype.name.equals(roomTypeName)) {
                return roomtype;
            }
        }
        return null;
    }

    @Override
    public List<BookingReference> getAllReservations() throws ErrorException {
        List<BookingReference> result = new ArrayList(bookingReferences.values());
        Collections.sort(result, new Comparator<BookingReference>() {
            public int compare(BookingReference reference1, BookingReference reference2) {
                return reference1.rowCreatedDate.compareTo(reference2.rowCreatedDate);
            }
        });

        return result;
    }

    @Override
    public void deleteReference(int reference) throws ErrorException {
        BookingReference ref = bookingReferences.get(reference);
        if (ref == null) {
            throw new ErrorException(1025);
        }
        for (String roomId : ref.roomIds) {
            Room room = getRoom(roomId);
            room.removeBookedRoomWithReferenceNumber(reference);
            databaseSaver.saveObject(room, credentials);
        }
        databaseSaver.deleteObject(ref, credentials);
        bookingReferences.remove(reference);
    }

    @Override
    public Room getRoom(String id) throws ErrorException {
        return rooms.get(id);
    }

    @Override
    public BookingReference getReservationByReferenceId(Integer referenceId) throws ErrorException {
        List<BookingReference> allReservations = getAllReservations();
        for (BookingReference reference : allReservations) {
            if (reference.bookingReference == referenceId) {
                return reference;
            }
        }
        return null;
    }

    @Override
    public void moveRoomOnReference(Integer reference, String oldRoom, String newRoomId) throws ErrorException {
        BookingReference bookingreference = getReservationByReferenceId(reference);
        Room existingRoom = getRoom(oldRoom);
        Room newRoom = getRoom(newRoomId);

        List<BookedDate> existingBookingDates = existingRoom.getBookedDatesByReference(reference);
        newRoom.bookedDates.addAll(existingBookingDates);
        existingRoom.bookedDates.removeAll(existingBookingDates);
        bookingreference.updateArx = true;
        bookingreference.roomIds.remove(oldRoom);
        bookingreference.roomIds.add(newRoomId);

        saveObject(bookingreference);
        saveRoom(newRoom);
        saveRoom(existingRoom);
    }

    @Override
    public void updateReservation(BookingReference reference) throws ErrorException {
        reference.updateArx = true;
        bookingReferences.put(reference.bookingReference, reference);
        saveObject(reference);
    }

    void checkForArxUpdate() throws ErrorException, UnsupportedEncodingException {
        
        if(arxSettings != null) {
            for (BookingReference reference : bookingReferences.values()) {
                
                if(reference.isToday()) {
                    for(String roomid : reference.roomIds) {
                        if(getRoom(roomid).isClean && !reference.isApprovedForCheckin(roomid)) {
                            reference.isApprovedForCheckIn.put(roomid, true);
                            reference.updateArx = true;
                        }
                    }
                }
                
                if (reference.updateArx) {
                    System.out.println("Need to update arx with reference: " + reference.bookingReference);
                    int i = 0;

                    for (String name : reference.contact.names) {
                        String roomId = reference.roomIds.get(i);
                        Room room = getRoom(roomId);
                        System.out.println(roomId);
                        ArxUser user = new ArxUser();
                        user.doorsToAccess.add("utedor");
                        if(reference.isApprovedForCheckin(room.id)) {
                            user.doorsToAccess.add(room.roomName);
                        }
                        String[] names = name.split(" ");
                        user.firstName = names[0];
                        user.lastName = name.substring(user.firstName.length()).trim();
                        user.id = reference.id + "-" + i;
                        user.startDate = reference.startDate;
                        user.endDate = reference.endDate;
                        user.code = reference.codes.get(i);
                        user.reference = reference.bookingReference + "";
                        sendUserToArx(user);
                        i++;
                    }
                    reference.updateArx = false;
                    databaseSaver.saveObject(reference, credentials);
                }
            }
        }
    }

    private void sendUserToArx(ArxUser user) throws ErrorException, UnsupportedEncodingException {
        String toPost = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
        toPost += "<arxdata timestamp=\""+new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss").format(new Date())+"\">\n";
        toPost += "<persons>\n";
        toPost += "<person>\n";
        toPost += "<id>"+user.id+"</id>\n";
        toPost += "<first_name>"+user.firstName+"</first_name>\n";
        toPost += "<last_name>"+user.lastName+"</last_name>\n";
        toPost += "<description>reference: "+user.reference+"</description>\n";
        toPost += "<pin_code></pin_code>\n";
        toPost += "<extra_fields/>\n";
        toPost += "<access_categories>\n";
        for(String room : user.doorsToAccess) {
            toPost += "<access_category>\n";
            toPost += "<name>"+room+"</name>\n";
            toPost += "<start_date>"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.startDate)+"</start_date>\n";
            toPost += "<end_date>"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.endDate)+"</end_date>\n";
            toPost += "</access_category>\n";
        }
        toPost += "</access_categories>\n";
        toPost += "</person>\n";
        toPost += "</persons>\n";
        toPost += "<cards>";
        toPost += "<card>";
        toPost += "<number>00"+user.code+"</number>";
        toPost += "<format_name>kode</format_name>";
        toPost += "<description></description>";
        toPost += "<person_id>"+user.id+"</person_id>";
        toPost += "</card>";
        toPost += "</cards>";
        toPost += "</arxdata>";
                
        String result = httpLoginRequest(arxSettings.address, arxSettings.username, arxSettings.password, toPost);
        logArxCommunication(result, user);
    }
    
    
    
    public String httpLoginRequest(String address, String username, String password, String content) {
        String loginToken = null;
        String loginUrl = address;
        //
        DefaultHttpClient client = new DefaultHttpClient();
        client = wrapClient(client);
        HttpResponse httpResponse;

        HttpEntity entity;
        HttpPost request = new HttpPost(loginUrl);
        byte[] bytes = (username + ":" + password).getBytes();
        String encoding = Base64.getEncoder().encodeToString(bytes);

        request.addHeader("Authorization", "Basic " + encoding);

        StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

        StringBody body = new StringBody(content, ContentType.TEXT_PLAIN);
        
        HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("upfile", body)
                .addPart("comment", comment)
                .build();

        request.setEntity(reqEntity);

        try {
            httpResponse = client.execute(request);
            entity = httpResponse.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                int ch;
                StringBuilder sb = new StringBuilder();
                while ((ch = instream.read()) != -1) {
                    sb.append((char) ch);
                }
                String result = sb.toString();
                return result;
            }
        } catch (ClientProtocolException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException ex) {
			Logger.getLogger(HotelBookingManager.class.getName()).log(Level.SEVERE, null, ex);
		}
        return "failed";
    }

    public static DefaultHttpClient wrapClient(DefaultHttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = base.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            return new DefaultHttpClient(ccm, base.getParams());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void setArxConfiguration(String address, String username, String password) throws ErrorException {
        if(arxSettings == null) {
            arxSettings = new ArxSettings();
        }
        arxSettings.address = address;
        arxSettings.username = username;
        arxSettings.password = password;
        saveObject(arxSettings);
    }

    private void logArxCommunication(String result, ArxUser user) throws ErrorException {
        ArxLogEntry entry = new ArxLogEntry();
        entry.message = result;
        entry.user = user;
        entry.storeId = storeId;
        databaseSaver.saveObject(entry, credentials);
        logEntries.add(entry);
        if(logEntries.size() > 120) {
            logEntries.remove(0);
        }
    }

    @Override
    public List<ArxLogEntry> getArxLog() throws ErrorException {
        sortLogEntries(logEntries);
        return logEntries;
    }

    private void sortLogEntries(List<ArxLogEntry> logEntries) {
        Collections.sort(logEntries,new Comparator<ArxLogEntry>() {
            public int compare(ArxLogEntry f1, ArxLogEntry f2) {
                return f2.rowCreatedDate.compareTo(f1.rowCreatedDate);
            }
        });
    }

    @Override
    public void markRoomAsReady(String roomId) throws ErrorException {
        getRoom(roomId).isClean = true;
        saveObject(getRoom(roomId));
    }
}

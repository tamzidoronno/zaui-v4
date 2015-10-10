package com.thundashop.core.hotelbookingmanager;

import com.ibm.icu.util.Calendar;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.axis.encoding.Base64;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
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
    public Date lastNotifiedError = null;

    @Autowired
    private FrameworkConfig frameworkConfig;

    private MessageManager getMsgManager() {
        return getManager(MessageManager.class);
    }

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
                RoomType type = (RoomType) dbobj;
                if (type.id.equals("2d6ab163-f6f5-41ba-89fd-5a148dcb871e"))
                    continue;
                       
                System.out.println("id: "+ type.id + " name: " + type.name);
                if (type.name == null) {
                    type.name = "";
                }
                
                roomTypes.put(dbobj.id, type);
            }
            if (dbobj instanceof BookingSettings) {
                booksettings = (BookingSettings) dbobj;
            }
            if (dbobj instanceof VismaSettings) {
                vismaSettings = (VismaSettings) dbobj;
            }
            if (dbobj instanceof GlobalBookingSettings) {
                settings = (GlobalBookingSettings) dbobj;
            }

            if (dbobj instanceof Room) {
                Room room = (Room) dbobj;
                rooms.put(dbobj.id, room);
            }
            if (dbobj instanceof BookingReference) {
                BookingReference reference = (BookingReference) dbobj;
                bookingReferences.put(reference.bookingReference, reference);
            }
        }

        sortLogEntries(tmpLogEntries);
        int i = 0;
        for (ArxLogEntry entry : tmpLogEntries) {
            logEntries.add(entry);
            i++;
            if (i > 100) {
                break;
            }
        }
    }

    @Autowired
    public HotelBookingManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @Override
    public Integer checkAvailable(long startDate, long endDate, String typeName) throws ErrorException {
        Calendar todayCal = getTodayCalendar();
        Date start = getStartDate(startDate);
        Date end = getEndDate(endDate);

        if (start.before(todayCal.getTime())) {
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
            getMsgManager().mailFactory.send("post@getshop.com", "post@getshop.com", "Booking failed for " + storeId + " room type is fail type : " + typeName, getStore().webAddress + " : " + getStore().webAddressPrimary + " : ");
            throw new ErrorException(1023);
        }

        int count = 0;
        for (Room room : rooms.values()) {
            if (room.roomType != null && room.roomType.equals(rtype.id) && isAvilable(room.id, start.getTime() ,end.getTime())) {
                count++;
            }
        }

        return count;
    }

    @Override
    public String reserveRoom(String roomType, long startDate, long endDate, int count, ContactData contact, boolean markRoomInactive, String language) throws ErrorException {
        //First make sure there is enough rooms available.
        RoomType roomtype = getRoomType(roomType);
        Integer availableRooms = checkAvailable(startDate, endDate, roomtype.name);

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

        BookingReference reference = new BookingReference();
        
        reference.roomtype = roomType;
        reference.bookingReference = genereateReferenceId();
        reference.startDate = start;
        reference.endDate = end;
        reference.sentWelcomeMessages = "false";
        if (getSession().currentUser != null) {
            logSkippedSendingEmail(reference);
            reference.sentWelcomeMessages = "true";
        }

        reference.language = language;
        for (int i = 0; i < count; i++) {
            Room room = getAvailableRoom(roomtype.id, start, end);
            Integer code = room.reserveDates(start, end, reference.bookingReference);
            reference.codes.add(code);
            reference.addRoom(room.id);
            room.storeId = storeId;
            if (markRoomInactive) {
                room.isActive = false;
            }
            databaseSaver.saveObject(room, credentials);
        }
        reference.storeId = storeId;
        reference.contact = contact;
        reference.rooms.get(0).roomId = "ec6bd88b-fc67-48c4-ad1d-1f861d58d35c";
        
        databaseSaver.saveObject(reference, credentials);
        bookingReferences.put(reference.bookingReference, reference);
        sendSmsToKnutMartin(contact, roomtype);
        
        
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
                }
        );

        for (Room tmpRoom : room) {
            finalizeRoom(tmpRoom);
        }

        return room;
    }

    @Override
    public List<RoomType> getRoomTypes() throws ErrorException {
        ArrayList retval = new ArrayList(roomTypes.values());

        Collections.sort(retval,
                new Comparator<RoomType>() {
                    public int compare(RoomType f1, RoomType f2) {
                        if (f1 == null || f1.name == null || f2 == null || f2.name == null) {
                            return 0;
                        }
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
        
        for (Room room : rooms.values()) {
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
        
        OrderManager ordMgr = getManager(OrderManager.class);
        for(BookingReference ref : result) {
            if(ref.bookingFee < 1 || ref.userId == null) {
                Order order = ordMgr.getOrderByReference(ref.bookingReference + "");
                if(order != null && order.cart != null) {
                    ref.userId = order.userId;
                    ref.bookingFee = order.cart.getTotal(false);
                    if(ref.bookingFee > 0) {
                        saveObject(ref);
                    }
                }
            }
            if(ref.invoicedTo == null) {
                ref.invoicedTo = ref.getInvoicedTo();
                saveObject(ref);
            }
        }
        
        return result;
    }

    @Override
    public void deleteReference(int reference) throws ErrorException {
        BookingReference ref = bookingReferences.get(reference);
        if (ref == null) {
            throw new ErrorException(1025);
        }
        for (String roomId : ref.getRoomIds()) {
            Room room = getRoom(roomId);
            room.removeBookedRoomWithReferenceNumber(reference);
            databaseSaver.saveObject(room, credentials);
        }
        databaseSaver.deleteObject(ref, credentials);
        bookingReferences.remove(reference);

        OrderManager manager = getManager(OrderManager.class);
        manager.unsetExpiryDateByReference("" + reference);
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

        
        if(!oldRoom.equals(newRoomId)) {
            if(!isAvilable(newRoomId, bookingreference.startDate.getTime(), bookingreference.endDate.getTime())) {
                System.out.println("This room is taken");
                throw new ErrorException(1034);
            }
        }
        
        if (!existingRoom.isActive) {
            existingRoom.isActive = true;
            newRoom.isActive = false;
        }
        existingRoom.lastReservation = null;
        

        List<BookedDate> existingBookingDates = existingRoom.getBookedDatesByReference(reference);
        newRoom.bookedDates.addAll(existingBookingDates);
        existingRoom.bookedDates.removeAll(existingBookingDates);
        bookingreference.updateArx = true;
        bookingreference.moveRoom(oldRoom, newRoomId);

        saveObject(bookingreference);
        saveRoom(newRoom);
        saveRoom(existingRoom);
        checkForArxUpdate();
    }

    @Override
    public Room getRoomForCartItem(Integer reference, String cartItemId) throws ErrorException {
        BookingReference reservation = getReservationByReferenceId(reference);
        if (reservation == null) {
            return null;
        }
        String roomId = reservation.getRoomIdOnCartItem(cartItemId);
        
        saveObject(reservation);
        
        if (roomId != null) {
            return getRoom(roomId);
        }
        throw new ErrorException(1030);
    }

    @Override
    public void updateReservation(BookingReference reference) throws ErrorException {
        reference.updateArx = true;
        bookingReferences.put(reference.bookingReference, reference);
        saveObject(reference);
        checkForArxUpdate();
    }

    void checkForArxUpdate() throws ErrorException {

        if (arxSettings != null && arxSettings.address != null && !arxSettings.address.isEmpty()) {
            for (BookingReference reference : bookingReferences.values()) {

                if (reference.failed != null) {
                    long diff = new Date().getTime() - reference.failed.getTime();
                    if (diff < 10 * 60 * 1000) {
                        continue;
                    }
                }

                if (!reference.isToday()) {
                    continue;
                }

                checkForCleanRoomsToChange(reference);

                System.out.println("Need to update arx with reference: " + reference.bookingReference);
                int i = 0;
                boolean success = false;
                for (String name : reference.contact.names) {
                    String roomId = reference.getRoomIds().get(i);
                    Room room = getRoom(roomId);
                    ArxUser user = new ArxUser();

                    if (reference.statusOnRoom(room) == BookingReference.uploadArxStatus.ALLROOMSUPDATED) {
                        continue;
                    }

                    Integer statusToSet = 0;
                    if (room.isClean || room.isCleanedToday()) {
                        reference.startDate = new Date();
                        user.doorsToAccess.add(room.roomName);
                        statusToSet = BookingReference.uploadArxStatus.ALLROOMSUPDATED;
                    } else if (reference.statusOnRoom(room) == BookingReference.uploadArxStatus.OUTDOORSUPLOADED) {
                        continue;
                    } else {
                        statusToSet = BookingReference.uploadArxStatus.OUTDOORSUPLOADED;
                        user.doorsToAccess.add("Ytterdører");
                    }

                    String[] names = name.split(" ");
                    user.firstName = names[0];
                    user.lastName = name.substring(user.firstName.length()).trim();
                    user.id = reference.id + "-" + i;
                    user.startDate = reference.startDate;
                    user.endDate = reference.endDate;
                    user.code = reference.codes.get(i);
                    user.reference = reference.bookingReference + "";
                    success = sendUserToArx(user);
                    if (!success) {
                        break;
                    }
                    reference.uploadedRoomToArx.put(room.id, statusToSet);
                    if (statusToSet == (int) BookingReference.uploadArxStatus.ALLROOMSUPDATED) {
                        notifyCustomerReadyRoom(reference, i);
                    }
                    i++;
                }
                if (success) {
                    reference.failed = null;
                    reference.updateArx = false;
                } else {
                    reference.failed = new Date();
                }
                databaseSaver.saveObject(reference, credentials);
            }
        }
    }

    private boolean sendUserToArx(ArxUser user) throws ErrorException {
        String toPost = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
        toPost += "<arxdata timestamp=\"" + new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss").format(new Date()) + "\">\n";
        toPost += "<persons>\n";
        toPost += "<person>\n";
        toPost += "<id>" + user.id + "</id>\n";
        toPost += "<first_name>" + user.firstName + "</first_name>\n";
        toPost += "<last_name>" + user.lastName + "</last_name>\n";
        toPost += "<description>reference: " + user.reference + "</description>\n";
        toPost += "<pin_code></pin_code>\n";
        toPost += "<extra_fields/>\n";
        toPost += "<access_categories>\n";
        for (String room : user.doorsToAccess) {
            toPost += "<access_category>\n";
            toPost += "<name>" + room + "</name>\n";
            if (room.equals("utedor")) {
                toPost += "<start_date>" + new SimpleDateFormat("yyyy-MM-dd").format(user.startDate) + " 12:00:00</start_date>\n";
            } else {
                toPost += "<start_date>" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.startDate) + "</start_date>\n";
            }
            toPost += "<end_date>" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.endDate) + "</end_date>\n";
            toPost += "</access_category>\n";
        }
        toPost += "</access_categories>\n";
        toPost += "</person>\n";
        toPost += "</persons>\n";
        toPost += "<cards>";
        toPost += "<card>";
        toPost += "<number>00" + user.code + "</number>";
        toPost += "<format_name>kode</format_name>";
        toPost += "<description></description>";
        toPost += "<person_id>" + user.id + "</person_id>";
        toPost += "</card>";
        toPost += "</cards>";
        toPost += "</arxdata>";

        String result = httpLoginRequest(arxSettings.address, arxSettings.username, arxSettings.password, toPost);
        logArxCommunication(result, user);
        return !result.equals("failed");
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
        String encoding = Base64.encode(bytes);

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
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
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
    public void setArxConfiguration(ArxSettings settings) throws ErrorException {
        if (arxSettings == null) {
            arxSettings = new ArxSettings();
        }
        settings.id = arxSettings.id;
        arxSettings = settings;
        saveObject(arxSettings);
    }

    private void logArxCommunication(String result, ArxUser user) throws ErrorException {
        ArxLogEntry entry = new ArxLogEntry();
        entry.message = result;
        entry.user = user;
        entry.storeId = storeId;
        entry.type = "arx";
        databaseSaver.saveObject(entry, credentials);
        logEntries.add(entry);
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
    public void markRoomAsReady(String roomId) throws ErrorException {
        Room room = getRoom(roomId);
        room.isClean = true;
        room.cleaningDates.add(new Date());
        saveObject(getRoom(roomId));
        checkForArxUpdate();
    }

    /**
     * Not sure what or why we do this.
     *
     * @param user
     */
    private String generateOrderLines(User user, Map<String, Setting> settingsFromVismaApp, VismaUsers factory) throws ErrorException, ClassNotFoundException {
        ProductManager manager = getManager(ProductManager.class);
        MessageManager msgManager = getManager(MessageManager.class);
        OrderManager ordermgr = getManager(OrderManager.class);
        HashMap<Integer, BookingReference> references = new HashMap();
        for (Order order : ordermgr.getAllOrdersForUser(user.id)) {
            references.put(new Integer(order.reference), getReservationByReferenceId(new Integer(order.reference)));
        }

        return factory.generateOrderLines(ordermgr.getAllOrdersForUser(user.id), user, references, manager, this, settingsFromVismaApp, msgManager, ordermgr);
    }

    @Override
    public void checkForVismaTransfer() throws ErrorException {
        try {
            checkForVismaTransferInternal();
        } catch (ClassNotFoundException ex) {
            System.out.println("Sql driver not found, check that the jdbc jar driver is in classpath");
            ex.printStackTrace();
        } catch (IOException ex) {
            sendSqlErrorMessage(ex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void checkForVismaTransferInternal() throws ErrorException, ClassNotFoundException, IOException {

        if (vismaSettings == null || vismaSettings.address == null || vismaSettings.address.isEmpty()) {
            return;
        }

        Map<String, Setting> settingsFromVismaApp = getSettings("Visma");

        if (settingsFromVismaApp == null) {
            System.out.println("Did not find settings");
            return;
        }

        Setting activated = settingsFromVismaApp.get("vismaactivate");
        if (activated == null || activated.value.equals("false")) {
            System.out.println("Visma not activated, check visma app");
            return;
        }

        String result = "";
        UserManager usrmgr = getManager(UserManager.class);

        List<User> allUsers = usrmgr.getAllUsers();
        List<User> usersTransferred = new ArrayList();
        VismaUsers factory = new VismaUsers();
        for (User user : allUsers) {
            if (!user.isCustomer()) {
                continue;
            }

            try {
                if (!user.isTransferredToAccountSystem) {
                    result += VismaUsers.generateVismaUserString(user) + "\r\n";
                    usersTransferred.add(user);
                }

            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        
        for (User user : allUsers) {
            if (!user.isCustomer()) {
                continue;
            }

            result += generateOrderLines(user, settingsFromVismaApp, factory);
        }

        if (result.isEmpty()) {
            return;
        }

        String filename = "orders_" + new SimpleDateFormat("yyyyMMdd-k_m").format(new Date()) + ".edi";
        String path = "/tmp/" + filename;
        
        if (frameworkConfig.productionMode) {
            FTPClient client = new FTPClient();
            client.connect(vismaSettings.address, vismaSettings.port);
            client.login(vismaSettings.username, vismaSettings.password);
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            int reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new IOException("Failed to connect to FTP Server, " + vismaSettings.address);
            }
            
            try (PrintWriter writer = new PrintWriter(path, "ISO-8859-1")) {
                writer.print(result);
            }
            InputStream inputStream = new FileInputStream(new File(path));
            boolean done = client.storeFile("./" + filename, inputStream);
            inputStream.close();
            client.disconnect();
            if (!done) {
                throw new IOException("Failed to transfer file to VISMA FTP server");
            }
            
        } else {
            try (PrintWriter writer = new PrintWriter(path, "ISO-8859-1")) {
                writer.print(result);
            }
            
            System.out.println("Debug visma file: " + path);
        }
        
        UserManager userManager = getManager(UserManager.class);
        for (User userTransferred : usersTransferred) {    
            userManager.markUserAsTransferredToVisma(userTransferred);
        }

        OrderManager orderManager = getManager(OrderManager.class);
        for (Order order : factory.getOrdersTransferred()) {
            orderManager.setDontSendToAccountSystem(order);
        }
    }

    
    @Override
    public void setVismaConfiguration(VismaSettings settings) throws ErrorException {
        settings.id = vismaSettings.id;
        vismaSettings = settings;
        saveObject(vismaSettings);
    }

    private void notifyCustomerReadyRoom(BookingReference reference, Integer offset) throws ErrorException {
        String phonenumber = reference.contact.phones.get(offset);
        Room room = getRoom(reference.getRoomIds().get(offset));
        int code = reference.codes.get(offset);
        String origMessage = arxSettings.smsReady;
        if (reference.language.equals("nb_NO")) {
            origMessage = arxSettings.smsReadyNO;
        }

        String message = formatMessage(reference, origMessage, room.roomName, code, reference.contact.names.get(offset));
        sendSMS(phonenumber, message);
    }

    private String formatMessage(BookingReference reference, String message, String roomName, Integer code, String name) throws ErrorException {
        if (code != null) {
            message = message.replaceAll("\\{code\\}", code + "");
        }
        if (roomName != null) {
            message = message.replaceAll("\\{roomName\\}", roomName);
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
        String contacts = "";
        for (int i = 0; i < reference.contact.names.size(); i++) {
            contacts += reference.contact.names.get(i) + "<br>";
            contacts += reference.contact.phones.get(i) + "<br>";
        }
        message = message.replaceAll("\\{contacts\\}", contacts);

        UserManager usermgr = getManager(UserManager.class);
        User user = usermgr.getUserById(reference.userId);
        if (user != null) {
            message = message.replaceAll("\\{email\\}", user.emailAddress);
            message = message.replaceAll("\\{address\\}", user.address.address);
            message = message.replaceAll("\\{postCode\\}", user.address.postCode);
            message = message.replaceAll("\\{city\\}", user.address.city);
        }
        return message;
    }

    private void sendSMS(String phonenumber, String message) throws ErrorException {
        try {
            getMsgManager().smsFactory.send(arxSettings.smsFrom, phonenumber, message);
            logsms(phonenumber, message, true);
        } catch (Exception e) {
            logsms(phonenumber, message, false);
        }
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

    private String getEmailTitle(String language) {
        if (arxSettings == null) {
            return "";
        }
        if (language != null && language.equals("nb_NO")) {
            return arxSettings.emailWelcomeTitleNO;
        }
        if (arxSettings.emailWelcomeTitle == null || arxSettings.emailWelcomeTitle.isEmpty()) {
            return "";
        }
        return arxSettings.emailWelcomeTitle;
    }

    @Override
    public void checkForArxTransfer() throws ErrorException {
        try {
            checkForArxUpdate();
        } catch (Exception ex) {
            getMsgManager().sendMail("post@getshop.com", "POst getshop", "Failed to tranfser to arx", ex.getMessage(), "internal process", "internal@getshop.com");
            java.util.logging.Logger.getLogger(HotelBookingManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void checkForWelcomeMessagesToSend() throws ErrorException {
        UserManager usermgr = getManager(UserManager.class);
        
        for (BookingReference reference : getAllReservations()) {
            if (reference.sentWelcomeMessages.equals("true")) {
                continue;
            }

            System.out.println("Sending welcome messages");

            //Send sms messages.
            for (int i = 0; i < reference.contact.names.size(); i++) {
                if (arxSettings != null && arxSettings.smsWelcome != null && !arxSettings.smsWelcome.isEmpty()) {
                    String origMessage = arxSettings.smsWelcome;
                    if (reference.language.equals("nb_NO")) {
                        origMessage = arxSettings.smsWelcomeNO;
                    }
                    if (i >= reference.getRoomIds().size()) {
                        continue;
                    }
                    Room room = getRoom(reference.getRoomIds().get(i));
                    String message = formatMessage(reference, origMessage, room.roomName, reference.codes.get(i), reference.contact.names.get(i));
                    System.out.println("Sending sms: " + message);
                    sendSMS(reference.contact.phones.get(i), message);
                }
            }

            //Sending email confirmation to user.
            String message = formatMessage(reference, getEmailMessage(reference.language), reference.roomtype, 0, reference.contact.names.get(0));
            String title = formatMessage(reference, getEmailTitle(reference.language),  reference.roomtype, 0, reference.contact.names.get(0));

            
            
            User user = usermgr.getUserById(reference.userId);
            if (user != null) {
                String copyadress = getSettings("Settings").get("mainemailaddress").value;
                if (copyadress != null && !copyadress.isEmpty()) {
                    sendMail(copyadress, user.emailAddress, title, message, user);
                    logMailSent(copyadress, "System owner", true, reference.bookingReference);
                    // Apperently Fastnames mailservers does not support to send two emails at the same time. Need to sleep a bit so the mailservers dont crashes.
                    try { Thread.sleep(1000); } catch (InterruptedException ex) {}
                    sendMail(copyadress, copyadress, title, message, user);
                    reference.sentWelcomeMessages = "true";
                    logMailSent(user.emailAddress, user.fullName, true, reference.bookingReference);
                    saveObject(reference);
                }
            }
        }

    }
    
    private void sendMail(String copyadress, String to, String title, String message, User user) {
        MessageManager messageManager = getMsgManager();
        
        String extension = "";
        if(user.isPrivatePerson) {
            extension = "private";
        } else {
            if(user.mvaRegistered) {
                extension = "company";
            } else {
                extension = "company_ex_taxes";
            }
        }
        
        Map<String, String> files = new HashMap();
        
        if (!extension.isEmpty()) {
            files.put("/opt/files/contract_"+extension+".pdf", "Leievilkår.pdf");
        }
        
        messageManager.mailFactory.sendWithAttachments(copyadress, to, title, message, files, false);
    }

    private void finalizeRoom(Room tmpRoom) throws ErrorException {
        for (Date cleaned : tmpRoom.cleaningDates) {
            if (tmpRoom.lastCleaning == null || tmpRoom.lastCleaning.before(cleaned)) {
                tmpRoom.lastCleaning = cleaned;
            }
        }

        List<BookingReference> allReservations = getAllReservations();
        for (BookingReference reservation : allReservations) {
            if (reservation.getRoomIds().contains(tmpRoom.id)) {
                if (tmpRoom.lastReservation != null) {
                    BookingReference lastReservation = tmpRoom.lastReservation;
                    //If this is the latest room reserved, or is todays room.
                    if (lastReservation.startDate.before(reservation.startDate) && !reservation.startDate.after(new Date())) {
                        tmpRoom.lastReservation = reservation;
                    }
                } else {
                    tmpRoom.lastReservation = reservation;
                }
            }
        }
    }

    private void logsms(String phonenumber, String message, boolean delivered) throws ErrorException {
        ArxLogEntry arxlog = new ArxLogEntry();
        arxlog.type = "sms";
        arxlog.message = message + " -> " + phonenumber;
        if (delivered) {
            arxlog.message = "sms sent: " + arxlog.message;
        } else {
            arxlog.message = "sms failed: " + arxlog.message;
        }
        arxlog.storeId = storeId;
        logEntries.add(arxlog);
        databaseSaver.saveObject(arxlog, credentials);

    }

    private void logMailSent(String email, String name, boolean sendt, int referenceid) throws ErrorException {
        ArxLogEntry arxlog = new ArxLogEntry();
        arxlog.type = "email";
        if (sendt) {
            arxlog.message = "Welcome message ";
        } else {
            arxlog.message = "Welcome message not ";
        }
        arxlog.message += " sent to " + email + " ( " + name + ") reference: " + referenceid;

        arxlog.storeId = storeId;
        logEntries.add(arxlog);
        databaseSaver.saveObject(arxlog, credentials);
    }

    private void logSkippedSendingEmail(BookingReference reference) throws ErrorException {
        ArxLogEntry arxlog = new ArxLogEntry();
        arxlog.type = "email";
        arxlog.message = " Skipped sending email for reference " + reference.bookingReference + " due to logged on user registering event.";
        arxlog.storeId = storeId;
        logEntries.add(arxlog);
        databaseSaver.saveObject(arxlog, credentials);
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

    private void checkForCleanRoomsToChange(BookingReference reference) {
        //If there is another room which is available which is of the same type and is clean, then switch to this one.
    }

    private boolean checkSql(String sql) throws ClassNotFoundException, SQLException {
        String constring = "jdbc:sqlserver://" + vismaSettings.address + ":1433;databaseName=" + vismaSettings.database;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        Connection conn = DriverManager.getConnection(constring, vismaSettings.sqlUsername, vismaSettings.sqlPassword);

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            boolean exists = rs.next();
            rs.close();
            return exists;
        } catch (SQLException x) {
            throw x;
        } finally {
            conn.close();
        }
    }

    private boolean checkInVismaIfUserExists(User user) throws ClassNotFoundException, SQLException {
        return checkSql("select * from dbo.Actor where CustNo = " + user.customerId);
    }

    boolean orderExistsInVisma(long incrementOrderId) throws ClassNotFoundException, SQLException {
        return checkSql("select ordno from dbo.Ord where csOrdNo = " + incrementOrderId);
    }

    private void sendSqlErrorMessage(Exception ex) {
        if (lastNotifiedError != null) {
            long msSinceLastNotified = new Date().getTime() - lastNotifiedError.getTime();
            long oneHourInMs = 1000 * 60 * 60;
            if (msSinceLastNotified < oneHourInMs) {
                return;
            }
        }

        MessageManager manager = getManager(MessageManager.class);
        manager.sendMail("post@getshop.com", "GetShop", "Visma failure", "Failed to query visma SQL database.<br/><br/>" + ex.getMessage(), "post@getshop.com", "GetShop System");
        lastNotifiedError = new Date();

        ex.printStackTrace();
    }

    @Override
    public void markReferenceAsStopped(int referenceId, Date stoppedDate) throws ErrorException {
        BookingReference ref = getReservationByReferenceId(referenceId);
        if (ref != null) {
            ref.endDate = stoppedDate;
            ref.active = false;
            OrderManager manager = getManager(OrderManager.class);
            manager.unsetExpiryDateByReference("" + referenceId);
            saveObject(ref);
        }
    }

    @Override
    public void confirmReservation(int bookingReferenceId) throws ErrorException {
        BookingReference bookingReference = getReservationByReferenceId(bookingReferenceId);
        bookingReference.invoicedTo = bookingReference.startDate;
        
        createOrderForReference(bookingReferenceId, 3);
        bookingReference.active = true;
        bookingReference.confirmed = true;
        
        updateReservation(bookingReference);
    }

    @Override
    public void setCartItemIds(int referenceId, List<String> ids) throws ErrorException {
        BookingReference reservation = getReservationByReferenceId(referenceId);
        reservation.setCartItemIds(ids);
        saveObject(reservation);
    }

    private void sendSmsToKnutMartin(ContactData contactData, RoomType roomtype) {
        String name = "Ukjent";
        if (contactData.names != null&& contactData.names.size() > 0) {
            name = contactData.names.get(0);
        }
         
        String roomType = "ukjent";
        if (roomType != null) {
            roomType = roomtype.name;
        }
               
        if (storeId != null && storeId.equals("3292fa74-32a2-4d52-b88f-6be6f3dff813")) {
            getMsgManager().smsFactory.send("GetShop", "46190000", "Ny bestilling opprettet, Navn: " + name + ", Rom: " + roomType);
        }
    }

    @Override
    public BookingStats buildStatistics(BookingStatsInput input) throws ErrorException {
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(input.startDate*1000);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(input.endDate*1000);
        
        BookingStats stats = new BookingStats();

        OrderManager orderMgr = getManager(OrderManager.class);
        while(true) {
            List<BookingReference> reservations = getReferencesOnDate(start.getTime());
            int daysinmonth = start.getActualMaximum(Calendar.DAY_OF_MONTH);
            
            BookingStatsDay bstatsDay = new BookingStatsDay();
            
            bstatsDay.income.put("total", 0);
            bstatsDay.count.put("total", 0);
            
            for(BookingReference ref : reservations) {
                try {
                    Double amount = ref.bookingFee; 
                    //The total amount.
                    int dayAmount = (int) (amount / daysinmonth);
                    Integer total = bstatsDay.income.get("total");
                    bstatsDay.income.put("total", total += dayAmount);

                    total = bstatsDay.count.get("total");
                    bstatsDay.count.put("total", total += 1);
                    
                    
                    //The income for each room type.
                    if(ref.rooms.isEmpty() || ref.rooms.get(0).roomId == null || getRoom(ref.rooms.get(0).roomId) == null) {
                        continue;
                    }
                    
                    String roomId = ref.rooms.get(0).roomId;
                    bstatsDay.takenRooms.add(roomId);
                    
                    String roomType = getRoom(ref.rooms.get(0).roomId).roomType;
                    if(!bstatsDay.income.containsKey(roomType)) {
                        bstatsDay.income.put(roomType, 0);
                        bstatsDay.count.put(roomType, 0);
                    }
                    total = bstatsDay.income.get(roomType);
                    if(amount == 0) {
                        System.out.println(dayAmount + " - " + amount);
                    }
                    bstatsDay.income.put(roomType, total += dayAmount);
                    
                    total = bstatsDay.count.get(roomType);
                    bstatsDay.count.put(roomType, total += 1);
                    
                } catch (ErrorException ex) {
                    java.util.logging.Logger.getLogger(HotelBookingManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            stats.stats.put(start.getTimeInMillis()/1000, bstatsDay);
            
            start.add(Calendar.DAY_OF_YEAR, 1);
            if(start.after(end)) {
                break;
            }
        }
        
        return stats;
    }

    private List<BookingReference> getReferencesOnDate(Date time) throws ErrorException {
        List<BookingReference>  result = new ArrayList();
        for(BookingReference ref : getAllReservations()) {
            if(!ref.isStopped()) {
                if(ref.startDate != null && ref.startDate.before(time)) {
                    result.add(ref);
                }
            } else {
                if(ref.startDate.before(time) && ref.endDate.after(time)) {
                    result.add(ref);
                }
            }
        }
        return result;
    }

    @Administrator
    public boolean isAvilable(String roomId, long start, long end) {
        for(BookingReference ref : bookingReferences.values()) {
            if(!ref.hasRoom(roomId)) {
                continue;
            }
            if(ref.isBetween(new Date(start), new Date(end))) {
                return false;
            }
        }
        return true;
    }

    private void createOrderForReference(int bookingReferenceId, int numberOfMonths) throws ErrorException {
        BookingReference bookingReference = getReservationByReferenceId(bookingReferenceId);
        if(bookingReference.bookingFee <= 0) {
            throw new ErrorException(1032);
        }
        
        Date startDate = bookingReference.invoicedTo;
        
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(startDate);
        calEnd.add(Calendar.MONTH, numberOfMonths);
        Date endDate = calEnd.getTime();
        
        Room room = rooms.get(bookingReference.rooms.get(0).roomId);
        if(room == null) {
            System.out.println("Room does not exists" + bookingReference.rooms.get(0).roomId);
            return;
        }
        RoomType type = getRoomTypeById(room.roomType);
        
        OrderManager orderManager = getManager(OrderManager.class);
        UserManager usrMgr = getManager(UserManager.class);
        User user = usrMgr.getUserById(bookingReference.userId);
        
        CartManager cartmgr = getManager(CartManager.class);
        cartmgr.clear();
        cartmgr.addProduct("fdcff9f1-dadc-4d73-be76-7ee6171f4231", numberOfMonths, null);
        
        
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String startText = formatter.format(startDate);
        String endText = formatter.format(endDate);

        //Create order and modify it to be correct.
        if(type == null) {
            throw new ErrorException(1033);
        }
        
        String itemName = type.name;
        String orderLineText = "bod " + room.roomName + ", " + startText + " - " + endText + " (" + itemName + ")";


        
        Order order = orderManager.createOrder(user.address);
        order.reference = "" + bookingReference.bookingReference;
        CartItem item = order.cart.getItems().get(0);
        item.getProduct().name = orderLineText;
        item.getProduct().price = bookingReference.bookingFee;
        order.userId = bookingReference.userId;
        orderManager.saveOrder(order);
        System.out.println("Order created: " + orderLineText + " , id: " + order.incrementOrderId + " id: " + order.id);
        
    }

    private RoomType getRoomTypeById(String roomTypeId) {
        return roomTypes.get(roomTypeId);
    }

    @Override
    public void buildRecurringOrders() throws ErrorException {
        int counter = 0;
        for(BookingReference ref : bookingReferences.values()) {
            if(ref.needNewOrder()) {
                try {
                    if(ref.bookingFee == 0) {
                        continue;
                    }
                    createOrderForReference(ref.bookingReference, 1);
                    ref.incrementInvoicedTo();
                    updateReservation(ref);
                }catch(ErrorException e) {
                    System.out.println("Failed building recurring order on : " + ref.bookingReference);
                }

            }
        }
    }

    @Override
    public BookingReference getLastReservation(String bodNr) throws ErrorException {
        Room room = getRoomByName(bodNr);
        BookingReference returnRes = null;
        for(BookingReference ref : bookingReferences.values()) {
            if(ref.hasRoom(room.id)) {
                if(returnRes == null) {
                    returnRes = ref;
                } else if(ref.startDate.after(returnRes.startDate)) {
                    returnRes = ref;
                }
            }
        }
        
        return returnRes;
    }

    private Room getRoomByName(String name) {
        for(Room room : rooms.values()) {
            if(room.roomName.equals(name)) {
                return room;
            }
        }
        return null;
    }
}

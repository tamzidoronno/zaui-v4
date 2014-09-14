package com.thundashop.core.hotelbookingmanager;

import com.ibm.icu.util.Calendar;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javapns.notification.management.EmailPayload;
import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.axis.encoding.Base64;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
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
    public ArxSettings arxSettings;
    public VismaSettings vismaSettings = new VismaSettings();

    public HashMap<String, Room> rooms = new HashMap();
    public HashMap<String, RoomType> roomTypes = new HashMap();
    public HashMap<Integer, BookingReference> bookingReferences = new HashMap();
    private VismaUsers transferredUsers = new VismaUsers();

    public List<ArxLogEntry> logEntries = new ArrayList();

    @Autowired
    private UserManager userManager;

    @Autowired
    private MessageManager messageManager;

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
            if (dbobj instanceof VismaSettings) {
                vismaSettings = (VismaSettings) dbobj;
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
            msgmgr.mailFactory.send("post@getshop.com", "post@getshop.com", "Booking failed for " + storeId + " room type is fail type : " + typeName, getStore().webAddress + " : " + getStore().webAddressPrimary + " : ");
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
    public String reserveRoom(String roomType, long startDate, long endDate, int count, ContactData contact, boolean markRoomInactive, String language) throws ErrorException {
        //First make sure there is enough rooms available.
        RoomType roomtype = getRoomType(roomType);
        Integer availableRooms = checkAvailable(startDate, startDate, roomtype.name);
        if (availableRooms < count) {
            return "-1";
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

        BookingReference reference = new BookingReference();
        reference.bookingReference = genereateReferenceId();
        reference.startDate = start;
        reference.endDate = end;
        reference.sentWelcomeMessages = false;
        reference.language = language;
        for (int i = 0; i < count; i++) {
            Room room = getAvailableRoom(roomtype.id, start, end);
            Integer code = room.reserveDates(start, end, reference.bookingReference);
            reference.codes.add(code);
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

        if (arxSettings != null && arxSettings.address != null && !arxSettings.address.isEmpty()) {
            for (BookingReference reference : bookingReferences.values()) {

                if (reference.isToday()) {
                    for (String roomid : reference.roomIds) {
                        if (getRoom(roomid).isClean && !reference.isApprovedForCheckin(roomid)) {
                            reference.isApprovedForCheckIn.put(roomid, true);
                            reference.updateArx = true;
                            notifyCustomersReadyRoom(reference);
                        }
                    }
                }

                if (reference.updateArx) {
                    System.out.println("Need to update arx with reference: " + reference.bookingReference);
                    int i = 0;

                    for (String name : reference.contact.names) {
                        if(reference.roomIds.size() >= i) {
                            continue;
                        }
                        String roomId = reference.roomIds.get(i);
                        Room room = getRoom(roomId);
                        ArxUser user = new ArxUser();
                        user.doorsToAccess.add("utedor");
                        if (reference.isApprovedForCheckin(room.id)) {
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
            toPost += "<start_date>" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.startDate) + "</start_date>\n";
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
        databaseSaver.saveObject(entry, credentials);
        logEntries.add(entry);
        if (logEntries.size() > 120) {
            logEntries.remove(0);
        }
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
        getRoom(roomId).isClean = true;
        saveObject(getRoom(roomId));
    }

    @Override
    public void checkForVismaTransfer() throws ErrorException {

        if (vismaSettings == null || vismaSettings.address == null || vismaSettings.address.isEmpty()) {
            return;
        }

        String result = "";
        UserManager usrmgr = getManager(UserManager.class);
        OrderManager ordermgr = getManager(OrderManager.class);
        List<User> allUsers = usrmgr.getAllUsers();

        for (User user : allUsers) {
            if (!transferredUsers.checkTransferred(user) && user.isCustomer()) {
                String generatedResult;
                generatedResult = VismaUsers.generateVismaUserString(user);
                if (generatedResult == null) {
                    messageManager.mailFactory.send("internal@getshop.com", "post@getshop.com", "Failed to expert user to visma", "For storid: " + storeId + " userid: " + user.id + "(" + user.toString() + ")");
                } else {
                    HashMap<Integer, BookingReference> references = new HashMap();
                    for (Order order : ordermgr.getAllOrdersForUser(user.id)) {
                        references.put(new Integer(order.reference), getReservationByReferenceId(new Integer(order.reference)));
                    }

                    result += generatedResult + "\r\n";
                    result += VismaUsers.generateOrderLines(ordermgr.getAllOrdersForUser(user.id), user, references);
                }
            }
        }
        FTPClient client = new FTPClient();
        if (vismaSettings != null && vismaSettings.address != null && !vismaSettings.address.isEmpty()) {
            try {
                client.connect(vismaSettings.address, vismaSettings.port);
                client.login(vismaSettings.username, vismaSettings.password);
                client.enterLocalPassiveMode();
                client.setFileType(FTP.BINARY_FILE_TYPE);
                int reply = client.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    messageManager.sendMail("post@getshop.com", "GetShop", "failed to log on to ftp visma server..", "Failed to connect to server: " + vismaSettings.address + " with username: " + vismaSettings.username + " to upload file. ", "post@getshop.com", "Internal process");
                    return;
                }
                String filename = "orders_" + new SimpleDateFormat("yyyyMMdd-k_m").format(new Date()) + ".edi";
                String path = "/tmp/" + filename;
                PrintWriter writer = new PrintWriter(path, "UTF-8");
                writer.print(result);
                writer.close();
                InputStream inputStream = new FileInputStream(new File(path));
                boolean done = client.storeFile("./" + filename, inputStream);
                inputStream.close();
                if (!done) {
                    messageManager.sendMail("post@getshop.com", "GetShop", "failed to upload file to visma.", "Failed to connect to server: " + vismaSettings.username + " to upload file. ( " + client.getReplyString() + ")", "post@getshop.com", "Internal process");
                }
            } catch (Exception e) {
                messageManager.sendMail("post@getshop.com", "GetShop", "failed to upload file to visma.", "something failed when uploading visma file. ", "post@getshop.com", "Internal process");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setVismaConfiguration(String address, String username, String password, Integer port) throws ErrorException {
        vismaSettings.address = address;
        vismaSettings.password = password;
        vismaSettings.username = username;
        vismaSettings.port = port;
        saveObject(vismaSettings);
    }

    private void notifyCustomersReadyRoom(BookingReference reference) throws ErrorException {
        int i = 0;
        for (String phonenumber : reference.contact.phones) {
            Room room = getRoom(reference.roomIds.get(i));
            int code = reference.codes.get(i);
            String origMessage = arxSettings.smsReady;
            if (reference.language.equals("nb_NO")) {
                origMessage = arxSettings.smsReadyNO;
            }

            String message = formatMessage(reference, origMessage, room.roomName, code, reference.contact.names.get(i));
            sendSMS(phonenumber, message);
            i++;
        }
    }

    private String formatMessage(BookingReference reference, String message, String roomName, Integer code, String name) throws ErrorException {
        if(code != null) {
            message = message.replaceAll("\\{code\\}", code + "");
        }
        if(roomName != null) {
            message = message.replaceAll("\\{room\\}", roomName);
        }
        message = message.replaceAll("\\{checkin_time\\}", new SimpleDateFormat("dd-MM-yyyy H:m").format(reference.startDate) + "0");
        message = message.replaceAll("\\{checkin_date\\}", new SimpleDateFormat("dd-MM-yyyy").format(reference.startDate));
        message = message.replaceAll("\\{checkout_time\\}", new SimpleDateFormat("dd-MM-yyyy H:m").format(reference.endDate) + "0");
        message = message.replaceAll("\\{name\\}", name);
        message = message.replaceAll("\\{referenceNumber\\}", reference.bookingReference + "");
        String contacts = "";
        for (int i = 0; i < reference.contact.names.size(); i++) {
            contacts += reference.contact.names.get(i) + "<br>";
            contacts += reference.contact.phones.get(i) + "<br>";
        }
        message = message.replaceAll("\\{contacts\\}", contacts);

        OrderManager ordermgr = getManager(OrderManager.class);
        UserManager usermgr = getManager(UserManager.class);
        Order order = ordermgr.getOrderByReference(reference.bookingReference + "");
        if (order != null) {
            message = message.replaceAll("\\{roomName\\}", order.cart.getItems().get(0).getProduct().name + "");
            User user = usermgr.getUserById(order.userId);
            if (user != null) {
                message = message.replaceAll("\\{email\\}", user.emailAddress);
                message = message.replaceAll("\\{address\\}", user.address.address);
                message = message.replaceAll("\\{postCode\\}", user.address.postCode);
                message = message.replaceAll("\\{city\\}", user.address.city);
            }
        }
        return message;
    }

    private void sendSMS(String phonenumber, String message) {
        try {
            messageManager.smsFactory.send(arxSettings.smsFrom, phonenumber, message);
        } catch (Exception e) {
            //What do we do with sms that fails to be delivered?
            e.printStackTrace();
        }
    }

    @Override
    public String getEmailMessage(String language) throws ErrorException {
        if (language != null && language.equals("nb_NO")) {
            return arxSettings.emailWelcomeNO;
        }
        if (arxSettings.emailWelcome == null || arxSettings.emailWelcome.isEmpty()) {
            return "";
        }
        return arxSettings.emailWelcome;
    }

    private String getEmailTitle(String language) {
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
        } catch (UnsupportedEncodingException ex) {
            messageManager.sendMail("post@getshop.com", "POst getshop", "Failed to tranfser to arx", ex.getMessage(), "internal process", "internal@getshop.com");
            java.util.logging.Logger.getLogger(HotelBookingManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void checkForWelcomeMessagesToSend() throws ErrorException {

        for (BookingReference reference : getAllReservations()) {
            if (reference.sentWelcomeMessages) {
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
                    if(i >= reference.roomIds.size()) {
                        continue;
                    }
                    Room room = getRoom(reference.roomIds.get(i));
                    String message = formatMessage(reference, origMessage, room.roomName, reference.codes.get(i), reference.contact.names.get(i));
                    sendSMS(reference.contact.phones.get(i), message);
                }
            }

            //Sending email confirmation to user.
            String message = formatMessage(reference, getEmailMessage(reference.language), null, 0, reference.contact.names.get(0));
            String title = formatMessage(reference, getEmailTitle(reference.language), null, 0, reference.contact.names.get(0));

            OrderManager ordermgr = getManager(OrderManager.class);
            UserManager usermgr = getManager(UserManager.class);
            Order order = ordermgr.getOrderByReference(reference.bookingReference + "");
            if (order != null) {
                User user = usermgr.getUserById(order.userId);
                if (user != null) {
                    String copyadress = getSettings("Settings").get("mainemailaddress").value;
                    if (copyadress != null && !copyadress.isEmpty()) {
                        messageManager.mailFactory.send(copyadress, user.emailAddress, title, message);
//                    messageManager.mailFactory.send(copyadress, copyadress, title, message);
                        reference.sentWelcomeMessages = true;
                        saveObject(reference);
                    }
                }
            }
        }

    }
}

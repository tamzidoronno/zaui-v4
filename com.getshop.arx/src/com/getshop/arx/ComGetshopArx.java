/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.arx;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.hotelbookingmanager.ArxUser;
import com.thundashop.core.hotelbookingmanager.BookingReference;
import com.thundashop.core.hotelbookingmanager.Room;
import com.thundashop.core.hotelbookingmanager.RoomInformation;
import com.thundashop.core.hotelbookingmanager.RoomInformation.RoomInfoState;
import com.thundashop.core.hotelbookingmanager.UsersBookingData;
import com.thundashop.core.usermanager.data.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.mime.content.StringBody;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.axis.encoding.Base64;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
/**
 *
 * @author boggi
 */
public class ComGetshopArx {

    private String hostname = "https://92.220.61.142:5002/arx/import";
    private String apiAddress = "www.getshop.com";
    private Integer backendport = 3224;
    
//    private String hostname = "https://192.168.1.103:5002/arx/import";
//    private Integer backendport = 25554;
//    private String apiAddress = "localhost";
    
    
    private String username = "master";
    private String password = "master";

    private String apiUsername = "kai@getshop.com";
    private String apiPassword = "g4kkg4kk";
    private String website = "wh.no";
    private boolean notifiedGetshop = false;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        ComGetshopArx arx = new ComGetshopArx();
        arx.start();
    }
    private String sessid;
    private GetShopApi api;
   
    private void start() throws Exception {
        sessid = UUID.randomUUID().toString();
        connectToBackend();
        while(true) {
            checkForArxUpdate();
            try { Thread.sleep(10000); }catch(Exception e) {}
        }
    }
    
    private void connectToBackend() throws Exception {

        System.out.println("Connecting to : " + apiAddress + " port : " + backendport);
        api = new GetShopApi(backendport, apiAddress, sessid, website);
        try {
            User result = api.getUserManager().logOn(apiUsername, apiPassword);
            if (!api.getUserManager().isLoggedIn()) {
                System.out.println("Failed to logon to backend");
                System.exit(0);
            }
            System.out.println("Connected to backend");
        } catch (Exception e) {
        }

    }
    
    
    void checkForArxUpdate() throws Exception {
        System.out.println("Checking for arx update");
        HashMap<String, BookingReference> bookingReferences = new HashMap();
        List<UsersBookingData> bookingData = api.getHotelBookingManager().getAllReservationsArx();
        List<Room> rooms = api.getHotelBookingManager().getAllRooms();
        
        Map<String,Room> allRooms = new HashMap();
        for(Room room : rooms) {
            allRooms.put(room.id, room);
        }
        
        for(UsersBookingData bdata : bookingData) {
            for (BookingReference reference : bdata.references) {
                //No need to check / update arx on reservation not valid for the time span.
                if (reference.isEnded() || (!reference.isStarted() && !reference.isToday())) {
                    continue;
                }

                int count = 0;
                for(RoomInformation roomInfo : reference.roomsReserved) {
                    if(roomInfo.roomState == RoomInfoState.accessGranted) {
                        continue;
                    }

                    Room roomGranted = allRooms.get(roomInfo.roomId);
                    if(roomInfo.roomState == RoomInfoState.externalDoorGranted && !roomGranted.isClean) {
                        //The user has access to external doors, but cant get access to the room since it is not clean yet. 
                        continue;
                    }

                    ArxUser user = createArxUser(roomInfo, reference, count);
                    if(user == null) {
                        continue;
                    }

                    if(roomGranted.isClean || roomInfo.roomState == RoomInfoState.regrantAccess) {
                        roomInfo.roomState = RoomInfoState.accessGranted;
                        user.doorsToAccess.add(roomGranted.roomName);
                        addToLog(user.firstName + " - " + user.lastName + " gained access to room: " + roomGranted.roomName);
                    } else {
                        addToLog(user.firstName + " - " + user.lastName + " got access to external doors.. wating for clean room.");
                        roomInfo.roomState = RoomInfoState.externalDoorGranted;
                    }
                    count++;

                    if(!sendUserToArx(user)) {
                        //Not able to upload to arx... action is needed.
                        notifyGetshopDown(user);
                        try { Thread.sleep(1*1000*60); }catch(Exception e) {}
                    } else {
                        notifyGetShopUp();
                        notifyUserAboutUpdate(reference, roomInfo, user.code);
                        api.getHotelBookingManager().updateUserBookingData(bdata);
                    }
                }
            }
        }
    }

    private boolean sendUserToArx(ArxUser user) throws UnsupportedEncodingException {
        String toPost = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
        String firstName = new String(user.firstName.toString().getBytes("ISO-8859-1"), "UTF-8");
        String lastName = new String(user.lastName.toString().getBytes("ISO-8859-1"), "UTF-8");
        
        toPost += "<arxdata timestamp=\"" + new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss").format(new Date()) + "\">\n";
        toPost += "<persons>\n";
        toPost += "<person>\n";
        toPost += "<id>" + user.id + "</id>\n";
        
        toPost += "<first_name>" + firstName + "</first_name>\n";
        toPost += "<last_name>" + lastName + "</last_name>\n";
        toPost += "<description>reference: " + user.reference + "</description>\n";
        toPost += "<pin_code></pin_code>\n";
        toPost += "<extra_fields/>\n";
        toPost += "<access_categories>\n";
        
        String startday = new SimpleDateFormat("d").format(user.startDate);
        String startmonth = new SimpleDateFormat("M").format(user.startDate);
        String startyear = new SimpleDateFormat("y").format(user.startDate);
        String endday = new SimpleDateFormat("d").format(user.endDate);
        String endmonth = new SimpleDateFormat("M").format(user.endDate);
        String endyear = new SimpleDateFormat("y").format(user.endDate);
        
        if(startday.length() == 1) { startday = "0" + startday; }
        if(startmonth.length() == 1) { startmonth = "0" + startmonth; }
        if(startyear.length() == 1) { startyear = "0" + startyear; }
        if(endday.length() == 1) { endday = "0" + endday; }
        if(endmonth.length() == 1) { endmonth = "0" + endmonth; }
        if(endyear.length() == 1) { endyear = "0" + endyear; }
        
        
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

        String result = httpLoginRequest(hostname, username, password, toPost);
        return result.equals("OK");
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
                return result.trim();
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

    private void notifyGetshopDown(ArxUser user) throws Exception {
        if(!notifiedGetshop) {
            addToLog("Arx down, not able to communiacate");
            api.getMessageManager().sendMail("post@getshop.com", 
                "support", "failed to upload user to arx server. retrying in 2 minutes.", 
                "User: " + user.firstName + 
                "Website: " + website + " " + 
                " " + 
                user.lastName, "arx@getshop.com", "Getshop Arx Integration");        
            notifiedGetshop = true;
        }
    }

    private void notifyGetShopUp() throws Exception {
        if(notifiedGetshop) {
            addToLog("Arx up, success on communication");
            api.getMessageManager().sendMail("post@getshop.com", 
                "support", "Success upload user to arx server, be happy and great.", 
                " ", "arx@getshop.com", "Getshop Arx Integration");
            notifiedGetshop = false;
        }
    }

    private void notifyUserAboutUpdate(BookingReference reference, RoomInformation roomInfo, Integer code) throws Exception {
        if(roomInfo.roomState == RoomInfoState.accessGranted) {
            addToLog("Notifying user about room: state, " + roomInfo.roomState + " roomid: " + roomInfo.roomId + ", name: " + roomInfo.visitors.get(0).name + " (" + roomInfo.visitors.get(0).phone + ")");
            api.getHotelBookingManager().notifyUserAboutRoom(reference, roomInfo, code);
        }
    }

    private ArxUser createArxUser(RoomInformation roomInfo, BookingReference reference, int count) {
        Random randomGenerator = new Random();
        int code = 0;
        do {
            code = randomGenerator.nextInt(9999);
        }while(code < 1000);

        if(roomInfo.visitors.isEmpty()) {
            addToLog("No visitors on reference: " + reference.bookingReference);
            return null;
        }
        
        String name = roomInfo.visitors.get(0).name;
        ArxUser user = new ArxUser();
        String[] names = name.split(" ");
        user.firstName = names[0];
        user.lastName = name.substring(user.firstName.length()).trim();
        user.id = reference.bookingReference + "-" + count;
        user.startDate = reference.startDate;
        user.endDate = reference.endDate;
        user.code = code;
        user.doorsToAccess.add("Ytterdører");
        user.reference = reference.bookingReference + "";
        addToLog("Generating arx user: " + user.firstName + " - " + user.lastName + " : " + user.startDate + " - " + user.endDate);
        return user;
    }

    private void addToLog(String info) {
        System.out.println(new Date().toString() + " : " + info);
    }
    
}

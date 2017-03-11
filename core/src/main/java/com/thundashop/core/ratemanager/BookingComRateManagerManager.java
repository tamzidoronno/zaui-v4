package com.thundashop.core.ratemanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bcomratemanager.RateManagerConfig;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.core.webmanager.WebManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.opentravel.ota._2003._05.BaseInvCountType;
import org.opentravel.ota._2003._05.InvCountType;
import org.opentravel.ota._2003._05.OTAHotelInvCountNotifRQ;
import org.opentravel.ota._2003._05.StatusApplicationControlType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class BookingComRateManagerManager extends GetShopSessionBeanNamed implements IBookingComRateManagerManager {
    
    RateManagerConfig config = new RateManagerConfig();
    
    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    PmsInvoiceManager pmsInvoiceManager;
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    public WebManager webManager;
    
    public BookingComRateManagerManager() {
        
    }
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon d : data.data) {
            if(d instanceof RateManagerConfig) {
                config = (RateManagerConfig) d;
            }
        }
    }
    
    @Override
    public void pushInventoryList() {
        OTAHotelInvCountNotifRQ res = createInventoryListToPush(null, null, null);
        
        try {
            StringWriter sw = new StringWriter();
            BookingRateManageSoapEnvelope envelope = new BookingRateManageSoapEnvelope();
            envelope.soapBody = (OTAHotelInvCountNotifRQ) res;
            JAXBContext jaxbContext = JAXBContext.newInstance(envelope.getClass(), res.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            sw = new StringWriter();
            jaxbMarshaller.marshal(envelope, sw);  

            String body = sw.toString();
            body = replaceHeader(body);
            
            body = body.replace("<soap:Body>", "<soap:Body><OTA_HotelInvCountNotifRQ xmlns=\"http://www.opentravel.org/OTA/2003/05\">");
            body = body.replace("</soap:Body>", "</OTA_HotelInvCountNotifRQ></soap:Body>");

            String url = "https://api.pricematch.travel/htng_message";
            String result = htmlPost(url, body);
         }catch(Exception e) {
             e.printStackTrace();
         }
    }
    
    public String htmlPost(String url, String data)  throws Exception {
        String encoding = "UTF-8";
        
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        
        connection.setRequestProperty("Content-Type", "application/xml");
        connection.setRequestProperty("Accept", "application/xml");
        
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(new String(data.getBytes(), encoding));
        outputStream.flush();
        outputStream.close();
        
        try {
            BufferedReader responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        
            String responseLine;
            StringBuilder responseBuffer = new StringBuilder();

            while((responseLine = responseStream.readLine()) != null) {
                responseBuffer.append(responseLine);
            }
            return responseBuffer.toString();
        }catch(IOException ex) {
            BufferedReader responseStream = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        
            String responseLine;
            StringBuilder responseBuffer = new StringBuilder();

            while((responseLine = responseStream.readLine()) != null) {
                responseBuffer.append(responseLine);
            }
            String res = responseBuffer.toString();
            System.out.println(res);
            
            throw ex;
        }    
    }
    
    @Override
    public void saveRateManagerConfig(RateManagerConfig config) {
        this.config = config;
        saveObject(config);
    }

    @Override
    public RateManagerConfig getRateManagerConfig() {
        return this.config;
    }

    private Integer getTypeSize(BookingItemType tmpType) {
        List<BookingItem> items = bookingEngine.getBookingItemsByType(tmpType.id);
        return items.size();
    }
    
    private OTAHotelInvCountNotifRQ createInventoryListToPush(Date startCheck, Date endCheck, List<String> types) {
        OTAHotelInvCountNotifRQ res = new OTAHotelInvCountNotifRQ();
        
        InvCountType type = new InvCountType();
        type.setHotelCode(config.hotelId);
        type.setHotelName(config.hotelName);
        type.setChainCode(config.hotelChainCode);
        
        List<BaseInvCountType> inventories = new ArrayList();
        
        Calendar startcal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        startcal.set(Calendar.HOUR_OF_DAY, 16);
        for (int i = 0; i < 365; i++) {
            Date start = startcal.getTime();
            endCal.setTime(startcal.getTime());
            endCal.add(Calendar.HOUR_OF_DAY, 16);

            for(BookingItemType tmpType : bookingEngine.getBookingItemTypes()) {
                int numberOfAvailable = bookingEngine.getNumberOfAvailable(tmpType.id, start, endCal.getTime());
                BaseInvCountType toAdd = new BaseInvCountType();

                StatusApplicationControlType statusApp = new StatusApplicationControlType();
                
                statusApp.setStart(new SimpleDateFormat("yyyy-MM-dd").format(start.getTime()));
                statusApp.setInvTypeCode(tmpType.name);
                toAdd.setStatusApplicationControl(statusApp);

                BaseInvCountType.InvCounts count = new BaseInvCountType.InvCounts();

                List<BaseInvCountType.InvCounts.InvCount> invCountList = new ArrayList();
                int roomSize = getTypeSize(tmpType);
                BaseInvCountType.InvCounts.InvCount physicals = new BaseInvCountType.InvCounts.InvCount();
                physicals.setCountType("1");
                physicals.setCount(BigInteger.valueOf(roomSize));
                invCountList.add(physicals);

                BaseInvCountType.InvCounts.InvCount freeRooms = new BaseInvCountType.InvCounts.InvCount();
                freeRooms.setCountType("2");
                freeRooms.setCount(BigInteger.valueOf(numberOfAvailable));
                invCountList.add(freeRooms);

                BaseInvCountType.InvCounts.InvCount nothing = new BaseInvCountType.InvCounts.InvCount();
                nothing.setCountType("3");
                nothing.setCount(BigInteger.valueOf(0));
                invCountList.add(nothing);

                BaseInvCountType.InvCounts.InvCount takenRooms = new BaseInvCountType.InvCounts.InvCount();
                takenRooms.setCountType("4");
                takenRooms.setCount(BigInteger.valueOf(roomSize - numberOfAvailable));
                invCountList.add(takenRooms);

                for(int j = 5; j <= 8; j++) {
                    if(j == 7) {
                        continue;
                    }
                    BaseInvCountType.InvCounts.InvCount toFill = new BaseInvCountType.InvCounts.InvCount();
                    toFill.setCountType(j + "");
                    toFill.setCount(BigInteger.valueOf(0));
                    invCountList.add(toFill);
                }

                count.setInvCount(invCountList);
                toAdd.setInvCounts(count);
                
                inventories.add(toAdd);
            }
            startcal.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        type.setInventory(inventories);
        res.setInventories(type);
        return res;
    }

    private String replaceHeader(String body) throws SOAPException {
        String uuid = UUID.randomUUID().toString();
        
        body = body.replace("<header></header>", "  <soap:Header>\n" +
"    <wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" soap:mustUnderstand=\"1\">\n" +
"      <wsse:UsertextToken>\n" +
"        <wsse:Username>getshop</wsse:Username>\n" +
"        <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-usertext-token-profile-1.0#PasswordText\">HeeqwFNVLbyC</wsse:Password>\n" +
"      </wsse:UsertextToken>\n" +
"    </wsse:Security>\n" +
"    <wsa:MessageID>"+uuid+"</wsa:MessageID>\n" +
"    <wsa:CorrelationID>9e0205ce-e16b-4131-a44e-d2765cf76a76</wsa:CorrelationID>\n" +
"    <wsa:To>https://api.pricematch.travel/htng_message</wsa:To>\n" +
"  </soap:Header>");
        
        body = body.replace("<envelope SOAP-ENV:encodingStyle=\"http://www.w3.org/2001/12/soap-encoding\" xmlns:ns2=\"http://www.opentravel.org/OTA/2003/05\">",
        "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">");

        body = body.replace("ns2:", "");
        body = body.replace("</envelope>", "</soap:Envelope>");

        return body;
    }

    @Override
    public void pushAllBookings() {
        List<PmsBooking> allbookings = pmsManager.getAllBookings(null);
        for(PmsBooking booking : allbookings) {
            pushBooking(booking, "Commit");
        }
    }

    public void pushBooking(PmsBooking booking, String actionType) {
        if(config.hotelId == null || config.hotelId.isEmpty()) {
            return;
        }
        
        String uuid = UUID.randomUUID().toString();
        String toPush ="";
        try {
            String date = convertDateToApi(booking.rowCreatedDate);
            toPush = "<Envelope xmlns=\"http://www.w3.org/2003//05/soap-envelope\">";
            toPush += "<Header>\n" +
                        "  <Security mustUnderstand=\"true\">\n" +
                        "    <UsertextToken>\n" +
                        "      <Username>getshop</Username>\n" +
                        "      <Password>HeeqwFNVLbyC</Password>\n" +
                        "    </UsertextToken>\n" +
                        "  </Security>\n" +
                        "  <MessageID>"+uuid+"</MessageID>" +
                        "</Header>\n";
            
            
            toPush += "<Body>";
            toPush += " <OTA_HotelResNotifRQ ResStatus=\""+actionType+"\" TimeStamp=\""+date+"\">";
            toPush += createReservationXml(booking);
            toPush += "</OTA_HotelResNotifRQ>";
            toPush += "</Body>";
            toPush += "</Envelope>";
            htmlPost("https://api.pricematch.travel/htng_message", toPush);
        }catch(Exception e) {
            System.out.println(toPush);
            e.printStackTrace();
        }
    }
    
    private String createReservationXml(PmsBooking booking) throws DatatypeConfigurationException {
        String toReturn = "<HotelReservations>";
        for(PmsBookingRooms room : booking.rooms) {
            toReturn += createReservationXmlForRoom(booking, room);
        }
        toReturn += "</HotelReservations>";
        return toReturn;
    }

    private String getStatus(PmsBookingRooms room, PmsBooking booking) {
            String status = "Reserved";
            if(room.isStarted() && room.addedToArx) {
                status = "In-House";
            }
            if(room.isEnded() && booking.payedFor) {
                status = "Checked-Out";
            }
            if(room.deleted || booking.isDeleted) {
                status = "Cancelled";
            }
            return status;
    }

    private String createReservationXmlForRoom(PmsBooking booking, PmsBookingRooms room) throws DatatypeConfigurationException {
        String date = convertDateToApi(booking.rowCreatedDate);
        
        String result = "<HotelReservation CreateDateTime=\""+date+"\" ResStatus=\""+getStatus(room, booking)+"\">";
        
        result += "<POS>";
        result += "<Source>";
        result += "<BookingChannel>";
        result += "<CompanyName Code=\""+getChannel(room, booking)+"\">"+getChannel(room, booking)+"</CompanyName>";
        result += "</BookingChannel>";
        result += "</Source>";
        result += "</POS>";
        
        result += "<UniqueID ID=\""+room.pmsBookingRoomId+"\"/>";
        
        result += createRoomStays(room);
        result += createResGuests(booking, room);
        
        result += "</HotelReservation>";
        return result;
    }

    private void prettyPrint(String toPush) throws TransformerException {
        Source xmlInput = new StreamSource(new StringReader(toPush));
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(xmlInput, new StreamResult(stringWriter));

        String pretty = stringWriter.toString();
        pretty = pretty.replace("\r\n", "\n");
        System.out.println(pretty);
    }

    private String convertDateToApi(Date rowCreatedDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return dateFormat.format(rowCreatedDate);
    }
    
    private String convertDateToApi2(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private String getChannel(PmsBookingRooms room, PmsBooking booking) {
        String channel = "WEB";
        if(booking.channel != null && booking.channel.isEmpty()) {
            channel = booking.channel;
        }
        return channel;
    }

    private String createRoomStays(PmsBookingRooms room) {
        String result = "<RoomStays>";
        result += "<RoomStay>";
        
        result += "<RoomRates>";
        result += "<RoomRate NumberOfUnits=\"1\" RatePlanCode=\"RACK\" RoomTypeCode=\""+getBookingItemName(room.bookingItemTypeId)+"\">";
        
        result += "<Rates>";
        for(String date : room.priceMatrix.keySet()) {
            result += "<Rate EffectiveDate=\""+convertToCorrectDateFormat(date, 0)+"\" ExpireDate=\""+convertToCorrectDateFormat(date, 1)+"\" RateTimeUnit=\"Night\" UnitMultiplier=\"1\">";
            result += "<Base AmountAfterTax=\""+room.priceMatrix.get(date)+"\" CurrencyCode=\"NOK\"/>";
            result += "</Rate>";
        }
        result += "</Rates>";
        
        result += "</RoomRate>";
        result += "</RoomRates>";
        
        result += "<GuestCounts IsPerRoom=\"1\">";
        result += "<GuestCount AgeQualifyingCode=\"10\" Count=\""+room.numberOfGuests+"\"/>";
        result += "</GuestCounts>";
        
        result += "<TimeSpan End=\""+convertDateToApi2(room.date.end)+"\" Start=\""+convertDateToApi2(room.date.start)+"\"/>";
        result += "<BasicPropertyInfo HotelCode=\""+config.hotelId+"\"/>";
        
        result += "</RoomStay>";
        result += "</RoomStays>";
        
        return result;
    }

    private String getBookingItemName(String bookingItemTypeId) {
        BookingItemType type = bookingEngine.getBookingItemType(bookingItemTypeId);
        return type.name;
    }

    private String createResGuests(PmsBooking booking, PmsBookingRooms room) {
        String result = "<ResGuests>\n" +
"            <ResGuest>\n" +
"              <Profiles>\n" +
"                <ProfileInfo>\n" +
"                  <Profile>\n" +
"                    <Customer>\n" +
"                      <Address>\n" +
"                        <CountryName Code=\""+booking.countryCode+"\"/>\n" +
"                      </Address>\n" +
"                    </Customer>\n" +
"                  </Profile>\n" +
"                </ProfileInfo>\n" +
"              </Profiles>\n" +
"            </ResGuest>\n" +
"          </ResGuests>";
        return result;
    }

    private String convertToCorrectDateFormat(String date, int days) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date test = dateFormat.parse(date);
        
            if(days > 0) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(test);
                cal.add(Calendar.DAY_OF_YEAR, days);
                test = cal.getTime();
            }
            return convertDateToApi2(test);
        }catch(Exception e) {
            logPrintException(e);
        }
        return null;
    }

    @Override
    public String updateRate(Date start, Date end, String roomId, Double rate) {
        PmsPricing prices = pmsManager.getPriceObject("default");
        
        String roomTypeId = "";
        for(String key : config.roomTypeIdMap.keySet()) {
            if(config.roomTypeIdMap.get(key).equals(roomId)) {
                roomTypeId = key;
                break;
            }
        }
        
        if(roomTypeId.isEmpty()) {
            return "Invalid room mapping for roomid: " + roomId;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        while(true) {
            String offset = PmsBookingRooms.getOffsetKey(cal, PmsBooking.PriceType.daily);
            HashMap<String, Double> map = prices.dailyPrices.get(roomTypeId);
            
            if(map == null) {
                map = new HashMap();
            }
            map.put(offset, rate);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTime().after(end)) {
                break;
            }
        }
        
        pmsManager.setPrices("default", prices);
        return "";
    }


}

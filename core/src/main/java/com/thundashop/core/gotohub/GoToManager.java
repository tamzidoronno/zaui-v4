package com.thundashop.core.gotohub;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.gotohub.dto.Contact;
import com.thundashop.core.gotohub.dto.Hotel;
import com.thundashop.core.gotohub.dto.PriceAllotment;
import com.thundashop.core.gotohub.dto.RoomType;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.wubook.WubookRoomData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.thundashop.core.wubook.WuBookApiCalls.UPDATE_PLAN_PRICES;

@Component
@GetShopSession
public class GoToManager extends ManagerBase implements IGoToManager {

    @Autowired
    PmsManager pmsManager;
    @Autowired
    StoreManager storeManager;
    @Autowired
    StorePool storePool;
    @Autowired
    BookingEngine bookingEngine;
    @Autowired
    PmsInvoiceManager pmsInvoiceManager;

    List<GoToRoomData> goToRoomData = new ArrayList<GoToRoomData>();

    private static final Logger logger = LoggerFactory.getLogger(GoToManager.class);
    private static final String MANAGER = GoToManager.class.getSimpleName();
    private GoToSettings settings = new GoToSettings();

    public synchronized void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof GoToSettings) {
                settings = (GoToSettings) dataCommon;
            }
        }
    }

    @Override
    public boolean changeToken(String newToken) {
        try{
            settings.authToken = newToken;
            saveObject(settings);
            return true;
        } catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public String testConnection() throws Exception {
        return settings.authToken;
    }

    @Override
    public Hotel getHotelInformation() {
        Store store = storeManager.getMyStore();
        PmsConfiguration pmsConfiguration = pmsManager.getConfiguration();

        return mapStoreToGoToHotel(store, pmsConfiguration);
    }

    GoToRoomData mapBookingItemTypeToGoToRoomData(BookingItemType bookingItemType, PmsAdditionalTypeInformation additionalInfo){
        GoToRoomData roomData = new GoToRoomData();

        roomData.bookingEngineTypeId = bookingItemType.id;
        roomData.description = bookingItemType.description;
        roomData.name = bookingItemType.name;
        roomData.maxGuest = bookingItemType.size;
        roomData.numberOfAdults = additionalInfo.numberOfAdults;
        roomData.numberOfChildren = additionalInfo.numberOfChildren;
        roomData.numberOfUnits = bookingEngine.getBookingItemsByType(bookingItemType.id).size();

        return roomData;
    }

    void updateGoToRoomData() throws Exception {
        List<GoToRoomData> updatedGoToRoomData = new ArrayList<GoToRoomData>();
        Set<String> goToRoomIDs;
        goToRoomIDs = goToRoomData.stream().map(roomData-> roomData.bookingEngineTypeId).collect(Collectors.toSet());
        List<BookingItemType> bookingItemTypes = bookingEngine.getBookingItemTypes();
        for (BookingItemType type : bookingItemTypes) {
            PmsAdditionalTypeInformation additionalInfo = pmsManager.getAdditionalTypeInformationById(type.id);
            GoToRoomData roomData = mapBookingItemTypeToGoToRoomData(type, additionalInfo);
            if(goToRoomIDs.contains(type.id)){
                roomData.goToRoomTypeCode = type.id+"-GoTo";
            } else{
                roomData.goToRoomTypeCode = "-1";
                saveObject(roomData);
            }
            updatedGoToRoomData.add(roomData);
        }

        goToRoomData = updatedGoToRoomData;
    }

    List<GoToRoomData> getGoToRoomData() throws Exception {
        updateGoToRoomData();
        return goToRoomData;
    }

    @Override
    public List<RoomType> getRoomTypeDetails() throws Exception{
        updateGoToRoomData();
        List<RoomType> roomTypes = new ArrayList<RoomType>();
        for(GoToRoomData roomData: goToRoomData){
            if(roomData.goToRoomTypeCode.equals("-2")){
                continue;
            }
            RoomType roomType = mapBookingItemTypeToGoToRoomType(roomData);
            roomTypes.add(roomType);
        }

        return roomTypes;
    }

    @Override
    public List<PriceAllotment> getPriceAndAllotment() throws Exception {
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, pmsManager.getConfigurationSecure().daysAllowedInTheFuture + 20);
        Date end = cal.getTime();

        return getPriceBetweenDates(now, end);

    }

    List<PriceAllotment> getPriceBetweenDates(Date now, Date end) throws Exception {
        updateGoToRoomData();
        List<BookingItemType> bookingItemTypes = bookingEngine.getBookingItemTypes();
        List<GoToRoomData> goToRoomData1 = getGoToRoomData();
        Hashtable table = new Hashtable();
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String dfrom = format.format(now);
        pmsInvoiceManager.startCacheCoverage();
        for (GoToRoomData roomData : goToRoomData) {
            if (roomData.roomCategory.equals("-1") || roomData.roomCategory.equals("-2")) {
                continue;
            }
            BookingItemType roomType = bookingEngine.getBookingItemType(roomData.bookingEngineTypeId);
            if (roomType == null) {
                continue;
            }

            PmsPricing prices = pmsManager.getPrices(now, end);
            Calendar calStart = Calendar.getInstance();
            calStart.setTime(pmsManager.getConfigurationSecure().getDefaultStart(now));

            HashMap<String, Double> pricesForType = prices.dailyPrices.get(roomType.id);
            if (pricesForType == null) {
                logger.error("Invalid price daily prices for : {}", roomType.id);
                continue;
            }

            String[] roomIds = new String[1];
            roomIds[0] = roomType.wubookroomid + "";
            if (roomType.newRoomPriceSystem) {
                roomIds = roomType.virtualWubookRoomIds.split(";");
            }
            int guests = 1;
            for (String roomId : roomIds) {
                Vector list = new Vector();
                Calendar copy = Calendar.getInstance();
                copy.setTime(calStart.getTime());
                list = createRoomPriceList(roomType, pricesForType, copy, list, guests, end);
                if (!list.isEmpty()) {
                    if (!roomId.equals("-1") && !roomId.isEmpty()) {
                        table.put(roomId, list);
                    }
                }
                guests++;
            }
        }

//        Vector params = new Vector();
//        params.addElement(token);
//        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
//        params.addElement(0);
//        params.addElement(dfrom);
//        params.addElement(table);

//        Vector result = executeClient(UPDATE_PLAN_PRICES.value(), params);
        if ((Integer) result.get(0) != SUCCESS_STATUS_CODE) {
//            logText("Unable to update prices:" + result.get(1));
//            logText("parameters sent:");
//            logText(params.toString());
//            return "Failed to update price, " + result.get(1);
        } else {
//            logText("Prices updated between " + now + " - " + end);
        }

    }

    private Vector createRoomPriceList(BookingItemType rdata, HashMap<String, Double> pricesForType, Calendar calStart,
                                       Vector list, int guests, Date endAtDate) {
        Double defaultPrice = pricesForType.get("default");
        PmsConfiguration config = pmsManager.getConfigurationSecure();

        for (int i = 0; i < (365 * 3); i++) {
            int year = calStart.get(Calendar.YEAR);
            int month = calStart.get(Calendar.MONTH) + 1;
            int day = calStart.get(Calendar.DAY_OF_MONTH);
            String dateString = "";

            if (day < 10) {
                dateString += "0" + day;
            } else {
                dateString += day;
            }
            dateString += "-";
            if (month < 10) {
                dateString += "0" + month;
            } else {
                dateString += month;
            }
            dateString += "-" + year;
            Double priceToAdd = null;
            if (pricesForType.containsKey(dateString)) {
                priceToAdd = pricesForType.get(dateString);
            }
            if ((priceToAdd == null || priceToAdd == 0.0) && defaultPrice != null) {
                priceToAdd = defaultPrice;
            }
            if (priceToAdd == null) {
                priceToAdd = 999999.0;
            } else if (rdata.newRoomPriceSystem) {
                PmsBookingRooms room = new PmsBookingRooms();
                room.numberOfGuests = guests;
                room.bookingItemTypeId = rdata.id;
                room.date.start = calStart.getTime();
                room.date.end = new Date();
                room.date.end.setTime(calStart.getTimeInMillis() + 57600000);
                PmsBooking booking = new PmsBooking();
                pmsManager.setPriceOnRoom(room, true, booking);
                priceToAdd = room.price;
            } else if (pmsManager.getConfigurationSecure().enableCoveragePrices) {
                PmsBooking booking = new PmsBooking();
                priceToAdd = pmsInvoiceManager.calculatePrice(rdata.id, calStart.getTime(),
                        calStart.getTime(), true, booking);
            }

            if (priceToAdd == 0.0) {
                priceToAdd = 1.0;
            }

            if (config.increaseByPercentage > 0) {
                double factor = 1.15;
                if (config.increaseByPercentage > 0) {
                    factor = 1.0 + ((double) config.increaseByPercentage / 100);
                }
                priceToAdd *= factor;
                priceToAdd = (double) Math.round(priceToAdd);
            }

            list.add(priceToAdd);
            if (endAtDate.before(calStart.getTime())) {
                break;
            }
            calStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return list;
    }

    RoomType mapBookingItemTypeToGoToRoomType(GoToRoomData roomData){
        RoomType roomType = new RoomType();

        if(roomData.goToRoomTypeCode=="-1"){
            roomData.goToRoomTypeCode= roomData.bookingEngineTypeId+"-GoTo";
        }
        roomType.room_type_code = roomData.goToRoomTypeCode;
        roomType.description = roomData.description;
        roomType.name = roomData.name;
        roomType.max_guest = roomData.maxGuest;
        roomType.number_of_adults = roomData.numberOfAdults;
        roomType.number_of_children = roomData.numberOfChildren;
        roomType.number_of_unit = roomData.numberOfUnits;

        return roomType;
    }

    Hotel mapStoreToGoToHotel(Store store, PmsConfiguration pmsConfiguration){
        Hotel hotel = new Hotel();
        Contact contact = new Contact();

        hotel.name = pmsConfiguration.bookingTag;
//        hotel.name = store.configuration.shopName;
        hotel.address = store.configuration.Adress+ ", "+store.configuration.streetAddress;
        hotel.checkin_time = pmsConfiguration.getDefaultStart();
        hotel.checkout_time = pmsConfiguration.getDefaultEnd();
        contact.email = store.configuration.emailAdress;
        contact.organization_number = store.configuration.orgNumber;
        contact.phone_number = store.configuration.phoneNumber;
        String website = getHotelWebAddress(store);
        if(website!=null) contact.website = website;

        hotel.contact_details = contact;
        hotel.hotel_code = store.additionalDomainNames.get(0);

        return hotel;
    }
    String getHotelWebAddress(Store store){
        String webAddress = store.getDefaultWebAddress();
        if(webAddress.contains("getshop.com")) return webAddress;
        for(String address: store.additionalDomainNames){
            if(address.contains("getshop.com")) return address;
        }
        return null;
    }

}

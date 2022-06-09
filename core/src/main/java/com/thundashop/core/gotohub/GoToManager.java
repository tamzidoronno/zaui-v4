package com.thundashop.core.gotohub;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.gotohub.dto.*;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
@GetShopSession
@Slf4j
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


    private static final String MANAGER = GoToManager.class.getSimpleName();
    private GoToSettings settings = new GoToSettings();

    public synchronized void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof GoToSettings) {
                settings = (GoToSettings) dataCommon;
            }
           /* else if(dataCommon instanceof GoToRoomData) {
                goToRoomData.put(((GoToRoomData) dataCommon).bookingEngineTypeId, (GoToRoomData) dataCommon);
            }*/
            /*else if(dataCommon instanceof RatePlan) {
                ratePlans.put(((RatePlan) dataCommon).rate_plan_code, (RatePlan) dataCommon);
            }*/
        }
    }

    @Override
    public boolean changeToken(String newToken) {
        try{
            settings.authToken = newToken;
            saveObject(settings);
            return true;
        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public String testConnection() throws Exception {
        return settings.authToken;
    }

    @Override
    public Hotel getHotelInformation() {
        return mapStoreToGoToHotel(storeManager.getMyStore(), pmsManager.getConfiguration());
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

    public  HashMap<String, GoToRoomData> getGoToRoomData() throws Exception {
        HashMap<String, GoToRoomData> goToRoomData = new HashMap<>();
        List<BookingItemType> bookingItemTypes = bookingEngine.getBookingItemTypes();
        for (BookingItemType type : bookingItemTypes) {
            PmsAdditionalTypeInformation additionalInfo = pmsManager.getAdditionalTypeInformationById(type.id);
            GoToRoomData roomData = mapBookingItemTypeToGoToRoomData(type, additionalInfo);
            goToRoomData.put(roomData.bookingEngineTypeId, roomData);
        }
        return goToRoomData;
    }
    @Override
    public List<RoomType> getRoomTypeDetails() throws Exception{
        HashMap<String, GoToRoomData> goToRoomData = getGoToRoomData();
        List<RoomType> roomTypes = new ArrayList<RoomType>();
        for(GoToRoomData roomData: goToRoomData.values()){
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
        List<BookingItemType> bookingItemTypes = bookingEngine.getBookingItemTypes();
        Hashtable table = new Hashtable();
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String dfrom = format.format(now);
        pmsInvoiceManager.startCacheCoverage();
        HashMap<String, GoToRoomData> goToRoomData = getGoToRoomData(); 
        for (GoToRoomData roomData : goToRoomData.values()) {
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

            HashMap<String, Double> pricesForType = prices.dailyPrices.get(roomData.bookingEngineTypeId);
            if (pricesForType == null) {
                log.error("Invalid price daily prices for : {}", roomType.id);
                continue;
            }

            String roomIds = roomData.goToRoomTypeCode;
            int guests = 1;
            for (RatePlan ratePlan : roomData.ratePlans) {
                Calendar copy = Calendar.getInstance();
                copy.setTime(calStart.getTime());
                ArrayList<Double> priceList = createRoomPriceList(roomData, pricesForType, copy, guests, end);
                if (!priceList.isEmpty()) {
//                    if (!roomId.equals("-1") && !roomId.isEmpty()) {
//                        table.put(roomId, list);
//                    }
                    for(double price : priceList){

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
//        if ((Integer) result.get(0) != SUCCESS_STATUS_CODE) {
//            logText("Unable to update prices:" + result.get(1));
//            logText("parameters sent:");
//            logText(params.toString());
//            return "Failed to update price, " + result.get(1);
//        } else {
//            logText("Prices updated between " + now + " - " + end);
//        }
        return null;
    }

    private ArrayList<Double> createRoomPriceList(GoToRoomData rdata, HashMap<String, Double> pricesForType, Calendar calStart,
                                       int guests, Date endAtDate) {
        Double defaultPrice = pricesForType.get("default");
        PmsConfiguration config = pmsManager.getConfigurationSecure();
        ArrayList<Double> priceList = new ArrayList<Double>();

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
               // room.bookingItemTypeId = rdata.id;
                room.date.start = calStart.getTime();
                room.date.end = new Date();
                room.date.end.setTime(calStart.getTimeInMillis() + 57600000);
                PmsBooking booking = new PmsBooking();
                pmsManager.setPriceOnRoom(room, true, booking);
                priceToAdd = room.price;
            } else if (pmsManager.getConfigurationSecure().enableCoveragePrices) {
                PmsBooking booking = new PmsBooking();
               /* priceToAdd = pmsInvoiceManager.calculatePrice(rdata.id, calStart.getTime(),
                        calStart.getTime(), true, booking);*/
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

            priceList.add(priceToAdd);
            if (endAtDate.before(calStart.getTime())) {
                break;
            }
            calStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return priceList;
    }

    RoomType mapBookingItemTypeToGoToRoomType(GoToRoomData roomData){
        RoomType roomType = new RoomType();

        if(roomData.goToRoomTypeCode=="-1"){
            roomData.goToRoomTypeCode= roomData.bookingEngineTypeId+"-GoTo";
        }
        roomType.setRoomTypeCode(roomData.goToRoomTypeCode);
        roomType.setDescription(roomData.description);
        roomType.setName(roomData.name);
        roomType.setMaxGuest(roomData.maxGuest);
        roomType.setNumberOfUnit(roomData.numberOfAdults);
        roomType.setNumberOfChildren(roomData.numberOfChildren);
        roomType.setNumberOfUnit(roomData.numberOfUnits);
        List<RatePlan> ratePlans = new ArrayList<>();
        for(int guest=1; guest<=roomData.maxGuest; guest++){
            RatePlan newRatePlan = createNewRatePlan(guest);
            ratePlans.add(newRatePlan);
            // For room type, can be duplicated as max guest common
               /// RatePlan newRatePlan = createNewRatePlan(guest);
                //TODO no need to save run saveObject(newRatePlan);
        }
        roomType.setRatePlans(ratePlans);

        return roomType;
    }

    RatePlan createNewRatePlan(int numberOfGuests){
        RatePlan ratePlan = new RatePlan();
        ratePlan.setRestriction(getRatePlanCode(numberOfGuests));
        ratePlan.setName("Rate Plan "+String.valueOf(numberOfGuests));
        ratePlan.setDescription("Rate Plan for "+String.valueOf(numberOfGuests)+" guests");
        String about = "Rate Plan "+String.valueOf(numberOfGuests)+" is mainly for "+String.valueOf(numberOfGuests)+" guests.";
        about += " Price may vary for this rate plan and this rate plan will be applied";
        about += " when someone book a room for "+String.valueOf(numberOfGuests)+" guests";
        ratePlan.setAbout(about);
        return ratePlan;
    }

    Hotel mapStoreToGoToHotel(Store store, PmsConfiguration pmsConfiguration){
        Hotel hotel = new Hotel();
        Contact contact = new Contact();

        hotel.setName(store.configuration.shopName);
        String address = "";
        if(StringUtils.isNotBlank(store.configuration.streetAddress)) {
            address = store.configuration.streetAddress;
        }
        address += store.configuration.Adress;
        hotel.setAddress(address);
        hotel.setCheckinTime(pmsConfiguration.getDefaultStart());
        hotel.setCheckoutTime(pmsConfiguration.getDefaultEnd());
        contact.setEmail(store.configuration.emailAdress);
        contact.setOrganizationNumber(store.configuration.orgNumber);
        contact.setPhoneNumber(store.configuration.phoneNumber);
        String website = getHotelWebAddress(store);
        if(StringUtils.isNotBlank(website)) contact.setWebsite(website);

        hotel.setContactDetails(contact);
        hotel.setHotelCode(store.additionalDomainNames.get(0));
        hotel.setDescription("");

        String imageUrlPrefix = "https://" + store.webAddressPrimary + "//displayImage.php?id=";
        if(StringUtils.isNotBlank(store.configuration.mobileImagePortrait)) hotel.getImages().add(imageUrlPrefix + store.configuration.mobileImagePortrait);
        if(StringUtils.isNotBlank(store.configuration.mobileImageLandscape)) hotel.getImages().add(imageUrlPrefix + store.configuration.mobileImageLandscape);
        hotel.setStatus(store.deactivated ? "Deactivated" : "Active");
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

    String getRatePlanCode(int numberOfGuests){
        return "RP-"+String.valueOf(numberOfGuests);
    }

    String getGoToRoomTypeCode(){
        return "";
    }

}

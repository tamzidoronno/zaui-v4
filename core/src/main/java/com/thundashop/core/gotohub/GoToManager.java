package com.thundashop.core.gotohub;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.gotohub.dto.*;
import com.thundashop.core.listmanager.data.Entry;
import com.thundashop.core.pmsbookingprocess.BookingProcessRooms;
import com.thundashop.core.pmsbookingprocess.PmsBookingProcess;
import com.thundashop.core.pmsbookingprocess.StartBooking;
import com.thundashop.core.pmsbookingprocess.StartBookingResult;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.data.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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


    @Autowired
    PmsBookingProcess pmsProcess;


    private static final String MANAGER = GoToManager.class.getSimpleName();
    private GoToSettings settings = new GoToSettings();
    private static SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

    public enum RoomCategory {
        ROOM("0"),
        CONFERENCE("1"),
        RESTAURANT("2"),
        CAMPING("3"),
        CABIN("4"),
        HOSTELBED("5"),
        APARTMENT("6");
        RoomCategory(String s) {
        }
    }
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

    private GoToRoomData mapBookingItemTypeToGoToRoomData(BookingItemType bookingItemType, BookingProcessRooms room, PmsAdditionalTypeInformation additionalInfo){
        GoToRoomData roomData = new GoToRoomData();

        roomData.setBookingEngineTypeId(bookingItemType.id);
        roomData.setDescription(bookingItemType.description);
        roomData.setName(bookingItemType.name);
        roomData.setGoToRoomTypeCode(bookingItemType.name);
        roomData.setRoomCategory(getRoomType(bookingItemType.systemCategory));
        roomData.setMaxGuest(bookingItemType.size);
        roomData.setNumberOfAdults(additionalInfo.numberOfAdults);
        roomData.setNumberOfChildren(additionalInfo.numberOfChildren);
        roomData.setNumberOfUnits(bookingEngine.getBookingItemsByType(bookingItemType.id).size());
        if(room.images != null) {
            List<String> res = room.images.stream().filter(e -> isNotBlank(e.filename)).map(e -> e.filename).collect(Collectors.toList());
            roomData.setImages(res);
        }
        roomData.setStatus(bookingItemType.visibleForBooking);
        roomData.setPricesByGuests(room.pricesByGuests);
        roomData.setAvailableRooms(room.availableRooms);

        return roomData;
    }

    private String getRoomType(Integer type) {
        try {
            RoomCategory rm = RoomCategory.valueOf(type.toString());
            return rm.name();
        } catch (Exception e) {
            log.error("invalid room type {}", type);
            return RoomCategory.ROOM.name();
        }
    }

    public  List<GoToRoomData> getGoToRoomData(boolean needPricing) throws Exception {
        List<GoToRoomData> goToRoomData = new ArrayList<>();
        List<BookingItemType> bookingItemTypes = bookingEngine.getBookingItemTypesWithSystemType(null);

        boolean isAdministrator = false;
        User user = getSession() != null && getSession().currentUser != null ? getSession().currentUser : new User();
        if(getSession() != null && getSession().currentUser != null && getSession().currentUser.isAdministrator()) {
            isAdministrator = true;
        }

        StartBooking arg = getBookingArgument();
        long totalRooms = 0;
        for(BookingItemType type : bookingItemTypes) {
            if(!type.visibleForBooking) {
                continue;
            }
            BookingProcessRooms room = new BookingProcessRooms();
            room.userId = user.id;
            room.description = type.getTranslatedDescription(getSession().language);
            room.availableRooms = pmsManager.getNumberOfAvailable(type.id, arg.start, arg.end, true, true);
            room.id = type.id;
            room.systemCategory = type.systemCategory;
            room.visibleForBooker = type.visibleForBooking;
            totalRooms += room.availableRooms;
            PmsAdditionalTypeInformation typeInfo = pmsManager.getAdditionalTypeInformationById(type.id);
            try {
                room.images.addAll(typeInfo.images);
                room.sortDefaultImageFirst();
                room.name = type.getTranslatedName(getSession().language);
                room.maxGuests = type.size;
                if (needPricing) {
                    for (int i = 1; i <= type.size; i++) {
                        Double price = getPriceForRoom(room, arg.start, arg.end, i, "");
                        room.pricesByGuests.put(i, price);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(PmsBookingProcess.class.getName()).log(Level.SEVERE, null, ex);
            }

            goToRoomData.add(mapBookingItemTypeToGoToRoomData(type, room, typeInfo));
        }
        return goToRoomData;
    }

    private Double getPriceForRoom(BookingProcessRooms bookingProcessRoom, Date start, Date end, int numberofguests, String discountcode) {
        PmsBookingRooms room = new PmsBookingRooms();
        room.bookingItemTypeId = bookingProcessRoom.id;
        room.date = new PmsBookingDateRange();
        room.date.start = start;
        room.date.end = end;
        room.numberOfGuests = numberofguests;

        PmsBooking booking = new PmsBooking();
        booking.priceType = PmsBooking.PriceType.monthly;
        booking.couponCode = discountcode;
        booking.userId = bookingProcessRoom.userId;
        pmsManager.setPriceOnRoom(room, true, booking);
        return room.price;
    }

    private StartBooking getBookingArgument() {
        StartBooking arg = new StartBooking();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        cal.set(Calendar.DAY_OF_MONTH, 18);

        Date start = cal.getTime();
        arg.start = start;

        cal.add(Calendar.DAY_OF_WEEK, 1);
        cal.set(Calendar.HOUR_OF_DAY, 11);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);

        Date end = cal.getTime();
        arg.end = end;
        arg.rooms  = 0;
        arg.adults  = 1;
        arg.children = 0;

        return arg;
    }
    private StartBookingResult getAvailability() {
        StartBooking arg = getBookingArgument();
        return pmsProcess.startBooking(arg);
    }
    
    @Override
    public List<RoomType> getRoomTypeDetails() throws Exception {
        List<GoToRoomData> goToRoomData = getGoToRoomData(false);
        List<RoomType> roomTypes = new ArrayList<>();
        for(GoToRoomData roomData: goToRoomData){
            if("-2".equals(roomData.getGoToRoomTypeCode())){
                continue;
            }
            RoomType roomType = getRoomTypesFromRoomData(roomData);
            roomTypes.add(roomType);
        }
        return roomTypes;
    }

    @Override
    public List<PriceAllotment> getPriceAndAllotment() throws Exception {
        return getPriceAllotments();
    }

    private List<PriceAllotment> getPriceAllotments() throws Exception {
        List<GoToRoomData> goToRoomData = getGoToRoomData(true);
        StartBooking range = getBookingArgument();
        List<PriceAllotment> allotments = new ArrayList<>();
        for (GoToRoomData roomData : goToRoomData) {
            if ("-1".equals(roomData.getRoomCategory()) || "-2".equals(roomData.getRoomCategory()) || roomData.getPricesByGuests() == null) {
                continue;
            }
            for(Map.Entry<Integer, Double> priceEntry : roomData.getPricesByGuests().entrySet()) {
                PriceAllotment al = new PriceAllotment();
                al.setStartDate(df.format(range.start));
                al.setEndDate(df.format(range.end));
                RatePlan plan = roomData.getRatePlans().get(priceEntry.getKey());
                al.setRatePlanCode(plan != null ? plan.getRatePlanCode() : "");
                al.setRoomTypeCode(roomData.getGoToRoomTypeCode());
                al.setPrice(priceEntry.getValue());
                al.setAllotment(roomData.getAvailableRooms());
            }
        }
        return allotments;
    }
    private RoomType getRoomTypesFromRoomData(GoToRoomData roomData) {
        RoomType roomType = new RoomType();
        roomType.setHotelCode(storeManager.getMyStore().webAddressPrimary);
        roomType.setRoomTypeCode(roomData.getGoToRoomTypeCode());
        roomType.setDescription(roomData.getDescription());
        roomType.setName(roomData.getName());
        roomType.setMaxGuest(roomData.getMaxGuest());
        roomType.setNumberOfAdults(roomData.getNumberOfAdults());
        roomType.setNumberOfUnit(roomData.getNumberOfAdults());
        roomType.setNumberOfChildren(roomData.getNumberOfChildren());
        List<RatePlan> ratePlans = new ArrayList<>();
        StartBooking range = getBookingArgument();
        for(int guest=1; guest<=roomData.getMaxGuest(); guest++){
            RatePlan newRatePlan = createNewRatePlan(guest, range, roomData.getName());
            ratePlans.add(newRatePlan);
        }
        roomType.setRatePlans(ratePlans);
        return roomType;
    }

    private RatePlan createNewRatePlan(int numberOfGuests, StartBooking range, String name){
        RatePlan ratePlan = new RatePlan();
        ratePlan.setRatePlanCode(name + "-" + numberOfGuests);
        ratePlan.setRestriction("");
        ratePlan.setName("Rate Plan - "+ name + " - " + String.valueOf(numberOfGuests));
        ratePlan.setDescription("Rate Plan for "+String.valueOf(numberOfGuests)+" guests");
        String about = "Rate Plan "+String.valueOf(numberOfGuests)+" is mainly for "+String.valueOf(numberOfGuests)+" guests.";
        about += " Price may vary for this rate plan and this rate plan will be applied";
        about += " when someone book a room for "+String.valueOf(numberOfGuests)+" guests";
        ratePlan.setAbout(about);
        ratePlan.setGuestCount(String.valueOf(numberOfGuests));
        ratePlan.setEffectiveDate(df.format(range.start));
        ratePlan.setExpireDate(df.format(range.end));
        return ratePlan;
    }

    private Hotel mapStoreToGoToHotel(Store store, PmsConfiguration pmsConfiguration){
        Hotel hotel = new Hotel();
        Contact contact = new Contact();

        hotel.setName(store.configuration.shopName);
        String address = "";
        if(isNotBlank(store.configuration.streetAddress)) {
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
        if(isNotBlank(website)) contact.setWebsite(website);

        hotel.setContactDetails(contact);
        hotel.setHotelCode(store.webAddressPrimary);
        hotel.setDescription("");

        String imageUrlPrefix = "https://" + store.webAddressPrimary + "//displayImage.php?id=";
        if(isNotBlank(store.configuration.mobileImagePortrait)) hotel.getImages().add(imageUrlPrefix + store.configuration.mobileImagePortrait);
        if(isNotBlank(store.configuration.mobileImageLandscape)) hotel.getImages().add(imageUrlPrefix + store.configuration.mobileImageLandscape);
        hotel.setStatus(store.deactivated ? "Deactivated" : "Active");
        return hotel;
    }
    private String getHotelWebAddress(Store store){
        String webAddress = store.getDefaultWebAddress();
        if(webAddress.contains("getshop.com")) return webAddress;
        for(String address: store.additionalDomainNames){
            if(address.contains("getshop.com")) return address;
        }
        return null;
    }
}

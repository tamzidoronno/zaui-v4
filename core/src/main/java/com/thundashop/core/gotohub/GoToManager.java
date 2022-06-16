package com.thundashop.core.gotohub;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.gotohub.dto.*;
import com.thundashop.core.pmsbookingprocess.BookingProcessRooms;
import com.thundashop.core.pmsbookingprocess.PmsBookingProcess;
import com.thundashop.core.pmsbookingprocess.StartBooking;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.data.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @Autowired PmsManager pmsManager;
    @Autowired StoreManager storeManager;
    @Autowired StorePool storePool;
    @Autowired BookingEngine bookingEngine;
    @Autowired PmsInvoiceManager pmsInvoiceManager;
    @Autowired PmsBookingProcess pmsProcess;

    private GoToSettings settings = new GoToSettings();
    private static final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");


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
    public String testConnection() {
        return settings.authToken;
    }

    @Override
    public Hotel getHotelInformation() {
        return mapStoreToGoToHotel(storeManager.getMyStore(), pmsManager.getConfiguration());
    }

    @Override
    public List<RoomType> getRoomTypeDetails() throws Exception {
        StartBooking arg = getBookingArgument(0);
        List<GoToRoomData> goToRoomData = getGoToRoomData(false, arg);
        List<RoomType> roomTypes = new ArrayList<>();
        for(GoToRoomData roomData: goToRoomData){
            if(isBlank(roomData.getGoToRoomTypeCode())){
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
        return BookingItemType.BookingSystemCategory.categories.get(type) != null ?  BookingItemType.BookingSystemCategory.categories.get(type) : "ROOM";
    }

    private List<GoToRoomData> getGoToRoomData(boolean needPricing, StartBooking arg) throws Exception {
        List<GoToRoomData> goToRoomData = new ArrayList<>();
        List<BookingItemType> bookingItemTypes = bookingEngine.getBookingItemTypesWithSystemType(null);
        User user = getSession() != null && getSession().currentUser != null ? getSession().currentUser : new User();
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
            PmsAdditionalTypeInformation typeInfo = pmsManager.getAdditionalTypeInformationById(type.id);
            try {
                room.images.addAll(typeInfo.images);
                room.sortDefaultImageFirst();
                room.name = type.getTranslatedName(getSession().language);
                room.maxGuests = type.size;
                if (needPricing) {
                    for (int i = 1; i <= type.size; i++) {
                        room.roomsSelectedByGuests.put(i, i);
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
        booking.priceType = PmsBooking.PriceType.daily;
        booking.couponCode = discountcode;
        booking.userId = bookingProcessRoom.userId;
        pmsManager.setPriceOnRoom(room, true, booking);
        return room.price;
    }

    private StartBooking getBookingArgument(int i) {
        StartBooking arg = new StartBooking();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        cal.add(Calendar.DAY_OF_YEAR, i);

        Date start = cal.getTime();
        arg.start = start;

        cal.add(Calendar.DAY_OF_YEAR, 1);
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

    private List<PriceAllotment> getPriceAllotments() throws Exception {
        List<PriceAllotment> allotments = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            StartBooking range = getBookingArgument(i);
            List<GoToRoomData> goToRoomData = getGoToRoomData(true, range);
            for (GoToRoomData roomData : goToRoomData) {
                if (roomData.getPricesByGuests() == null) {
                    continue;
                }
                for (Map.Entry<Integer, Double> priceEntry : roomData.getPricesByGuests().entrySet()) {
                    PriceAllotment al = new PriceAllotment();
                    al.setStartDate(df.format(range.start));
                    al.setEndDate(df.format(range.end));
                    al.setRatePlanCode(roomData.getName() + "-" + priceEntry.getKey());
                    al.setRoomTypeCode(roomData.getGoToRoomTypeCode());
                    al.setPrice(priceEntry.getValue());
                    al.setAllotment(roomData.getAvailableRooms());
                    allotments.add(al);
                }
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
        roomType.setRoomCategory(roomData.getRoomCategory());
        List<RatePlan> ratePlans = new ArrayList<>();
        String start = LocalDate.now().format(formatter);
        String end = LocalDate.now().plusYears(1).format(formatter);
        for(int guest=1; guest <= roomData.getMaxGuest(); guest++){
            RatePlan newRatePlan = createNewRatePlan(guest, roomData.getName(), start, end);
            ratePlans.add(newRatePlan);
        }
        roomType.setRatePlans(ratePlans);
        return roomType;
    }

    private RatePlan createNewRatePlan(int numberOfGuests, String name, String start, String end){
        RatePlan ratePlan = new RatePlan();
        ratePlan.setRatePlanCode(name + "-" + numberOfGuests);
        ratePlan.setRestriction("");
        ratePlan.setName("Rate Plan - " + name + " - " + numberOfGuests);
        ratePlan.setDescription("Rate Plan for " + numberOfGuests + " guests");
        StringBuilder about = new StringBuilder().append("Rate Plan ").append(numberOfGuests).append(" is mainly for ").append(numberOfGuests).append(" guests.")
        .append(" Price may vary for this rate plan and this rate plan will be applied")
        .append(" when someone book a room for ").append(numberOfGuests).append(" guests");
        ratePlan.setAbout(about.toString());
        ratePlan.setGuestCount(String.valueOf(numberOfGuests));
        ratePlan.setEffectiveDate(start);
        ratePlan.setExpireDate(end);
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

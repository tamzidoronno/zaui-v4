package com.thundashop.core.gotohub;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.gotohub.dto.*;
import com.thundashop.core.gotohub.dto.Room;
import com.thundashop.core.jomres.JomresManager;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsbookingprocess.BookingProcessRooms;
import com.thundashop.core.pmsbookingprocess.PmsBookingProcess;
import com.thundashop.core.pmsbookingprocess.StartBooking;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.data.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
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
    @Autowired StoreApplicationPool storeApplicationPool;
    @Autowired OrderManager orderManager;
    @Autowired MessageManager messageManager;

    private GoToSettings settings = new GoToSettings();
    private static final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final SimpleDateFormat bookingDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JomresManager.class);


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

    private PmsBooking getBooking(Booking booking) throws Exception {
        PmsBooking pmsBooking = findCorrelatedBooking(booking.getReservationId());
        if (pmsBooking == null) {
            pmsBooking = pmsManager.startBooking();
        }

        pmsBooking = mapBookingToPmsBooking(booking, pmsBooking);
        pmsBooking = setPaymentMethod(pmsBooking);
        pmsManager.setBooking(pmsBooking);
        pmsInvoiceManager.clearOrdersOnBooking(pmsBooking);
        pmsBooking = pmsManager.doCompleteBooking(pmsBooking);
        return pmsBooking;
    }

    private String getBookingDetailsTextForMail(Booking booking){
        String text = "Booking Details:<br><br>";
        text += "&emsp Arrival Date: "+ booking.getCheckInDate();
        text += "<br><br>";
        text += "&emsp Departure Date: "+ booking.getCheckOutDate();
        text += "<br><br>";
        text += "&emsp Rooms:";
        text += "<br><br>";
        for(Room room: booking.getRooms()){
            BookingItemType type = bookingEngine.getBookingItemType(room.getRoomCode());
            if(type!=null) text += "&emsp &emsp "+type.name+"<br><br>";
            else text += "&emsp &emsp Room Type (BookingItemType) isn't found for Id: "+room.getRoomCode()+"<br><br>";

        }
        return text;
    }

    private void handleOverbooking(PmsBooking pmsBooking, String emailText){
        pmsManager.deleteBooking(pmsBooking.id);

        String email = getStoreEmailAddress();
        String content = "Booking creation has been failed due to overbooking:<br>" + emailText;
        messageManager.sendMail(email, email, "Goto Booking Failed, Reason: Overbooking", content, email,
                email);
    }
    private boolean checkForPaidOrders(PmsBooking newbooking) {
        boolean hasPaidOrders = false;

        if (newbooking.orderIds == null) {
            newbooking.orderIds = new ArrayList<>();
        }

        for (String orderId : newbooking.orderIds) {
            Order ord = orderManager.getOrder(orderId);
            if (ord.status == Order.Status.PAYMENT_COMPLETED) {
                hasPaidOrders = true;
            }
        }
        return hasPaidOrders;
    }

    private void handlePaymentOrder(PmsBooking pmsBooking, String checkoutDate) throws ParseException {
        Date endInvoiceAt = new Date();
        if (bookingDateFormatter.parse(checkoutDate).after(endInvoiceAt))
            endInvoiceAt = bookingDateFormatter.parse(checkoutDate);

        NewOrderFilter filter = new NewOrderFilter();
        filter.createNewOrder = false;
        filter.prepayment = true;
        filter.endInvoiceAt = endInvoiceAt;

        if (pmsBooking.paymentType != null && !pmsBooking.paymentType.isEmpty()) {
            pmsInvoiceManager.autoCreateOrderForBookingAndRoom(pmsBooking.id, pmsBooking.paymentType);
        }


        boolean hasPaidOrders = checkForPaidOrders(pmsBooking);
        if (hasPaidOrders) {
            String orderId = pmsInvoiceManager.autoCreateOrderForBookingAndRoom(pmsBooking.id,
                    pmsBooking.paymentType);
            Order ord = orderManager.getOrder(orderId);
            Double amount = orderManager.getTotalAmount(ord);
            if (amount == 0.0) {
                pmsInvoiceManager.markOrderAsPaid(pmsBooking.id, orderId);
            }
        }
    }

    public boolean saveBooking(Booking booking) {
        try {
            if (!isCurrencySameWithSystem(booking.getCurrency())) {
                handleDifferentCurrencyBooking(booking);
                return false;
            }
            PmsBooking pmsBooking = getBooking(booking);
            if (pmsBooking == null) {
                String errorText = "Failed to add new Goto Booking, Arrival: "+ booking.getCheckInDate()
                        + ", Departure: " + booking.getCheckOutDate();
                logger.error(errorText);
                String emailDetails = "Booking Creation/Updation has been failed.<br><br>"+
                        "Some possible reason: <br>"+
                        "1. Maybe there is one or more invalid room to book"+
                        "2. The payment method is not valid or failed to activate"+
                        "Please notify admin to check<br>"
                        +getBookingDetailsTextForMail(booking);
                String email = getStoreEmailAddress();
                messageManager.sendMail(email, email, "Goto Booking Failure", emailDetails, email,
                        email);
                return false;
            }
            pmsManager.saveBooking(pmsBooking);
            pmsInvoiceManager.clearOrdersOnBooking(pmsBooking);
            if (pmsBooking.hasOverBooking()) {
                handleOverbooking(pmsBooking, getBookingDetailsTextForMail(booking));
                return false;
            }
//            handlePaymentOrder(pmsBooking, booking.getCheckOutDate());
            return true;

        } catch (Exception e) {
            logPrintException(e);
            messageManager.sendMessageToStoreOwner("Unexpected Error Occured.. <br><br>"
                    +getBookingDetailsTextForMail(booking),
                    "Goto Booking Creation Failed"
            );
            return false;
        }
    }

    private boolean isCurrencySameWithSystem(String currencyCode){
        return currencyCode.equals(storeManager.getStoreSettingsApplicationKey("currencycode"));
    }

    private void handleDifferentCurrencyBooking(Booking booking){
        String emailContent = "Booking failed due to different currency. <br><br>"+getBookingDetailsTextForMail(booking);
        String email = getStoreEmailAddress();
        messageManager.sendMail(email, email, "Goto Booking Failed, Reason: Overbooking", emailContent, email,
                email);

    }

    private void activatePaymentMethod(String pmethod) {
        if (!storeApplicationPool.isActivated(pmethod)) {
            storeApplicationPool.activateApplication(pmethod);
        }
    }

    private String getPaymentTypeId(){
        //TODO will return specific payment type id for goto, will get from configuration
        return "70ace3f0-3981-11e3-aa6e-0800200c9a66";
    }

    private Date fixTime(String dateStr, String time) throws ParseException {
        Date date = bookingDateFormatter.parse(dateStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        String[] hourAndSecond = time.split(":");
        cal.set(Calendar.HOUR_OF_DAY, new Integer(hourAndSecond[0]));
        cal.set(Calendar.MINUTE, new Integer(hourAndSecond[1]));

        return cal.getTime();
    }

    private PmsBookingRooms setCorrectStartEndTime(PmsBookingRooms room, Booking booking) throws Exception {
        room.date = new PmsBookingDateRange();
        try {
            room.date.start = fixTime(booking.getCheckInDate(), pmsManager.getConfigurationSecure().getDefaultStart());
            room.date.end = fixTime(booking.getCheckOutDate(), pmsManager.getConfigurationSecure().getDefaultEnd());
            return room;
        } catch (ParseException e) {
            throw new Exception("Checkin / Checkout Date parsing failed");
        }

    }

    private PmsBookingRooms mapRoomToPmsRoom(Booking booking, Room gotoBookingRoom) throws Exception {
        PmsBookingRooms pmsBookingRoom = new PmsBookingRooms();
//        @SerializedName("cancelationDeadline")
//        @Expose
//        private String cancelationDeadline;

        pmsBookingRoom = setCorrectStartEndTime(pmsBookingRoom, booking);
        pmsBookingRoom.numberOfGuests = gotoBookingRoom.getAdults() + gotoBookingRoom.getChildrenAges().size();
        pmsBookingRoom.bookingItemTypeId = gotoBookingRoom.getRoomCode();

        if (bookingEngine.getBookingItemType(gotoBookingRoom.getRoomCode())==null) {
            logger.error("booking room type does not exist, BookingItemTypeId: "+gotoBookingRoom.getRoomCode());
            return null;
        }
        PmsGuests guest = new PmsGuests();
        guest.email = booking.getOrderer().getEmail();
        guest.name = booking.getOrderer().getFirstName()+" "+booking.getOrderer().getLastName();
        guest.phone = booking.getOrderer().getMobile().getAreaCode()+booking.getOrderer().getMobile().getPhoneNumber();

        pmsBookingRoom.guests.add(guest);
        return pmsBookingRoom;
    }

    private PmsBooking setPaymentMethod(PmsBooking pmsBooking){
        String paymentMethodId = getPaymentTypeId();
        activatePaymentMethod(paymentMethodId);
        pmsBooking.paymentType = paymentMethodId;
        pmsBooking.isPrePaid = true;
        return pmsBooking;
    }
    private PmsBooking mapBookingToPmsBooking(Booking booking, PmsBooking pmsBooking) throws Exception {
        for (PmsBookingRooms room : pmsBooking.getAllRooms()) {
            room.unmarkOverBooking();
        }
        pmsBooking.channel = "goto";
        pmsBooking.language = booking.getLanguage();
        pmsBooking.isPrePaid = true;
        if (StringUtils.isNotBlank(booking.getComment())) {
            PmsBookingComment comment = new PmsBookingComment();
            comment.userId = "";
            comment.comment = booking.getComment();
            comment.added = new Date();
            pmsBooking.comments.put(System.currentTimeMillis(), comment);
        }

        Orderer booker = booking.getOrderer();
        String user_fullName = booker.getFirstName()+ " " + booker.getLastName();
        String user_cellPhone = booker.getMobile().getAreaCode()+booker.getMobile().getPhoneNumber();

        pmsBooking.registrationData.resultAdded.put("user_fullName", user_fullName);
        pmsBooking.registrationData.resultAdded.put("user_cellPhone", user_cellPhone);
        pmsBooking.registrationData.resultAdded.put("user_emailAddress", booker.getEmail());

        List<Room> bookingRooms = booking.getRooms();

        for (Room gotoBookingRoom : bookingRooms) {
            PmsBookingRooms room = mapRoomToPmsRoom(booking, gotoBookingRoom);
            if(room==null) return null;
            pmsBooking.addRoom(room);
        }

        if (pmsBooking.rooms.isEmpty()) {
            logger.debug("Returning since there are no rooms to add id:");
            return null;
        }
        return pmsBooking;
    }

    private PmsBooking findCorrelatedBooking(String reservationId){
        if(StringUtils.isNotBlank(reservationId))
            return pmsManager.getBooking(reservationId);
        return null;
    }

    private GoToRoomData mapBookingItemTypeToGoToRoomData(BookingItemType bookingItemType, BookingProcessRooms room, PmsAdditionalTypeInformation additionalInfo){
        GoToRoomData roomData = new GoToRoomData();

        roomData.setBookingEngineTypeId(bookingItemType.id);
        roomData.setDescription(bookingItemType.description);
        roomData.setName(bookingItemType.name);
        roomData.setGoToRoomTypeCode(bookingItemType.id);
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

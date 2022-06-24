package com.thundashop.core.availability;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.Summary;
import biweekly.util.Duration;
import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.arx.AccessLog;
import com.thundashop.core.arx.DoorManager;
import com.thundashop.core.availability.dto.AvailabilityRequest;
import com.thundashop.core.availability.dto.AvailabilityResponse;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.*;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.AddonsInclude;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.Coupon;
import com.thundashop.core.checklist.ChecklistManager;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.GetShop;
import com.thundashop.core.getshoplock.GetShopDeviceLog;
import com.thundashop.core.getshoplock.GetShopLockManager;
import com.thundashop.core.getshoplocksystem.*;
import com.thundashop.core.gsd.DevicePrintRoomCode;
import com.thundashop.core.gsd.GdsManager;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.messagemanager.SmsHandlerAbstract;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderShipmentLogEntry;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.pmsbookingprocess.*;
import com.thundashop.core.pmseventmanager.PmsEvent;
import com.thundashop.core.pmseventmanager.PmsEventManager;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.pos.PosManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import com.thundashop.core.stripe.StripeManager;
import com.thundashop.core.ticket.Ticket;
import com.thundashop.core.ticket.TicketManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserCard;
import com.thundashop.core.utils.BrRegEngine;
import com.thundashop.core.utils.Constants;
import com.thundashop.core.utils.NullSafeConcurrentHashMap;
import com.thundashop.core.utils.UtilManager;
import com.thundashop.core.webmanager.WebManager;
import com.thundashop.core.wubook.WubookManager;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * @author Naim Murad (naim)
 * @since 6/21/22
 */
@Data
@Component
@GetShopSession
public class AvailabilityManager extends GetShopSessionBeanNamed implements IAvailabilityManager {

    @Autowired private  StoreManager storeManager;
    @Autowired private PmsManager pmsManager;
    @Autowired private UserManager usStartBookingerManager;
    @Autowired private PmsInvoiceManager pmsInvoiceManager;
    @Autowired private BookingEngine bookingEngine;
    @Autowired private CartManager cartManager;
    @Autowired private ProductManager productManager;
    @Autowired private UserManager userManager;

    @Override
    public AvailabilityResponse checkAvailability(AvailabilityRequest arg) {
        if(isNotBlank(arg.getDiscountCode())) {
            arg.setDiscountCode(arg.getDiscountCode().replaceAll("&amp;", "&"));
        }

        if(arg.getStart() == null || arg.getEnd() == null) {
            return new AvailabilityResponse();
        }
        long timediff = arg.getEnd().getTime() - arg.getStart().getTime();
        timediff = timediff / (86400*1000);
        if(timediff > 1825 || timediff < 0) {
            return new AvailabilityResponse();
        }

        Date now = storeManager.getMyStore().getCurrentTimeInTimeZone();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR_OF_DAY, -4);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date toCheck = cal.getTime();
        if(toCheck.after(arg.getStart())) {
            return new AvailabilityResponse();
        }

        Gson gson = new Gson();
        logPrint(gson.toJson(arg));
        if(arg.getGuests() < arg.rooms) {
            return new AvailabilityResponse();
        }
        PmsBooking booking = pmsManager.getStart()Booking();
        if(arg.language != null && !arg.language.isEmpty()) {
            setSessionLanguage(arg.language);
        }

        if(booking.language == null || booking.language.isEmpty()) {
            booking.language = getSession().language;
        }
        if(arg.discountCode != null && !arg.discountCode.isEmpty()) {
            User discountUser = userManager.getUserByReference(arg.discountCode);
            if(discountUser != null) {
                booking.userId = discountUser.id;
                booking.couponCode = pmsInvoiceManager.getDiscountsForUser(discountUser.id).attachedDiscountCode;
            } else {
                booking.couponCode = arg.discountCode;
            }
        }

        booking.browserUsed = arg.browser;

        try {
            pmsManager.setBooking(booking);
        }catch(Exception e) {
            logPrintException(e);
        }

        arg.getStart() = pmsManager.getConfigurationSecure().getDefaultStart(arg.getStart());
        arg.getEnd() = pmsManager.getConfigurationSecure().getDefaultEnd(arg.getEnd());

        if(arg.getStart().after(arg.getEnd())) {
            arg.getEnd() = correctToDayAfter(arg);
        }

        AvailabilityResponse result = new AvailabilityResponse();
        List<BookingItemType> types = bookingEngine.getBookingItemTypesWithSystemType(null);
        result.numberOfDays = pmsInvoiceManager.getNumberOfDays(arg.getStart(), arg.getEnd());

        Collections.sort(types, new Comparator<BookingItemType>() {
            public int compare(BookingItemType o1, BookingItemType o2) {
                return o1.order.compareTo(o2.order);
            }
        });

        PmsBooking existing = pmsManager.getCurrentBooking();

        boolean isAdministrator = false;

        if(getSession() != null && getSession().currentUser != null && getSession().currentUser.isAdministrator()) {
            isAdministrator = true;
        }

        pmsInvoiceManager.getStart()CacheCoverage();
        for(BookingItemType type : types) {
            if(!type.visibleForBooking && !isAdministrator) {
                continue;
            }
            BookingProcessRooms room = new BookingProcessRooms();
            room.userId = booking.userId;
            room.description = type.getTranslatedDescription(getSession().language);
            room.availableRooms = pmsManager.getNumberOfAvailable(type.id, arg.getStart(), arg.getEnd(), true, true);
            room.id = type.id;
            room.systemCategory = type.systemCategory;
            room.visibleForBooker = type.visibleForBooking;
            result.totalRooms += room.availableRooms;
            try {
                PmsAdditionalTypeInformation typeInfo = pmsManager.getAdditionalTypeInformationById(type.id);
                room.images.addAll(typeInfo.images);
                room.sortDefaultImageFirst();
                room.name = type.getTranslatedName(getSession().language);
                room.maxGuests = type.size;

                for(int i = 1; i <= type.size;i++) {
                    int count = 0;
                    for(PmsBookingRooms existingRoom : existing.rooms) {
                        if(existingRoom.bookingItemTypeId.equals(type.id) && existingRoom.numberOfGuests == i) {
                            count++;
                        }
                    }
                    room.roomsSelectedByGuests.put(i, count);
                    Double price = getPriceForRoom(room, arg.getStart(), arg.getEnd(), i, booking.couponCode);
                    room.pricesByGuests.put(i, price);
                }
            } catch (Exception ex) {
                Logger.getLogger(PmsBookingProcess.class.getName()).log(Level.SEVERE, null, ex);
            }

            result.rooms.add(room);
        }

        PmsConfiguration pmsConfig = pmsManager.getConfigurationSecure();
        if(!pmsConfig.doNotRecommendBestPrice) {
            selectMostSuitableRooms(result, arg);
        }
        result.totalAmount = pmsManager.getCurrentBooking().getTotalPrice();
        result.supportPayLaterButton = checkIfSupportPayLater(arg);
        result.supportedPaymentMethods = checkForSupportedPaymentMethods(booking);
        result.prefilledContactUser = "";
        result.getStart()Yesterday = isMidleOfNight() && PmsBookingRooms.isSameDayStatic(arg.getStart(), new Date());
        result.bookingId = existing.id;

        if(booking.userId != null && !booking.userId.isEmpty()) {
            result.prefilledContactUser = userManager.getUserById(booking.userId).fullName;
        }

        //TODO need for pricing addCouponPricesToRoom(result, arg);
        checkIfCouponIsValid(result, arg);
        checkForRestrictions(result, arg);
        addAddonsIncluded(result,arg);
        result.hasAvailableRooms = result.hasAvailableRooms();
        return result;
    }

    private boolean denyPayLaterButton(BookingProcessRooms r, StartBooking arg, AvailabilityResponse result) {
        try {
            List<TimeRepeaterData> mingueststimes = bookingEngine.getOpeningHoursWithType(r.id, TimeRepeaterData.TimePeriodeType.denyPayLater);

            TimeRepeater repeater = new TimeRepeater();
            for (TimeRepeaterData res : mingueststimes) {
                LinkedList<TimeRepeaterDateRange> ranges = repeater.generateRange(res);
                if(!res.containsCategory(r.id)) {
                    continue;
                }
                for (TimeRepeaterDateRange range : ranges) {
                    if(range.intercepts(arg.getStart(), arg.getEnd())) {
                        return true;
                    }
                }
            }
        }catch(Exception e) {
            logPrintException(e);
        }
        return false;
    }

    private void checkForRestrictions(AvailabilityResponse result, StartBooking arg) {
        boolean remove = false;
        List<String> types = new ArrayList();

        boolean denyPayLater = false;
        for(BookingProcessRooms r : result.rooms) {
            r.minGuests = getMinGuestsCount(r, arg, result);
            if(!denyPayLater) {
                denyPayLater = denyPayLaterButton(r, arg, result);
            }
            if (pmsManager.isRestricted(r.id, arg.getStart(), arg.getEnd(), TimeRepeaterData.TimePeriodeType.min_stay)) {
                remove = true;
                result.errorMessage = "min_days:{arg}:" + pmsManager.getLatestRestrictionTime();
                types.add(r.id);
            }
            if (pmsManager.isRestricted(r.id, arg.getStart(), arg.getEnd(), TimeRepeaterData.TimePeriodeType.max_stay)) {
                remove = true;
                result.errorMessage = "max_days:{arg}:" + pmsManager.getLatestRestrictionTime();
                types.add(r.id);
            }
        }
        if(denyPayLater) {
            result.supportPayLaterButton = false;
        }

        if(types.size() > 0) {
            removeAllRooms(result, types);
        }
    }

    private Integer getMinGuestsCount(BookingProcessRooms r, StartBooking arg, AvailabilityResponse result) {
        try {
            List<TimeRepeaterData> mingueststimes = bookingEngine.getOpeningHoursWithType(r.id, TimeRepeaterData.TimePeriodeType.minGuests);

            TimeRepeater repeater = new TimeRepeater();
            for (TimeRepeaterData res : mingueststimes) {
                LinkedList<TimeRepeaterDateRange> ranges = repeater.generateRange(res);
                if(!res.containsCategory(r.id)) {
                    continue;
                }
                for (TimeRepeaterDateRange range : ranges) {
                    if(range.intercepts(arg.getStart(), arg.getEnd())) {
                        Integer minguests = new Integer(res.timePeriodeTypeAttribute);
                        result.errorMessage = "min_guests:{arg}:" + minguests;
                        return minguests;
                    }
                }
            }
        } catch(Exception e) {
            logPrintException(e);
        }
        return 1;
    }

    private void addAddonsIncluded(AvailabilityResponse result, StartBooking arg) {

        if(!storeId.equals("ba845b2d-2293-4afc-91f1-eef47db7f8ca")) {
            return;
        }

        List<PmsBookingAddonItem> addons = pmsManager.getAddonsAvailable();
        for(PmsBookingAddonItem addon : addons) {
            if(addon.isIncludedInRoomPrice) {
                continue;
            }
            if(addon.includedInBookingItemTypes.size() > 0 && addon.isValidForPeriode(arg.getStart(), arg.getEnd(), new Date())) {
                for(BookingProcessRooms tmp : result.rooms) {
                    if(!addon.includedInBookingItemTypes.contains(tmp.id)) {
                        continue;
                    }

                    for(Integer guests : tmp.pricesByGuests.keySet()) {
                        double price = tmp.pricesByGuests.get(guests);
                        if(addon.isSingle) {
                            price += addon.price;
                        } else {
                            price += addon.price * guests;
                        }
                        tmp.pricesByGuests.put(guests, price);
                    }
                }
            }

        }
    }
    private void removeAllRooms(AvailabilityResponse result, List<String> types) {
        for(BookingProcessRooms r : result.rooms) {
            if(!types.isEmpty() && !types.contains(r.id)) {
                continue;
            }
            r.availableRooms = 0;
            r.roomsSelectedByGuests = new HashMap();
        }
        result.roomsSelected = 0;


        PmsBooking currentbooking = pmsManager.getCurrentBooking();
        currentbooking.rooms.clear();
        try {
            pmsManager.setBooking(currentbooking);
        }catch(Exception e) {
            logPrintException(e);
        }
    }

    private void checkIfCouponIsValid(AvailabilityResponse result, StartBooking arg) {
        if(isBlank(arg.discountCode)) return;
        Coupon coupon = cartManager.getCoupon(arg.discountCode);
        if(coupon == null) return;

        boolean removeAvailability = false;
        List<String> roomsToRemove = new ArrayList();
        for(BookingProcessRooms r : result.rooms) {
            BookingItemType type = bookingEngine.getBookingItemType(r.id);
            if(!cartManager.couponIsValid(new Date(), arg.discountCode, arg.getStart(),arg.getEnd(), type.productId, arg.getNumberOfDays())) {
                roomsToRemove.add(r.id);
                removeAvailability = true;
            }

            if(removeAvailability && roomsToRemove.size() == result.rooms.size()) {
                result.errorMessage = "outsideperiode::";
            }

        }


        if(coupon.minDays > 0 && coupon.minDays > arg.getNumberOfDays()) {
            result.errorMessage = "min_days:{arg}:" + coupon.minDays;
            removeAvailability = true;
        }
        if(coupon.maxDays > 0 && coupon.maxDays < arg.getNumberOfDays()) {
            result.errorMessage = "max_days:{arg}:" + coupon.maxDays;
            removeAvailability = true;
        }
        if(removeAvailability) {
            removeAllRooms(result, roomsToRemove);
        }
    }

    private void addCouponPricesToRoom(AvailabilityResponse result, StartBooking arg) {
        if(isBlank(arg.discountCode)) return;
        Coupon coupon = cartManager.getCoupon(arg.discountCode);
        if(coupon == null || coupon.addonsToInclude == null || coupon.addonsToInclude.isEmpty()) return;
        List<AddonsInclude> addons = coupon.addonsToInclude.stream().filter(e -> e.includeInRoomPrice).collect(Collectors.toList());
        for(AddonsInclude inc : addons) {
            Product product = productManager.getProduct(inc.productId);
            for(BookingProcessRooms tmp : result.rooms) {
                for(Integer guests : tmp.pricesByGuests.keySet()) {
                    double price = tmp.pricesByGuests.get(guests);
                    price += product.price * guests;
                    tmp.pricesByGuests.put(guests, price);
                }
            }
        }
    }

    private boolean isMidleOfNight() {
        Date now = pmsManager.getConfigurationSecure().getCurrentTimeInTimeZone();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);
        if(hourofday < 7) {
            return true;
        }
        return false;
    }

    private List<String> checkForSupportedPaymentMethods(PmsBooking booking) {
        if(getSession() != null && getSession().currentUser != null) {
            return getSession().currentUser.enabledPaymentOptions;
        }

        if(booking.userId != null && !booking.userId.isEmpty()) {
            User bookinguser = userManager.getUserById(booking.userId);
            return bookinguser.enabledPaymentOptions;
        }

        return new ArrayList();
    }

    private boolean checkIfSupportPayLater(StartBooking arg) {
        PmsBooking booking = pmsManager.getCurrentBooking();
        if(booking == null) {
            return false;
        }
        Date startDate = booking.getStartDate();
        if(startDate == null) {
            startDate = arg.getStart();
        }
        if(startDate == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        PmsConfiguration config = pmsManager.getConfigurationSecure();
        Integer ignoreDays = config.ignorePaymentWindowDaysAheadOfStay;

        if (ignoreDays == 0) {
            return true;
        }

        if(ignoreDays <= 0) {
            return false;
        }

        long now = System.currentTimeMillis();
        long diff = cal.getTimeInMillis() - now;

        int days = (int) ((diff / 1000) / 86400);

        if(days >= ignoreDays) {
            return true;
        }

        return false;
    }

    private void selectMostSuitableRooms(AvailabilityResponse result, StartBooking arg) {
        logPrint("Need to find: " + arg.rooms + " rooms for :" + arg.getGuests());

        List<PmsBookingProcessorCalculator> toUse = new ArrayList();

        PmsBooking booking = pmsManager.getCurrentBooking();
        HashMap<String, Integer> maxRooms = new HashMap();

        for(BookingProcessRooms room : result.rooms) {
            if(!room.visibleForBooker) {
                continue;
            }
            for(Integer guest : room.pricesByGuests.keySet()) {
                Double price = room.pricesByGuests.get(guest);
                PmsBookingProcessorCalculator res = new PmsBookingProcessorCalculator();
                res.room = room;
                res.guests = guest;
                res.price = price;
                res.guestPrice = price/guest;
                res.maxRooms = room.availableRooms;
                maxRooms.put(room.id, room.availableRooms);
                toUse.add(res);

            }
        }

        Collections.sort(toUse, new Comparator<PmsBookingProcessorCalculator>(){
            public int compare(PmsBookingProcessorCalculator o1, PmsBookingProcessorCalculator o2){
                return o1.guestPrice.compareTo(o2.guestPrice);
            }
        });

        List<PmsBookingProcessorCalculator> listOfRooms = new ArrayList();
        int guestLeft = arg.getGuests();
        int roomsLeft = arg.rooms;
        int breaker = 0;
        while(true) {
            for(Integer roomIdx = 0; roomIdx < arg.rooms; roomIdx++) {
                for(PmsBookingProcessorCalculator lowest : toUse) {
                    if(maxRooms.get(lowest.room.id) == 0) {
                        continue;
                    }
                    if((guestLeft - lowest.guests) < (roomsLeft-1)) {
                        continue;
                    }
                    if((guestLeft - lowest.guests) < 0) {
                        continue;
                    }
                    int count = maxRooms.get(lowest.room.id);
                    count--;
                    maxRooms.put(lowest.room.id, count);

                    listOfRooms.add(lowest);
                    guestLeft -= lowest.guests;
                    roomsLeft--;
                    break;
                }
                if(guestLeft <= 0) {
                    break;
                }
            }
            if(guestLeft <= 0) {
                break;
            }
            boolean roomsleft = false;
            for(PmsBookingProcessorCalculator lowest : toUse) {
                if(lowest.maxRooms > 0) {
                    roomsleft = true;
                }
            }
            if(!roomsleft) {
                break;
            }

            breaker++;
            if(breaker >= 1000) {
                break;
            }
        }

        boolean failed = false;

        if(guestLeft > 0) {
            logPrint("################ WARNING ################");
            logPrint("Not all guests where assigned a room");
            logPrint("################ WARNING ################");
            failed = true;
        }
        if(guestLeft < 0) {
            logPrint("################ WARNING ################");
            logPrint("Too many guests where assigned a room");
            logPrint("################ WARNING ################");
            failed = true;
        }
        if(roomsLeft > 0) {
            logPrint("################ WARNING ################");
            logPrint("Not all rooms where assigned");
            logPrint("################ WARNING ################");
            failed = true;
        }
        if(roomsLeft < 0) {
            logPrint("################ WARNING ################");
            logPrint("Too many rooms where assigned");
            logPrint("################ WARNING ################");
            failed = true;
        }

        logPrint("---------------: " + guestLeft + "-----------");
        int childToSet = arg.children;
        if(!failed) {
            for(PmsBookingProcessorCalculator check : listOfRooms) {
                logPrint(check.guests + " : "+ check.room.availableRooms + " : " + check.price + " : " + check.room.name);
                Integer current = check.room.roomsSelectedByGuests.get(check.guests);
                if(current == null) {
                    current = 0;
                }
                current++;
                check.room.roomsSelectedByGuests.put(check.guests, current);

                PmsBookingRooms toAddToCurrentBooking = new PmsBookingRooms();
                toAddToCurrentBooking.bookingItemTypeId = check.room.id;
                toAddToCurrentBooking.numberOfGuests = check.guests;
                toAddToCurrentBooking.date = new PmsBookingDateRange();
                toAddToCurrentBooking.date.getStart() = normalizeDate(arg.getStart(), true);
                toAddToCurrentBooking.date.getEnd() = normalizeDate(arg.getEnd(), false);
                if(childToSet > 0) {
                    childToSet -= toAddToCurrentBooking.setGuestAsChildren(childToSet);
                }
                pmsManager.setPriceOnRoom(toAddToCurrentBooking, true, booking);
                result.roomsSelected++;
                booking.addRoom(toAddToCurrentBooking);
                check.room.totalPriceForRoom = toAddToCurrentBooking.price;
            }
        }
        try {
            booking.calculateTotalCost();
            pmsManager.setBooking(booking);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    private Date normalizeDate(Date date, boolean isStart) {
        String[] defaultStart = pmsManager.getConfigurationSecure().getDefaultStart().split(":");
        String[] defaultEnd = pmsManager.getConfigurationSecure().getDefaultEnd().split(":");
        if(isStart) {
            Calendar calStart = Calendar.getInstance();
            calStart.setTime(date);
            calStart.set(Calendar.HOUR_OF_DAY, new Integer(defaultStart[0]));
            calStart.set(Calendar.MINUTE, new Integer(defaultStart[1]));
            date = calStart.getTime();
        } else {
            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(date);
            calEnd.set(Calendar.HOUR_OF_DAY, new Integer(defaultEnd[0]));
            calEnd.set(Calendar.MINUTE, new Integer(defaultEnd[1]));
            date = calEnd.getTime();
        }
        return date;
    }
    private void setSessionLanguage(String language) {
        if(language.equals("en")) {
            language = "en_en";
        }
        if(language.equals("no")) {
            language = "nb_NO";
        }
        storeManager.setSessionLanguage(language);
    }
    private Date correctToDayAfter(StartBooking arg) {
        Date start = arg.getStart();
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(arg.getEnd());
        endCal.set(Calendar.DAY_OF_YEAR, startCal.get(Calendar.DAY_OF_YEAR)+1);
        endCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
        return endCal.getTime();
    }

    private Double getPriceForRoom(BookingProcessRooms bookingProcessRoom, Date start, Date end, int numberofguests, String discountcode) {
        PmsBookingRooms room = new PmsBookingRooms();
        room.bookingItemTypeId = bookingProcessRoom.id;
        room.date = new PmsBookingDateRange();
        room.date.getStart() = start;
        room.date.getEnd() = end;
        room.numberOfGuests = numberofguests;

        PmsBooking booking = new PmsBooking();
        booking.priceType = PmsBooking.PriceType.daily;
        booking.couponCode = discountcode;
        booking.userId = bookingProcessRoom.userId;
        pmsManager.setPriceOnRoom(room, true, booking);
        return room.price;
    }
}

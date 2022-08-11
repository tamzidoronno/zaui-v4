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
import com.thundashop.core.appmanager.data.AvailableApplications;
import com.thundashop.core.arx.AccessLog;
import com.thundashop.core.arx.DoorManager;
import com.thundashop.core.availability.dto.Availability;
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
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
@EqualsAndHashCode(callSuper=false)
public class AvailabilityManager extends GetShopSessionBeanNamed implements IAvailabilityManager {

    @Autowired private  StoreManager storeManager;
    @Autowired private PmsManager pmsManager;
    @Autowired private UserManager usStartBookingerManager;
    @Autowired private PmsInvoiceManager pmsInvoiceManager;
    @Autowired private BookingEngine bookingEngine;
    @Autowired private CartManager cartManager;
    @Autowired private ProductManager productManager;
    @Autowired private UserManager userManager;

    private SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");


    @Override
    public AvailabilityResponse checkAvailability(AvailabilityRequest arg) {
        if (isNotBlank(arg.getDiscountCode())) {
            arg.setDiscountCode(arg.getDiscountCode().replaceAll("&amp;", "&"));
        }

        if (arg.getStart() == null || arg.getEnd() == null) {
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
        if(arg.getGuests() < arg.getRooms()) {
            return new AvailabilityResponse();
        }
        if(isNotEmpty(arg.getLanguage())) {
            setSessionLanguage(arg.getLanguage());
        }
        User discountUser = null;
        String userId = "";
        String couponCode = arg.getDiscountCode();
        if(arg.getDiscountCode() != null && !arg.getDiscountCode().isEmpty()) {
            discountUser = userManager.getUserByReference(arg.getDiscountCode());
            if(discountUser != null) {
                userId = discountUser.id;
                couponCode = pmsInvoiceManager.getDiscountsForUser(discountUser.id).attachedDiscountCode;
            }
        }

        //arg.setStart(arg.getStart());
        arg.setEnd(pmsManager.getConfigurationSecure().getDefaultEnd(arg.getEnd()));

        if(arg.getStart().after(arg.getEnd())) {
            arg.setEnd(correctToDayAfter(arg));
        }

        StartBookingResult result = new StartBookingResult();
        List<BookingItemType> types = bookingEngine.getBookingItemTypesWithSystemType(null);
        result.numberOfDays = pmsInvoiceManager.getNumberOfDays(arg.getStart(), arg.getEnd());

        Collections.sort(types, new Comparator<BookingItemType>() {
            public int compare(BookingItemType o1, BookingItemType o2) {
                return o1.order.compareTo(o2.order);
            }
        });

        boolean isAdministrator = false;
        if(getSession() != null && getSession().currentUser != null && getSession().currentUser.isAdministrator()) {
            isAdministrator = true;
            userId = userId == null ? getSession().currentUser.id : userId;
        }

        pmsInvoiceManager.startCacheCoverage();
        result.startYesterday = isMidleOfNight() && PmsBookingRooms.isSameDayStatic(arg.getStart(), new Date());

        PmsConfiguration config = pmsManager.getConfiguration();
        List<Availability> avs = new ArrayList<>();
        Calendar dt = Calendar.getInstance();
        dt.setTime(arg.getStart());
        for(int i = 0; i <= result.numberOfDays + 1; i++) {
            Availability av = new Availability();
            Calendar dtToShow = Calendar.getInstance();
            dtToShow.setTime(dt.getTime());
            dtToShow.add(Calendar.DAY_OF_YEAR, -1);
            av.setDate(DATE_FORMATTER.format(dtToShow.getTime()));
            av.setCurrencyCode("NOK");
            if(isClosed(config.closedOfPeriode, dt.getTime())) {
                av.setTotalNoOfRooms(0L);
                av.setTotalNoOfRoomsByCategory(new HashMap<>());
                av.setClosed(true);
                dt.add(Calendar.DATE, 1);
                avs.add(av);
                continue;
            }

            int totalAvailableOfDay = 0;
            double lowestPriceOfDay = 0;
            boolean atleastOneCategoryUnAvailable = false;
            Map<String, Integer> rm = new HashMap<>();

            for (BookingItemType type : types) {
                if (!type.visibleForBooking && !isAdministrator) {
                    continue;
                }
                if(BookingItemType.BookingSystemCategory.CONFERENCE.equals(type.systemCategory)) {
                    continue;
                }
                BookingProcessRooms room = new BookingProcessRooms();
                room.userId = userId;
                room.description = type.getTranslatedDescription(getSession().language);

                List<BookingItem> allocatedRooms = bookingEngine.getBookingItemsByType(type.id);
                if(allocatedRooms.isEmpty()) continue;
                Calendar startDate = Calendar.getInstance();
                startDate.setTime(dt.getTime());
                startDate.set(Calendar.SECOND,0);

                Calendar endDate = Calendar.getInstance();
                endDate.setTime(dt.getTime());
                endDate.add(Calendar.DAY_OF_YEAR, 1);
                endDate.set(Calendar.SECOND,0);

                room.availableRooms = pmsManager.getNumberOfAvailable(type.id,
                        config.getDefaultStart(startDate.getTime()),
                        config.getDefaultEnd(endDate.getTime()),
                        true, true);
                room.id = type.id;
                try {
                    /*PmsAdditionalTypeInformation typeInfo = pmsManager.getAdditionalTypeInformationById(type.id);
                    room.images.addAll(typeInfo.images);
                    room.sortDefaultImageFirst();*/
                    room.name = type.getTranslatedName(getSession().language);
                    room.maxGuests = type.size;

                    /*room.roomsSelectedByGuests.put(arg.getGuests(), 0);
                    Double price = getPriceForRoom(room, arg.getStart(), end.getTime(), arg.getGuests(), couponCode);
                    room.pricesByGuests.put(arg.getGuests(), price);*/
                } catch (Exception ex) {
                    Logger.getLogger(PmsBookingProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
                room.systemCategory = type.systemCategory;
                room.visibleForBooker = type.visibleForBooking;
                result.totalRooms += room.availableRooms;
                totalAvailableOfDay += room.availableRooms;
                if(room.availableRooms > 0 && room.totalPriceForRoom < lowestPriceOfDay) lowestPriceOfDay = room.totalPriceForRoom;
                if(room.availableRooms < 1) atleastOneCategoryUnAvailable = true;
                rm.put(room.name, room.availableRooms);

                result.rooms.add(room);
            }
            av.setTotalNoOfRooms((long) totalAvailableOfDay);
            av.setLowestPrice(BigDecimal.valueOf(lowestPriceOfDay));
            av.setTotalNoOfRoomsByCategory(rm);
            av.setAllCategoryAvailable(!atleastOneCategoryUnAvailable);
            dt.add(Calendar.DATE, 1);
            avs.add(av);
        }

        checkIfCouponIsValid(result, arg);
        checkForRestrictions(result, arg);

        return new AvailabilityResponse(arg.getStart(), arg.getEnd(), arg.getRooms(), arg.getAdults(), arg.getChildren(), arg.getDiscountCode(), avs);
    }
    
    private boolean isClosed(List<TimeRepeaterData> closedPeriods, Date date) {
        if(closedPeriods.isEmpty()) return false;
        LocalDate dateToCheck = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        for(TimeRepeaterData period : closedPeriods) {
            Date start = period.firstEvent.start;
            Date end = period.firstEvent.end;
            if(!(date.before(start) || date.after(end))) {
                return true;
            }
        }
        return false;
    }

    private boolean denyPayLaterButton(BookingProcessRooms r, AvailabilityRequest arg, StartBookingResult result) {
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

    private void checkForRestrictions(StartBookingResult result, AvailabilityRequest arg) {
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

    private Integer getMinGuestsCount(BookingProcessRooms r, AvailabilityRequest arg, StartBookingResult result) {
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

    private void addAddonsIncluded(StartBookingResult result, AvailabilityRequest arg) {

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
    private void removeAllRooms(StartBookingResult result, List<String> types) {
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

    private void checkIfCouponIsValid(StartBookingResult result, AvailabilityRequest arg) {
        if(isBlank(arg.getDiscountCode())) return;
        Coupon coupon = cartManager.getCoupon(arg.getDiscountCode());
        if(coupon == null) return;

        boolean removeAvailability = false;
        List<String> roomsToRemove = new ArrayList();
        for(BookingProcessRooms r : result.rooms) {
            BookingItemType type = bookingEngine.getBookingItemType(r.id);
            if(!cartManager.couponIsValid(new Date(), arg.getDiscountCode(), arg.getStart(),arg.getEnd(), type.productId, arg.getNumberOfDays())) {
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

    private void addCouponPricesToRoom(StartBookingResult result, AvailabilityRequest arg) {
        if(isBlank(arg.getDiscountCode())) return;
        Coupon coupon = cartManager.getCoupon(arg.getDiscountCode());
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

    private boolean checkIfSupportPayLater(AvailabilityRequest arg) {
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

    private void selectMostSuitableRooms(StartBookingResult result, AvailabilityRequest arg) {
        //logPrint("Need to find: " + arg.rooms + " rooms for :" + arg.getGuests());

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
        int guestLeft = (int) arg.getGuests();
        int roomsLeft = arg.getRooms();
        int breaker = 0;
        while(true) {
            for(Integer roomIdx = 0; roomIdx < arg.getRooms(); roomIdx++) {
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
        int childToSet = arg.getChildren();
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
                toAddToCurrentBooking.date.start = normalizeDate(arg.getStart(), true);
                toAddToCurrentBooking.date.end = normalizeDate(arg.getEnd(), false);
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
    private Date correctToDayAfter(AvailabilityRequest arg) {
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
}

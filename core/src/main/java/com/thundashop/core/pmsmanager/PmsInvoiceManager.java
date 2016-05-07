package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsInvoiceManager extends GetShopSessionBeanNamed {

    private boolean avoidOrderCreation = false;
    
    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    CartManager cartManager;
    
    @Autowired
    UserManager userManager;
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    ProductManager productManager;
    
    @Autowired
    OrderManager orderManager;
    
    public String createOrder(String bookingId, NewOrderFilter filter) {
        this.avoidOrderCreation = filter.avoidOrderCreation;
        if(avoidOrderCreation) {
            cartManager.clear();
        }
        
        List<PmsBooking> allbookings = new ArrayList();
        if(bookingId == null) {
            List<PmsBooking> tocheck = pmsManager.getAllBookings(null);
            for(PmsBooking book : tocheck) {
                if(book.isEndedOverTwoMonthsAgo() || !shouldBeProcessed(book)) {
                    continue;
                }
                allbookings.add(book);
            }
        } else {
            PmsBooking booking = pmsManager.getBookingUnsecure(bookingId);
            allbookings.add(booking);
        }
        
        for(PmsBooking booking : allbookings) {
            if(addBookingToCart(booking, filter)) {
                if(avoidOrderCreation) {
                    continue;
                }
                
                Order order = createOrderFromCart(booking);
                if (order == null) {
                    return "Could not create order.";
                }
                booking.orderIds.add(order.id);
                booking.payedFor = false;
                if(getSession() != null && getSession().currentUser != null && 
                        (getSession().currentUser.isEditor() || getSession().currentUser.isAdministrator())) {
                    booking.avoidAutoDelete = true;
                }
                pmsManager.saveBooking(booking);
            }
        }
        
        if(avoidOrderCreation) {
            for(CartItem item : cartManager.getCart().getItems()) {
                item.doFinalize();
            }
        }
        
        return "";
    }
    
    
    private boolean addBookingToCart(PmsBooking booking, NewOrderFilter filter) {
        if(!avoidOrderCreation) {
            cartManager.clear();
        }

        boolean foundInvoice = false;
        double totalprice = 0;
        for (PmsBookingRooms room : booking.rooms) {
            if(!room.needInvoicing(filter)) {
                continue;
            }
            
            Date startDate = room.getInvoiceStartDate();
            Date endDate = room.getInvoiceEndDate(filter, booking);

            int daysInPeriode = Days.daysBetween(new LocalDate(startDate), new LocalDate(endDate)).getDays();
            if(booking.priceType.equals(PmsBooking.PriceType.monthly)) {
                daysInPeriode = getNumberOfMonthsBetweenDates(startDate, endDate);
                if(daysInPeriode > 1000) {
                    //Infinate dates, noone wants to pay 100 years in advance.
                    daysInPeriode = 1;
                }
            }
            Double price = getPriceInPeriode(booking, room, startDate, endDate);
            CartItem item = createCartItem(room, startDate, endDate);
            if (item == null) {
                return false;
            }
            
            if (pmsManager.getConfigurationSecure().substractOneDayOnOrder && !filter.onlyEnded) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(item.endDate);
                cal.add(Calendar.DAY_OF_YEAR, -1);
                item.endDate = cal.getTime();
            }
            
            boolean includeTaxes = true;
            if(pmsManager.getPriceObject().privatePeopleDoNotPayTaxes) {
                User user = userManager.getUserById(booking.userId);
                if(user.company.isEmpty()) {
                    includeTaxes = false;
                } else {
                    Company company = userManager.getCompany(user.company.get(0));
                    includeTaxes = company.vatRegisterd;
                }
            }
            
            if (pmsManager.getPriceObject().pricesExTaxes && includeTaxes) {
                double tax = 1 + (calculateTaxes(room.bookingItemTypeId) / 100);
                //Order price needs to be inc taxes.. 
                price *= tax;
            }

            String guestName = "";
            if (room.guests.size() > 0) {
                guestName = room.guests.get(0).name;
            }

            totalprice += price;
            
            item.getProduct().discountedPrice = price;
            item.getProduct().price = price;
            item.getProduct().metaData = guestName;
            item.getProduct().externalReferenceId = room.pmsBookingRoomId;
            item.setCount(daysInPeriode);
            if(!avoidOrderCreation) {
                room.invoicedTo = endDate;
            }
            foundInvoice = true;
            cartManager.saveCartItem(item);
        }
        
        if(totalprice == 0.0) {
            return false;
        }
        
        return foundInvoice;
    }
    
    public double calculateTaxes(String bookingItemTypeId) {
        BookingItemType item = bookingEngine.getBookingItemType(bookingItemTypeId);
        if (item.productId != null && !item.productId.isEmpty()) {
            Product product = productManager.getProduct(item.productId);
            if (product != null && product.taxGroupObject != null) {
                return product.taxGroupObject.taxRate;
            }
            return -1.0;
        }
        return -2.0;
    }

    private CartItem createCartItem(PmsBookingRooms room, Date startDate, Date endDate) {
        BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
        BookingItem bookingitem = null;
        if (room.bookingItemId != null) {
            bookingitem = bookingEngine.getBookingItem(room.bookingItemId);
        }
        if (type == null) {
            return null;
        }

        String productId = type.productId;
        if (productId == null) {
            System.out.println("Product not set for this booking item type");
            return null;
        }
        int numberOfDays = getNumberOfDays(room, startDate, endDate);
        if (numberOfDays == 0) {
            return null;
        }

        CartItem item = cartManager.addProductItem(productId, 1);
        item.startDate = startDate;
        item.endDate = endDate;
        
        item.getProduct().name = type.name;
        if (bookingitem != null) {
            item.getProduct().additionalMetaData = bookingitem.bookingItemName;
        }
        return item;
    }

    
    private int getNumberOfDays(PmsBookingRooms room, Date startDate, Date endDate) {
        if(startDate.after(endDate)) {
            Date tmpStart = startDate;
            startDate = endDate;
            endDate = tmpStart;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(room.date.start);
        cal.set(Calendar.HOUR_OF_DAY, 11);
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(room.date.end);
        endCal.set(Calendar.HOUR_OF_DAY, 12);
        
        int days = 0;
        while (true) {
            if (cal.getTime().after(startDate) && (cal.getTime().before(endDate) || cal.getTime().equals(endDate))) {
                days++;
            }

            cal.add(Calendar.DAY_OF_YEAR, 1);
            if (cal.getTime().after(endCal.getTime())) {
                break;
            }
        }
        return days;
    }

    private boolean shouldBeProcessed(PmsBooking booking) {
        if (booking.rooms == null) {
            return false;
        }
        if (booking.isDeleted) {
            return false;
        }
        if (booking.sessionId != null && !booking.sessionId.isEmpty()) {
            return false;
        }
        if (!booking.confirmed) {
            return false;
        }
        if (!booking.payedFor) {
            return false;
        }
        return true;
    }

    private Order createOrderFromCart(PmsBooking booking) {
       
        User user = userManager.getUserById(booking.userId);
        if (user == null) {
            return null;
        }

        user.address.fullName = user.fullName;

        Order order = orderManager.createOrder(user.address);

        order.payment = new Payment();
        order.payment.paymentType = user.preferredPaymentType;
        order.userId = booking.userId;
        order.invoiceNote = booking.invoiceNote;

        if (pmsManager.getConfigurationSecure().substractOneDayOnOrder) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(order.rowCreatedDate);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            order.rowCreatedDate = cal.getTime();
        }

        if (order.cart.address == null || order.cart.address.address == null || order.cart.address.address.isEmpty()) {
            if (!user.company.isEmpty()) {
                Company company = userManager.getCompany(user.company.get(0));
                order.cart.address = company.address;
                order.cart.address.fullName = company.name;
            }
        }

        orderManager.saveOrder(order);
        return order;
    }


    private Double getPriceInPeriode(PmsBooking booking, PmsBookingRooms room, Date startDate, Date endDate) {
        Double price = null;
        if (booking.priceType.equals(PmsBooking.PriceType.monthly)) {
            price = room.price;
        } else if (booking.priceType.equals(PmsBooking.PriceType.progressive)) {
            int days = Days.daysBetween(new LocalDate(room.date.start), new LocalDate(startDate)).getDays();
            price = calculateProgressivePrice(room.bookingItemTypeId, startDate, endDate, days, true, booking.priceType);
        } else if (booking.priceType.equals(PmsBooking.PriceType.daily)
                || booking.priceType.equals(PmsBooking.PriceType.interval)
                || booking.priceType.equals(PmsBooking.PriceType.progressive)) {
            price = room.price;
        } else if (booking.priceType.equals(PmsBooking.PriceType.weekly)) {
            price = (room.price / 7);
        }
        
        price = cartManager.calculatePriceForCoupon(booking.couponCode, price);
        return price;
    }

    
    private Double calculateProgressivePrice(String typeId, Date start, Date end, int offset, boolean avgPrice, Integer priceType) {
        ArrayList<ProgressivePriceAttribute> priceRange = pmsManager.getPriceObject().progressivePrices.get(typeId);
        if (priceRange == null) {
            System.out.println("No progressive price found for type");
            return -0.123;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int days = offset;
        Double total = 0.0;
        while (true) {
            int daysoffset = 0;
            for (ProgressivePriceAttribute attr : priceRange) {
                daysoffset += attr.numberOfTimeSlots;
                if (daysoffset > days) {
                    total += attr.price;
                    daysoffset = 0;
                    break;
                }
            }
            days++;
            if(priceType.equals(PmsBooking.PriceType.daily)) { cal.add(Calendar.DAY_OF_YEAR, 1); }
            else if(priceType.equals(PmsBooking.PriceType.weekly)) { cal.add(Calendar.DAY_OF_YEAR, 7); }
            else if(priceType.equals(PmsBooking.PriceType.monthly)) { cal.add(Calendar.MONTH, 1); }
            else if(priceType.equals(PmsBooking.PriceType.hourly)) { cal.add(Calendar.HOUR, 1); }
            else { cal.add(Calendar.DAY_OF_YEAR, 1); }
            if (end == null || cal.getTime().after(end) || cal.getTime().equals(end)) {
                break;
            }
            if(priceType.equals(PmsBooking.PriceType.daily) || priceType.equals(PmsBooking.PriceType.progressive)) { 
                if(isSameDay(cal.getTime(), end)) {
                    break;
                }
            }

        }

        if (avgPrice) {
            total = total / days;
        }
        return total;
    }

    public boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    private int getNumberOfMonthsBetweenDates(Date startDate, Date endDate) {
        int months = 1;
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        while(true) {
            cal.add(Calendar.MONTH, 1);
            if(cal.getTime().after(endDate)) {
                break;
            }
            if(cal.getTime().equals(endDate)) {
                break;
            }
            months++;
        }
        return months;
    }
    
    public Double calculatePrice(String typeId, Date start, Date end, boolean avgPrice, String couponCode, Integer priceType) {
        PmsPricing prices = pmsManager.getPriceObject();
        double price = 0.0;
        if (prices.defaultPriceType == 1) {
            price = calculateDailyPricing(typeId, start, end, avgPrice);
        }
        if (prices.defaultPriceType == 2) {
            price = calculateMonthlyPricing(typeId);
        }
        if (prices.defaultPriceType == 7) {
            price = calculateProgressivePrice(typeId, start, end, 0, avgPrice, priceType);
        }
        if (prices.defaultPriceType == 8) {
            price = calculateIntervalPrice(typeId, start, end, avgPrice);
        }
        
        if(couponCode != null && !couponCode.isEmpty()) {
            price = cartManager.calculatePriceForCoupon(couponCode, price);
        }
        
        return price;
    }

    private Double calculateDailyPricing(String typeId, Date start, Date end, boolean avgPrice) {
        HashMap<String, Double> priceRange = pmsManager.getPriceObject().dailyPrices.get(typeId);

        if (priceRange == null) {
            return 0.0;
        }

        Double defaultPrice = priceRange.get("default");
        if (defaultPrice == null) {
            defaultPrice = 0.0;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int days = 0;
        Double total = 0.0;
        while (true) {
            days++;
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String dateToUse = formatter.format(cal.getTime());
            if (priceRange.get(dateToUse) != null) {
                total += priceRange.get(dateToUse);
            } else {
                total += defaultPrice;
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if (end == null || cal.getTime().after(end) || cal.getTime().equals(end)) {
                break;
            }
        }
        if (avgPrice) {
            total = total / days;
        }
        return total;
    }

    
    private Double calculateMonthlyPricing(String typeId) {
        HashMap<String, Double> priceRange = pmsManager.getPriceObject().dailyPrices.get(typeId);
        if (priceRange == null) {
            return 0.0;
        }
        Double defaultPrice = priceRange.get("default");
        if (defaultPrice == null) {
            defaultPrice = 0.0;
        }

        return defaultPrice;
    }

    private Double calculateIntervalPrice(String typeId, Date start, Date end, boolean avgprice) {
        int totalDays = Days.daysBetween(new LocalDate(start), new LocalDate(end)).getDays();
        ArrayList<ProgressivePriceAttribute> priceRange = pmsManager.getPriceObject().progressivePrices.get(typeId);
        if (priceRange == null) {
            System.out.println("No progressive price found for type");
            return -0.124;
        }
        int daysoffset = 0;
        for (ProgressivePriceAttribute attr : priceRange) {
            daysoffset += attr.numberOfTimeSlots;
            if (daysoffset >= totalDays) {
                if (avgprice) {
                    return attr.price;
                } else {
                    return attr.price;
                }
            }
        }

        //Could not find price to use.
        return -0.333;
    }

    String createPrePaymentOrder(String bookingId) {
        PmsBooking booking = pmsManager.getBookingUnsecure(bookingId);
        if(booking.avoidCreateInvoice && 
                getSession() != null && 
                getSession().currentUser != null && 
                (getSession().currentUser.isAdministrator() || getSession().currentUser.isEditor())) {
            return "";
        }
        NewOrderFilter filter = new NewOrderFilter();
        filter.prepayment = true;
        filter.endInvoiceAt = booking.getEndDate();
        if(addBookingToCart(booking, filter)) {
            Order order = createOrderFromCart(booking);
            booking.orderIds.add(order.id);
            pmsManager.saveBooking(booking);
            return order.id;
        }
        return "";

    }
    
}

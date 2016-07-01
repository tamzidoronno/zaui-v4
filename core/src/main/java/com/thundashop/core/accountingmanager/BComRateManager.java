package com.thundashop.core.accountingmanager;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *  Field               label	Type Description
    reservation_id	string	Unique identifier for the reservation (to follow modifications and cancellations)
    date                date	In the case of a reservation with length of stay > 1
    check_in            date	Check in date of the reservation
    check_out           date	Check out date of the reservation
    number_rooms	integer	Number of rooms associated to this reservation (for the given room type)
    adults              integer	Number of adults
    children            integer	Number of children
    room_type           string	Code of the room type
    price               date	Price per room for the specific night indicated by field 'date'
    currency            string	Currency of the price
    confirmed           boolean	Is the reservation confirmed? or is it still an option?
    confirmed_date	date	The date when the reservation turned from option to confirmed
    canceled            boolean	Is the reservation canceled?
    cancel_date         date	The date when the reservation turned from option to cancelled
    is_group    	boolean	Is the reservation part of a group?
    channel             string	Channel of the reservation (OTA, Direct...)
    channel_detail	string	If necessary more detail about the channel (ex. Booking.com, phone)
    booking_date	date	The date when the reservation was created
    allotment           boolean	Is the reservation an allotment to an OTA?
    segment             string	Segment of the reservation (Leisure, Corporate...)
    segment_detail	string	If necessary more detail about the segment
    out_of_order	boolean	Does this line record an Out of order room?
    nationality         string	Nationality of the customer
    rate_policy         string	Code of the rate plan (refundable / non refundable...)
    no_show             boolean	Is this reservation a no­s​how?
 */

public class BComRateManager {

    private final List<PmsBooking> bookings;
    private List<BookingItemType> types;

    private String createLine(Calendar time, PmsBookingRooms room, PmsBooking booking) {
        
        BookingItemType type = getBookingType(room.bookingItemTypeId);
        String typeName = "";
        if(type != null) {
            typeName = type.name;
        }
        
        LinkedHashMap<String, String> result = new LinkedHashMap();
        result.put("reservation_id", room.pmsBookingRoomId);
        result.put("date", convertDate(time.getTime()));
        result.put("check_in", convertDate(room.date.start));
        result.put("check_out", convertDate(room.date.end));
        result.put("number_rooms", "1");
        result.put("adults", room.numberOfGuests + "");
        result.put("children", "0");
        result.put("room_type", typeName);
        result.put("price", room.getPriceForDay(booking.priceType, time) + "");
        result.put("currency", room.currency);
        result.put("confirmed", transformBoolean(booking.confirmed));
        result.put("confirmed_date", convertDate(booking.confirmedDate));
        result.put("canceled", transformBoolean(!room.deleted));
        result.put("canceled_date",convertDate(room.deletedDate));
        result.put("isgroup", transformBoolean(booking.rooms.size() > 1));
        result.put("channel", booking.channel);
        result.put("channel_detail", "");
        result.put("booking_date", convertDate(booking.rowCreatedDate));
        result.put("allotment", "0");
        result.put("segment", "");
        result.put("segment_detail", "");
        result.put("out_of_order", transformBoolean(booking.testReservation));
        result.put("nationality", "");
        result.put("rate_policy", "");
        result.put("no_show", transformBoolean(booking.wubookNoShow));
        
        return convertToLine(result);
    }
    
    BComRateManager(List<PmsBooking> bookings, List<BookingItemType> types) {
        this.bookings = bookings;
        this.types = types;
    }

    List<String> generateLines() {
        if(bookings.isEmpty()) {
            return new ArrayList();
        }
        List<String> result = new ArrayList();
        result.add(generateHeader());
        for(PmsBooking booking : bookings) {
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                result.addAll(createLinesFromRoom(room, booking));
            }
        }
        
        return result;
    }

    private List<String> createLinesFromRoom(PmsBookingRooms room, PmsBooking booking) {
        List<String> result = new ArrayList();
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(room.date.start);
        startCal.set(Calendar.HOUR_OF_DAY, 15);
        
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(room.date.end);
        endCal.set(Calendar.HOUR_OF_DAY, 12);
        
        Date end = endCal.getTime();
        
        while(true) {
           String line = createLine(startCal, room, booking);
           result.add(line);
           startCal.add(Calendar.DAY_OF_YEAR, 1);
           if(startCal.getTime().after(end)) {
               break;
           }
        }
        
        return result;
    }

    private String convertDate(Date time) {
        if(time == null) {
            return "";
        }
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        return format1.format(time);
    }

    private BookingItemType getBookingType(String bookingItemTypeId) {
        for(BookingItemType type : types) {
            if(type.id.equals(bookingItemTypeId)) {
                return type;
            }
        }
        return null;
    }

    private String convertToLine(HashMap<String, String> result) {
        String line = "";
        
        for(String field : result.keySet()) {
            String value = result.get(field);
            value = value.replaceAll("\"", "");
            line += "\"" + value + "\",";
        }
        return line.substring(0, line.length()-1) + "\r\n";
    }

    private String transformBoolean(boolean confirmed) {
        if(confirmed) {
            return "1";
        }
        return "0";
    }

    private String generateHeader() {
        return "\"reservation_id\","+
                "\"date\","+
                "\"check_in\","+
                "\"check_out\","+
                "\"number_rooms\","+
                "\"adults\","+
                "\"children\","+
                "\"room_type\","+
                "\"price\","+
                "\"currency\","+
                "\"confirmed\","+
                "\"confirmed_date\","+
                "\"canceled\","+
                "\"canceled_date\","+
                "\"isgroup\","+
                "\"channel\","+
                "\"channel_detail\","+
                "\"booking_date\","+
                "\"allotment\","+
                "\"segment\","+
                "\"segment_detail\","+
                "\"out_of_order\","+
                "\"nationality\","+
                "\"rate_policy\","+
                "\"no_show\"\r\n";
    }
    
}

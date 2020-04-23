package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.RegistrationRulesField;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class PmsBookingMessageFormatter { 
    
    public PmsBookingMessageFormatter(PmsInvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }
    

    private ProductManager productManager;
    private PmsInvoiceManager invoiceManager;
    private PmsConfiguration config;
    public String formatRoomData(String message, PmsBookingRooms room, BookingEngine bookingEngine, PmsGuests guest) {
        if(message == null) {
            return "";
        }

        Date start = null;
        Date end = null;
        
        if(room.date != null && room.date.start != null) {
            start = productManager.getStore().convertToTimeZone(room.date.start);
        }
        if(room.date != null && room.date.end != null) {
            end = productManager.getStore().convertToTimeZone(room.date.end);
        }
        
         String startMinute = new SimpleDateFormat("m").format(start).toString();
        if (startMinute.length() < 2) {
            startMinute = "0" + startMinute;
        }
        String endMinute = new SimpleDateFormat("m").format(end).toString();
        if (endMinute.length() < 2) {
            endMinute = "0" + endMinute;
        }
        
        if(room.code != null) {
            message = message.replace("{code}", room.code);
        }
        if(room.date != null && start != null) {
            message = message.replace("{checkin_date}", formatDate(start));
        }
        if(room.date != null && end != null) {
            message = message.replace("{checkout_date}", formatDate(end));
        }
        if(room.date != null && start != null) {
            message = message.replace("{checkin_time}", new SimpleDateFormat("H:").format(start) + startMinute);
        }
        if(room.date != null && end != null) {
            message = message.replace("{checkout_time}", new SimpleDateFormat("H:").format(end) + endMinute);
        }
        if(room.date != null && end != null) {
            String link = invoiceManager.getPaymentLinkConfig().webAdress + "/something";
            message = message.replace("{passportdetailslink}", link);
        }
        if(room.guests != null && !room.guests.isEmpty() && guest == null) {
            guest = room.guests.get(0);
        }
        
        if(guest != null) {
            if(guest.name != null && !guest.name.isEmpty()) {
                message = message.replace("{name}", guest.name);
            }
        }
        
        if(room.bookingItemTypeId != null && !room.bookingItemTypeId.isEmpty()) {
             BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
             if(type != null) {
                message = message.replace("{roomType}", type.name);
             }
        }
        
        if(room.pmsBookingRoomId.equals("dummyroom")) {
            message = message.replace("{roomName}", "101");
        }
        
        if(room.bookingItemId != null && !room.bookingItemId.trim().isEmpty()) {
            message = message.replace("{roomName}", bookingEngine.getBookingItem(room.bookingItemId).bookingItemName);
        } else {
            message = message.replace("{roomName}", "");
        }
        

        
        if(room.pmsBookingRoomId != null && room.pmsBookingRoomId.equals("dummyroom")) {
           message = message.replace("{roomType}", "Double room");
           message = message.replace("{roomName}", "100");
        }
        
        return message;
    }
    
    public String formatContactData(String message, User user, PmsGuests guest, PmsBooking booking) {
        
        LinkedHashMap<String, String> resultAdded = booking.registrationData.resultAdded;
        if(!resultAdded.containsKey("name") || resultAdded.get("name") == null) { resultAdded.put("name", ""); }
        if(!resultAdded.containsKey("email") || resultAdded.get("email") == null) { resultAdded.put("email", ""); }
        if(!resultAdded.containsKey("prefix") || resultAdded.get("prefix") == null) { resultAdded.put("prefix", ""); }
        if(!resultAdded.containsKey("phone") || resultAdded.get("phone") == null) { resultAdded.put("phone", ""); }
        if(!resultAdded.containsKey("userid") || resultAdded.get("userid") == null) { resultAdded.put("userid", ""); }
        if(!resultAdded.containsKey("name") || resultAdded.get("name") == null) { resultAdded.put("name", ""); }
        if(!resultAdded.containsKey("email") || resultAdded.get("email") == null) { resultAdded.put("email", ""); }
        if(!resultAdded.containsKey("prefix") || resultAdded.get("prefix") == null) { resultAdded.put("prefix", ""); }
        if(!resultAdded.containsKey("phone") || resultAdded.get("phone") == null) { resultAdded.put("phone", ""); }
        if(!resultAdded.containsKey("user_fullName") || resultAdded.get("user_fullName") == null) { resultAdded.put("user_fullName", ""); }
        if(!resultAdded.containsKey("user_cellPhone") || resultAdded.get("user_cellPhone") == null) { resultAdded.put("user_cellPhone", ""); }
        if(!resultAdded.containsKey("user_prefix") || resultAdded.get("user_prefix") == null) { resultAdded.put("user_prefix", ""); }
        if(!resultAdded.containsKey("user_emailAddress") || resultAdded.get("user_emailAddress") == null) { resultAdded.put("user_emailAddress", ""); }
        if(!resultAdded.containsKey("address") || resultAdded.get("address") == null) { resultAdded.put("address", ""); }
        if(!resultAdded.containsKey("postCode") || resultAdded.get("postCode") == null) { resultAdded.put("postCode", ""); }
        if(!resultAdded.containsKey("city") || resultAdded.get("city") == null) { resultAdded.put("city", ""); }
        if(!resultAdded.containsKey("contact_name") || resultAdded.get("contact_name") == null) { resultAdded.put("contact_name", ""); }
        if(!resultAdded.containsKey("contact_prefix") || resultAdded.get("contact_prefix") == null) { resultAdded.put("contact_prefix", ""); }
        if(!resultAdded.containsKey("contact_phone") || resultAdded.get("contact_phone") == null) { resultAdded.put("contact_phone", ""); }
        if(!resultAdded.containsKey("contact_email") || resultAdded.get("contact_email") == null) { resultAdded.put("contact_email", ""); }
        
        if(guest != null) {
            if(guest.name != null) { message = message.replace("{name}", guest.name); }
            if(guest.email != null) { message = message.replace("{email}", guest.email); } 
            if(guest.prefix != null) { message = message.replace("{prefix}", guest.prefix); }
            if(guest.phone != null) { message = message.replace("{phone}", guest.phone); }
        } else if(user != null) {
            if(user.id != null) { message = message.replace("{userid}", user.id); }
            if(user.fullName != null) { message = message.replace("{name}", user.fullName); }
            if(user.emailAddress != null) { message = message.replace("{email}", user.emailAddress); }
            if(user.prefix != null) { message = message.replace("{prefix}", user.prefix); }
            if(user.cellPhone != null) { message = message.replace("{phone}", user.cellPhone); }
        }
        if(booking.registrationData.resultAdded.containsKey("user_fullName")) {
            message = message.replace("{name}", booking.registrationData.resultAdded.get("user_fullName"));
        }
        if(booking.registrationData.resultAdded.containsKey("user_cellPhone")) {
            message = message.replace("{phone}", booking.registrationData.resultAdded.get("user_cellPhone"));
        }
        if(booking.registrationData.resultAdded.containsKey("user_prefix")) {
            message = message.replace("{prefix}", booking.registrationData.resultAdded.get("user_prefix"));
        }
        if(booking.registrationData.resultAdded.containsKey("user_emailAddress")) {
            message = message.replace("{email}", booking.registrationData.resultAdded.get("user_emailAddress"));
        }
        
        if(user != null) {
            if(user.address != null) {
                if(user.address.address != null) { message = message.replace("{address}", user.address.address); }
                if(user.address.postCode != null) { message = message.replace("{postCode}", user.address.postCode); }
                if(user.address.city != null) { message = message.replace("{city}", user.address.city); }
            }
            
            if(user.fullName != null) { message = message.replace("{contact_name}", user.fullName); }
            if(user.prefix != null) { message = message.replace("{contact_prefix}", user.prefix); }
            if(user.cellPhone != null) { message = message.replace("{contact_phone}", user.cellPhone); }
            if(user.emailAddress != null) { message = message.replace("{contact_email}", user.emailAddress); }
        }
        
        if(booking.id.equals("dummybooking")) {
            message = message.replace("{contact_name}", "John Downson");
            message = message.replace("{userid}", "d493387f-fb89-4547-a841-fe2d22848f90");
            message = message.replace("{contact_prefix}", "1");
            message = message.replace("{contact_phone}", "32552112323");
            message = message.replace("{contact_email}", "john@dowson.com");

            message = message.replace("{name}", "John Downson");
            message = message.replace("{email}", "john@dowson.com");
            message = message.replace("{prefix}", "1");
            message = message.replace("{phone}", "32552112323");
            
            message = message.replace("{address}", "Karl johans gate 1");
            message = message.replace("{postCode}", "0001");
            message = message.replace("{city}", "Oslo");
            message = message.replace("{orderid}", "100101");
            message = message.replace("{paymentlink}", "https://www.link.payment");
            message = message.replace("{passportdetailslink}", "https://www.link.passport");
            
        }
        
        return message;
    }

    String formatBookingData(String message, PmsBooking booking, BookingEngine bookingEngine) {
        booking.calculateTotalCost();
        String simpleRoomList = "";
        String bookingData = "";
        String rooms = "";
        
        List<PmsBookingRooms> roomsToIterate = booking.getActiveRooms();
        if(config != null && config.deleteAllWhenAdded) {
            roomsToIterate = booking.getAllRoomsIncInactive();
        }
        
        for(PmsBookingRooms room : roomsToIterate) {
            String simpleRoom = "";
            if(room.bookingItemTypeId != null && !room.bookingItemTypeId.isEmpty() && !room.bookingItemTypeId.equals("waiting_list")) {
                simpleRoom += "<td style='font-size:12px;'>" + bookingEngine.getBookingItemType(room.bookingItemTypeId).name + "</td>";
                rooms += bookingEngine.getBookingItemType(room.bookingItemTypeId).name;
            }
            long diff = 365*60*60*100;
            Date start = null;
            Date end = null;
            
            if(room.date != null && room.date.start != null) {
                start = productManager.getStore().convertToTimeZone(room.date.start);
            }
            if(room.date != null && room.date.end != null) {
                end = productManager.getStore().convertToTimeZone(room.date.end);
            }
            if(end != null && start != null) {
                diff = (end.getTime() - start.getTime()) / 1000;
            }
            if(diff > (365*60*60*24)) {
                simpleRoom += "<td style='font-size:12px;'>" + formatDate(start) + "</td><td></td>";
                rooms += formatDate(start);
            } else {
                simpleRoom += "<td style='font-size:12px;'>" + formatDate(start) + "</td><td>" + formatDate(end) + "</td>";
                rooms += formatDate(start) + " - " + formatDate(end);
            }
            if(room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
                if(item != null) {
                    simpleRoom += "<td style='font-size:12px;'>" + item.bookingItemName + "</td>";
                    rooms += item.bookingItemName;
                }
            } else {
                simpleRoom += "<td style='font-size:12px;'></td>";
            }
            if(room.totalCost > 0 && room.totalCost < 100000) {
                simpleRoom += "<td style='font-size:12px;'>" + room.totalCost + "</td>";
            }
            rooms += "\n";
            simpleRoomList += "<tr bgcolor='#ffffff'>" + simpleRoom +  "</tr>";
            
            bookingData += "<tr style='font-size: 12px;' bgcolor='#ffffff'>" + simpleRoom + "</tr>";
            String guests = "";
            for(PmsGuests guest : room.guests) {
                if(guest.name != null && !guest.name.isEmpty()) {
                    guests += "<tr style='font-size: 10px;' bgcolor='#ffffff'>";
                    guests += "<td style='font-size: 10px;'>&nbsp;&nbsp;&nbsp;&nbsp;" + guest.name + "</td>";
                    if(guest.phone != null && !guest.phone.isEmpty()) {
                        guests += "<td style='font-size: 10px;'>+" + guest.prefix + " " + guest.phone + "</td>";
                    }
                    if(guest.email != null && !guest.email.isEmpty()) {
                        guests += "<td style='font-size: 10px;'>" + guest.email + "</td>";
                    }
                    guests += "<td></td>";
                    guests += "<td></td>";
                    guests += "</tr>";
                }
            }
            
            bookingData += guests;
            
            try {
                HashMap<String, Integer> addonsCount = new HashMap(); 
                HashMap<String, Double> addonsPrice = new HashMap(); 
                String addonText = "";
                for(PmsBookingAddonItem addon : room.addons) {
                    addonText += "<tr bgcolor='#ffffff'>";
                    int count = 0;
                    if(addonsCount.get(addon.productId) != null) {
                        count = addonsCount.get(addon.productId);
                    }
                    count += addon.count;
                    
                    double price = 0;
                    if(addonsPrice.get(addon.productId) != null) {
                        price = addonsPrice.get(addon.productId);
                    }
                    price += (addon.price * addon.count);
                    addonsCount.put(addon.productId, count);
                    addonsPrice.put(addon.productId, price);
                }
                for(String prodId : addonsCount.keySet()) {
                    Product product = productManager.getProduct(prodId);
                    if(product != null) {
                        addonText += "<tr bgcolor='#ffffff'>";
                        if(addonsCount.get(prodId) != null) {
                            addonText += "<td style='font-size: 10px;'>&nbsp;&nbsp;&nbsp;&nbsp;" + addonsCount.get(prodId) + " x " + product.name + "</td>";
                        }
                        addonText += "<td style='font-size: 10px;'></td>";
                        addonText += "<td style='font-size: 10px;'></td>";
                        addonText += "<td style='font-size: 10px;'></td>";
                        addonText += "<td style='font-size: 10px;'>" + addonsPrice.get(prodId) + "</td>";
                        addonText += "</tr>";
                    }
                }
                bookingData += addonText;
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        bookingData += "<tr bgcolor='#ffffff'>";
        bookingData += "<td></td>";
        bookingData += "<td></td>";
        bookingData += "<td></td>";
        bookingData += "<td></td>";
        if(booking.getTotalPrice() > 0 && booking.getTotalPrice() < 100000) {
            bookingData += "<td>"+booking.getTotalPrice()+"</td>";
        }
        bookingData += "</tr>";
        
        String bookinginfo = "<table>";
        for(String key : booking.registrationData.resultAdded.keySet()) {
            if("agreetoterms".equals(key)) {
                continue;
            }
            String title = getTitle(key, booking);
            
            String text = booking.registrationData.resultAdded.get(key);
            if(key.equals("choosetyperadio")) {
                title = "Reg type";
                if(text.equals("registration_company")) {
                    text = "Organisasjon";
                } else {
                    text ="Privat";
                }
            }

            if(text == null || text.trim().isEmpty() || title.trim().isEmpty()) {
                continue;
            }
            bookinginfo += "<tr><td valign='top'>" + title + "</td><td>" + text + "</td></tr>";
        }
        bookinginfo += "</table>";
        
        
        String total = "";
        String nightPrice = "";
        try {
            booking.calculateTotalCost();
            long totalCost = Math.round(booking.getTotalPrice());
            total = totalCost + "";
            
            int nights = 0;
            for(PmsBookingRooms r : booking.getActiveRooms()) {
                nights += r.getNumberOfNights();
            }
            if(nights > 0) {
                nightPrice = (totalCost / nights) + "";
            }
            
        }catch(Exception e) {}
        
        
        String translation = "en";
        if(message.contains("{translation=")) {
            int offset = message.indexOf("{translation=")+13;
            translation = message.substring(offset, message.indexOf("}", offset));
            message = message.replace("{translation=" + translation + "}", "");
        }
        
        String room = "Room";
        String start = "Start";
        String end = "End";
        String amount = "Amount";
        
        if(translation != null && translation.equals("de")) {
            room = "Kategorie";
            start = "Anreise";
            end = "Abreise";
            amount = "EUR";
        }
        
        String header = "<tr bgcolor='#efefef'>";
        header += "<th align='left' style='font-size:12px;'>"+room+"</th>";
        header += "<th align='left' style='font-size:12px;'>"+start+"</th>";
        header += "<th align='left' style='font-size:12px;'>"+end+"</th><th align='left'></th>";
        if(booking.getTotalPrice() > 0 && booking.getTotalPrice() < 100000) {
            header += "<th align='left' style='font-size:12px;'>"+amount+"</th>";
        }
        header += "</tr>";
        
        
        message = message.replace("{rooms}", rooms);
        message = message.replace("{roomlist}", "<table cellspacing='1' cellpadding='3' bgcolor='#efefef'>" + header + bookingData + "</table>");
        message = message.replace("{simpleRoomList}", "<table cellspacing='1' cellpadding='3' bgcolor='#efefef'>" + simpleRoomList + "</table>");
        message = message.replace("{bookingid}", booking.id);
        message = message.replace("{bookinginformation}", bookinginfo);
        if(booking.incrementBookingId != null) {
            message = message.replace("{referenceNumber}", booking.incrementBookingId + "");
        }
        message = message.replace("{totalcost}", total + "");
        message = message.replace("{nightprice}", nightPrice + "");
        message = message.replace("{roomlist2}", getRoomList2(booking, bookingEngine));
        message = message.replace("{roomlist3}", getRoomList3(booking, bookingEngine));
        message = formatSpecifics(message, booking);
        
        
        if(booking.id.equals("dummybooking")) {
            message = message.replace("{referenceNumber}", "12321");
        }
        
        return message;
    }
    
    
    private String formatDate(Date startDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return sdf.format(startDate);
    }

    private String getTitle(String key, PmsBooking booking) {
        if(key.equals("openforpublic")) {
            return "Åpent for publikum";
        }
        for(RegistrationRulesField data : booking.registrationData.data.values()) {
            if(data.name.equals(key)) {
                String title = data.title;
                return title;
            }
        }
        return "";
    }

    void setProductManager(ProductManager productManager) {
        this.productManager = productManager;
    }

    void setConfig(PmsConfiguration configurationSecure) {
        this.config = configurationSecure;
    }

    String formatHtml(String message) {
        
        Pattern httpLinkPattern = Pattern.compile("(http[s]?)://(www\\.)?([\\S&&[^.@]]+)(\\.[\\S&&[^@]]+)");

        Pattern wwwLinkPattern = Pattern.compile("(?<!http[s]?://)(www\\.+)([\\S&&[^.@]]+)(\\.[\\S&&[^@]]+)");

        Pattern mailAddressPattern = Pattern.compile("[\\S&&[^@]]+@([\\S&&[^.@]]+)(\\.[\\S&&[^@]]+)");
        if (Objects.nonNull(message)) {

          Matcher httpLinksMatcher = httpLinkPattern.matcher(message);
          message = httpLinksMatcher.replaceAll("<a href=\"$0\" target=\"_blank\">$0</a>");

          final Matcher wwwLinksMatcher = wwwLinkPattern.matcher(message);
          message = wwwLinksMatcher.replaceAll("<a href=\"http://$0\" target=\"_blank\">$0</a>");

          final Matcher mailLinksMatcher = mailAddressPattern.matcher(message);
          message = mailLinksMatcher.replaceAll("<a href=\"mailto:$0\">$0</a>");
        }
        return message;
    }

    private String formatSpecifics(String message, PmsBooking booking) {
        Document doc = Jsoup.parse(message);
        for (Element specific : doc.select("specific")){
            boolean show = false;
            String channel = specific.attr("channel");
            String type = specific.attr("type");
            String text = specific.attr("text");
            
            if(channel != null && channel.equals(booking.channel)) {
                show = true;
            }
            
            if(type != null && booking.containsType(type)) {
                show = true;
            }
            
            if(!show) {
               message = message.replace(specific.toString() + "<br>", "");
               message = message.replace(specific.toString(), "");
            } else {
               message = message.replace(specific.toString(), text);
            }
            
        }
        return message;
    }

    private String getRoomList2(PmsBooking booking, BookingEngine bookingEngine) {
        String list = "";
        
        SimpleDateFormat slf = new SimpleDateFormat("dd.MM.YY");
        
        HashMap<String, String> productName = new HashMap();
        
        String lang = booking.language;
        if(lang != null && lang.equalsIgnoreCase("en")) { lang = "en_en"; }
        if(lang != null && lang.equalsIgnoreCase("no")) { lang = "nb_NO"; }
        
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            HashMap<String, Integer> productCounter = new HashMap();
            BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
            
            for(PmsBookingAddonItem item : room.addons) {
                Integer counter = productCounter.get(item.productId);
                if(counter == null) {
                    counter = 0;
                }
                counter += item.count;
                productCounter.put(item.productId, counter);
                String prodName = item.getName();
                String nameTranslated = item.getTranslationsByKey("name", lang);
                if(nameTranslated != null && !nameTranslated.isEmpty()) {
                    prodName = nameTranslated;
                }
                if(prodName != null && !prodName.isEmpty()) {
                    productName.put(item.productId, prodName);
                }
            }
            
            
            Date start = null;
            Date end = null;
            
            if(room.date != null && room.date.start != null) {
                start = productManager.getStore().convertToTimeZone(room.date.start);
            }
            if(room.date != null && room.date.end != null) {
                end = productManager.getStore().convertToTimeZone(room.date.end);
            }
            
            String typeName = "";
            if(type != null) {
                typeName = type.name;
            }
            if(booking.id.equals("dummybooking")) { typeName = "Double room"; }
            
            
            list += "<div class='roominfo' style='margin-top: 10px; margin-bottom: 10px;'>"; 
            list += "   <div class='roomname' style='font-weight: bold; padding-bottom: 10px;'>" + typeName + " ( " + slf.format(start) + " - " + slf.format(end) + " )"+ "</div>";
            for (PmsGuests guest : room.guests) {
                String text = guest.name;
                String phone = "";
                if(guest.prefix != null && !guest.prefix.isEmpty() && guest.phone != null && !guest.phone.isEmpty()) {
                    text += ", +" + guest.prefix + guest.phone;
                }
                if(guest.email!=null && !guest.email.isEmpty()) {
                    text += ", " + guest.email;
                }
                
                if(text != null && !text.isEmpty()) {
                    list += "   <div class='guestinfo' style='padding-left: 10px;'>" + text + "</div>";
                }
            }
            for(String productId : productCounter.keySet()) {
                Product product = productManager.getProduct(productId);
                String prodName = product.name;
                String prodNameFromItem = productName.get(productId);
                if(prodNameFromItem != null) {
                    prodName = prodNameFromItem;
                }
                Integer count = productCounter.get(productId);
                list += "   <div class='addon' style='padding-left: 10px;'>" + count + " x " + prodName + "</div>";
            }
            list += "</div>";
        }
        
        return list;
    }
    private String getRoomList3(PmsBooking booking, BookingEngine bookingEngine) {
        String list = "";
        
        SimpleDateFormat slf = new SimpleDateFormat("dd.MM.YY");
        
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
            String typeName = "";
            if(type != null) {
                typeName = type.name;
            }
            
            if(booking.id.equals("dummybooking")) { typeName = "Double room"; }
            
            Date start = null;
            Date end = null;
            
            if(room.date != null && room.date.start != null) {
                start = productManager.getStore().convertToTimeZone(room.date.start);
            }
            if(room.date != null && room.date.end != null) {
                end = productManager.getStore().convertToTimeZone(room.date.end);
            }
            
            list +=  typeName + " ( " + slf.format(start) + " - " + slf.format(end) + " )"+ "\n";
        }
        
        return list;
    }

}

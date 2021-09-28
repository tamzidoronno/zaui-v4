/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.comfortmanager.ComfortManager;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderShipmentLogEntry;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.pdf.data.AccountingDetails;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class PmsNotificationManager extends GetShopSessionBeanNamed implements IPmsNotificationManager {

    private String specifiedMessage;
    
    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    ProductManager productManager;
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    UserManager userManager;
    
    @Autowired
    StoreManager storeManager;
    
    @Autowired
    MessageManager messageManager;
    
    @Autowired
    InvoiceManager invoiceManager;
    
    @Autowired
    ComfortManager comfortManager;
    
    @Autowired
    PmsInvoiceManager pmsInvoiceManager;
    
    
    HashMap<String, PmsNotificationMessage> messages = new HashMap();
    private String orderIdToSend = null;
    private String paymentRequestId = null;
    private String emailToSendTo = null;
    private String prefixToSendTo = null;
    private String phoneToSendTo = null;
    private String messageToSend;
    private PmsNotificationMessage messageInUse;
    private String emailMessage;
    private String emailSubject;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon com : data.data) {
            if(com instanceof PmsNotificationMessage) {
                messages.put(com.id, (PmsNotificationMessage) com);
            }
        }
    }
    
    @Override
    public List<PmsNotificationMessage> getAllMessages() {
        doConvertToNewSystem();
        return new ArrayList(messages.values());
    }

    @Override
    public void saveMessage(PmsNotificationMessage msg) {
        saveObject(msg);
        messages.put(msg.id, msg);
    }

    @Override
    public PmsNotificationMessage getMessage(String messageId) {
        doConvertToNewSystem();
        return messages.get(messageId);
    }

    private void convertOldNotificationSystem() {
        if(!messages.isEmpty()) {
            return;
        }
        
        PmsConfiguration config = pmsManager.getConfigurationSecure();
        for(String emailKey : config.emails.keySet()) {
            String email = config.emails.get(emailKey);
            if(email != null && !email.isEmpty()) {
                PmsNotificationMessage msg = new PmsNotificationMessage();
                msg.title = config.emailTitles.get(emailKey);
                msg.content = email;
                msg.type = "email";
                msg.key = emailKey;
                msg.isDefault = true;
                convertLanguageSetup(msg);
                messages.put(msg.id, msg);
            }
        }
        for(String adminKey : config.adminmessages.keySet()) {
            String email = config.adminmessages.get(adminKey);
            if(email != null && !email.isEmpty()) {
                PmsNotificationMessage msg = new PmsNotificationMessage();
                msg.title = "";
                msg.content = email;
                msg.type = "admin";
                msg.key = adminKey;
                msg.isDefault = true;
                convertLanguageSetup(msg);
                messages.put(msg.id, msg);
            }
        }
        for(String smsKey : config.smses.keySet()) {
            String sms = config.smses.get(smsKey);
            if(sms != null && !sms.isEmpty()) {
                PmsNotificationMessage msg = new PmsNotificationMessage();
                msg.content = sms;
                msg.type = "sms";
                msg.key = smsKey;
                msg.isDefault = true;
                convertLanguageSetup(msg);
                messages.put(msg.id, msg);
            }
        }
    }

    private void convertLanguageSetup(PmsNotificationMessage msg) {
        List<String> language = new ArrayList();
        language.add("en_en");
        language.add("nb_no");
        language.add("dk");
        language.add("nl_nl");
        language.add("de");
        language.add("fi_fi");
        
        String key = msg.key;
        for(String lang : language) {
            if(key.toLowerCase().contains("_" + lang.toLowerCase())) {
                key = key.toLowerCase().replace("_"+lang.toLowerCase(), "");
                msg.key = key;
            }
        }
        saveObject(msg);
    }

    boolean isActive() {
        if(storeId.equals("8888708d-ede5-4bcd-b5ab-7cfe7ee3d489")) { return false; }
        if(storeId.equals("26c65d63-e353-4997-83df-488cc2fa3550")) { return false; }
        if(storeId.equals("fc8f7789-fcdb-4dad-b2ba-7d1d8dcca4d2")) { return false; }
        if(storeId.equals("bd3909ea-e5e9-4138-9d50-e8eab4a96045")) { return false; }
        if(storeId.equals("873ea5a5-5200-44b5-a2be-4b8f2e015c92")) { return false; }
        return true;
    }

    void doNotification(String key, PmsBooking booking, PmsBookingRooms room) {
        String addNotificationSent = key;
        getAllMessages();
        
        try {
            if(room != null && room.bookingItemId != null) {
                comfortManager.pmsEvent(key, room.bookingItemId);
            }
        }catch(Exception e) {
            logPrintException(e);
        }

        try {
            notifyAdmin(key, booking, room);
            notifyBySms(key, booking, room);
            notifyByEmail(key, booking, room);
        }catch(Exception e) {
            e.printStackTrace();
        }
        if(booking != null) {
            booking.notificationsSent.add(addNotificationSent);
            pmsManager.saveBooking(booking);
        }
        clearSendingTo();
    }

    private void notifyByEmail(String key, PmsBooking booking, PmsBookingRooms room) {
        key = checkIfNeedOverride(key, booking, room, "email");
        List<String> emailRecipients = new ArrayList();
        PmsNotificationMessage message = getSpecificMessage(key, booking, room, "email", null);
        if(message != null) {
            if(key.startsWith("room_")) {
                emailRecipients.addAll(sendEmail(key, booking, room, "room", message));
            } else {
                emailRecipients.addAll(sendEmail(key, booking, room, "booker", message));
            }
            
            String roomId = null;
            if(room != null) {
                roomId = room.pmsBookingRoomId;
            }

            if(!emailRecipients.isEmpty()) {
                if(orderIdToSend != null && !orderIdToSend.isEmpty()) {
                    for(String email : emailRecipients) {
                        logToOrder(key, email, orderIdToSend);
                    }
                }

            }
        }
    }
    
    private void notifyBySms(String key, PmsBooking booking, PmsBookingRooms room) {
        key = checkIfNeedOverride(key, booking, room, "email");
        List<PmsGuests> smsRecipients = new ArrayList();
        
        if(key.startsWith("room_")) {
            smsRecipients.addAll(sendSms(key, booking, room, "room"));
        } else {
            smsRecipients.addAll(sendSms(key, booking, room, "booker"));
        }
    }

    @Override
    public PmsNotificationMessage getSpecificMessage(String key, PmsBooking booking, PmsBookingRooms room, String type, String prefix) {
        if(messageToSend != null && !messageToSend.isEmpty() && (type.equals("sms") || type.equals("email"))) {
            PmsNotificationMessage notificationmsg = new PmsNotificationMessage();
            notificationmsg.content = messageToSend;
            return notificationmsg;
        }
        if(emailMessage != null && type.equals("email")) {
            PmsNotificationMessage notificationmsg = new PmsNotificationMessage();
            notificationmsg.content = emailMessage;
            notificationmsg.title = emailSubject != null ? emailSubject : "";
            return notificationmsg;
        }
        
        if(prefix== null) {
            prefix = "";
        }
        prefix = prefix.replace("+", "");
        String code = getSession().language;
        if(booking != null) {
            code = booking.language;
        }
        if(room != null && room.language != null && !room.language.isEmpty()) {
            code = room.language;
        }
        String language = convertLanguage(code);
        
        
        List<String> languagesSupported = getLanguagesForMessage(key, type);
        List<String> prefixesSupported = getPrefixesForMessage(key, type);
        List<String> roomTypes = new ArrayList();
        if(booking != null) {
            roomTypes = booking.rooms.stream().map(e->e.bookingItemTypeId).collect(Collectors.toList());
        }
        
        for(PmsNotificationMessage msg : messages.values()) {
            if(msg.isManual) { continue; }
            if(!msg.type.equals(type)) {
                continue;
            }
            if(msg.languages.isEmpty() && msg.prefixes.isEmpty() && msg.roomTypes.isEmpty()) {
                continue;
            }
            if(languagesSupported.contains(language) && !msg.containsLanguage(language)) {
                continue;
            }
            if(prefixesSupported.contains(prefix) && (!msg.prefixes.isEmpty() && !msg.prefixes.contains(prefix)) && type.equals("sms")) {
                continue;
            }
            if(!msg.languages.isEmpty() && !msg.containsLanguage(language)) {
                continue;
            }
            if(!msg.prefixes.isEmpty() && !msg.prefixes.contains(prefix)) {
                continue;
            }
            if(!msg.roomTypes.isEmpty() && !msg.containsOneOrMoreRoomType(roomTypes)) {
                continue;
            }
            if(msg.key.equalsIgnoreCase(key)) {
                if(msg.content == null) { msg.content = ""; }
                if(msg.title == null) { msg.title = ""; }
                return msg;
            }
        }
        
        for(PmsNotificationMessage msg : messages.values()) {
            if(msg.isManual) { continue; }
            if(!msg.type.equals(type)) {
                continue;
            }
            if(!msg.key.equalsIgnoreCase(key)) {
                continue;
            }
            if(!msg.languages.isEmpty()) {
                continue;
            }
            if(!msg.prefixes.isEmpty()) {
                continue;
            }
            if(!msg.roomTypes.isEmpty()) {
                continue;
            }
            if(msg.content == null) { msg.content = ""; }
            if(msg.title == null) { msg.title = ""; }
            return msg;
        }
        return null;
    }

    @Override
    public PmsNotificationMessage doFormationOnMessage(PmsNotificationMessage msg, String bookingId, String pmsBookingRoomId) {
        PmsBooking booking = pmsManager.getBookingUnsecure(bookingId);
        PmsBookingRooms room = null;
        if(pmsBookingRoomId != null) {
            room = booking.getRoom(pmsBookingRoomId);
        }
        msg.content = formatMessage(msg.content, booking, room, msg.key, msg.type, null);
        msg.title = formatMessage(msg.title, booking, room, msg.key, msg.type, null);
        return msg;
    }
    
    private String formatMessage(String message, PmsBooking booking, PmsBookingRooms room, String key, String type, PmsGuests guest) {
        if(booking == null) {
            booking = new PmsBooking();
        }
        if (message != null) {
            message = message.trim();
        }
        PmsBookingMessageFormatter formater = new PmsBookingMessageFormatter(pmsInvoiceManager);
        formater.setProductManager(productManager);
        formater.setConfig(pmsManager.getConfigurationSecure());

        if (this.specifiedMessage != null && message != null) {
            String specifiedmsg = this.specifiedMessage.replace("\n", "<br>\n");
            message = message.replace("{personalMessage}", specifiedmsg);
        }

        if (room != null) {
            message = formater.formatRoomData(message, room, bookingEngine, guest);
            try {
                PmsAdditionalItemInformation addinfo = pmsManager.getAdditionalInfo(room.bookingItemId);
                message = message.replace("{roomDescription}", addinfo.textMessageDescription);
            } catch (Exception e) {
            }
        }
        message = formater.formatContactData(message, userManager.getUserById(booking.userId), null, booking);
        message = formater.formatBookingData(message, booking, bookingEngine);

        message = message.replace("{extrafield}", pmsManager.getConfigurationSecure().extraField);

        if (key.startsWith("booking_sendpaymentlink")
                || key.startsWith("booking_unabletochargecard")
                || key.startsWith("booking_paymentmissing")
                || key.startsWith("booking_confirmed")
                || key.startsWith("booking_completed")
                || key.startsWith("order_")) {
            if(paymentRequestId != null) {
                String link = pmsInvoiceManager.getPaymentLinkConfig().webAdress + "/pr.php?id=" + paymentRequestId;
                if (type.equals("email")) {
                    message = message.replace("{paymentlink}", "<a href='" + link + "'>" + link + "</a>");
                } else {
                    message = message.replace("{paymentlink}", link);
                }
            } else if (orderIdToSend != null) {
                message = message.replace("{orderid}", this.orderIdToSend);
                Order order = orderManager.getOrderSecure(this.orderIdToSend);
                String link = pmsInvoiceManager.getPaymentLinkConfig().webAdress + "/p.php?id=" + order.incrementOrderId;
                if (type.equals("email")) {
                    message = message.replace("{paymentlink}", "<a href='" + link + "'>" + link + "</a>");
                } else {
                    message = message.replace("{paymentlink}", link);
                }
                message = message.replace("{selfmanagelink}", pmsInvoiceManager.getPaymentLinkConfig().webAdress + "/?page=booking_self_management&id=" + booking.secretBookingId);
            } else {
                String link = pmsInvoiceManager.getPaymentLinkConfig().webAdress + "/pr.php?id=" + booking.id;
                if (type.equals("email")) {
                    message = message.replace("{paymentlink}", "<a href='" + link + "'>" + link + "</a>");
                } else {
                    message = message.replace("{paymentlink}", link);
                }
            }
        }        
        
        return message;    
    }

    private String checkIfNeedOverride(String key, PmsBooking booking, PmsBookingRooms room, String messageForm) {
        if(key.equals("booking_completed") && booking.channel != null && booking.channel.contains("wubook")) {
            boolean isChargedByOta = isChargedByOta(booking);
            PmsNotificationMessage message = getSpecificMessage("booking_completed_ota", booking, room, messageForm, null);
            if(message != null) {
                key = "booking_completed_ota"; 
            }
            if(isChargedByOta) {
                message = getSpecificMessage("booking_completed_payed_ota", booking, room, messageForm, null);
                if(message != null) { 
                    key = "booking_completed_payed_ota"; 
                }
            }
        }
        return key;
    }
    
    
    private boolean isChargedByOta(PmsBooking booking) {
        if(booking.orderIds == null) {
            return false;
        }
        for(String orderId : booking.orderIds) {
            Order ord = orderManager.getOrder(orderId);
            if(ord != null) {
                if(ord.isPrepaidByOTA()) {
                    return true;
                }
            }
        }
        return false;
    }

    String getMessageFormattedMessage(String bookingId, String key, String type) {
        doConvertToNewSystem();
        PmsBooking booking = pmsManager.getBooking(bookingId);
        PmsNotificationMessage message = getSpecificMessage(key, booking, null, type, null);
        if(message==null) {
            return "";
        }
        return message.content;
   }

    void setOrderIdToSend(String orderId) {
        orderIdToSend = orderId;
    }

    void setEmailToSendTo(String email) {
        emailToSendTo = email;
    }

    void setPrefixToSendTo(String prefix) {
        prefixToSendTo = prefix;
    }

    void setPhoneToSendTo(String phone) {
        phoneToSendTo = phone;
    }
    
    public void clearSendingTo() {
        phoneToSendTo = null;
        prefixToSendTo = null;
        emailToSendTo = null;
        orderIdToSend = null;
        messageToSend = null;
        messageInUse = null;
        emailMessage = null;
        emailSubject = null;
        paymentRequestId = null;
    }

    private List<String> sendEmail(String key, PmsBooking booking, PmsBookingRooms room, String type, PmsNotificationMessage message) {
        List<String> recipients = getEmailRecipients(booking, room, type, key);
        
        AccountingDetails details = invoiceManager.getAccountingDetails();
        HashMap<String, String> attachments = new HashMap();
        if (key.startsWith("booking_completed")) {
            attachments.put("termsandcondition.html", createContractAttachment(booking.id));
        }
        if (key.startsWith("sendreciept")) {
            String name = "receipt";
            if(details != null && details.language != null && details.language.contains("de")) {
                name = "Rechnung";
            }
            attachments.put(name+".pdf", createInvoiceAttachment());
        }
        if (key.startsWith("sendinvoice")) {
            String name = "invoice";
            if(details != null && details.language != null && details.language.contains("de")) {
                name = "Rechnung";
            }
            attachments.put(name+".pdf", createInvoiceAttachment());
        }

        for(String email : recipients) {
            
            PmsGuests guest = null;
            
            if(email.startsWith("pmsguest_")) {
                for(PmsGuests g : room.guests) {
                    if(email.contains(g.guestId)) {
                        guest = g;
                        email = g.email;
                    }
                }
            }
            
            String title = formatMessage(message.title, booking, room, key, "email", guest);
            String content = formatMessage(message.content, booking, room, key, "email", guest);
            content = wrapContentOnMessage(content);

            
            messageManager.sendMailWithAttachments(email, email, title, content, getFromEmail(), getFromName(), attachments);
            String logText = title + "<bR>" + content + "<div>Sent to: " + email;
            logText += "</div>";
            String roomId = "";
            if(room != null) { roomId = room.pmsBookingRoomId; }
            if(booking != null) {
                pmsManager.logEntry(logText, booking.id, null, roomId, key + "_email");
            }
        }
        
        return recipients;
    }


    private String createInvoiceAttachment() {
        String invoice = invoiceManager.getBase64EncodedInvoice(orderIdToSend);
        return invoice;
    }

    public String createContractAttachment(String bookingId) {
        String contract = "";
        try {
            contract = pmsManager.getContract(bookingId);
            contract = invoiceManager.base64Encode(contract);

        } catch (Exception e) {

        }
        return contract;
    }    
    
    private List<PmsGuests> sendSms(String key, PmsBooking booking, PmsBookingRooms room, String type) {
        PmsConfiguration configuration = pmsManager.getConfigurationSecure();
        if(booking == null) {
            booking = new PmsBooking();
            if(orderIdToSend != null) {
                Order tmpOrder = orderManager.getOrder(orderIdToSend);
                booking.userId = tmpOrder.userId;
            }
        }

        if(room.deleted && key != "room_cancelled" ) return new ArrayList<>();

        List<PmsGuests> recipients = getSmsRecipients(booking, room, type);
        for(PmsGuests guest : recipients) {
            PmsNotificationMessage message = getSpecificMessage(key, booking, room, "sms", guest.prefix);
            if(message != null) {
                String content = formatMessage(message.content, booking, room, key, "sms", guest);
                if (guest.prefix != null && (guest.prefix.equals("47") || guest.prefix.equals("+47"))) {
                    messageManager.sendSms("sveve", guest.phone, content, guest.prefix, configuration.smsName);
                } else {
                    messageManager.sendSms("nexmo", guest.phone, content, guest.prefix, configuration.smsName);
                }
                logSentMessage(content, room, key, "(" + guest.prefix+")" + guest.phone, booking);
            }
        }
        
        return recipients;
    }

    private List<String> getEmailRecipients(PmsBooking booking, PmsBookingRooms room, String type, String key) {
        List<String> recipients = new ArrayList();
        if(emailToSendTo != null && !emailToSendTo.isEmpty()) {
            recipients.add(emailToSendTo);
            return recipients;
        }
        
        if(type.equals("admin")) {
            String email = storeManager.getMyStore().configuration.emailAdress;
            recipients.add(email);
        }
        
        if(type.equals("booker")) {
            if (booking != null && booking.registrationData != null && 
                    key != null && key.equals("booking_completed") &&
                    booking.registrationData.resultAdded != null && 
                    booking.registrationData.resultAdded.containsKey("company_email") &&
                    booking.registrationData.resultAdded.get("company_email").contains("@")) {
                        String companyEmail = booking.registrationData.resultAdded.get("company_email");
                        recipients.add(companyEmail);
            } else {
                User user = userManager.getUserById(booking.userId);
                recipients.add(user.emailAddress);
            }
        }
        
        if(type.equals("room") && room != null) {
            boolean sentToBooker = !pmsManager.getConfigurationSecure().sendToBookerIfGuestIsEmpty;
            
            for(PmsGuests guest : room.guests) {
                if(guest != null && guest.email != null && guest.email.contains("@")) {
                    sentToBooker = true;
                }
            }
            
            for(PmsGuests guest : room.guests) {
                if(guest != null && guest.email != null && guest.email.contains("@")) {
                    recipients.add("pmsguest_" + guest.guestId);
                } else if(!sentToBooker) {
                    User user = userManager.getUserById(booking.userId);
                    recipients.add(user.emailAddress);
                    sentToBooker = true;
                }
            }
        }
        return recipients;
    }

    
    private String getFromEmail() {
        String fromEmail = storeManager.getMyStore().configuration.emailAdress;
        if (!pmsManager.getConfigurationSecure().senderEmail.isEmpty()) {
            fromEmail = pmsManager.getConfigurationSecure().senderEmail;
        }
        return fromEmail;
    }

    private String getFromName() {
        String fromName = getFromEmail();
        if (!pmsManager.getConfigurationSecure().senderName.isEmpty()) {
            fromName = pmsManager.getConfigurationSecure().senderName;
        }
        return fromName;
    }

    void setMessageToSend(String message) {
        this.messageToSend = message;
    }

    void doNotification(String key, String bookingId) {
        PmsBooking booking = pmsManager.getBookingUnsecure(bookingId);
        if(booking == null) {
            booking = pmsManager.getBookingFromRoomSecure(bookingId);
        }
        doNotification(key, booking, null);
    }

    private List<PmsGuests> getSmsRecipients(PmsBooking booking, PmsBookingRooms room, String type) {

        List<PmsGuests> recipients = new ArrayList();
        if(phoneToSendTo != null && !phoneToSendTo.isEmpty()) {
            PmsGuests guest = new PmsGuests();
            
            if(room != null && room.guests != null && !room.guests.isEmpty()) {
                guest = room.guests.get(0);
            }
            
            guest.phone = phoneToSendTo;
            guest.prefix = prefixToSendTo;
            
            recipients.add(guest);
            return recipients;
        }
        
        if(type.equals("admin")) {
            PmsGuests guest = new PmsGuests();
            guest.phone = storeManager.getMyStore().configuration.phoneNumber;
            guest.prefix = storeManager.getMyStore().configuration.defaultPrefix + "";
            recipients.add(guest);
        }
        
        if(type.equals("booker")) {
            User user = userManager.getUserById(booking.userId);
            PmsGuests guest = new PmsGuests();
            guest.phone = user.cellPhone;
            guest.prefix = user.prefix;
            recipients.add(guest);
        }
        
        if(type.equals("room") && room != null) {
            boolean sentToBooker = false;
            
            for(PmsGuests guest : room.guests) {
                if(guest != null && guest.phone != null && !guest.phone.isEmpty()) {
                    sentToBooker = true;
                }
            }
            
            sentToBooker = !pmsManager.getConfigurationSecure().sendToBookerIfGuestIsEmpty;
            
            for(PmsGuests guest : room.guests) {
                if(guest != null && guest.phone != null && !guest.phone.isEmpty()) {
                    recipients.add(guest);
                } else if(!sentToBooker) {
                    User user = userManager.getUserById(booking.userId);
                    PmsGuests tmpGuest = new PmsGuests();
                    tmpGuest.phone = user.cellPhone;
                    tmpGuest.prefix = user.prefix;
                    recipients.add(tmpGuest);
                    sentToBooker = true;
                }
            }
        }
        return recipients;
    }

    private void notifyAdmin(String key, PmsBooking booking, PmsBookingRooms room) {
        key = checkIfNeedOverride(key, booking, room, "admin");
        PmsNotificationMessage message = getSpecificMessage(key, booking, room, "admin", null);
        if(message != null) {
            String content = formatMessage(message.content, booking, room,key,"admin", null);
            String email = storeManager.getMyStore().configuration.emailAdress;
            String phone = storeManager.getMyStore().configuration.phoneNumber;
            String prefix = storeManager.getMyStore().configuration.defaultPrefix + "";
            
            pmsManager.logEntry("Notified admin :" + content + " phone: " + phone + ", email:" + email, booking.id, null);
            messageManager.sendMail(email, "Administrator", "Information from GetShop PMS", content, getFromEmail(), getFromName());
            messageManager.sendSms("sveve", phone, content, prefix);
        }
    }

    private void doConvertToNewSystem() {
        convertOldNotificationSystem();
    }

    private void logToOrder(String key, String msg, String orderIdToSend) {
        Order order = orderManager.getOrder(orderIdToSend);
        OrderShipmentLogEntry e = new OrderShipmentLogEntry();
        e.type = key;
        e.date = new Date();
        e.address = msg;
        order.shipmentLog.add(e);
        orderManager.saveOrder(order);
    }

    @Override
    public void deleteMessage(String messageId) {
        PmsNotificationMessage msg = getMessage(messageId);
        if(msg != null) {
            deleteObject(msg);
            messages.remove(messageId);
        }
    }

    @Override
    public List<String> getLanguagesForMessage(String key, String type) {
        List<String> languages = new ArrayList();
        for(PmsNotificationMessage msg : messages.values()) {
            if(!msg.key.equals(key)) {
                continue;
            }
            if(!msg.type.equals(type)) {
                continue;
            }
            languages.addAll(msg.languages);
        }
        return languages;
    }

    @Override
    public List<String> getPrefixesForMessage(String key, String type) {
        List<String> prefixes = new ArrayList();
        for(PmsNotificationMessage msg : messages.values()) {
            if(!msg.key.equals(key)) {
                continue;
            }
            if(!msg.type.equals(type)) {
                continue;
            }
            prefixes.addAll(msg.prefixes);
        }
        return prefixes;        
    }

    private void logSentMessage(String content, PmsBookingRooms room, String key, String recipient, PmsBooking booking) {

        String roomId = null;
        if(room != null) {
            roomId = room.pmsBookingRoomId;
        }

        String logText = content + "<div>Sent to: ";
        logText += recipient;
        logText += "</div>";
        pmsManager.logEntry(logText, booking.id, null, roomId, key + "_sms");

        if(orderIdToSend != null && !orderIdToSend.isEmpty()) {
            logToOrder(key, recipient, orderIdToSend);
        }
    }

    private String convertLanguage(String language) {
        if(language == null) {
            return "no";
        }
        if(language.toLowerCase().equals("nb_no")) {
            return "no";
        }
        if(language.toLowerCase().equals("nn_no")) {
            return "no";
        }
        if(language.toLowerCase().equals("en_en")) {
            return "en";
        }
        return language;
    }

    private String wrapContentOnMessage(String message) {
        if(message != null) {
            PmsBookingMessageFormatter formater = new PmsBookingMessageFormatter(pmsInvoiceManager);
            if (message != null) {
                if (message.contains("http") && !message.contains("<a")) {
                    message = formater.formatHtml(message);
                }

                message = pmsManager.getConfigurationSecure().emailTemplate.replace("{content}", message);
                message = message.trim();
                message = message.replace("\n", "<br>\n");
            }
        }
        return message;
    }

    void setEmailMessageToSend(String message) {
        this.emailMessage = message;
    }

    boolean hasResendCode() {
        if(messages == null) {
            return false;
        }
        for(PmsNotificationMessage msg : messages.values()) {
            if(msg != null && msg.key.equals("room_resendcode") && msg.content != null && !msg.content.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    void setEmailSubject(String subject) {
        this.emailSubject = subject;
    }

    @Override
    public void sendEmail(PmsNotificationMessage msg, String email, String bookingId, String roomId) {
        msg.content = wrapContentOnMessage(msg.content);
        messageManager.sendMail(email, email, msg.title, msg.content, email, email);


        String logText = "a" +msg.content + "<div>Sent to: ";
        logText += email;
        logText += "</div>";
        pmsManager.logEntry(logText, bookingId, null, roomId, msg.key + "_email");
    }

    void setPaymentRequestId(String prid) {
        this.paymentRequestId = prid;
    }

    @Override
    public String createPreview(String key, String message) {
        PmsBooking booking = new PmsBooking();
        booking.id = "dummybooking";
        
        PmsBookingRooms room = new PmsBookingRooms();
        room.code = "1234";
        room.date.start = new Date();
        room.date.end = new Date();
        room.pmsBookingRoomId = "dummyroom";
        
        room.guests = new ArrayList();
        PmsGuests guest = new PmsGuests();
        guest.prefix = "1";
        guest.email = "homealone@doe.com";
        guest.name = "Home Alone Kevin";
        guest.phone = "43242341123";
        room.guests.add(guest);
        
        guest = new PmsGuests();
        guest.prefix = "49";
        guest.email = "clark@doe.com";
        guest.name = "Clark Kent";
        guest.phone = "123123333";
        room.guests.add(guest);
        
        booking.rooms.add(room);
        
        room = new PmsBookingRooms();
        room.code = "2234";
        room.date.start = new Date();
        room.date.end = new Date();
        room.pmsBookingRoomId = "dummyroom";
        
        room.guests = new ArrayList();
        guest = new PmsGuests();
        guest.prefix = "1";
        guest.email = "June@doe.com";
        guest.name = "June Galwik";
        guest.phone = "1234322333";
        room.guests.add(guest);
        
        guest = new PmsGuests();
        guest.prefix = "49";
        guest.email = "jane@doe.com";
        guest.name = "Jane Witson";
        guest.phone = "122234213";
        room.guests.add(guest);
        
        booking.rooms.add(room);
        
        
        return formatMessage(message, booking, room, key, "email", guest);
    }

}

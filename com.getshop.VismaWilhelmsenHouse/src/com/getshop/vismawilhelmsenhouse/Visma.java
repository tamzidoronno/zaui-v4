/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.vismawilhelmsenhouse;

import com.getshop.javaapi.GetShopApi;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.hotelbookingmanager.BookingReference;
import com.thundashop.core.hotelbookingmanager.Room;
import com.thundashop.core.hotelbookingmanager.RoomInformation;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import sun.misc.IOUtils;

/**
 *
 * @author ktonder
 */
public class Visma {
    private final GetShopApi api;
    private List<Order> orders;
    private final List<Order> ordersToTranser = new ArrayList();
    private final List<Order> ordersToTranserCreditCard = new ArrayList();
    private final List<Order> orderWithErrors = new ArrayList();
    private final List<User> usersToTransfer = new ArrayList();
    private final List<User> usersToTransferCreditCard = new ArrayList();
    private final VismaSql vismaSql;
    private final Application application;

    public Visma(GetShopApi api, String vismaPassword) throws ClassNotFoundException, SQLException, Exception {
        this.api = api;
        application = api.getStoreApplicationPool().getApplication("37d409be-1207-45e8-bf3b-6465442b58d9");
        this.vismaSql = new VismaSql(application, vismaPassword);
    }
    
    public void parse() throws Exception {
        fetchOrdersFromGetShop();
        filterOrders();
        filterUsers();
        createEdiFile();
        createEdiFileCreditCard();
        handleOrdersWithErrors();
    }

    private void fetchOrdersFromGetShop() throws Exception {
        this.orders = api.getOrderManager().getOrdersNotTransferredToAccountingSystem();
    }

    private void filterOrders() throws Exception {
        for (Order order : orders) {
            if (vismaSql.checkInVismaIfUserExists(order)) {
                markOrdersAsTransferred(order);
                continue;
            }
            
            // If user has been deleted, dont transfer the order.
            try {
                User user = api.getUserManager().getUserById(order.userId);
            } catch (Exception ex) {
                orderWithErrors.add(order);
                continue;
            }
            
            if (!validateOrder(order)) {
                continue;
            }
            
            
            if (order.payment.paymentType.equals("ns_def1e922_972f_4557_a315_a751a9b9eff1\\Netaxept")) {
                ordersToTranserCreditCard.add(order);
            } else {
                ordersToTranser.add(order);
            }
        }
    }

    
    private void filterUsers() throws SQLException, Exception {
        for (Order order : ordersToTranser) {
            User user = api.getUserManager().getUserById(order.userId);
            if (!vismaSql.checkIfUserExists(user)) {
                if (order.payment.paymentType.equals("ns_def1e922_972f_4557_a315_a751a9b9eff1\\Netaxept")) {
                    usersToTransferCreditCard.add(user);
                } else {
                    usersToTransfer.add(user);
                }
            }
        }
    }

    private void createEdiFile() throws Exception {
        String result = "";
        
        for (User user : usersToTransfer) {
            result += getActor(user);
        }
        
        for (Order order : ordersToTranser) {
            User user = api.getUserManager().getUserById(order.userId);
            BookingReference reference = api.getHotelBookingManager().getReservationByReferenceId(Integer.valueOf(order.reference));
            
            String ordrehode = "H;"; // Fast H
            ordrehode += "1;"; // Fast 1 for salg
            ordrehode += application.getSetting("ordertype") + ";"; // Fast 1 for normalordre
            ordrehode += order.incrementOrderId + ";"; // GetShop ordre id
            ordrehode += user.customerId + ";"; // Kundenr 
            ordrehode += new SimpleDateFormat("yyyyMMdd").format(order.createdDate) + ";"; // Ordredato
            ordrehode += new SimpleDateFormat("yyyyMMdd").format(reference.startDate) + ";"; // Leveringsdato
            ordrehode += application.getSetting("paymentterm") + ";"; //Betalingsbetingelse
            ordrehode += application.getSetting("paymenttype") + ";"; //Betalingsmåte ( 10 = avtalegiro )
            ordrehode += ";"; // avgiftskode ( tom = bruk fra kunde )
            result += ordrehode + "\r\n";

            for (CartItem item : order.cart.getItems()) {
                Integer vismaId = Integer.valueOf(getProductVismaProductId(item));
                String orderline = "L;"; // Fast L for orderline
                orderline += vismaId + ";"; // ProdNO
                orderline += ";"; // Avgiftskode ( hentes fra kunden )
                
                String productName = item.getProduct().name;
                orderline += productName + ";"; // Produkt beskrivelse
                orderline += item.getCount() + ";"; // Antall mnder
                orderline += item.getProduct().price + ";"; // Pris pr antall, hvis blank hentes pris fra Visma
                orderline += ";"; // ikke i bruk
                orderline += getRoomId(reference, item)+";"; // Hotel room id. hvordan finner man denne?
                orderline += ";"; // 
                result += orderline + "\r\n";
            }
        }
        
        Files.write(Paths.get(application.getSetting("vismafilelocation")+"OrdersrAct.EDI"), result.getBytes());
    }
    
    private String getProductVismaProductId(CartItem cartItem) throws Exception {
        if (cartItem.getProduct().accountingSystemId != null) {
            return cartItem.getProduct().accountingSystemId;
        }
        
        Product product = api.getProductManager().getProduct(cartItem.getProduct().id);
        if (product == null) {
            return null;
        }
        
        return product.accountingSystemId;
    }

    private boolean validateOrder(Order order) throws Exception {
        for (CartItem cartItem : order.cart.getItems()) {
            String accountingSystemId = getProductVismaProductId(cartItem);
            
            if (accountingSystemId == null) {
                System.out.println("1:"+order.incrementOrderId + " " + cartItem.getProduct().name);
                orderWithErrors.add(order);
                return false;
            }
        }
        
        return true;
    }

    private void handleOrdersWithErrors() {
        for (Order order : orderWithErrors) {
            System.out.println("Failed transfer: " + order.id);
        }
    }

    private String getRoomId(BookingReference reference, CartItem item) throws Exception {
        for (RoomInformation info : reference.roomsReserved) {
            if (info.cartItemId.equals(item.getCartItemId())) {
                Room room = api.getHotelBookingManager().getRoom(info.roomId);
                if (room != null) {
                    return room.roomName;
                }
            }
        }
        
        return "";
    }

    private void markOrdersAsTransferred(Order order) throws Exception {
        Order getShopOrder = api.getOrderManager().getOrder(order.id);
        getShopOrder.transferredToAccountingSystem = true;
        api.getOrderManager().saveOrder(getShopOrder);
    }

    private void createEdiFileCreditCard() throws Exception {
        String result = "";
        
        for (User user : usersToTransferCreditCard) {
            result += getActor(user);
        }
        
       

        for (Order order : ordersToTranserCreditCard) {
            BookingReference reference = api.getHotelBookingManager().getReservationByReferenceId(Integer.valueOf(order.reference));
            User user = api.getUserManager().getUserById(order.userId);
            
            result += "H;";
            result += new SimpleDateFormat("yyyyMMdd").format(new Date())+";";
            result += order.incrementOrderId+";"; // 
            result += "GetShop order: "+order.incrementOrderId+";";
            result += "\r\n";
            
            result += "L;"; //Fixed value L
            result += ";"; // Voucher no (If Voucher serie no is used,this can be empty)
            result += new SimpleDateFormat("yyyyMMdd").format(new Date())+";"; // Voucher date
            result += ";"; // Value date (Usees from Batch if empty)
            result += user.customerId+";"; // Debit account
            result += application.getSetting("vismadebitaccount")+";"; // Credit account
            result += api.getOrderManager().getTotalAmount(order)+";"; // Total amount
            result += ";"; // R3 Oppdrag ID
            result += ";"; // R4 Gjenstand ID
            result += ";"; // LinjeText hvis nødvendig. f.eks Salg, Betaling, Refnr fra FV. Hvis tomt, hentes tekst fra bilagsart.
            result += "\r\n";
            
            for (CartItem item : order.cart.getItems()) {
                result += "L;"; //Fixed value L
                result += ";"; // Voucher no (If Voucher serie no is used,this can be empty)
                result += new SimpleDateFormat("yyyyMMdd").format(new Date())+";"; // Voucher date
                result += ";"; // Value date (Usees from Batch if empty)
                result += getProductDebitNumber(item.getProduct())+";"; // Debit account
                result += user.customerId+";"; // Credit account
                result += getProductTotalAmount(item)+";"; // Total amount
                result += ";"; // R3 Oppdrag ID
                result += getRoomId(reference, item)+";"; // R4 Gjenstand ID
                result += ";"; // LinjeText hvis nødvendig. f.eks Salg, Betaling, Refnr fra FV. Hvis tomt, hentes tekst fra bilagsart.
                result += "\r\n";
            }
            result += "\r\n";
            savePdfInvoice(order);
        }
        
        Files.write(Paths.get(application.getSetting("vismafilelocation")+"OrdersrAct.EDI"), result.getBytes());
        System.out.println(result);
    }

    private String getActor(User user) {
        String result = "A;"; //Fast A som forteller at det er Aktør
        result += user.customerId + ";"; //Kundenummer
        result += user.fullName + ";"; //Kundenavn
        result += user.address.address + ";"; //Kunde adresse 1
        result += user.address.postCode + ";"; //Kunde Postnummer
        result += user.address.city + ";"; //Kunde Poststed
        result += user.emailAddress + ";"; //Kunde e-post
        result += user.cellPhone + ";"; //Kunde mobil tlf.

        if (user.isPrivatePerson) {
            Date date = null;
            try {
                if (user.birthDay.contains(".")) {
                    SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yy");
                    date = dt.parse(user.birthDay);
                } else {
                    if (user.birthDay.length() == 6) {
                        SimpleDateFormat dt = new SimpleDateFormat("ddMMyy");
                        date = dt.parse(user.birthDay);
                    } else {
                        SimpleDateFormat dt = new SimpleDateFormat("ddMMyy");
                        date = dt.parse(user.birthDay);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            result += new SimpleDateFormat("ddMMyyyy").format(date) + ";"; //Kunde Org nr el fødselsnr.
        } else {
            result += user.birthDay + ";"; //Kunde Org nr el fødselsnr.
        }
        if (user.mvaRegistered) {
            result += "1;"; //Kunde avgiftskode hvis tom = mva pliktig
        } else {
            result += ";"; //ingen avgift.
        }
        result += application.getSetting("paymentterm")+";"; //Betaling per 30 dag.
        result += application.getSetting("paymenttype")+";"; //Autogiro.
        result += "\r\n";
        return result;
    }

    private String getProductDebitNumber(Product product) {
        return "xxxx";
    }

    private Double getProductTotalAmount(CartItem item) {
        return item.getCount()*item.getProduct().price;
    }

    private void savePdfInvoice(Order order) throws Exception {
        String base64 = api.getInvoiceManager().getBase64EncodedInvoice(order.id);
//        System.out.println(base64);
        byte[] bytes = Base64.decode(base64);
        String targetFile = application.getSetting("vismafilelocation") + order.incrementOrderId+".pdf";
        Files.write(Paths.get(targetFile), bytes);
    }
}

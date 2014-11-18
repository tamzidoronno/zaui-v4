package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.hotelbookingmanager.BookingReference;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class VismaUsers extends DataCommon {

    static String payMentCondition = "15"; // gets it from PmtTrm
    static String payMentType = "10"; // gets it from PmtMt

    private static int getVismaId(ProductManager prodManager, CartItem item) throws ErrorException {
        
//        if (item.getProduct().vismaId > 0) {
//            return item.getProduct().vismaId;
//        }
//        
        Product currentProduct = prodManager.getProduct(item.getProduct().id);
        
        if (currentProduct == null) {
            return item.getProduct().vismaId;
        }
        
        return currentProduct.vismaId;
    }
    
    public HashMap<String, String> transfereddUserIds = new HashMap();
    
    
    public boolean checkTransferred(User user) {
        if(transfereddUserIds.containsKey(user.id) && transfereddUserIds.get(user.id).equals(user.toString())) {
            return true;
        }
        return false;
    }
    
    
    static String generateOrderLines(List<Order> orders, User user, HashMap<Integer, BookingReference> references, ProductManager prodManager, HotelBookingManager bookingManager) throws ErrorException {
        String result = "";
        for(Order order : orders) {
            if (bookingManager.orderExistsInVisma(order.incrementOrderId)) {
                continue;
            }
            
            if(order.reference == null) {
                continue;
            }
            
            BookingReference reference = references.get(new Integer(order.reference));
            if(reference == null) {
                continue;
            }
            String ordrehode = "H;"; // Fast H
            ordrehode += "1;"; // Fast 1 for salg
            ordrehode += "1;"; // Fast 1 for normalordre
            ordrehode += order.incrementOrderId+";"; // GetShop ordre id
            ordrehode += user.customerId+";"; // Kundenr 
            ordrehode += new SimpleDateFormat("yyyyMMdd").format(order.createdDate)+";"; // Ordredato
            ordrehode += new SimpleDateFormat("yyyyMMdd").format(reference.startDate) + ";"; // Leveringsdato
            ordrehode += payMentCondition+";"; //Betalingsbetingelse
            ordrehode += payMentType+";"; //Betalingsmåte ( 10 = avtalegiro )
            ordrehode += ";"; // avgiftskode ( tom = bruk fra kunde )
            result += ordrehode+ "\r\n";
            
            for(CartItem item : order.cart.getItems()) {
                String orderline = "L;"; // Fast L for orderline
                orderline += getVismaId(prodManager, item) + ";"; // ProdNO
                orderline += ";"; // Avgiftskode ( hentes fra kunden )
                orderline += item.getProduct().name + ";"; // Produkt beskrivelse
                orderline += item.getCount() + ";"; // Antall mnder
                orderline += item.getProduct().price + ";"; // Pris pr antall, hvis blank hentes pris fra Visma
                orderline += ";"; // ikke i bruk
                orderline += ";"; // Bod id ( denne får vi ikke satt pr nå)
                orderline += ";"; // 
                result += orderline+ "\r\n";
            }
        }
        return result;
    }
    
    static String generateVismaUserString(User user) {
        /*
            c,RecType,1,1,0,,,,,,,,,S,		'Fast A som forteller at det er Aktør
            c,Actor.CustNo,2,10,0,,,,,,,,,I,	'Kundenummer
            c,Actor.Nm,3,40,0,,,,,,,,,S,	'Kundenavn
            c,Actor.Ad1,4,40,0,,,,,,,,,S,	'Kunde adresse 1
            c,Actor.PNo,5,10,0,,,,,,,,,S,	'Kunde Postnummer
            c,Actor.PArea,6,40,0,,,,,,,,,S,	'Kunde Poststed
            c,Actor.MailAd,7,40,0,,,,,,,,,S,	'Kunde e-post
            c,Actor.MobPh,8,40,0,,,,,,,,,S,	'Kunde mobil tlf.
            c,Actor.BsNo,9,40,0,,,,,,,,,S,	'Kunde Org nr el fødselsnr.
            c,Actor.cVatNo,10,40,0,,,,,,,,,S,	'Kunde avgiftskode hvis tom = mva pliktig
            c,Actor.CPmtTrm,11,40,0,,,,,,,,,S,	'Kunde Betalingsbetingelse
            c,Actor.CPmtMt,12,40,0,,,,,,,,,S,	'Kunde Betalingsmåte
        */
        
        if(user.fullName == null) {
            user.fullName = "";
        }
        if(user.address == null) {
            user.address = new Address();
        }
        if(user.address.address == null) {
            user.address.address = "";
        }
        if(user.address.postCode == null) {
            user.address.postCode = "";
        }
        if(user.address.city == null) {
            user.address.city = "";
        }
        if(user.emailAddress == null) {
            user.emailAddress = "";
        }
        if(user.cellPhone == null) {
            user.cellPhone = "";
        }
        if(user.birthDay == null) {
            user.birthDay = "";
        }
        
        String result = "A;"; //Fast A som forteller at det er Aktør
        result += user.customerId + ";"; //Kundenummer
        result += user.fullName + ";"; //Kundenavn
        result += user.address.address + ";"; //Kunde adresse 1
        result += user.address.postCode + ";"; //Kunde Postnummer
        result += user.address.city + ";"; //Kunde Poststed
        result += user.emailAddress + ";"; //Kunde e-post
        result += user.cellPhone + ";"; //Kunde mobil tlf.
        
        if(user.isPrivatePerson) {
            Date date = null;
            try {
                if(user.birthDay.contains(".")) {
                    SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yy"); 
                    date = dt.parse(user.birthDay); 
                } else {
                    if(user.birthDay.length() == 6) {
                        SimpleDateFormat dt = new SimpleDateFormat("ddMMyy"); 
                        date = dt.parse(user.birthDay);
                    } else {
                        SimpleDateFormat dt = new SimpleDateFormat("ddMMyy"); 
                        date = dt.parse(user.birthDay);
                    }
                }
            }catch(Exception e) {
                throw new RuntimeException(e);
            }
            result += new SimpleDateFormat("ddMMyyyy").format(date) + ";"; //Kunde Org nr el fødselsnr.
        } else {
            result += user.birthDay + ";"; //Kunde Org nr el fødselsnr.
        }
        if(user.mvaRegistered) {
            result += "1;"; //Kunde avgiftskode hvis tom = mva pliktig
        } else {
            result += ";"; //ingen avgift.
        }
        result += "30;"; //Betaling per 30 dag.
        result += "10;"; //Autogiro.
        
        return result;
    }
}


package com.thundashop.core.accountingmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ForStore;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;

@ForStore(storeId="123865ea-3232-4b3b-9136-7df23cf896c6")
public class WilhelmsenHouse implements AccountingInterface {

    private UserManager userManager;
    private InvoiceManager invoiceManager;
    private OrderManager orderManager;

    @Override
    public void setUserManager(UserManager manager) {
        this.userManager = manager;
    }

    @Override
    public List<String> createUserFile(List<User> users) {
        List<String> lines = new ArrayList();
        for(User user : users) {
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

           if(user.company.isEmpty()) {
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
                   result += new SimpleDateFormat("ddMMyyyy").format(date) + ";"; //Kunde Org nr el fødselsnr.
               }catch(Exception e) {
                   result += ";"; //Kunde Org nr el fødselsnr.
               }
           } else {
               result += user.birthDay + ";"; //Kunde Org nr el fødselsnr.
           }
           if(user.companyObject != null && user.companyObject.vatRegisterd) {
               result += "1;"; //Kunde avgiftskode hvis tom = mva pliktig
           } else {
               result += ";"; //ingen avgift.
           }
           result += "30;"; //Betaling per 30 dag.
           result += "10;"; //Autogiro.

           lines.add(result + "\r\n");
        }
        return lines;
    }

    @Override
    public List<String> createOrderFile(List<Order> orders, String type) {
        if(type != null && type.equals("invoice")) {
            return createInvoiceFile(orders);
        } else {
            return createCreditCardFile(orders);
        }
    }
    
    private void savePdfInvoice(Order order) throws Exception {
        String base64 = invoiceManager.getBase64EncodedInvoice(order.id);

        byte[] bytes = Base64.decodeBase64(base64);
        String realName = order.incrementOrderId+".pdf";
        String targetFile = "/tmp/"+realName;
        Files.write(Paths.get(targetFile), bytes);
    }
    
    public List<String> createCreditCardFile(List<Order> orders) {
        
        if (orders.isEmpty()) {
            return new ArrayList();
        }
        
        List<String> lines = new ArrayList();
        
        String result = "";        
        result += "H;";
        result += new SimpleDateFormat("yyyyMMdd").format(new Date())+";";
        result += "5;"; // 
        result += "Overføring fra GetShop\r\n";
        lines.add(result);
        
        for (Order order : orders) {
            result = "";
            User user = userManager.getUserById(order.userId);
            if(user == null || user.customerId == null) {
                continue;
            }
            String textDesc = "GetShop order: "+ order.incrementOrderId; 
            result += "L;"; //Fixed value L
            result += order.incrementOrderId+";"; // Voucher no (If Voucher serie no is used,this can be empty)
            result += new SimpleDateFormat("yyyyMMdd").format(new Date())+";"; // Voucher date
            result += ";"; // Value date (Usees from Batch if empty)
            if(!order.isCreditNote) {
                result += user.customerId+";"; // Debit account
                result += ";"; // Credit account
            } else {
                result += ";"; // Debit account
                result += user.customerId+";"; // Credit account
            }
            double amount = orderManager.getTotalAmount(order);
            if(amount < 0) {
                amount *= -1;
            }
            result += amount+";"; // Total amount
            result += ";"; // R3 Oppdrag ID
            result += ";"+textDesc; // LinjeText hvis nødvendig. f.eks Salg, Betaling, Refnr fra FV. Hvis tomt, hentes tekst fra bilagsart.
            result += ";"+ order.paymentTransactionId; // LinjeText hvis nødvendig. f.eks Salg, Betaling, Refnr fra FV. Hvis tomt, hentes tekst fra bilagsart.
            lines.add(result+"\r\n");

            for (CartItem item : order.cart.getItems()) {
                result = "";
                result += "L;"; //Fixed value L
                result += order.incrementOrderId+";"; // Voucher no (If Voucher serie no is used,this can be empty)
                result += new SimpleDateFormat("yyyyMMdd").format(new Date())+";"; // Voucher date
                result += ";"; // Value date (Usees from Batch if empty)
                if(!order.isCreditNote) {
                    result += ";"; // Debit account
                    result += "3000;"; // Credit account
                } else {
                    result += "3000;"; // Debit account
                    result += ";"; // Credit account
                }
                
                amount = item.getProduct().priceExTaxes;
                if(amount < 0) {
                    amount *= -1;
                }
                
                result += amount+";"; // Total amount
                result += ";"; // R3 Oppdrag ID
                String roomName = item.getProduct().additionalMetaData;
                result += roomName +";"; // R4 Gjenstand ID
                result += textDesc;
                lines.add(result+"\r\n");
            }
            
            try {
                savePdfInvoice(order);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        return lines;
                
    }
    
    public List<String> createInvoiceFile(List<Order> orders) {
        List<String> result = new ArrayList();
        for(Order order : orders) {
            System.out.print("Transferring: " + order.incrementOrderId);
            User user = userManager.getUserById(order.userId);
            
            
            Date startedDate = order.getStartDateByItems();
            
            String ordrehode = "H;"; // Fast H
            ordrehode += "1;"; // Fast 1 for salg
            ordrehode += "1;"; // Fast 1 for normalordre
            ordrehode += order.incrementOrderId + ";"; // GetShop ordre id
            ordrehode += user.customerId + ";"; // Kundenr 
            ordrehode += new SimpleDateFormat("yyyyMMdd").format(order.createdDate) + ";"; // Ordredato
            if(startedDate != null) {
                ordrehode += new SimpleDateFormat("yyyyMMdd").format(startedDate)+ ";"; // Leveringsdato
            }            
            if (order.payment != null && order.payment.paymentType.equals("ns_565ea7bd_c56b_41fe_b421_18f873c63a8f\\PayOnDelivery")) {
                ordrehode += "1;"; //Betalingsbetingelse
                ordrehode += "1;"; //Betalingsmåte
            } else if (order.payment != null && order.payment.paymentType.equals("ns_92bd796f_758e_4e03_bece_7d2dbfa40d7a\\ExpediaPayment")) {
                ordrehode += "14;"; //Betalingsbetingelse
                ordrehode += "30;"; //Betalingsmåte
            } else if (order.payment != null && order.payment.paymentType.equals("ns_639164bc_37f2_11e6_ac61_9e71128cae77\\AirBNBCollect")) {
                ordrehode += "14;"; //Betalingsbetingelse
                ordrehode += "40;"; //Betalingsmåte
            } else {
                ordrehode += "14;"; //Betalingsbetingelse
                ordrehode += "20;"; //Betalingsmåte ( 10 = avtalegiro )
            }
            ordrehode += ";"; // avgiftskode ( tom = bruk fra kunde )
            result.add(ordrehode +"\r\n");

            for (CartItem item : order.cart.getItems()) {
                if(item.getProduct() == null || item.getProduct().accountingSystemId == null) {
                    continue;
                }
                Integer vismaId = new Integer(item.getProduct().accountingSystemId);
                String orderline = "L;"; // Fast L for orderline
                orderline += vismaId + ";"; // ProdNO
                orderline += ";"; // Avgiftskode ( hentes fra kunden )
                orderline += createLineText(item) + ";"; // Produkt beskrivelse
                orderline += item.getCount() + ";"; // Antall mnder
                orderline += item.getProduct().priceExTaxes+ ";"; // Pris pr antall, hvis blank hentes pris fra Visma
                orderline += ";"; // ikke i bruk
                String roomName = item.getProduct().additionalMetaData;
                orderline += roomName + ";"; // R4 Gjenstand ID
                orderline += ";"; // 
                result.add(orderline+"\r\n");
            }
            
            if(order.invoiceNote.trim() != null && !order.invoiceNote.trim().isEmpty()) {
                String orderline = "L;"; // Fast L for orderline
                orderline += ";"; // ProdNO
                orderline += ";"; // Avgiftskode ( hentes fra kunden )
                orderline += order.invoiceNote.trim() + ";"; // Produkt beskrivelse
                orderline += ";"; // Antall mnder
                orderline += ";"; // Pris pr antall, hvis blank hentes pris fra Visma
                orderline += ";"; // ikke i bruk
                orderline += ";"; // R4 Gjenstand ID
                orderline += ";"; // 
                result.add(orderline+"\r\n");                
            }
            
            System.out.println(" - done.");            
        }
        return result;
    }
    
    private String createLineText(CartItem item) {
        String lineText = "";
        String startDate = "";
        if(item.startDate != null) {
            DateTime start = new DateTime(item.startDate);
            startDate = start.toString("dd.MM.yy");
        }

        String endDate = "";
        if(item.endDate != null) {
            DateTime end = new DateTime(item.endDate);
            endDate = end.toString("dd.MM.yy");
        }
        
        if(!item.getProduct().additionalMetaData.isEmpty()) {
            lineText = item.getProduct().name + " " + item.getProduct().additionalMetaData + " (" + startDate + " - " + endDate + ")";
        } else {
            lineText = item.getProduct().name + " " + item.getProduct().metaData + " (" + startDate + " - " + endDate + ")";
        }
    
         try {
             lineText = new String(lineText.getBytes("ISO-8859-1"),"UTF-8");
         }catch(Exception e) {
             e.printStackTrace();
         }
         
        return lineText;
    }

    @Override
    public void setInvoiceManager(InvoiceManager manager) {
        this.invoiceManager = manager;
    }

    @Override
    public void setOrderManager(OrderManager manager) {
        this.orderManager = manager;
    }
}

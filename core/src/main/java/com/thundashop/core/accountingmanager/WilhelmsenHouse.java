
package com.thundashop.core.accountingmanager;

import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ForStore;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.productmanager.data.Product;
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
    private StoreApplicationPool storeApplicationPool;
    private AccountingManagers managers;

    @Override
    public void setUserManager(UserManager manager) {
        this.userManager = manager;
    }

    @Override
    public List<String> createUserFile(List<User> users) {
        users.add(userManager.getUserById("a775ffcb-d3c4-469d-a980-a09f10144d2e")); //Dibs user.
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
               c,Actor.DocSmt,12,40,0,,,,,,,,,S,	'Kunde sendemåte 3 hvis epost på kunden
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
           if(user.cellPhone == null || user.cellPhone.contains("+")) {
               user.cellPhone = "";
           }
           if(user.birthDay == null) {
               user.birthDay = "";
           }

           String result = "A;"; //Fast A som forteller at det er Aktør
           result += user.customerId + ";"; //Kundenummer
           result += removeSemiColon(user.fullName) + ";"; //Kundenavn
           result += removeSemiColon(user.address.address) + ";"; //Kunde adresse 1
           result += removeSemiColon(user.address.postCode) + ";"; //Kunde Postnummer
           result += removeSemiColon(user.address.city) + ";"; //Kunde Poststed
           result += removeSemiColon(user.emailAddress) + ";"; //Kunde e-post
           result += removeSemiColon(user.cellPhone) + ";"; //Kunde mobil tlf.

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
               result += ";"; //Kunde avgiftskode hvis tom = mva pliktig
           } else {
               result += ";"; //ingen avgift.
           }
           result += "30;"; //Betaling per 30 dag.
           result += "10;"; //Autogiro.
           if(user.emailAddress != null && user.emailAddress.contains("@")) {
               result += "3;";
           } else {
               result += "0;";
           }

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
    
    private String getProductDebitNumber(Product product, Application app) {
        return managers.productManager.getProduct(product.id).accountingAccount;
    }

    
    public List<String> createCreditCardFile(List<Order> orders) {
        
        if (orders.isEmpty()) {
            return new ArrayList();
        }
        
        List<String> lines = new ArrayList();
        double debet = 0;
        double credit = 0;
        Application app = storeApplicationPool.getApplication("37d409be-1207-45e8-bf3b-6465442b58d9");
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
            double amount = orderManager.getTotalAmount(order);
            if(order.incrementOrderId == 108605) {
                System.out.println("found");
            }
            amount = (double)Math.round(amount * 100) / 100.0;
            boolean doCredit = false;
            if(amount < 0) {
                doCredit = true;
                amount *= -1;
            }
            if(!doCredit) {
                result += "18071;"; // Debit account
                result += ";"; // Credit account
                debet += amount;
            } else {
                result += ";"; // Debit account
                result += "18071;"; // Credit account
                credit += amount;
            }
            result += amount+";"; // Total amount
            result += ";"; // R3 Oppdrag ID
            result += ";"+textDesc; // LinjeText hvis nødvendig. f.eks Salg, Betaling, Refnr fra FV. Hvis tomt, hentes tekst fra bilagsart.
            result += ";"; // LinjeText hvis nødvendig. f.eks Salg, Betaling, Refnr fra FV. Hvis tomt, hentes tekst fra bilagsart.
            lines.add(result+"\r\n");

            for (CartItem item : order.cart.getItems()) {
                result = "";
                result += "L;"; //Fixed value L
                result += order.incrementOrderId+";"; // Voucher no (If Voucher serie no is used,this can be empty)
                result += new SimpleDateFormat("yyyyMMdd").format(new Date())+";"; // Voucher date
                result += ";"; // Value date (Usees from Batch if empty)
                
                amount = item.getProduct().price * item.getCount();
                amount = Math.round(amount * 100) / 100.0;
                doCredit = false;
                if(amount < 0) {
                    amount *= -1;
                    doCredit = true;
                }
                if(!doCredit) {
                    result += ";"; // Debit account
                    result += getProductDebitNumber(item.getProduct(), app) +";"; // Credit account
                    credit += amount;
                } else {
                    result += getProductDebitNumber(item.getProduct(), app) + ";"; // Debit account
                    result += ";"; // Credit account
                    debet += amount;
                }
                
                
                result += amount+";"; // Total amount
                result += ";"; // R3 Oppdrag ID
                String roomName = item.getProduct().additionalMetaData;
                result += roomName +";"; // R4 Gjenstand ID
                result += textDesc;
                result += ";";
                lines.add(result+"\r\n");
            }
            try {
                savePdfInvoice(order);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        double diff = debet - credit;
        System.out.println("failed on : " +  " " + debet + " - " + credit + " = " + diff);

        return lines;
                
    }
    
    public List<String> createInvoiceFile(List<Order> orders) {
        List<String> result = new ArrayList();
        for(Order order : orders) {
            
            if (order.payment != null && order.payment.paymentType.equals("ns_92bd796f_758e_4e03_bece_7d2dbfa40d7a\\ExpediaPayment")) {
                if(order.getEndDateByItems() != null && order.getEndDateByItems().after(new Date())) {
                    continue;
                }
            }
            
            User user = userManager.getUserById(order.userId);
            
            
            Date startedDate = order.getStartDateByItems();
            
            String ordrehode = "H;"; // Fast H
            ordrehode += "1;"; // Fast 1 for salg
            ordrehode += "1;"; // Fast 1 for normalordre
            ordrehode += order.incrementOrderId + ";"; // GetShop ordre id
            if (order.payment != null && order.payment.paymentType.equals("ns_92bd796f_758e_4e03_bece_7d2dbfa40d7a\\ExpediaPayment")) {
                ordrehode += "11072;"; // Expedia customer id.
            } else {
                ordrehode += user.customerId + ";"; // Kundenr 
            }
            ordrehode += new SimpleDateFormat("yyyyMMdd").format(order.createdDate) + ";"; // Ordredato
            if(startedDate != null) {
                ordrehode += new SimpleDateFormat("yyyyMMdd").format(startedDate)+ ";"; // Leveringsdato
            }            
            if (order.payment != null && order.payment.paymentType.equals("ns_565ea7bd_c56b_41fe_b421_18f873c63a8f\\PayOnDelivery")) {
                ordrehode += "1;"; //Betalingsbetingelse
                ordrehode += "1;"; //Betalingsmåte
            } else if (order.payment != null && order.payment.paymentType.equals("ns_92bd796f_758e_4e03_bece_7d2dbfa40d7a\\ExpediaPayment")) {
                ordrehode += "30;"; //Betalingsbetingelse
                ordrehode += "30;"; //Betalingsmåte
            } else if (order.payment != null && order.payment.paymentType.equals("ns_639164bc_37f2_11e6_ac61_9e71128cae77\\AirBNBCollect")) {
                ordrehode += "14;"; //Betalingsbetingelse
                ordrehode += "40;"; //Betalingsmåte
            } else {
                ordrehode += "14;"; //Betalingsbetingelse
                ordrehode += "20;"; //Betalingsmåte ( 10 = avtalegiro )
            }
            ordrehode += order.incrementOrderId + ";";
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
                orderline += createLineText(item, order) + ";"; // Produkt beskrivelse
                orderline += item.getCount() + ";"; // Antall mnder
                orderline += Math.round(item.getProduct().priceExTaxes*100)/100.0+ ";"; // Pris pr antall, hvis blank hentes pris fra Visma
                orderline += ";"; // ikke i bruk
                String roomName = item.getProduct().additionalMetaData;
                orderline += roomName + ";"; // R4 Gjenstand ID
                orderline += ";"; // 
                result.add(orderline+"\r\n");
            }
            
            System.out.println(" - done.");
        }

        return result;
    }
    
    private String createLineText(CartItem item, Order order) {
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
        
        String startEnd = "";
        if(startDate != null && endDate != null && !endDate.isEmpty() && !startDate.isEmpty()) {
            startEnd = " (" + startDate + " - " + endDate + ")";
        }
        
        if(!item.getProduct().additionalMetaData.isEmpty()) {
            lineText = item.getProduct().name + " " + item.getProduct().additionalMetaData + startEnd;
        } else {
            String mdata = item.getProduct().metaData;
            if(mdata != null && mdata.startsWith("114")) {
                mdata = "";
            } else {
                mdata = ", " + mdata;
            }
            lineText = item.getProduct().name + mdata + startEnd;
        }
        
        lineText = lineText.replace("Dobbeltrom", "");
        lineText = lineText.replace("Kjøkken", "kj. ");
        lineText = lineText.replace("kjøkken", "kj. ");
        lineText = lineText.replace("Standard rom med minikjøkken", "Stdrom med minikj.");
        lineText = lineText.trim();
    
        if(order.invoiceNote != null && !order.invoiceNote.isEmpty()) {
            lineText = "(x)" + lineText;
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

    @Override
    public void setStoreApplication(StoreApplicationPool manager) {
        this.storeApplicationPool = manager;
    }

    @Override
    public void setManagers(AccountingManagers mgr) {
        this.managers = mgr;
    }

    private String removeSemiColon(String fullName) {
        if(fullName == null || fullName.isEmpty()) {
            return fullName;
        }
        
        return fullName.replaceAll(";", "");
    }

    
}

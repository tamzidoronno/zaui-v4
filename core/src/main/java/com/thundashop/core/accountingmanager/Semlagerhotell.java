
package com.thundashop.core.accountingmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ForStore;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joda.time.DateTime;

/**
 *      RecType         1   'Fast A som forteller at det er Aktør
 * c	Actor.CustNo	2   'Kundenummer
 * c	Actor.Nm	3   'Kundenavn
 * c	Actor.Ad1	4   'Kunde adresse 1
 * c	Actor.PNo	5   'Kunde Postnummer
 * c	Actor.PArea	6   'Kunde Poststed
 * c	Actor.MailAd	7   'Kunde e-post
 * c	Actor.MobPh	8   'Kunde mobil tlf.
 * c	Actor.BsNo	9   'Kunde Org nr el fødselsnr.
 * c	Actor.cVatNo	10  'Kunde avgiftskode hvis tom = mva pliktig
 * c	Actor.CPmtTrm	11  'Kunde Betalingsbetingelse
 * c	Actor.CPmtMt	12  'Kunde Betalingsmåte
 * 						
 * b	OrderHead	1   OrderHead	1				
 * c	RecType         1   'Fast H for ordreHode
 * c	Ord.TrTp	2   'Fast 1 for salg
 * c	Ord.OrdTp	3   Fast 1 for normalordre
 * c	Ord.CSOrdNo	4   'Ordrenr fra GetShop
 * c	Ord.CustNo	5   'Kundenr
 * c	Ord.OrdDt	6   'Ordredato på formen YYYYMMDD
 * c	Ord.DelDt	7   'Leveringsdato (ønsket start dato), YYYYMMDD
 * c	Ord.PmtTrm	8   'Betalingsbetingelse
 * c	Ord.PmtMt	9   'Betalingsmåte 
 * c	Ord.ExVat	10  'Avgiftskode hvis tom benyttes fra kunde
 * c	HeadStatus	11	
 * 				
 * b	OrderLine	1	
 * c	RecType         1   'Fast L for ordreLinje
 * c	OrdLn.ProdNo	2   'Produktnr
 * c	OrdLn.VatNo	3   'Avgiftskode
 * c	OrdLn.Descr	4   'Produktbeskrivelse
 * c	OrdLn.NoInvoAb	5   'Antall mnd
 * c	OrdLn.Price	6   'Pris pr antall
 * c	OrdLn.R1	7   'Ansvarsenhet 1 Avdeling (ikke i bruk)
 * c	OrdLn.R2	8   'Ansvarsenhet 2 Bod ID
 * c	OrdLn.R3	9   'Ansvarsenhet 3
 * c	LineStatus	10	
 * 
 */

@ForStore(storeId="c444ff66-8df2-4cbb-8bbe-dc1587ea00b7")
public class Semlagerhotell implements AccountingInterface {

    private UserManager userManager;

    @Override
    public void setUserManager(UserManager manager) {
        this.userManager = manager;
    }

    @Override
    public List<String> createUserFile(List<User> users) {
        List<String> result = new ArrayList<String>();
        for(User user : users) {
            HashMap<Integer, String> line = new HashMap();
            line.put(1, "A");
            line.put(2, user.customerId + "");
            line.put(3, user.fullName);
            if(user.address == null) {
                line.put(4, "");
                line.put(5, "");
                line.put(6, "");
            } else {
                line.put(4, user.address.address);
                line.put(5, user.address.postCode);
                line.put(6, user.address.city);
            }
            line.put(7, user.emailAddress);
            if(user.emailAddressToInvoice != null && !user.emailAddressToInvoice.isEmpty()) {
                line.put(7, user.emailAddressToInvoice);
            }
            line.put(8, user.cellPhone);
            line.put(9, user.birthDay);
            if(!user.company.isEmpty()) {
                Company comp = userManager.getCompany(user.company.get(0));
                line.put(9, comp.vatNumber);
                if(comp.vatRegisterd) {
                    line.put(10, "1");
                }
            } else {
                line.put(10, "");
            }
            line.put(11, "30"); //14 dagers forfall
            line.put(12, "10"); //Autogiro
            String commaLine = makeLine(line);
            result.add(commaLine);
        }
        return result;
    }

    @Override
    public List<String> createOrderFile(List<Order> orders) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        List<String> result = new ArrayList<String>();
        for(Order order : orders) {
            HashMap<Integer, String> ordreHode = new HashMap();
            ordreHode.put(1, "H");
            ordreHode.put(2, "1");
            ordreHode.put(3, "1");
            ordreHode.put(4, order.incrementOrderId + "");
            ordreHode.put(5, userManager.getUserById(order.userId).customerId + "");
            ordreHode.put(6, format1.format(order.rowCreatedDate));
            if(order.startDate != null) {
                ordreHode.put(7, format1.format(order.startDate));
            }
            ordreHode.put(8, "0"); //Betalingsbetingelse 14 dager?
            ordreHode.put(9, "10"); //Betalingsmåte, usikker her
            ordreHode.put(10, ""); //Avgiftskode, usikker her.
            ordreHode.put(11, "");
            
            result.add(makeLine(ordreHode));
            
            for(CartItem item : order.cart.getItems()) {
                HashMap<Integer, String> orderLine = new HashMap();
                orderLine.put(1, "L");
                if(userManager.getUserById(order.userId).company.size() > 0) {
                    orderLine.put(2, "200");
                } else {
                    orderLine.put(2, "100");
                }
                orderLine.put(3, ""); //Avgiftskode (hentes fra kunde)
                
                String metatekst = createLineText(item);
                orderLine.put(4, metatekst);
                orderLine.put(5, item.getCount()+ "");
                orderLine.put(5, item.getCount() + "");
                orderLine.put(6, item.getProduct().price + "");
                orderLine.put(7, "");
                orderLine.put(8, item.getProduct().additionalMetaData);
                orderLine.put(9, "");
                orderLine.put(10, "");
                result.add(makeLine(orderLine));
            }
        }
        return result;
    }

    private String makeLine(HashMap<Integer, String> line) {
        String result = "";
        for(Integer i = 1; i <= line.size(); i++) {
            String toAdd = line.get(i);
            if(toAdd == null) {
                toAdd = "";
            }
            toAdd = toAdd.replace(",", "");
            result += toAdd;
            if(i != line.size()) {
                result += ";";
            }
        }
        return result + "\r\n";
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
            lineText = item.getProduct().name + ", " + item.getProduct().additionalMetaData + " (" + startDate + " - " + endDate + ")";
        } else {
            lineText = item.getProduct().name + " (" + startDate + " - " + endDate + ")";
        }
        return lineText;
    }    
}

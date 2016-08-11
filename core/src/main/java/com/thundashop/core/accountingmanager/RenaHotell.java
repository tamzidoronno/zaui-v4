package com.thundashop.core.accountingmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ForStore;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joda.time.DateTime;

/**
 * Documentation found in google disk under file renahotell_import.pdf
 * @author boggi
 */
@ForStore(storeId="87cdfab5-db67-4716-bef8-fcd1f55b770b")
public class RenaHotell implements AccountingInterface {

    private UserManager userManager;

    @Override
    public void setUserManager(UserManager manager) {
        this.userManager = manager;
    }

    @Override
    public List<String> createUserFile(List<User> users) {
        return new ArrayList();
    }

    @Override
    public List<String> createOrderFile(List<Order> orders) {
        
        SimpleDateFormat format1 = new SimpleDateFormat("yyMMdd");
        List<String> result = new ArrayList();
        for(Order order : orders) {
            User user = userManager.getUserById(order.userId);
            for(CartItem item : order.cart.getItems()) {
                HashMap<Integer, String> fieldsInLine = new HashMap();
                fieldsInLine.put(1, "97");
                fieldsInLine.put(2, format1.format(order.rowCreatedDate));
                fieldsInLine.put(3, order.incrementOrderId + "");
                fieldsInLine.put(4, format1.format(order.rowCreatedDate));
                fieldsInLine.put(5, "45");
                
                String lineText = createLineText(item);
                
                fieldsInLine.put(6, lineText);
                fieldsInLine.put(8, order.invoiceNote);
                String account = item.getProduct().sku;
                String mvaKode = item.getProduct().accountingSystemId;
                
                if(order.payment.paymentType.toLowerCase().contains("invoice")) {
                    account = user.customerId + "";
                }
                
                int count = item.getCount();
                
                if(count > 0) {
                    fieldsInLine.put(9, account); //Debet konto frokost
                } else {
                    count = count * -1;
                    fieldsInLine.put(10, account); //Kredit konto frokost
                }
                
                Address address = user.address;
                if(address.address2 == null) { address.address2="";}
                if(address.address == null) { address.address="";}
                if(address.city == null) { address.city="";}
                if(address.postCode == null) { address.postCode="";}
                
                double price = item.getProduct().priceExTaxes;
                DecimalFormat df = new DecimalFormat("#.##");      
                price = Double.valueOf(df.format(price)); 
                if(price < 0) {
                    price *= -1;
                }
                
                fieldsInLine.put(11, mvaKode);
                fieldsInLine.put(12, "000");
                fieldsInLine.put(15, price + "");
                fieldsInLine.put(17, format1.format(order.rowCreatedDate));
                fieldsInLine.put(19, count + "");
                fieldsInLine.put(21, user.customerId + "");
                fieldsInLine.put(23, user.fullName + "");
                fieldsInLine.put(24, address.address + "");
                fieldsInLine.put(25, address.address2 + "");
                fieldsInLine.put(26, address.postCode + "");
                fieldsInLine.put(27, user.address.city + "");
                fieldsInLine.put(29, user.cellPhone + "");
                String line = createLine(fieldsInLine);
                result.add(line);
            }
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
    
    private String createLine(HashMap<Integer, String> toAdd) {
        String resultLine = "";
            for(int i = 1; i <= 46; i++) {
                String entry = "";
                if(toAdd.containsKey(i)) {
                    String text = toAdd.get(i);
                    if(text == null) {
                        GetShopLogHandler.logPrintStatic("Null on: " + i, "null");
                    }
                    text = text.replaceAll(",", "");
                    text = text.replaceAll("\n", "");
                    resultLine += text + ",";
                } else {
                    resultLine += ",";
                }
            }
            resultLine += "\r\n";
            return resultLine;
    }

    private boolean isBreakFast(CartItem item) {
        return false;
    }
    
}

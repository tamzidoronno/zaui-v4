/*
 */
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

@ForStore(storeId="7b21932d-26ad-40a5-b3b6-c182f5ee4b2f")
public class Hybelhotell implements AccountingInterface {
    
    UserManager userManager;
    
    @Override
    public List<String> createUserFile(List<User> users) {
        List<String> result = new ArrayList();
        for(User user : users) {
            if(user.fullName == null || user.fullName.trim().isEmpty()) {
                continue;
            }
            if(user.isCustomer()) {
                String line = createUserLine(user);
                result.add(line);
            }
        }
        return result;
    }
    
    @Override
    public List<String> createOrderFile(List<Order> allOrders) {
        List<String> allOrdersToReturn = new ArrayList();
        for(Order order : allOrders) {
            List<String> lines = createOrderLine(order);
            allOrdersToReturn.addAll(lines);
        }
        return allOrdersToReturn;
    }

    
    private List<String> createOrderLine(Order order) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyymmdd");

        List<String> allLines = new ArrayList();
        int linenumber = 1;
        for(CartItem item : order.cart.getItems()) {
            User user = userManager.getUserById(order.userId);
            HashMap<Integer, String> toAdd = new HashMap();
            
            String lineText = createLineText(item);
            
            toAdd.put(3, order.incrementOrderId + "");
            toAdd.put(4, order.incrementOrderId + "");
            toAdd.put(5, linenumber + "");
            toAdd.put(6, format1.format(order.rowCreatedDate));
            toAdd.put(7, user.customerId + "");
            toAdd.put(16, "NOK");
            toAdd.put(17, "100"); //Product code.
            toAdd.put(19, lineText);
            toAdd.put(22, "døgn");
            toAdd.put(23, item.getCount() + "");
            toAdd.put(25, item.getProduct().priceExTaxes+ "");
            toAdd.put(28, "22N");
            toAdd.put(32, order.invoiceNote);
            
            String resultLine = "";
            for(int i = 0; i < 60; i++) {
                String entry = "";
                if(toAdd.containsKey(i)) {
                    String text = toAdd.get(i);
                    text = text.replaceAll(";", "");
                    text = text.replaceAll("\n", "");
                    resultLine += text + ";";
                }
                resultLine += entry + ";";
            }
            resultLine += "EOL\r\n";
            allLines.add(resultLine);
            linenumber++;
        }
        return allLines;
    }
    
    private String createUserLine(User user) {
         HashMap<Integer, String> toAdd = new HashMap();
         toAdd.put(4, user.customerId + "");
         if(!user.company.isEmpty()) {
            Company company = userManager.getCompany(user.company.get(0));
            toAdd.put(6, company.vatNumber);
            toAdd.put(7, company.name);
            if(company.address != null) {
                toAdd.put(74, company.address.address);
                toAdd.put(75, company.address.postCode);
                toAdd.put(76, company.address.city);
            }
            if(company.invoiceEmail != null && !company.invoiceEmail.trim().isEmpty()) {
                toAdd.put(10, company.invoiceEmail.trim());
            } else {
                toAdd.put(10, company.email);
            }
        } else {
            toAdd.put(7, user.fullName);
            if(user.address != null) {
                toAdd.put(74, user.address.address);
                toAdd.put(75, user.address.postCode);
                toAdd.put(76, user.address.city);
            }
            toAdd.put(10, user.emailAddress);
         }
         toAdd.put(33, "NOK");
         
         String result = "";
         for(int i = 0; i < 90; i++) {
             if(toAdd.containsKey(i)) {
                    String text = toAdd.get(i);
                    text = text.replaceAll(";", "");
                    text = text.replaceAll("\n", "");
                    result += text + ";";
             } else {
                 result += ";";
             }
         }
         result += "EOL\r\n";
         
         return result;
    }

    @Override
    public void setUserManager(UserManager manager) {
        this.userManager = manager;
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

}

/*
 * Transfer orders to xledger.
 */
package com.thundashop.core.xledgermanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class XLedgerManager extends ManagerBase implements IXLedgerManager {

    @Autowired
    OrderManager orderManager;

    @Autowired
    UserManager userManager;

    @Autowired
    CartManager cartManager;
    
    @Override
    public List<String> createUserFile() {
        List<User> users = userManager.getAllUsers();
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
    public List<String> createOrderFile() {
        List<String> allOrdersToReturn = new ArrayList();
        List<Order> allOrders = orderManager.getOrders(null, null, null);
        for(Order order : allOrders) {
            if(order.status == Order.Status.SEND_TO_INVOICE) {
                List<String> lines = createOrderLine(order);
                allOrdersToReturn.addAll(lines);
            }
        }
        return allOrdersToReturn;
    }

    private List<String> createOrderLine(Order order) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyymmdd");
        List<String> allLines = new ArrayList();
        int linenumber = 0;
        for(CartItem item : order.cart.getItems()) {
            Days periods = Days.daysBetween(new LocalDate(item.startDate), new LocalDate(item.endDate));
            User user = userManager.getUserById(order.userId);
            Company company = null;
            if(!user.company.isEmpty()) {
                company = userManager.getCompany(user.company.get(0));
            }
            HashMap<Integer, String> toAdd = new HashMap();
            toAdd.put(3, user.customerId + "");
            toAdd.put(4, order.incrementOrderId + ""); //order number
            toAdd.put(5, linenumber + "");
            toAdd.put(6, format1.format(item.startDate));
            toAdd.put(7, user.customerId + "");
            toAdd.put(9, 1 + "");
            toAdd.put(16,"NOK");
            toAdd.put(17,"UKJENT");
            toAdd.put(19, order.invoiceNote);
            toAdd.put(23, item.getCount() + "");
            toAdd.put(24, item.getProduct().price + "");
            toAdd.put(38, order.paymentTerms + "");
            toAdd.put(39, 1 + "");
            
             if(company != null) {
                toAdd.put(34, company.name);
                toAdd.put(35, company.vatNumber);
                toAdd.put(40, company.address.address);
                toAdd.put(42, company.address.postCode);
                toAdd.put(43, company.address.city);
                
                toAdd.put(46, company.invoiceAddress.address);
                toAdd.put(48, company.invoiceAddress.postCode);
                toAdd.put(49, company.invoiceAddress.city);
            } else {
                toAdd.put(34, user.fullName);
                if(user.address != null) {
                    toAdd.put(40, user.address.address);
                    toAdd.put(42, user.address.postCode);
                    toAdd.put(43, user.address.city);
                }
            }
             
            String resultLine = "";
            for(int i = 0; i < 60; i++) {
                String entry = "";
                if(toAdd.containsKey(i)) {
                    entry = toAdd.get(i);
                }
                resultLine += entry + ";";
            }
            resultLine += "EOL";
            allLines.add(resultLine);
            System.out.println(resultLine);
            linenumber++;
        }
        return allLines;
    }
    
    private String getStay(CartItem item) {
        if (item.startDate == null || item.endDate == null) {
            return "";
        }
        
        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yy");
        String startDate = dt.format(item.startDate);
        String endDate = dt.format(item.endDate);
        return " ("+startDate+"-"+endDate+")";
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
                 result += toAdd.get(i) + ";";
             } else {
                 result += ";";
             }
         }
         result += "EOL\n\r";
         return result;
    }

}

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
    public List<String> createOrderFile() {
        List<String> allOrdersToReturn = new ArrayList();
        List<Order> allOrders = orderManager.getOrders(null, null, null);
        for(Order order : allOrders) {
            if(order.incrementOrderId == 100022) {
                List<String> lines = createOrderLine(order);
                allOrdersToReturn.addAll(lines);
            }
        }
        return allOrdersToReturn;
    }

    private List<String> createOrderLine(Order order) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyymmdd");
        List<String> allLines = new ArrayList();
        for(CartItem item : order.cart.getItems()) {
            Days periods = Days.daysBetween(new LocalDate(item.startDate), new LocalDate(item.endDate));
            User user = userManager.getUserById(order.userId);
            Company company = null;
            if(!user.company.isEmpty()) {
                company = userManager.getCompany(user.company.get(0));
            }
            HashMap<Integer, String> toAdd = new HashMap();
            toAdd.put(3, user.customerId + "");
            toAdd.put(4, "AP"); //Voucher type?
            toAdd.put(5, "1111"); //Voucher no?
            toAdd.put(9, "2400"); //Account?
            toAdd.put(14, order.invoiceNote);
            toAdd.put(15, format1.format(item.startDate)); //Periode start (int) hva er dette?
            toAdd.put(16, periods.getDays() + ""); //Er dette måneder? Dager, uke?
            toAdd.put(17, "Faktura"); //Hva skal stå på faktura.
            toAdd.put(23, format1.format(order.rowCreatedDate)); //Voucher date, order date?
            toAdd.put(24, order.incrementOrderId + "");
            
            LocalDate date = new LocalDate(order.rowCreatedDate);
            date.plusDays(user.invoiceDuePeriode);
            
            toAdd.put(25, format1.format(date.toDate()));
            toAdd.put(29, item.getProduct().name + " " + getStay(item));
            toAdd.put(31, "NOK");
            toAdd.put(32, (double)Math.round(item.getProduct().price * 100d) / 100d + "");
            toAdd.put(37, "1");
            
            if(company != null) {
                toAdd.put(39, company.vatNumber);
                toAdd.put(40, company.address.address);
                toAdd.put(42, company.address.postCode);
                toAdd.put(43, company.address.city);
                
                toAdd.put(46, company.invoiceAddress.address);
                toAdd.put(48, company.invoiceAddress.postCode);
                toAdd.put(49, company.invoiceAddress.city);
            } else {
                if(user.address != null) {
                    toAdd.put(40, user.address.address);
                    toAdd.put(42, user.address.postCode);
                    toAdd.put(43, user.address.city);
                }
            }
            
            toAdd.put(52, user.cellPhone);
            toAdd.put(53, user.emailAddress);
            
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

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.accountingmanager;

import com.ibm.icu.util.Calendar;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ForAccountingSystem;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joda.time.DateTime;

@ForAccountingSystem(accountingSystem="gbat10")
public class GBat10 extends AccountingTransferOptions implements AccountingTransferInterface {
    boolean hasFail = false;
    
    @Override
    public void setManagers(AccountingManagers managers) {
        this.managers = managers;
    }

    @Override
    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public void setConfig(AccountingTransferConfig config) {
        this.config = config;
    }

    @Override
    public SavedOrderFile generateFile() {
        checkTaxCodes();
        if(hasFail) {
            return null;
        }
        
        SavedOrderFile file = new SavedOrderFile();
        List<String> toPrint = new ArrayList();
        file.orders = new ArrayList();
        for(Order order : orders) {
            file.orders.add(order.id);
            List<HashMap<Integer, String>> lines = generateLine(order);
            for(HashMap<Integer, String> toConvert : lines) {
                String line = makeLine(toConvert);
                toPrint.add(line);
            }
        }
        file.result = toPrint;
        return file;
    }
    
    private String makeLine(HashMap<Integer, String> line) {
        String result = "";
        for(Integer i = 0; i <= line.size(); i++) {
            String toAdd = line.get(i);
            if(toAdd == null) {
                toAdd = "";
            }
            toAdd = toAdd.replace(";", "");
            result += toAdd;
            if(i != line.size()) {
                result += ";";
            }
        }
        return result + "\r\n";
    }


    private List<HashMap<Integer, String>> generateLine(Order order) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(order.rowCreatedDate);
        
        if(order.getStartDateByItems() != null) {
            cal.setTime(order.getStartDateByItems());
        }
        
        List<HashMap<Integer, String>> lines = new ArrayList();
        
        int firstMonth = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);
        Integer customerId = getAccountingId(order.userId);
        String unique = getUniqueCustomerIdForOrder(order);
        if(unique != null) {
            customerId = new Integer(unique);
        }
        Double total = managers.orderManager.getTotalAmount(order);
        DecimalFormat df = new DecimalFormat("#.##");    
        User user = managers.userManager.getUserById(order.userId);
        String kid = order.kid;
        if(kid == null) {
            kid = "";
        }
        HashMap<Integer, String> line = new HashMap();
        line.put(0, "GBAT10");
        line.put(1, order.incrementOrderId+ "");
        line.put(2, format.format(order.rowCreatedDate));
        line.put(3, "1");
        line.put(4, firstMonth + "");
        line.put(5, year + "");
        line.put(6, "1510");
        line.put(7, "1");
        line.put(8, df.format(total)+"");
        line.put(9, customerId+"");
        line.put(10, "");
        line.put(11, user.fullName);
        line.put(12, user.address.address);
        line.put(13, user.address.postCode);
        line.put(14, user.address.city);
        line.put(15, order.incrementOrderId + "");
        line.put(16, kid); //KID
        line.put(17, format.format(order.rowCreatedDate)); //Forfallsdato
        line.put(18, "");
        line.put(19, "");
        line.put(20, "GetShop order: " + order.incrementOrderId); //Forfallsdato
        line.put(21, order.payment.readablePaymentType()); //Forfallsdato
        
        lines.add(line);
        
        for(CartItem item : order.cart.getItems()) {
            HashMap<Integer, String> subLine = new HashMap();
            
            subLine.put(0, "GBAT10");
            subLine.put(1, order.incrementOrderId+ "");
            subLine.put(2, format.format(order.rowCreatedDate));
            subLine.put(3, "1");
            subLine.put(4, firstMonth + "");
            subLine.put(5, year + "");
            subLine.put(6, managers.productManager.getProduct(item.getProduct().id).accountingAccount);
            subLine.put(7, managers.productManager.getProduct(item.getProduct().id).sku);
            subLine.put(8, df.format(item.getProduct().price * item.getCount() * -1)+"");
            subLine.put(9, "");
            subLine.put(10, "");
            subLine.put(11, "");
            subLine.put(12, "");
            subLine.put(13, "");
            subLine.put(14, "");
            subLine.put(15, order.incrementOrderId + "");
            subLine.put(16, kid); //KID
            subLine.put(17, format.format(order.rowCreatedDate)); //Forfallsdato
            subLine.put(18, "");
            subLine.put(19, "");
            subLine.put(20, createLineText(item)); //Forfallsdato
            subLine.put(21, order.payment.readablePaymentType()); //Forfallsdato
            
            lines.add(subLine);

        }
        
        return lines;
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
        
        String startEnd = "";
        if(startDate != null && endDate != null && !endDate.isEmpty() && !startDate.isEmpty()) {
            startEnd = " (" + startDate + " - " + endDate + ")";
        }
        
        if(!item.getProduct().additionalMetaData.isEmpty()) {
            lineText = item.getProduct().name + " " + item.getProduct().additionalMetaData + startEnd;
        } else {
            String mdata = item.getProduct().metaData;
            mdata = ", " + mdata;
            lineText = item.getProduct().name + mdata + startEnd;
        }
        
        lineText = lineText.trim();
    
        return lineText;
    }

    private void checkTaxCodes() {
        for(Order order : orders) {
            for(CartItem item : order.cart.getItems()) {
                Product prod = managers.productManager.getProduct(item.getProduct().id);
                if(prod.sku == null || prod.sku.trim().isEmpty()) {
                    logError("Tax code not set for product: " + prod.name);
                    hasFail = true;
                }
            }
        }
    }

    private void logError(String string) {
        System.out.println(string);
    }

}

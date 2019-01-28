
package com.thundashop.core.accountingmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ForAccountingSystem;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ForAccountingSystem(accountingSystem="rubicon")
public class RubiCon extends AccountingTransferOptions implements AccountingTransferInterface {

    private List<User> users;
    private AccountingManagers managers;
    private List<Order> orders;
    private AccountingTransferConfig config;

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
    public SavedOrderFile generateFile() {
        List<String> result = new ArrayList();
        List<String> orderIds = new ArrayList();
         
        for(Order order : orders) {
            User user = managers.userManager.getUserById(order.userId);
            HashMap<Integer, String> fieldsInLine = createItemLine(null, order, user);
            String headerline = createLine(fieldsInLine);
            result.add(headerline);
            for(CartItem item : order.cart.getItems()) {
                fieldsInLine = createItemLine(item, order, user);
                String line = createLine(fieldsInLine);
                result.add(line);
            }
            orderIds.add(order.id);
            order.transferredToAccountingSystem = true;
            managers.orderManager.saveObject(order);
        }
        
        SavedOrderFile file = new SavedOrderFile();
        file.result = result;
        file.orders = orderIds;
        return file;
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
                    resultLine += text + ",";
                } else {
                    resultLine += ",";
                }
            }
            resultLine = resultLine.substring(0, resultLine.length()-1);
            resultLine += "\r\n";
            return resultLine;
    }
    
    private String prependZeros(String counter, int i) {
        int diff = i - counter.length();
        String toPrepend = "";
        for(i = 0; i < diff; i++) {
            toPrepend += "0";
        }
        return toPrepend + counter;
    }

    private String stripText(String text, int length) {
        if(text == null) {
            text = "";
        }
        text = text.replaceAll(",", "");
        text = text.replaceAll("\r", "");
        text = text.replaceAll("\n", "");
        text = text.replaceAll("\"", "");
        if(text.length() > length) {
            text = text.substring(0, length);
        }
        String padding = "";
        for(int i = text.length(); i < length; i++) {
            padding += " ";
        }
        return text + padding;
    }
    
    private HashMap<Integer, String> createItemLine(CartItem item, Order order, User user) {
        SimpleDateFormat format1 = new SimpleDateFormat("ddMMyy");
        HashMap<Integer, String> fieldsInLine = new HashMap();
        fieldsInLine.put(1, "97");
        fieldsInLine.put(2, format1.format(order.rowCreatedDate));
        fieldsInLine.put(3, prependZeros(new Long(order.incrementOrderId-100000).intValue() + "", 6));
        fieldsInLine.put(4, format1.format(order.rowCreatedDate));
        fieldsInLine.put(5, "0045");

        fieldsInLine.put(6, "\"" + stripText("", 30) + "\"");
        fieldsInLine.put(7, "0000");
        fieldsInLine.put(8, "00000");
        String account = null;
        String mvaKode = null;

        //This is the configuration
        if(item != null) {
            account = managers.productManager.getProduct(item.getProduct().id).getAccountingAccount();
            mvaKode = managers.productManager.getProduct(item.getProduct().id).sku;
        } else {
            if(order.payment != null && order.payment.paymentType != null && !order.payment.paymentType.toLowerCase().contains("invoice")) {
                account = "010900";
            } else {
                account = prependZeros(user.customerId + "", 6); 
           }
            mvaKode = "00";
        }
        //End of configuration
        double total = managers.orderManager.getTotalAmount(order);
        
        if(account == null) {
            account = "-1";
        }
        if(mvaKode == null) {
            mvaKode = "-1";
        }

        Integer count = -1;
        if(item != null) {
            count = item.getCount();
        } else if(total < 0) {
            count = 1;
        }

        if(count > 0) {
            fieldsInLine.put(9, "000000"); //Debet konto
            fieldsInLine.put(10, account + "");//Kredit konto
        } else {
            count = count * -1;
            fieldsInLine.put(9, account + "");//Debet konto
            fieldsInLine.put(10, "000000"); //Kredit konto
        }

        
        fieldsInLine.put(21, account + ""); //Debet konto

        Address address = user.address;
        if(address  == null) {
             address = new Address(); 
        }
        if(user.address == null) {
            user.address = new Address();
        }
        if(address.address2 == null) { address.address2="";}
        if(address.address == null) { address.address="";}
        if(address.city == null) { address.city="";}
        if(address.postCode == null) { address.postCode="";}

        Double price = null;
        if(item != null) {
            price = item.getProduct().price * item.getCount();
        } else {
            price = managers.orderManager.getTotalAmount(order);
        }
        
        if(price < 0) {
            price *= -1;
        }
        
        DecimalFormat df = new DecimalFormat("#.##");      
        String priceToSend = df.format(price); 
        if(!priceToSend.contains(".")) {
            priceToSend = priceToSend + ".00";
        } else {
            String[] splitted = priceToSend.split("\\.");
            if(splitted[1].length() == 1) {
                priceToSend = priceToSend + "0";
            }
        }

        priceToSend = prependZeros(priceToSend, 14);
        fieldsInLine.put(11, mvaKode);
        fieldsInLine.put(12, "000");
        fieldsInLine.put(13, "000000.0000");
        fieldsInLine.put(14, "00000000000.00");
        fieldsInLine.put(15, priceToSend + "");
        fieldsInLine.put(16, "000000");
        fieldsInLine.put(17, format1.format(order.rowCreatedDate));
        String counter = count + ".00";
        counter = prependZeros(counter, 11);
        fieldsInLine.put(18, prependZeros("", 10));
        fieldsInLine.put(19, counter);
        fieldsInLine.put(20, "\"" + stripText(order.incrementOrderId + "", 25) + "\"");
        fieldsInLine.put(22, "000000");
        fieldsInLine.put(23, "\"" + stripText(user.fullName, 30) + "\"" + "");
        fieldsInLine.put(24, "\"" + stripText(address.address, 30) + "\"" + "");
        fieldsInLine.put(25, "\"" + stripText(address.address2, 30) + "\"" + "");
        fieldsInLine.put(26, "\"" + stripText(address.postCode, 6) + "\"" + "");
        fieldsInLine.put(27, "\"" + stripText(user.address.city, 25) + "\""  + "");
        fieldsInLine.put(28, "\"" + stripText("", 30) + "\"");
        fieldsInLine.put(29, "\"" + stripText(user.cellPhone, 15) + "\"");

        fieldsInLine.put(30, "\"" + stripText("", 15) + "\"");
        fieldsInLine.put(31, "\"" + stripText("", 5) + "\"");
        fieldsInLine.put(32, "\"" + stripText("", 15) + "\"");
        fieldsInLine.put(33, "\"" + stripText("", 15) + "\"");
        fieldsInLine.put(34, "00000000000.00");
        fieldsInLine.put(35, "\"" + stripText("", 30) + "\"");
        fieldsInLine.put(36, "\"" + stripText("", 30) + "\"");
        fieldsInLine.put(37, "\"" + stripText("", 30) + "\"");
        fieldsInLine.put(38, "\"" + stripText("", 6) + "\"");
        fieldsInLine.put(39, "\"" + stripText("", 25) + "\"");

        fieldsInLine.put(40, "000");
        fieldsInLine.put(41, "0000");
        fieldsInLine.put(42, "00");
        fieldsInLine.put(43, "00");
        fieldsInLine.put(44, "000");
        fieldsInLine.put(45, "000");
        fieldsInLine.put(46,"00");
        
        return fieldsInLine;
    }

    @Override
    public void setConfig(AccountingTransferConfig config) {
        this.config = config;
    }
    
}

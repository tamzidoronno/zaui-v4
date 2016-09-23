package com.thundashop.core.accountingmanager;

import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ForStore;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceManager;
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
    private InvoiceManager invoiceManager;
    private OrderManager orderManager;
    
    @Override
    public void setUserManager(UserManager manager) {
        this.userManager = manager;
    }

    @Override
    public List<String> createUserFile(List<User> users) {
        return new ArrayList();
    }

    @Override
    public List<String> createOrderFile(List<Order> orders, String type) {
        
        SimpleDateFormat format1 = new SimpleDateFormat("ddMMyy");
        List<String> result = new ArrayList();
        for(Order order : orders) {
            User user = userManager.getUserById(order.userId);
            for(CartItem item : order.cart.getItems()) {
                HashMap<Integer, String> fieldsInLine = new HashMap();
                fieldsInLine.put(1, "\"97");
                fieldsInLine.put(2, format1.format(order.rowCreatedDate));
                fieldsInLine.put(3, order.incrementOrderId + "");
                fieldsInLine.put(4, format1.format(order.rowCreatedDate));
                fieldsInLine.put(5, "0045");
                
                String lineText = createLineText(item);
                
                fieldsInLine.put(6, "\"\"" + stripText(lineText, 30) + "\"\"");
                fieldsInLine.put(7, "0000");
                fieldsInLine.put(8, stripText(order.invoiceNote, 5));
                String account = item.getProduct().sku;
                String mvaKode = item.getProduct().accountingSystemId;
                
                if(account == null) {
                    account = "-1";
                }
                if(mvaKode == null) {
                    mvaKode = "-1";
                }
                
                if(order.payment.paymentType.toLowerCase().contains("invoice")) {
                    if(user.customerId < 100000) {
                        account = "0" + user.customerId + "";
                    } else {
                        account = user.customerId + "";
                    }
                }
                
                Integer count = item.getCount();
                
                if(count > 0) {
                    fieldsInLine.put(9, account); //Debet konto
                } else {
                    count = count * -1;
                    fieldsInLine.put(10, account); //Kredit konto
                }
                
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
                
                double price = item.getProduct().priceExTaxes;
                DecimalFormat df = new DecimalFormat("#.##");      
                String priceToSend = df.format(price); 
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
                fieldsInLine.put(18, "0000000000");
                fieldsInLine.put(19, counter);
                fieldsInLine.put(20, "\"\"" + stripText("", 25) + "\"\"");
                fieldsInLine.put(21, user.customerId + "");
                fieldsInLine.put(22, "000000");
                fieldsInLine.put(23, "\"\"" + stripText(user.fullName, 30) + "\"\"" + "");
                fieldsInLine.put(24, "\"\"" + stripText(address.address, 30) + "\"\"" + "");
                fieldsInLine.put(25, "\"\"" + stripText(address.address2, 30) + "\"\"" + "");
                fieldsInLine.put(26, "\"\"" + stripTextPrependNumber(address.postCode, 6) + "\"\"" + "");
                fieldsInLine.put(27, "\"\"" + stripText(user.address.city, 25) + "");
                fieldsInLine.put(28, "\"\"" + stripText("", 30) + "\"\"");
                fieldsInLine.put(29, "\"\"" + stripText(user.cellPhone, 15) + "\"\"");
                
                fieldsInLine.put(30, "\"\"" + stripText("", 15) + "\"\"");
                fieldsInLine.put(31, "\"\"" + stripText("", 5) + "\"\"");
                fieldsInLine.put(32, "\"\"" + stripText("", 15) + "\"\"");
                fieldsInLine.put(33, "\"\"" + stripText("", 15) + "\"\"");
                fieldsInLine.put(34, "00000000000.00");
                fieldsInLine.put(35, "\"\"" + stripText("", 30) + "\"\"");
                fieldsInLine.put(36, "\"\"" + stripText("", 30) + "\"\"");
                fieldsInLine.put(37, "\"\"" + stripText("", 30) + "\"\"");
                fieldsInLine.put(38, "\"\"" + stripText("", 6) + "\"\"");
                fieldsInLine.put(39, "\"\"" + stripText("", 25) + "\"\"");
                
                fieldsInLine.put(40, "000");
                fieldsInLine.put(41, "0000");
                fieldsInLine.put(42, "00");
                fieldsInLine.put(43, "00");
                fieldsInLine.put(44, "000");
                fieldsInLine.put(45, "000");
                fieldsInLine.put(46,"00\"");
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
        
        if(lineText.length() > 30) {
            lineText = lineText.substring(0, 30);
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
                    resultLine += text + ",";
                } else {
                    resultLine += ",";
                }
            }
            resultLine = resultLine.substring(0, resultLine.length()-1);
            resultLine += "\r\n";
            return resultLine;
    }

    private boolean isBreakFast(CartItem item) {
        return false;
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
    
    private String stripTextPrependNumber(String text, int length) {
        if(text == null) {
            return "";
        }
        text = text.replaceAll(",", "");
        text = text.replaceAll("\n", "");
        text = text.replaceAll("\"", "");
        if(text.length() > length) {
            text = text.substring(0, length);
        }        
        
        String padding = "";
        for(int i = text.length(); i < length; i++) {
            padding += "0";
        }
        return padding + text;
    }
    
}

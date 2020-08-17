/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.accountingmanager;

import java.util.Calendar;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ForAccountingSystem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.joda.time.DateTime;

@ForAccountingSystem(accountingSystem="gbat10")
public class GBat10 extends AccountingTransferOptions implements AccountingTransferInterface {
    
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
        
        Date periodeDate = order.rowCreatedDate;
        if(config.orderFilterPeriode != null && config.orderFilterPeriode.equals("paymentdate") && order.paymentDate != null) {
            periodeDate = order.paymentDate;
            cal.setTime(periodeDate);
        }

        managers.invoiceManager.generateKidOnOrder(order);
        
        List<HashMap<Integer, String>> lines = new ArrayList();
        
        int firstMonth = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);
        int duedays = 14;
        
        if(order.dueDays != null) {
            duedays = order.dueDays;
        }
        
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
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(order.rowCreatedDate);
        cal2.add(Calendar.DAY_OF_YEAR, duedays);
        Date dueDate = cal2.getTime();

        HashMap<Integer, String> line = new HashMap();
        line.put(0, "GBAT10");
        line.put(1, order.incrementOrderId+ "");
        line.put(2, format.format(periodeDate));
        line.put(3, "1");
        line.put(4, firstMonth + "");
        line.put(5, year + "");
        line.put(6, "1510");
        line.put(7, "1");
        line.put(8, df.format(total)+"");
        line.put(9, customerId+"");
        line.put(10, "");
        line.put(11, makeSureIsOkay(user.fullName));
        if(user.address != null) {
            line.put(12, makeSureIsOkay(user.address.address));
            line.put(13, makeSureIsOkay(user.address.postCode));
            line.put(14, makeSureIsOkay(user.address.city));
        } else {
            line.put(12, "");
            line.put(13, "");
            line.put(14, "");
        }
        line.put(15, order.incrementOrderId + "");
        line.put(16, kid); //KID
        line.put(17, format.format(dueDate)); //Forfallsdato
        line.put(18, "");
        line.put(19, "");
        line.put(20, "GetShop order: " + order.incrementOrderId);
        line.put(21, order.payment.readablePaymentType()); //Forfallsdato
        line.put(22, "");
        line.put(23, "");
        line.put(24, "");
        line.put(25, "");
        line.put(26, "T");
        line.put(27, df.format(total)+"");
        lines.add(line);
        
        Double linesTotal = 0.0;
        for(CartItem item : order.cart.getItems()) {
            linesTotal += item.getProduct().price * item.getCount();
            
            HashMap<Integer, String> subLine = new HashMap();
            
            Product product = managers.productManager.getProduct(item.getProduct().id);
            if(product == null) {
                product = managers.productManager.getDeletedProduct(item.getProduct().id);
            }
            
            subLine.put(0, "GBAT10");
            subLine.put(1, order.incrementOrderId+ "");
            subLine.put(2, format.format(periodeDate));
            subLine.put(3, "1");
            subLine.put(4, firstMonth + "");
            subLine.put(5, year + "");
            subLine.put(6, product.getAccountingAccount());
            subLine.put(7, product.sku);
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
            subLine.put(22, "");
            subLine.put(23, "");
            subLine.put(24, "");
            subLine.put(25, "");
            subLine.put(26, "T");
            subLine.put(27, df.format(item.getProduct().price * item.getCount() * -1)+"");
            
            lines.add(subLine);

        }
        if(!linesTotal.equals(total)) {
            logError("Lines are not the same as the total for order : " + order.incrementOrderId);
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
        lineText = makeSureIsOkay(lineText);
        return lineText;
    }

    private void checkTaxCodes() {
        for(Order order : orders) {
            for(CartItem item : order.cart.getItems()) {
                Product prod = managers.productManager.getProduct(item.getProduct().id);
                if(prod == null) {
                    prod = managers.productManager.getDeletedProduct(item.getProduct().id);
                }
                if(prod == null) {
                    logError("Product does not exists anymore on order, regarding order: " + order.incrementOrderId);
                    hasFail = true;
                } else if(prod.sku == null || prod.sku.trim().isEmpty()) {
                    if(prod.deleted != null && (prod.name == null || prod.name.trim().isEmpty())) {
                        prod.name = item.getProduct().name;
                        managers.productManager.saveProduct(prod);
                    }
                    logError("Tax code not set for product: " + prod.name + ", regarding order: " + order.incrementOrderId);
                    hasFail = true;
                }
            }
        }
    }

    private void logError(String string) {
        addToLog(string);
    }

    private String makeSureIsOkay(String text) {
        if(text == null) {
            return "";
        }
        return text.replaceAll(",", " ");
    }

}

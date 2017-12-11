/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class GBat10AccountingSystem extends AccountingSystemBase {
   
    @Override
    public List<SavedOrderFile> createFiles(List<Order> inOrders) {
        boolean hasFail = checkTaxCodes(inOrders);
        
        if(hasFail) {
            return null;
        }
        
        Map<String, List<Order>> allOrders = groupOrders(inOrders);
        
        ArrayList<SavedOrderFile> retFiles = new ArrayList();
        
        for (String subType : allOrders.keySet()) {
            List<Order> orders = allOrders.get(subType);
            
            SavedOrderFile file = new SavedOrderFile();
            List<String> toPrint = new ArrayList();
            file.orders = new ArrayList();
            file.subtype = subType;
            
            for(Order order : orders) {
                file.orders.add(order.id);
                List<HashMap<Integer, String>> lines = generateLine(order);
                for(HashMap<Integer, String> toConvert : lines) {
                    String line = makeLine(toConvert);
                    toPrint.add(line);
                }
            }

            file.result = toPrint;
            retFiles.add(file);
        }
        
        return retFiles;
    }

    @Override
    public SystemType getSystemType() {
        return SystemType.GBAT10;
    }
    
    @Autowired
    private ProductManager productManager;
    
    @Autowired
    private InvoiceManager invoiceManager;
   
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
        
        Date periodeDate = order.transferToAccountingDate;
        invoiceManager.generateKidOnOrder(order);
        
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
        Double total = orderManager.getTotalAmount(order);
        DecimalFormat df = new DecimalFormat("#.##");    
        User user = userManager.getUserById(order.userId);
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
            
            Product product = productManager.getProduct(item.getProduct().id);
            if(product == null) {
                product = productManager.getDeletedProduct(item.getProduct().id);
            }
            
            subLine.put(0, "GBAT10");
            subLine.put(1, order.incrementOrderId+ "");
            subLine.put(2, format.format(periodeDate));
            subLine.put(3, "1");
            subLine.put(4, firstMonth + "");
            subLine.put(5, year + "");
            subLine.put(6, product.accountingAccount);
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

    private boolean checkTaxCodes(List<Order> orders) {
        boolean hasFail = false;
        for(Order order : orders) {
            for(CartItem item : order.cart.getItems()) {
                Product prod = productManager.getProduct(item.getProduct().id);
                if(prod == null) {
                    prod = productManager.getDeletedProduct(item.getProduct().id);
                }
                if(prod == null) {
                    logError("Product does not exists anymore on order, regarding order: " + order.incrementOrderId);
                    hasFail = true;
                } else if(prod.sku == null || prod.sku.trim().isEmpty()) {
                    if(prod.deleted != null && (prod.name == null || prod.name.trim().isEmpty())) {
                        prod.name = item.getProduct().name;
                        productManager.saveProduct(prod);
                    }
                    logError("Tax code not set for product: " + prod.name + ", regarding order: " + order.incrementOrderId);
                    hasFail = true;
                }
            }
        }
        
        return hasFail;
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

    private Map<String, List<Order>> groupOrders(List<Order> orders) {
        Map<String, List<Order>> retMap = new HashMap();
        
        for (Order order : orders) {
            String paymentId = order.getPaymentApplicationId();
            String subType = getSubType(paymentId);
            if (retMap.get(subType) == null) {
                retMap.put(subType, new ArrayList());
            }
            retMap.get(subType).add(order);
        }

        return retMap;
    }

    private String getSubType(String paymentId) {
        // InvoicePayment
        if (paymentId.equals("70ace3f0-3981-11e3-aa6e-0800200c9a66")) {
            return "invoice";
        }
        
        // EhfPayment
        if (paymentId.equals("bd13472e-87ee-4b8d-a1ae-95fb471cedce")) {
            return "invoice";
        }
        
        return "other";
    }

}

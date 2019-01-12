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
import com.thundashop.core.common.ErrorException;
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
    public List<SavedOrderFile> createFiles(List<Order> inOrders, Date start, Date end) {
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
    public HashMap<String, String> getConfigOptions() {
       HashMap<String, String> ret = new HashMap();
        ret.put("eiendelskonto", "Eiendelskonto (default 1510)");
        ret.put("brukkundeinstedenforeiendelskonto", "Før mot kunde istedenfor eiendelskonto, settes til 1");
        ret.put("avdeling", "Avdeling");
        ret.put("momsKontoHovedPost", "Moms konto hovedpost");
        return ret;
    }
        
    @Override
    public SystemType getSystemType() {
        return SystemType.GBAT10;
    }
    
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
        
        Date periodeDate = getAccountingPostingDate(order);
        cal.setTime(periodeDate);
        invoiceManager.generateKidOnOrder(order);
        
        List<HashMap<Integer, String>> lines = new ArrayList();
        
        int firstMonth = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);
        int duedays = 14;
        
        if(order.dueDays != null) {
            duedays = order.dueDays;
        }
        
        Integer customerId = getAccountingAccountId(order.userId);
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

        String eiendelsKonto = getConfig("eiendelskonto");
        if(eiendelsKonto == null || eiendelsKonto.isEmpty()) {
            eiendelsKonto = "1510";
        }

        String brukKundeIstedenforEiendel = getConfig("brukkundeinstedenforeiendelskonto");
        if(brukKundeIstedenforEiendel != null && brukKundeIstedenforEiendel.equals("1")) {
            eiendelsKonto = getAccountingAccountId(user.id) + "";
        }
        
        String avdeling = getConfig("avdeling");
        if(avdeling == null) {
            avdeling = "";
        }
        
        
        String momskontohovedpost = getConfig("momsKontoHovedPost");
        if(momskontohovedpost == null || momskontohovedpost.isEmpty()) {
            momskontohovedpost = "1";
        }
        
        HashMap<Integer, String> line = new HashMap();
        line.put(0, "GBAT10");
        line.put(1, order.incrementOrderId+ "");
        line.put(2, format.format(periodeDate));
        line.put(3, "1");
        line.put(4, firstMonth + "");
        line.put(5, year + "");
        line.put(6, eiendelsKonto);
        line.put(7, momskontohovedpost);
        line.put(8, df.format(total)+"");
        line.put(9, customerId+"");
        line.put(10, "");
        line.put(11, nullAndCsvCheck(user.fullName));
        if(user.address != null) {
            line.put(12, nullAndCsvCheck(user.address.address));
            line.put(13, nullAndCsvCheck(user.address.postCode));
            line.put(14, nullAndCsvCheck(user.address.city));
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
        if(!avdeling.isEmpty()) {
            line.put(28, avdeling);
        }
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
            if(!avdeling.isEmpty()) {
                subLine.put(28, avdeling);
            }

            lines.add(subLine);

        }
        if(!linesTotal.equals(total)) {
            logError("Lines are not the same as the total for order : " + order.incrementOrderId);
        }
        
        return lines;
    }

    private void logError(String string) {
        addToLog(string);
    }

    @Override
    public String getSystemName() {
        return "GBAT10";
    }

    @Override
    public void handleDirectTransfer(String orderId) {
        throw new ErrorException(1049);
    }

    @Override
    boolean isUsingProductTaxCodes() {
        return true;
    }

    @Override
    boolean isPrimitive() {
        return false;
    }
}

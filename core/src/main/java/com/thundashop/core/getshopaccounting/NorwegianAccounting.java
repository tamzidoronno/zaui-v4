/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.accountingmanager.AccountingTransaction;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.AccountingDetail;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class NorwegianAccounting extends AccountingSystemBase {
    
    @Autowired
    private StoreApplicationPool storeApplicationPool;
    
    @Autowired
    private ProductManager productManager;
    
    @Override
    public List<SavedOrderFile> createFiles(List<Order> inOrders) {
        
        List<SavedOrderFile> retFiles = new ArrayList();
        SavedOrderFile file = new SavedOrderFile();
        file.subtype = "all";
        inOrders.stream().forEach(order -> addTransactionLineToFile(order, file));
        retFiles.add(file);
       
        List<String> toPrint = new ArrayList();
        
        file.accountingTransactionLines.stream()
            .forEach(transactionLine -> {
                AccountingDetail accountingDetail = productManager.getAccountingDetail(transactionLine.accountNumber);
                if (accountingDetail == null && transactionLine.accountNumber >= 2000) {
                    throw new RuntimeException("Accounting details not created for product accounting account: " + transactionLine.accountNumber);
                }
                
                int mvaCode = transactionLine.accountNumber >= 2000 ? transactionLine.accountNumber : 1;

                String gbat10line = createGBat10Line(transactionLine.start, transactionLine.accountNumber, mvaCode, transactionLine.getSum());
                toPrint.add(gbat10line); 
            });
        
        file.result = toPrint;
        
        return retFiles;
    }

    @Override
    public SystemType getSystemType() {
        return SystemType.GENERELL_NORWEGIAN; 
   }

    @Override
    public String getSystemName() {
        return "Primitive Norwegian Accounting Report";
    }

    @Override
    public void handleDirectTransfer(String orderId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void addTransactionLineToFile(Order order, SavedOrderFile file) {
        
        addPaymentTransaction(order, file);
        addExpenseTransaction(order, file);
        
        file.orders.add(order.id);
        
        
    }

    private void addPaymentTransaction(Order order, SavedOrderFile file) {
        int accountNumber = getAccountNumberForPaymentMethod(order);
        
        if (order.paymentDate != null) {
            AccountingTransaction paymentTransaction = getTransactionFile(file, order.paymentDate, accountNumber);
            paymentTransaction.debit = paymentTransaction.debit.add(order.getTotalAmountRoundedTwoDecimals());
            Application paymentapp = storeApplicationPool.getApplication(order.getPaymentApplicationId());
            paymentTransaction.description = paymentapp.appName;
        } else {
            AccountingTransaction paymentTransaction = getTransactionFile(file, order.createdDate, 1510);
            paymentTransaction.debit = paymentTransaction.debit.add(order.getTotalAmountRoundedTwoDecimals());
            paymentTransaction.description = "Kundefordringer";
        }
    }

    private AccountingTransaction getTransactionFile(SavedOrderFile file, Date postingDate, int accountNumber) {
        AccountingTransaction transactionFile = file.accountingTransactionLines.stream()
                .filter(i -> i.isFor(accountNumber, postingDate))
                .findAny()
                .orElse(null);
        
        
        if (transactionFile == null) {
            transactionFile = new AccountingTransaction();
            transactionFile.accountNumber = accountNumber;
            transactionFile.start = getDayBreakDateStart(postingDate);
            transactionFile.end = getDayBreakDateEnd(postingDate);
            file.accountingTransactionLines.add(transactionFile);
        }
        
        return transactionFile;
    }

    private Date getDayBreakDateEnd(Date postingDate) {
        Calendar cal = getBreakDateCal(postingDate);
        
        if (postingDate.after(cal.getTime())) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        return cal.getTime();
    }

    private Date getDayBreakDateStart(Date postingDate) {
        Calendar cal = getBreakDateCal(postingDate);
        
        if (postingDate.before(cal.getTime())) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        
        return cal.getTime();
    }
    
    private Calendar getBreakDateCal(Date postingDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(postingDate);
        
        cal.set(Calendar.HOUR_OF_DAY, getDayBreakOrderTime());
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal;
    }

    /**
     * TODO: Make this configureable with default to 5 in the morning. (if needed)
     * 
     * @return 
     */
    private int getDayBreakOrderTime() {
        return 5;
    }

    private void addExpenseTransaction(Order order, SavedOrderFile file) {
        Date postDate = getAccountingPostingDate(order);
        for (CartItem cartItem : order.cart.getItems()) {
            BigDecimal total = cartItem.getTotalAmountRoundedWithTwoDecimals();
            int accountNumber = Integer.valueOf(getAccountingNumberForProduct(cartItem.getProduct().id));
            AccountingTransaction paymentTransaction = getTransactionFile(file, postDate, accountNumber);
            paymentTransaction.credit = paymentTransaction.credit.add(total);
        }
    }

    private String createGBat10Line(Date bilagsDato, int accountNumber, int mvaCode, BigDecimal sum) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        
        com.ibm.icu.util.Calendar cal = com.ibm.icu.util.Calendar.getInstance();
        cal.setTime(bilagsDato);
        int firstMonth = cal.get(com.ibm.icu.util.Calendar.MONTH)+1;
        int year = cal.get(com.ibm.icu.util.Calendar.YEAR);
        
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        
        
        HashMap<Integer, String> columns = new HashMap();
        columns.put(0, "GBAT10"); // Alltid lik: GBAT10
        columns.put(1, ""); // Numerisk (Benyttes for å skille bilag fra hverandre)
        columns.put(2, format.format(bilagsDato)); // Dato (format: YYYYMMDD)
        columns.put(3, "1"); // Bilagsart
        columns.put(4, ""+firstMonth); // Periode
        columns.put(5, ""+year); // Regnskapsår
        columns.put(6, ""+accountNumber); // Konto
        columns.put(7, ""+mvaCode); // Mva-kode
        columns.put(8, ""+sum); // Saldo
        columns.put(9, ""); // Saldo
        columns.put(10, ""); // Saldo
        columns.put(11, ""); // Saldo
        columns.put(12, ""); // Saldo
        columns.put(13, ""); // Saldo
        columns.put(14, ""); // Saldo
        columns.put(15, ""); // Saldo
        columns.put(16, ""); // Saldo
        columns.put(17, ""); // Saldo
        columns.put(18, ""); // Saldo
        columns.put(19, ""); // Saldo
        columns.put(20, ""); // Saldo
        columns.put(21, ""); // Saldo
        columns.put(22, ""); // Saldo
        columns.put(23, ""); // Saldo
        columns.put(24, ""); // Saldo
        columns.put(25, ""); // Saldo
        columns.put(26, ""); // Saldo
        columns.put(26, "T");
        columns.put(28, ""); // Saldo
        columns.put(29, ""); // Saldo
        
        String result = "";
        for(Integer i = 0; i <= columns.size(); i++) {
            String toAdd = columns.get(i);
            if(toAdd == null) {
                toAdd = "";
            }
            toAdd = toAdd.replace(";", "");
            result += toAdd;
            if(i != columns.size()) {
                result += ";";
            }
        }
        return result + "\r\n";
    }
    
}

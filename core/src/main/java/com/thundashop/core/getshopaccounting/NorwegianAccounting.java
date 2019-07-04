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
import com.thundashop.core.ordermanager.data.OrderTransaction;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.AccountingDetail;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
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
    public List<SavedOrderFile> createFiles(List<Order> inOrders, Date start, Date end) {
        
        List<SavedOrderFile> retFiles = new ArrayList();
        SavedOrderFile file = new SavedOrderFile();
        file.subtype = "all";
        List<Order> ordersInScope = getOrdersInScope(inOrders, start, end);
        ordersInScope.stream().forEach(order -> addTransactionLineToFile(order, file, start, end));
        
        retFiles.add(file);
        
        // This will move the order out to a list that is to be accounted for later.
        moveOrdersOutsideOfScope(file, start, end);
        
        // This will move orders that has been accounted for in previouse files.
        addAllOrdersOutsideOfScope(file, start, end);
        
        // Add orders that has been created later but needs to be accounted in this file.
        addAllUnaccountedOrdersInFuture(file, start, end, inOrders, ordersInScope);
        
        file.result = createGbat10Lines(file);
        
        return retFiles;
    }

    private List<String> createGbat10Lines(SavedOrderFile file) {
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
        return toPrint;
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

    private void addTransactionLineToFile(Order order, SavedOrderFile file, Date start, Date end) {
        int accountNumber = getAccountNumberForPaymentMethod(order);
        
        if (accountNumber == -1) {
            return;
        }
        
        addPaymentTransaction(order, file, start, end);
        addExpenseTransaction(order, file);
        
        file.orders.add(order.id);
        
        
    }

    private void addPaymentTransaction(Order order, SavedOrderFile file, Date start, Date end) {
        int accountNumber = getAccountNumberForPaymentMethod(order);
        String accountDescription = getAccountDescriptionForPaymentMethod(order);
        
        Application paymentapp = storeApplicationPool.getApplication(order.getPaymentApplicationId());
        accountDescription = accountDescription.isEmpty() ? paymentapp.appName : accountDescription;
        
        Date useDate = order.paymentDate != null ? order.paymentDate : order.createdDate;
        if (useDate.after(end)) {
            
            // Bruk 1530 periodiseringskontoen når betalingen er opprettet eller innhentet utenfor angitt tidsperiode.
            AccountingTransaction paymentTransaction = getTransactionFile(file, end, 1530);
            paymentTransaction.debit = paymentTransaction.debit.add(order.getTotalAmountRoundedTwoDecimals(2));

            paymentTransaction.description = "Opptjent, ikke fakturert driftsinntekter.";
            paymentTransaction.orderIds.add(order.id);
        } else {
            AccountingTransaction paymentTransaction = getTransactionFile(file, useDate, accountNumber);
            paymentTransaction.debit = paymentTransaction.debit.add(order.getTotalAmountRoundedTwoDecimals(2));

            paymentTransaction.description = accountDescription;
            paymentTransaction.orderIds.add(order.id);
        }
    }

    private AccountingTransaction getTransactionFileTemp(List<AccountingTransaction> accountingTransactionLines, Date postingDate, int accountNumber) {
        AccountingTransaction transactionFile = accountingTransactionLines.stream()
                .filter(i -> i.isFor(accountNumber, postingDate))
                .findAny()
                .orElse(null);
        
        
        if (transactionFile == null) {
            transactionFile = new AccountingTransaction();
            transactionFile.accountNumber = accountNumber;
            transactionFile.start = getDayBreakDateStart(postingDate);
            transactionFile.end = getDayBreakDateEnd(postingDate);
        }
        
        return transactionFile;
    }
    
    private AccountingTransaction getTransactionFile(SavedOrderFile file, Date postingDate, int accountNumber) {
        AccountingTransaction transactionFile = getTransactionFileTemp(file.accountingTransactionLines, postingDate, accountNumber);
        
        if (!file.accountingTransactionLines.contains(transactionFile)) {
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
            BigDecimal total = cartItem.getTotalAmountRoundedWithTwoDecimals(2);
            int accountNumber = Integer.valueOf(getAccountingNumberForProduct(cartItem.getProduct().id));
            AccountingTransaction paymentTransaction = getTransactionFile(file, postDate, accountNumber);
            paymentTransaction.credit = paymentTransaction.credit.add(total);
            
            if (!paymentTransaction.orderIds.contains(order.id)) {
                paymentTransaction.orderIds.add(order.id);
            }
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

    private void moveOrdersOutsideOfScope(SavedOrderFile file, Date start, Date end) {
        List<AccountingTransaction> filesToMove = file.accountingTransactionLines.stream()
                .filter(o -> o.start.after(end))
                .collect(Collectors.toList());
        
        file.accountingTransactionLines.removeAll(filesToMove);
        file.accountingTransactionOutOfScope.addAll(filesToMove);
        
        BigDecimal sum = new BigDecimal(BigInteger.ZERO);
        
        for (AccountingTransaction o : filesToMove) {
            sum = sum.add(o.credit);
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date endTime = cal.getTime();
        
        AccountingTransaction accountTransactionLine = getTransactionFile(file, endTime, 2900);
        accountTransactionLine.credit = sum;
    }

    private void addAllOrdersOutsideOfScope(SavedOrderFile file, Date start, Date end) {
        List<Order> orders = files.values().stream()
                .flatMap(o -> o.accountingTransactionOutOfScope.stream())
                .flatMap(o -> o.orderIds.stream())
                .map(orderId -> orderManager.getOrder(orderId))
                .distinct()
                .collect(Collectors.toList());
        
        
        for (Order order : orders) {
            Date postingDate = getAccountingPostingDate(order);
            AccountingTransaction transaction = getTransactionFileTemp(new ArrayList(), postingDate, 2900);
            
            if (transaction.start.before(end) && transaction.start.after(start)) {
                addPaymentFromDepth(order, file);
                addExpenseTransaction(order, file);
                
                files.values().stream().forEach(iFile -> { 
                    boolean foundOne = iFile.moveOrderToProcessedInADifferentFile(order.id);
                    if (foundOne) {
                        saveObject(iFile);
                    }
                });
            }
        }
    }

    private void addPaymentFromDepth(Order order, SavedOrderFile file) {
        Date postingDate = getAccountingPostingDate(order);
        AccountingTransaction paymentTransaction = getTransactionFile(file, postingDate, 2900);
        paymentTransaction.debit = paymentTransaction.debit.add(order.getTotalAmountRoundedTwoDecimals(2));
        paymentTransaction.description = productManager.getAccountingDetail(2900).description;
    }   
    
    @Override
    boolean isUsingProductTaxCodes() {
        return false;
    }

    private List<Order> getOrdersInScope(List<Order> inOrders, Date start, Date end) {
        List<Order> orders = new ArrayList();
        for (Order order : inOrders) {
            String id = getUniqueCustomerIdForOrder(order);
            if (id == null || Integer.parseInt(id) < 0)
                continue;
            
            if (order.status == Order.Status.PAYMENT_COMPLETED && order.paymentDate != null) {
                if (order.paymentDateWithin(start, end)) {
                    orders.add(order);
                }
            } else {
                if (order.isInvoice() && order.createdBetween(start, end)) {
                    orders.add(order);
                }
                
                Date accountingDate = getAccountingPostingDate(order);
                if (order.isPaymentLinkType() && accountingDate.after(start) && accountingDate.before(end)) {
                    orders.add(order);
                }
            }
        }
        
        return orders;
    }

    private void addAllUnaccountedOrdersInFuture(SavedOrderFile file, Date start, Date end, List<Order> inOrders, List<Order> ordersInScope) {
        List<Order> allOrdersToCheck = new ArrayList(inOrders);
        allOrdersToCheck.removeAll(ordersInScope);
        
        
        List<Order> futureOrders = new ArrayList();
        for (Order order : allOrdersToCheck) {            
            String id = getUniqueCustomerIdForOrder(order);

            Date accountingDate = getAccountingPostingDate(order);

            if (id != null && Integer.parseInt(id) > 0) {
                if (order.createdDate.after(end) && accountingDate.after(start) && accountingDate.before(end)) {
                    futureOrders.add(order);
                }
            } else {
                if (order.isPaymentLinkType() && accountingDate.after(start) && accountingDate.before(end)) {
                    futureOrders.add(order);
                }
            }
        }
     
        futureOrders.stream().forEach(order -> addTransactionLineToFile(order, file, start, end));
    }
    
    @Override
    public String createBankTransferFile() {
        List<OrderTransaction> transactions = orderManager.getBankOrderTransactions();
        if (transactions.isEmpty()) {
            return null;
        }
        
        SavedOrderFile saveOrderFile = new SavedOrderFile();
        saveOrderFile.type = "banktransfer";
        saveOrderFile.subtype = "banktransfer";
        
        transactions.stream().forEach(transaction -> {
            boolean success = addAccountinTransferLineForBanking(transaction, saveOrderFile);
            if (success) {
                orderManager.markTransactionAsTransferredToAccounting(transaction);
            }
        });
        
        if (!saveOrderFile.accountingTransactionLines.isEmpty()) {
            saveObject(saveOrderFile);
            saveOrderFile.result = createGbat10Lines(saveOrderFile);
            files.put(saveOrderFile.id, saveOrderFile);    
        }
        
        return saveOrderFile.id;
    }

    private boolean addAccountinTransferLineForBanking(OrderTransaction transaction, SavedOrderFile saveOrderFile) {
        String invoiceAccountingId = getAccountingNumberForPaymentApplicationId("70ace3f0-3981-11e3-aa6e-0800200c9a66");
        if (invoiceAccountingId == null || invoiceAccountingId.isEmpty()) {
            return false;
        }
        
        AccountingTransaction transactionToAdd = new AccountingTransaction();
        transactionToAdd.accountNumber = 1920;
        transactionToAdd.start = transaction.date;
        transactionToAdd.debit = new BigDecimal(transaction.amount);
        saveOrderFile.accountingTransactionLines.add(transactionToAdd);
        
        AccountingTransaction transactionToAdd2 = new AccountingTransaction();
        transactionToAdd2.accountNumber = Integer.parseInt(invoiceAccountingId);
        transactionToAdd2.start = transaction.date;
        transactionToAdd2.credit = new BigDecimal(transaction.amount);
        saveOrderFile.accountingTransactionLines.add(transactionToAdd2);
        
        return true;
    }

    @Override
    boolean isPrimitive() {
        return true;
    }
}
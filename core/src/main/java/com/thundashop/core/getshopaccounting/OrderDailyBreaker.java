/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.ibm.icu.util.Calendar;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.TwoDecimalRounder;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderTransaction;
import com.thundashop.core.paymentmanager.PaymentManager;
import com.thundashop.core.paymentmanager.StorePaymentConfig;
import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.TaxGroup;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class OrderDailyBreaker {
    private final PaymentManager paymentManager;
    private final List<DayIncome> dayIncomes;
    private final List<Order> ordersToBreak;
    private final int whatHourOfDayStartADay = 0;
    private final ProductManager productManager;
    private Date end;
    private Date start;
    private List<DayEntry> orderDayEntries;
    private List<String> errors = new ArrayList();
    private int precision = 10;
    public boolean ignoreConfig = false;
    
    public OrderDailyBreaker(List<Order> ordersToBreak, Date start, Date end, PaymentManager paymentManager, ProductManager productManager, boolean ignoreConfig) {
        this.dayIncomes = new ArrayList();
        this.ordersToBreak = ordersToBreak;
        this.start = start;
        this.end = end;        
        this.paymentManager = paymentManager;
        this.productManager = productManager;
        this.ignoreConfig = ignoreConfig;
        correctStartAndEndTime();
        createEmptyDays();
    }

    private void correctStartAndEndTime() {
        start = calculateStartTimeForDate(start);
        end = calculateEndTimeForDate(end);
    }
    
    /**
     * We start the process by creating empty days
     * for the whole periode.
     */
    private void createEmptyDays() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        
        while(true) {
            Date dayStart = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date dayEnd = cal.getTime();
            
            DayIncome dayIncome = new DayIncome();
            dayIncome.start = dayStart;
            dayIncome.end = dayEnd;
            
            dayIncomes.add(dayIncome);
            
            if (dayEnd.equals(this.end) || dayEnd.after(this.end)) {
                break;
            }
        }
    }

    public void breakOrders() {
        this.ordersToBreak.stream().forEach(order -> {
            try {
                if (order.isNullOrder() || order.isVirtual)
                    return;

                if (ignoreOrderDueToDisabledAccountingPaymentMethod(order)) {
                    return;
                }

                if (!order.payment.isPaymentTypeValid()) {
                    throw new RuntimeException("Missing payment method on order? " + order.incrementOrderId);
                }

                proccessOrder(order);
            } catch (Exception ex) {
                ex.printStackTrace();
                errors.add(ex.getMessage());
            }
        });
    }

    private void proccessOrder(Order order) {
        orderDayEntries = getDayEntriesForOrder(order);
        setPostAndPreFlags(orderDayEntries, order);
        calculatePrePaidAndAccrued(orderDayEntries, order);
        validateAccrudAndPrepaidPayments(orderDayEntries);
        invertAccountNumbers(orderDayEntries);
        
        try {
            createOffsetAccountEntries(orderDayEntries, order);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(OrderDailyBreaker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        createVatLines(order, orderDayEntries);
        
        addToDayIncome(orderDayEntries);
    }
    
    public List<DayEntry> getDayEntries() {
        return orderDayEntries;
    }
    
    public List<DayIncome> getDayIncomes() {
        return dayIncomes;
    }
    
    private List<DayEntry> getDayEntriesForOrder(Order order) {
        List<DayEntry> orderDayEntries = new ArrayList();
        
        DayEntry incomeEntry = createIncomeEntry(order);
        
        if (incomeEntry != null) {
            orderDayEntries.add(incomeEntry);
        }
        
        if (order.cashWithdrawal != 0) {
            createCashWithdrawalEntries(order, orderDayEntries);
        }
        
        order.cart.getItems().stream().forEach(item -> {
            if (item.isPriceMatrixItem()) {
                List<DayEntry> entries = createEntriesOfPriceMatrix(order, item);
                orderDayEntries.addAll(entries);
            }
            
            if (item.isPmsAddons()) {
                List<DayEntry> entries = createEntriesOfPricePmsAddons(order, item);
                orderDayEntries.addAll(entries);
            }
            
            if (!item.isPmsAddons() && !item.isPriceMatrixItem()) {
                DayEntry entry = createNormalOrderEntry(order, item);
                orderDayEntries.add(entry);
            }
        });
        
        createPaymentRecords(orderDayEntries, order);
        correctRoundingProblemsDueToExTaxes(orderDayEntries);
        
        boolean orderValidated = validateDayEntries(orderDayEntries, order);
        
        // TODO - handle execption
        if (!orderValidated) {
            String message = "The pricematrix, items added or normal cartitems does not match up with total income for the order, order: " + order.incrementOrderId;
            throw new RuntimeException(message);
        }
        
        return orderDayEntries;
    }

    private DayEntry createIncomeEntry(Order order) {
        boolean shouldDoublePost = shouldRegisterPaymentTransactions(order);
        boolean isPaid = order.status == Order.Status.PAYMENT_COMPLETED && order.paymentDate != null;
        
        if (!shouldDoublePost && !isPaid) {
            return null;
        }
        
        DayEntry dayEntry = new DayEntry();
        dayEntry.amount = TwoDecimalRounder.roundTwoDecimals(order.getTotalAmount(), precision);
        dayEntry.amountExTax = TwoDecimalRounder.roundTwoDecimals(order.getTotalAmount(), precision); //TwoDecimalRounder.roundTwoDecimals(order.getTotalAmount() - order.getTotalAmountVat());
        dayEntry.isIncome = true;
        dayEntry.orderId = order.id;
        dayEntry.incrementalOrderId = order.incrementOrderId;
        dayEntry.date = shouldDoublePost ? order.rowCreatedDate : order.paymentDate;
        dayEntry.accountingNumber = getAccountingNumberForPaymentApplicationId(order.payment.getPaymentTypeId());
        return dayEntry;
    }

    private List<DayEntry> createEntriesOfPriceMatrix(Order order, CartItem item) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            List<DayEntry> entries = new ArrayList();

            for (String dateString : item.priceMatrix.keySet()) {
                DayEntry entry = new DayEntry();
                entry.cartItemId = item.getCartItemId();
                entry.orderId = order.id;
                entry.incrementalOrderId = order.incrementOrderId;
                entry.isActualIncome = item.getProduct().isActuallyIncome();
                entry.amount = TwoDecimalRounder.roundTwoDecimals(item.priceMatrix.get(dateString), precision);
                entry.amountExTax = TwoDecimalRounder.roundTwoDecimals(item.getPriceMatrixWithoutTax(dateString), precision);
                
                entry.date = sdf.parse(dateString+" 09:00:00");
                
                if (order.overrideAccountingDate != null)
                    entry.date = order.overrideAccountingDate;
        
                entry.accountingNumber = getAccountingNumberForProduct(item);
                entries.add(entry);
            }

            return entries;
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<DayEntry> createEntriesOfPricePmsAddons(Order order, CartItem item) {
        List<DayEntry> entries = new ArrayList();
        
        for (PmsBookingAddonItem i : item.itemsAdded) {
            double total = ((double)i.count * i.price); 
            double taxFactor = 1;
            
            if (item.getProduct().taxGroupObject != null) {
                taxFactor = item.getProduct().taxGroupObject.getTaxRate() + 1;
            }             
            
            DayEntry entry = new DayEntry();
            entry.cartItemId = item.getCartItemId();
            entry.orderId = order.id;
            entry.amount = TwoDecimalRounder.roundTwoDecimals(total, precision);
            entry.amountExTax = TwoDecimalRounder.roundTwoDecimals((total / taxFactor), precision);
            entry.incrementalOrderId = order.incrementOrderId;
            entry.date = i.date;
            entry.isActualIncome = item.getProduct().isActuallyIncome();
            if (order.overrideAccountingDate != null)
                entry.date = order.overrideAccountingDate;
            entry.accountingNumber = getAccountingNumberForProduct(item);
            
            entries.add(entry);
        }
        
        return entries;
    }

    private DayEntry createNormalOrderEntry(Order order, CartItem item) {
        DayEntry entry = new DayEntry();
        entry.cartItemId = item.getCartItemId();
        entry.orderId = order.id;
        entry.incrementalOrderId = order.incrementOrderId;
        entry.isActualIncome = item.getProduct().isActuallyIncome();
        entry.amount = TwoDecimalRounder.roundTwoDecimals(item.getTotalAmount(), precision);
        entry.amountExTax = TwoDecimalRounder.roundTwoDecimals(item.getTotalEx(), precision);
        entry.date = order.rowCreatedDate;
        
        if (order.overrideAccountingDate != null)
            entry.date = order.overrideAccountingDate;
        
        entry.accountingNumber = getAccountingNumberForProduct(item);
        return entry;
    }

    private boolean validateDayEntries(List<DayEntry> orderDayEntries, Order order) {
        BigDecimal sum = new BigDecimal(0D);
        BigDecimal sumEx = new BigDecimal(0D);
        
        sum = TwoDecimalRounder.roundTwoDecimals(order.getTotalAmount(), precision);
        sumEx = TwoDecimalRounder.roundTwoDecimals(order.getTotalAmount() - order.getTotalAmountVat(), precision);
        
        for (DayEntry dayEntry : orderDayEntries) {
            if (!dayEntry.isIncome) {
                sum = sum.subtract(dayEntry.amount);
                sumEx = sum.subtract(dayEntry.amountExTax);
            }
        };
        
        // We are not able to match this 100% due to rounding problems.
        if ( sum.doubleValue() > 0.5 && sum.doubleValue() < -0.5) {
            return false;
        }
        
        if ( sumEx.doubleValue() > 0.5 && sumEx.doubleValue() < -0.5) {
            return false;
        }
        
        return true;
    }

    private void setPostAndPreFlags(List<DayEntry> orderDayEntries, Order order) {
        DayEntry income = orderDayEntries.stream()
                .filter(o -> o.isIncome)
                .findFirst()
                .orElse(null);
        
        orderDayEntries.stream()
                .filter(o -> !o.isIncome)
                .forEach(item -> {
                    Date startTimeForDay = calculateStartTimeForDate(item.date);
                    Date endTimeForDay = calculateEndTimeForDate(item.date);
                    
                    item.isPrePayment = income != null && income.date.before(startTimeForDay);
                    item.isAccrued = income == null || income.date.after(endTimeForDay) || income.date.equals(endTimeForDay);
                });
    }
    
    public DayIncome getDayIncome(Date date) {
        return dayIncomes.stream()
                .filter(dayIncome -> dayIncome.within(date))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Adds all the day entries to the specific day-span
     * 
     * @param orderDayEntries 
     */
    private void addToDayIncome(List<DayEntry> orderDayEntries) {
        orderDayEntries.stream()
                .forEach(dayEntry -> {
                    DayIncome income = getDayIncome(dayEntry.date);
                    if (income != null) {
                        income.dayEntries.add(dayEntry);
                    }
                });
    }
    
    public static void main(String[] args) throws ParseException, UnknownHostException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = dateFormatter.parse("2018-10-01 00:00:00");
        Date end = dateFormatter.parse("2018-10-31 00:00:00");
        
        Database database = new Database();
        
        List<Order> orders = database.getAll("OrderManager", "fd2fecef-1ca1-4231-86a6-0ec445fbac83")
            .filter(dataCommon -> dataCommon.isOf(Order.class))
            .map(dataCommon -> (Order)dataCommon)
            .filter(o -> !o.isSamleFaktura())
            .collect(Collectors.toList());
        
        long time = System.currentTimeMillis();
        OrderDailyBreaker dayBreaker = new OrderDailyBreaker(orders, start, end, null, null, false);
        dayBreaker.breakOrders();
        
        List<DayIncome> dayIncomes = dayBreaker.getDayIncomes();
        
        for (DayIncome dayIncome : dayIncomes) {
            BigDecimal totalForMonth = new BigDecimal(0D);
            
            for (DayEntry dayEntry : dayIncome.dayEntries) {
                if (dayEntry.isIncome)
                    continue;
                
                totalForMonth = totalForMonth.add(dayEntry.amount);
            }
            
            System.out.println(totalForMonth);
        }
    }

    private void correctRoundingProblemsDueToExTaxes(List<DayEntry> orderDayEntries) {
        BigDecimal sum = new BigDecimal(0D);
        for (DayEntry dayEntry : orderDayEntries) {
            if (dayEntry.isIncome) {
                sum = sum.add(dayEntry.amountExTax);
            } else {
                sum = sum.subtract(dayEntry.amountExTax);
            }
        };
        
        
        DayEntry incomeDay = getDayIncome(orderDayEntries);
        
        if (incomeDay == null) {
            return;
        }
        
        if ( sum.doubleValue() < 0.5 && sum.doubleValue() > -0.5 && sum.doubleValue() != 0) {
            incomeDay.amountExTax = incomeDay.amountExTax.subtract(sum);
        }
    }

    private DayEntry getDayIncome(List<DayEntry> orderDayEntries) throws RuntimeException {
        List<DayEntry> incomeDays = orderDayEntries.stream()
                .filter(o -> o.isIncome)
                .collect(Collectors.toList());
        
        // This should actually be possible.
        if (incomeDays.size() > 1) {
            throw new RuntimeException("Multiple income days?");
        }
        
        if (incomeDays.size() < 1) {
            return null;
        }
        
        DayEntry incomeDay = incomeDays.get(0);
        return incomeDay;
    }

    
    private String getAccountingNumberForPaymentApplicationId(String paymentId) {
        if (paymentId == null) {
            return null;
        }
        
        StorePaymentConfig config = paymentManager.getStorePaymentConfiguration(paymentId);
        
        if (config != null) {
            return config.userCustomerNumber;
        }

        return null;
    }
    
    private String getAccountingNumberForPaymentApplicationId_paid(String paymentId) {
        if (paymentId == null) {
            return null;
        }
        
        StorePaymentConfig config = paymentManager.getStorePaymentConfiguration(paymentId);
        
        if (config != null) {
            return config.userCustomerNumberPaid;
        }

        return null;
    }

    private String getAccountingNumberForProduct(CartItem item) throws DailyIncomeException {
        if (ignoreConfig) {
            return "";
        }
        
        String result = getAccountingNumberForProduct(item.getProduct().id);
        
        if (result == null || result.isEmpty()) {
            throw new DailyIncomeException("Could not find accounting number for product: " + item.getProduct().name);
        }
        
        return result;
    }
    
    public String getAccountingNumberForProduct(String productId) {
        return productManager.getProduct(productId).accountingAccount;
    }

    private void calculatePrePaidAndAccrued(List<DayEntry> orderDayEntries, Order order) {
        DayEntry income = getDayIncome(orderDayEntries);
        
        if (income != null) {
            income.accruedAmount = calculateAccruedAmount(orderDayEntries, true);
            income.prePaidAmount = calculatePrepaidAmount(orderDayEntries, true);
            
            income.accruedAmountExTaxes = calculateAccruedAmount(orderDayEntries, false);
            income.prePaidAmountExTaxes = calculatePrepaidAmount(orderDayEntries, false);
            
            income.sameDayPayment = income.amount.subtract(income.accruedAmount).subtract(income.prePaidAmount);
            income.sameDayPaymentExTaxes = income.amount.subtract(income.accruedAmountExTaxes).subtract(income.prePaidAmountExTaxes);
        }
    }

    private BigDecimal calculateAccruedAmount(List<DayEntry> orderDayEntries, boolean incTaxes) {
        BigDecimal retVal = new BigDecimal(0D);
        
        List<DayEntry> entries = orderDayEntries.stream()
                .filter(entry -> !entry.isIncome)
                .filter(entry -> entry.isAccrued)
                .collect(Collectors.toList());
        
        for (DayEntry dayEntry : entries) {
            if (incTaxes) {
                retVal = retVal.add(dayEntry.amount);
            } else {
                retVal = retVal.add(dayEntry.amount);
            }
        }
        
        return retVal;
    }

    private BigDecimal calculatePrepaidAmount(List<DayEntry> orderDayEntries, boolean incTaxes) {
        BigDecimal retVal = new BigDecimal(0D);
        
        List<DayEntry> entries = orderDayEntries.stream()
                .filter(entry -> !entry.isIncome)
                .filter(entry -> entry.isPrePayment)
                .collect(Collectors.toList());
        
        for (DayEntry dayEntry : entries) {
            if (incTaxes) {
                retVal = retVal.add(dayEntry.amount);
            } else {
                retVal = retVal.add(dayEntry.amountExTax);
            }
        }
        
        return retVal;
    }

    private void validateAccrudAndPrepaidPayments(List<DayEntry> orderDayEntries) {
        DayEntry income = getDayIncome(orderDayEntries);
        
        if (income == null) {
            return;
        }
        
        BigDecimal total = income.sameDayPayment.add(income.accruedAmount).add(income.prePaidAmount);
        
        if (total.compareTo(income.amount) != 0) {
            throw new DailyIncomeException("Accrude amount + prepaid amount + sameday != total");
        }
    }

    private Date calculateEndTimeForDate(Date date) {
        if (date.equals(end))
            return date;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, whatHourOfDayStartADay);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    private Date calculateStartTimeForDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, whatHourOfDayStartADay);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void invertAccountNumbers(List<DayEntry> orderDayEntries) {
        orderDayEntries.stream()
                .filter(o -> !o.isIncome)
                .forEach(o -> {
                    o.amount = o.amount.multiply(new BigDecimal(-1));
                    o.amountExTax = o.amountExTax.multiply(new BigDecimal(-1));
                });
    }

    private void createOffsetAccountEntries(List<DayEntry> orderDayEntries, Order order) throws CloneNotSupportedException {
        List<DayEntry> toAdd = new ArrayList();
        
        StorePaymentConfig storePaymentConfig = paymentManager.getStorePaymentConfiguration(order.getPaymentApplicationId());
        
        if (ignoreConfig) {
            storePaymentConfig = new StorePaymentConfig();
            storePaymentConfig.offsetAccountingId_prepayment = "1";
            storePaymentConfig.offsetAccountingId_accrude = "2";
        }
        
        for (DayEntry entry : orderDayEntries) {
            DayEntry entryAccrude = entry.clone();
            entryAccrude.isOffsetRecord = true;
            entryAccrude.isIncome = false;
            
            DayEntry entryPrePayment = entry.clone();
            entryPrePayment.isOffsetRecord = true;
            entryPrePayment.isIncome = false;
            
            if (entry.isPrePayment) {
                checkStorePayment(storePaymentConfig, order);
                entryPrePayment.accountingNumber = storePaymentConfig.offsetAccountingId_prepayment;
                entryPrePayment.amount = entryPrePayment.amount.multiply(new BigDecimal(-1D));                
                entryPrePayment.amountExTax = entryPrePayment.amount; //entryPrePayment.amountExTax.multiply(new BigDecimal(-1D));                
                toAdd.add(entryPrePayment);
            }
            
            if (entry.isAccrued) {
                checkStorePayment(storePaymentConfig, order);
                entryAccrude.accountingNumber = storePaymentConfig.offsetAccountingId_accrude;
                entryAccrude.amount = entryAccrude.amount.multiply(new BigDecimal(-1D));                
                entryAccrude.amountExTax = entryAccrude.amount; //entryAccrude.amountExTax.multiply(new BigDecimal(-1D));                
                toAdd.add(entryAccrude);
            }
            
            if (entry.isIncome) {
                checkStorePayment(storePaymentConfig, order);
                entryPrePayment.accountingNumber = storePaymentConfig.offsetAccountingId_prepayment;
                entryAccrude.accountingNumber = storePaymentConfig.offsetAccountingId_accrude;
                
                entryAccrude.amount = entryAccrude.accruedAmount;
                entryAccrude.amountExTax = entryAccrude.amount; //entryAccrude.accruedAmountExTaxes;
                
                entryPrePayment.amount = entryPrePayment.prePaidAmount;
                entryPrePayment.amountExTax = entryPrePayment.amount; //entryPrePayment.prePaidAmountExTaxes;
                
                entryAccrude.amount = entryAccrude.amount.multiply(new BigDecimal(-1D));                
                entryPrePayment.amount = entryPrePayment.amount.multiply(new BigDecimal(-1D));                
                
                entryAccrude.amountExTax = entryAccrude.amountExTax.multiply(new BigDecimal(-1D));                
                entryPrePayment.amountExTax = entryPrePayment.amountExTax.multiply(new BigDecimal(-1D));                
                
                toAdd.add(entryAccrude);
                toAdd.add(entryPrePayment);
            }
        }
        
        orderDayEntries.addAll(toAdd);
    }

    private boolean ignoreOrderDueToDisabledAccountingPaymentMethod(Order order) {
        String number = getAccountingNumberForPaymentApplicationId(order.getPaymentApplicationId());
        return number != null && number.equals("-1");
    }

    private void checkStorePayment(StorePaymentConfig storePaymentConfig, Order order) {
        if (storePaymentConfig == null) {
            throw new NullPointerException("Missing paymentconfig for " + order.payment.readablePaymentType() + ", orderid: " + order.incrementOrderId);
        }
    }

    private boolean shouldRegisterPaymentTransactions(Order order) {
        
        StorePaymentConfig config = paymentManager.getStorePaymentConfiguration(order.getPaymentApplicationId());
        
        if (config != null) {
            return config.userCustomerNumberPaid != null && !config.userCustomerNumberPaid.isEmpty();
        }
        
        return false;
    }

    private void createPaymentRecords(List<DayEntry> orderDayEntries, Order order)  {
        if (!shouldRegisterPaymentTransactions(order)) {
            return;
        }
        
        if (order.orderTransactions == null)
            return;
        
        for (OrderTransaction orderTransaction : order.orderTransactions) {
            DayEntry dayEntry = new DayEntry();
            dayEntry.amount = TwoDecimalRounder.roundTwoDecimals(orderTransaction.amount, precision);
            dayEntry.amountExTax = TwoDecimalRounder.roundTwoDecimals(orderTransaction.amount, precision);
            dayEntry.accountingNumber = getAccountingNumberForPaymentApplicationId(order.getPaymentApplicationId());
            dayEntry.orderId = order.id;
            dayEntry.incrementalOrderId = order.incrementOrderId;
            dayEntry.date = orderTransaction.date;
            orderDayEntries.add(dayEntry);
            
            try {
                dayEntry = dayEntry.clone();
                dayEntry.accountingNumber = getAccountingNumberForPaymentApplicationId_paid(order.getPaymentApplicationId());
                dayEntry.amount = dayEntry.amount.multiply(new BigDecimal(-1));
                dayEntry.amountExTax = dayEntry.amountExTax.multiply(new BigDecimal(-1));
                orderDayEntries.add(dayEntry);
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<DayIncome> getErrors() {
        return errors.stream()
                .map(error -> {
                    DayIncome inc = new DayIncome();
                    inc.errorMsg = error;
                    return inc;
                })
                .collect(Collectors.toList());
    }

    private DayEntry createCashWithdrawalEntries(Order order, List<DayEntry> orderDayEntries) {
        try {
            if (order.isFullyPaid()) {
                DayEntry dayEntry = new DayEntry();
                dayEntry.amount = TwoDecimalRounder.roundTwoDecimals(order.cashWithdrawal, precision);
                dayEntry.amountExTax = TwoDecimalRounder.roundTwoDecimals(order.cashWithdrawal, precision);
                dayEntry.accountingNumber = getAccountingNumberForPaymentApplicationId("565ea7bd-c56b-41fe-b421-18f873c63a8f");
                dayEntry.orderId = order.id;
                dayEntry.incrementalOrderId = order.incrementOrderId;
                dayEntry.date = order.paymentDate;
                orderDayEntries.add(dayEntry);

                dayEntry = dayEntry.clone();
                dayEntry.amount = TwoDecimalRounder.roundTwoDecimals(order.cashWithdrawal, precision).multiply(new BigDecimal(-1));
                dayEntry.amountExTax = TwoDecimalRounder.roundTwoDecimals(order.cashWithdrawal, precision).multiply(new BigDecimal(-1));
                dayEntry.accountingNumber = getAccountingNumberForPaymentApplicationId(order.getPaymentApplicationId());
                dayEntry.orderId = order.id;
                dayEntry.incrementalOrderId = order.incrementOrderId;
                dayEntry.date = order.paymentDate;
                orderDayEntries.add(dayEntry);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        
        return null;
    }

    private void createVatLines(Order order, List<DayEntry> orderDayEntries) {
        List<DayEntry> entriesToCreateVATtransactionOf = orderDayEntries.stream()
                .filter(transaction -> transaction.isActualIncome && !transaction.isOffsetRecord)
                .collect(Collectors.toList());
                
        for (DayEntry entry : entriesToCreateVATtransactionOf) {
            Product product = order.cart.getCartItem(entry.cartItemId).getProduct();
            TaxGroup taxGroup = product.taxGroupObject;
            
            if (taxGroup == null) {
                continue;
            }
            
            if (taxGroup.accountingTaxAccount == null || taxGroup.accountingTaxAccount.isEmpty()) {
                continue;
            }
            
            BigDecimal taxValue = entry.amount.subtract(entry.amountExTax);
            
            DayEntry dayEntry = new DayEntry();
            dayEntry.amount = taxValue;
            dayEntry.amountExTax = taxValue;
            dayEntry.accountingNumber = taxGroup.accountingTaxAccount;
            dayEntry.date  = entry.date;
            dayEntry.orderId  = entry.orderId;
            dayEntry.cartItemId  = entry.cartItemId;
            dayEntry.isTaxTransaction = true;
            orderDayEntries.add(dayEntry);
        }
    }

}
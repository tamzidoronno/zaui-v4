/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import com.google.gson.Gson;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pdf.data.AccountingDetails;
import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import com.thundashop.core.usermanager.data.User;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class Order extends DataCommon implements Comparable<Order> {
    public Boolean triedTransferredToAccountingSystem = false;
    public Boolean transferredToAccountingSystem = false;
    public Date transferredToCreditor = null;
    
    /**
     * This variable is wrong and should be removed. The one above is the corrent one.
     */
    private Boolean transferedToAccountingSystem = false;
    public Date transferToAccountingDate = null;
    public Date shouldHaveBeenTransferredToAccountingOnDate = null;
    
    public Date needCollectingDate = null;
    public String paymentTransactionId = "";
    public Shipping shipping;
    public Date shippingDate;
    public Payment payment = new Payment();
    public String session;
    public String recieptEmail = "";
    public String trackingNumber = "";
    public long incrementOrderId = 0;
    public String reference = "";
    public boolean activated = false;
    public boolean testOrder = false;
    public boolean captured = false;
    public List<CardTransaction> transactions = new ArrayList();
    public List<OrderLog> logLines = new ArrayList();
    public List<String> notifications = new ArrayList();
    public String invoiceNote = "";
    public boolean closed = false;
    public List<String> creditOrderId = new ArrayList();
    public boolean isCreditNote = false;
    public Date startDate = null;
    public Date endDate = null;
    public Date paymentDate = null;
    public String markedAsPaidByUserId = "";
    public Integer paymentTerms = 15;
    public String parentOrder = "";
    public boolean sentToCustomer = false;
    private boolean cleaned = false;
    public Date dateTransferredToAccount;
    public boolean avoidAutoSending = false;
    public Integer dueDays;
    public String createByManager = "";
    public String kid = "";
    public boolean isVirtual = false;
    public boolean forcedOpen = false;
    public boolean warnedNotAbleToPay = false;
    public String attachedToRoom = null;
    public LinkedList<OrderShipmentLogEntry> shipmentLog = new LinkedList();
    public List<OrderTransaction> orderTransactions = new ArrayList();
    
    /**
     * This will be populated if the order is created by merging multiple 
     * of other orders.
     */
    public List<String> createdBasedOnOrderIds = new ArrayList();
    public boolean bookingHasBeenDeleted;
    public Integer sendRegningId = 0;
    public Date sentToCustomerDate = null;
    public String sentToEmail = "";
    public String sentToPhone = "";
    public String sentToPhonePrefix = "";
    public Date chargeAfterDate = null;
    public boolean warnedNotPaid = false;
    public Date tryAutoPayWithDibs = null;
    
    /**
     * If this order has been transferred to an accountingsystem
     * and there is a two way communincation, this field 
     * will store the information needed to find the order again
     * in the accountingssystem.
     */
    public String accountingReference = "";
    @Transient
    public String wubookid = "";
    public boolean warnedNotAbleToCapture = false;
    
    @Transient
    private Date periodeDaySleptStart;
    @Transient
    private Date periodeDaySleptEnd;
    
    @Transient
    private Calendar cal2;
    
    @Transient
    private Calendar cal1;
    
    public boolean isUnderConstruction = false;
    
    public Order jsonClone() {
        Gson gson = new Gson();
        String gsonOrder = gson.toJson(this);
        Order orderNew = gson.fromJson(gsonOrder, Order.class);
        orderNew.id = UUID.randomUUID().toString();
        orderNew.expiryDate = null;
        orderNew.rowCreatedDate = new Date();
        orderNew.triedTransferredToAccountingSystem = false;
        orderNew.transferredToAccountingSystem = false;
        orderNew.createdDate = new Date();

        if (orderNew.cart != null) {
            orderNew.cart.rowCreatedDate = new Date();
        }

        return orderNew;
    }
    
    public boolean isMatrixAndItemsValid() {
        Double total = 0.0;
        boolean found = false;
        for(CartItem item : cart.getItems()) {
            item.correctIncorrectCalculation();
            if(item.itemsAdded != null && !item.itemsAdded.isEmpty()) {
               for(PmsBookingAddonItem pmsitem : item.itemsAdded) {
                   if (pmsitem == null ) {
                       continue;
                   }
                   
                   if (pmsitem.count == null) {
                       pmsitem.count = 0;
                   }
                   
                   if (pmsitem.price == null) {
                       pmsitem.price = 0D;
                   }
                   
                   total += (pmsitem.count * pmsitem.price);
                   found = true;
               }
            }
            if(item.priceMatrix != null && !item.priceMatrix.isEmpty()) {
                for(Double val : item.priceMatrix.values()) {
                    total += val;
                    found = true;
                }
            }
        }
        
        Double orderTotal = getTotalAmount();
        long ordertotalcheck = Math.round(orderTotal);
        long ordercheck = Math.round(total);

        if(found && ordercheck != ordertotalcheck) {
            return false;
        }
        return true;
    }
    
    public void generateKidLuhn(String byNumber, Integer length) {
        String result = checkSum(byNumber, true) + "";
        result = byNumber + result;
        int res = result.length();
        for(int i = 0; i < (length-res); i++) {
            result = "0" + result;
        }
        kid = result;
    }

	/**
	 * Validate a number string using Luhn algorithm
	 * 
	 * @param numberString
	 * @return
	 */
	public boolean validate(String numberString) {
		return checkSum(numberString) == 0;
	}

	/**
	 * Generate check digit for a number string. Assumes check digit or a place
	 * holder is already appended at end of the string.
	 * 
	 * @param numberString
	 * @return
	 */
	public int checkSum(String numberString) {
		return checkSum(numberString, false);
	}

	/**
	 * Generate check digit for a number string.
	 * 
	 * @param numberString
	 * @param noCheckDigit
	 *            Whether check digit is present or not. True if no check Digit
	 *            is appended.
	 * @return
	 */
	public int checkSum(String numberString, boolean noCheckDigit) {
		int sum = 0, checkDigit = 0;
		
		if(!noCheckDigit)
			numberString = numberString.substring(0, numberString.length()-1);
			
		boolean isDouble = true;
		for (int i = numberString.length() - 1; i >= 0; i--) {
			int k = Integer.parseInt(String.valueOf(numberString.charAt(i)));
			sum += sumToSingleDigit((k * (isDouble ? 2 : 1)));
			isDouble = !isDouble;
		}

		if ((sum % 10) > 0)
			checkDigit = (10 - (sum % 10));

		return checkDigit;
	}

	private int sumToSingleDigit(int k) {
            if (k < 10)
                    return k;
            return sumToSingleDigit(k / 10) + (k % 10);
	}

    public void checkForCorrectingTransferredToAccounting() {
        transferredToAccountingSystem = transferedToAccountingSystem;
    }
    
    public boolean useForStatistic() {
        if (status == Order.Status.CANCELED || status == Order.Status.PAYMENT_FAILED) {
            return false;
        }
        
        if (testOrder) {
            return false;
        }
        
        return true;
    }
    
    public void changePaymentType(Application paymentApplication) {
        if (payment == null) {
            payment = new Payment();
        }
        
        payment.paymentType = "ns_"+paymentApplication.id.replace("-", "_")+"\\"+paymentApplication.appName;
    }

    public boolean triedAutoPay() {
        if(payment.triedAutoPay.isEmpty()) {
            return false;
        }
        
        if(payment.triedAutoPay.size() >= 50) {
            //Tried to pay with card for 50 days.. time to give up.
            return true;
        }
        Calendar yestercal = Calendar.getInstance();
        yestercal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterdate = yestercal.getTime();
        for(Date lastTime : payment.triedAutoPay) {
            if(lastTime.after(yesterdate)) {
                return true;
            }
        }
        
        return false;
       
    }

    public boolean createdOnDay(Date time) {
        Calendar createdCal = Calendar.getInstance();
        createdCal.setTime(rowCreatedDate);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);
        
        if((createdCal.get(Calendar.YEAR) == timeCal.get(Calendar.YEAR)) && 
            (createdCal.get(Calendar.DAY_OF_YEAR) == timeCal.get(Calendar.DAY_OF_YEAR))) {
            return true;
        }
        return false;
    }
    
    public boolean paidOnDay(Date time) {
        if(paymentDate == null) {
            return false;
        }
        Calendar createdCal = Calendar.getInstance();
        createdCal.setTime(paymentDate);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);
        
        if((createdCal.get(Calendar.YEAR) == timeCal.get(Calendar.YEAR)) && 
            (createdCal.get(Calendar.DAY_OF_YEAR) == timeCal.get(Calendar.DAY_OF_YEAR))) {
            return true;
        }
        return false;
    }

    public void doFinalize() {
        if(cart == null || cart.getItems() == null) {
            return;
        }
        for(CartItem item : cart.getItems()) {
            item.doFinalize();
        }
        if(!closed && !forcedOpen) {
            if(status == Order.Status.PAYMENT_COMPLETED) {
                closed = true;
            }
            if(transferredToAccountingSystem) {
                closed = true;
            }
        }
        
        if (forcedOpen) {
            closed = false;
        }
        if(sentToPhone != null && !sentToPhone.isEmpty()) {
            OrderShipmentLogEntry entry = new OrderShipmentLogEntry();
            entry.date = sentToCustomerDate;
            entry.type = "phone";
            entry.address = "+" + sentToPhonePrefix + sentToPhone;
            shipmentLog.add(entry);
        }
        if(sentToEmail != null && !sentToEmail.isEmpty()) {
            OrderShipmentLogEntry entry = new OrderShipmentLogEntry();
            entry.date = sentToCustomerDate;
            entry.type = "email";
            entry.address = sentToEmail;
            shipmentLog.add(entry);
        }
        sentToEmail = "";
        sentToPhone = null;
        sentToPhonePrefix = null;
        sentToCustomerDate = null;
    }

    public void markAsSent(String type, String adress) {
        OrderShipmentLogEntry entry = new OrderShipmentLogEntry();
        entry.date = new Date();
        entry.type = type;
        entry.address = adress;
        shipmentLog.add(entry);
    }
    
    public boolean needToBeTranferredToCreditor() {
        if(transferredToCreditor != null) {
            return false;
        }
        
        if(status == Order.Status.PAYMENT_COMPLETED) {
            return false;
        }
        
        for(CartItem item : cart.getItems()) {
            if(item.periodeStart == null) {
                continue;
            }
            if(item.getProduct().minPeriode <= 0) {
                continue;
            }
            
            long diff = item.getStartingDate().getTime() - item.periodeStart.getTime();
            
            diff /= 1000;
            
            if(diff < item.getProduct().minPeriode) {
                return true;
            }
        }
        
        return false;
    }

    public boolean isInvoice() {
        if(payment != null && payment.paymentType != null && payment.paymentType.toLowerCase().contains("invoice")) {
            return true;
        }
        return false;
    }

    public boolean isExpedia() {
        if(payment != null && payment.paymentType != null && payment.paymentType.toLowerCase().contains("expedia")) {
            return true;
        }
        return false;
    }    
    
    /**
     * Added because there was lots of bogus data added to translation, causing the orders to become huge objects.
     * 
     * @return 
     */
    public boolean cleanMe() {
        if (cart == null)
            return false;
        
        if (cleaned)
            return false;
        
        for (CartItem cartItem : cart.getItems()) {
            cartItem.getProduct().validateTranslationMatrix();
        }
        
        cleaned = true;
        return true;
    }

    public boolean hasPaymentMethod(String paymentMethod) {
        if(payment != null && payment.paymentType != null && paymentMethod != null) {
            String methodToTest = paymentMethod.replace("-", "_");
            if(payment.paymentType.contains(methodToTest)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPaymentDate(Date paymentDate) {
        if (paymentDate == null && this.paymentDate == null) {
            return false;
        }
        
        if (this.paymentDate == null) {
            return false;
        }
        
        return this.paymentDate.equals(paymentDate);
    }

    public boolean hasFreezeItem() {
        for(CartItem item : cart.getItems()) {
            if(item != null && item.getProduct() != null && item.getProduct().id != null && item.getProduct().id.equals("freezeSubscription")) {
                return true;
            }
        }
        return false;
    }

    public boolean isRecent() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -5);
        if(rowCreatedDate == null) {
            return true;
        }
        return rowCreatedDate.after(cal.getTime());
    }

    public boolean notChargeYet() {
        if(chargeAfterDate == null) {
            return false;
        }
        if(!chargeAfterDate.before(new Date())) {
            return true;
        }
        return false;
    }

    public boolean createdOnMonth(Date date) {
        Calendar check = Calendar.getInstance();
        Calendar createDate = Calendar.getInstance();
        createDate.setTime(rowCreatedDate);
        check.setTime(date);
        return (check.get(Calendar.MONTH) == createDate.get(Calendar.MONTH) && check.get(Calendar.YEAR) == createDate.get(Calendar.YEAR));
    }

    public String createThermalPrinterReciept(AccountingDetails details, User user) {
        SimpleDateFormat sm = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        
        String res = "";
        res += "Date    : " + sm.format(new Date()) + "\n";
        res += "Orderid : " + incrementOrderId + "\n";
        res += "Org id  : " + details.vatNumber + "\n";
        res += "Website : " + details.webAddress + "\n";
        res += "Email   : " + details.contactEmail + "\n";
        res += "\n";

        res += details.companyName + "\n";
        res += details.address + "\n";
        res += details.postCode + " " + details.city + "\n";
        res += "\n";

        
        double total = cart.getShippingCost() + cart.getTotal(true);
        total = Math.round(total);
        
        for (CartTax cartTax : cart.getCartTaxes()) {
            res += "Tax " + cartTax.taxGroup.taxRate + "% : " + Math.round(cartTax.sum) + " kr\n";
        }
        res += "Total     : " + total + " kr\n";
        
        res += "\n";
        
        res += user.fullName + "\n";
        if(user.cellPhone != null && user.cellPhone.isEmpty()) {
            res += "Phone: " + "(" + user.prefix + ")" + user.cellPhone + "\n";
        }
        if(user.address != null) {
            if(user.address.address != null && !user.address.address.trim().isEmpty()) {
                res += user.address.address + "\n";
            }
            if(user.address.postCode != null && !user.address.postCode.trim().isEmpty()) {
                res += user.address.postCode + " " + user.address.city + "\n";
            }
        }
        res += "\n";
        
        for(CartItem item : cart.getItems()) {
            res += createLineText(item) + "\n";
        }
        
        res += "\n";
        res = wrapText(res);
        
        return res;
    }

    private String createLineText(CartItem item) {
        String lineText = "";
        String startDate = "";
        SimpleDateFormat sm = new SimpleDateFormat("dd.MM.yyyy");
        if(item.startDate != null) {
            startDate = sm.format(item.startDate);
        }

        String endDate = "";
        if(item.endDate != null) {
            endDate = sm.format(item.endDate);
        }
        
        String startEnd = "";
        if(startDate != null && endDate != null && !endDate.isEmpty() && !startDate.isEmpty()) {
            startEnd = startDate + " - " + endDate + "\n";
        }
        
        if(!item.getProduct().additionalMetaData.trim().isEmpty()) {
            lineText = item.getProduct().name + "\n";
            lineText += item.getProduct().additionalMetaData + "\n";
            lineText += startEnd + "\n";
        } else {
            String mdata = item.getProduct().metaData;
            lineText = item.getProduct().name.trim()  + "\n";
            if(!mdata.trim().isEmpty()) {
//                lineText += mdata.trim() + "\n";
            }
            lineText += startEnd;
        }
        
        lineText = lineText.trim();
        lineText = makeSureIsOkay(lineText);
        return lineText;
    }
    
    private String makeSureIsOkay(String text) {
        if(text == null) {
            return "";
        }
        return text.replaceAll(",", " ");
    }

    private String wrapText(String res) {
        String[] lines = res.split("\n");
        String result = "";
        for(String line : lines) {
            if(line.length() > 29) {
                result += line.substring(0, 29) + "\n";
                result += line.substring(29) + "\n";
            } else {
                result += line + "\n";
            }
        }
        return result;
    }

    public double getTotalAmount() {
        double amount = 0.0;
        for(CartItem item : cart.getItems()) {
            amount += item.getTotalAmount();
        }
        return amount;
    }

    public double getTotalAmountUnfinalized() {
        double amount = 0.0;
        for(CartItem item : cart.getItemsUnfinalized()) {
            amount += item.getTotalAmount();
        }
        return amount;
    }

    public boolean sentToCustomer() {
        return shipmentLog.size() > 0;
    }

    public boolean isNullOrder() {
        if (cart == null) {
            return true;
        }
        
        return cart.isNullCart();
    }

    public String getPaymentApplicationId() {
        if (payment == null || payment.paymentType.isEmpty()) {
            return null;
        }
        
        String paymentId = payment.paymentType.replace("ns_", "");
        String[] splitted = paymentId.split("\\\\");
        if (splitted.length > 0) {
            paymentId = splitted[0];
            paymentId = paymentId.replace("_", "-");
            return paymentId;
        }
        
        return null;
    }
    
    public boolean isOrderFinanciallyRelatedToDates(Date start, Date end) {
        if (createdBetween(start, end)) {
            return true;
        }
        
        long startTime = start.getTime();
        long endTime = end.getTime();
        
        if (intercepts(paymentDate, startTime, endTime)) {
            return true;
        }
        
        if (intercepts(transferToAccountingDate, startTime, endTime)) {
            return true;
        }
        
        if (cart != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            
            for (CartItem item : cart.getItems()) {
                if (item.priceMatrix != null) {
                    for (String date : item.priceMatrix.keySet()) {
                        try {
                            Date checkDate = sdf.parse(date);
                            if (intercepts(checkDate, startTime, endTime)) {
                                return true;
                            }
                        } catch (ParseException ex) {
                            System.out.println("failed");
                        }
                    }
                }
                
                if (intercepts(item.startDate, startTime, endTime)) {
                    return true;
                }
                
                if (intercepts(item.endDate, startTime, endTime)) {
                    return true;
                }
                
                if (intercepts(item.newEndDate, startTime, endTime)) {
                    return true;
                }
                
                if (intercepts(item.newStartDate, startTime, endTime)) {
                    return true;
                }
                
                if (item.itemsAdded != null) {
                    for (PmsBookingAddonItem addon : item.itemsAdded) {
                        if (intercepts(addon.date, startTime, endTime)) {
                            return true;
                        }  
                    } 
                }
            }
            
        }
        
        return false;
    }
    
    private boolean intercepts(Date dateToCheckAgainst, long startTime, long endTime) {
        if (dateToCheckAgainst == null) {
            return false;
        }
        
        long time = dateToCheckAgainst.getTime();
        
        if (startTime <= time && time <= endTime) {
            return true;
        }
        return false;
    }

    public boolean isBookingCom() {
        if(payment != null && payment.paymentType != null && payment.paymentType.toLowerCase().contains("bookingcomcollectpayments")) {
            return true;
        }
        return false;
    }

    private boolean isSameDay(Date date1, Date date2) {
        if(date1 == null || date2 == null) {
            return false;
        }
        
        if(!isSameDayByMilliSeconds(date1, date2)) {
            return false;
        }
        
        if(cal1 == null) {
            cal1 = Calendar.getInstance();
        }
        if(cal2 == null) {
            cal2 = Calendar.getInstance();
        }
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        return sameDay;
    }

    public boolean isForPeriodedaySlept(Date date) {
        if(periodeDaySleptStart == null) {
            periodeDaySleptStart = rowCreatedDate;
            Date start = getStartDateByItemsAndAddons();
            if(start != null) {
                periodeDaySleptStart = start;
            }
        }
        if(periodeDaySleptEnd == null) {
            periodeDaySleptEnd = rowCreatedDate;
            Date end = getEndDateByItemsAndAddons();
            if(end != null) {
                periodeDaySleptEnd = end;
            }
        }
        
        if(intercepts(date, periodeDaySleptStart.getTime(), periodeDaySleptEnd.getTime())) {
            return true;
        }
        if(isSameDay(date, periodeDaySleptStart)) {
            return true;
        }
        if(isSameDay(date, periodeDaySleptEnd)) {
            return true;
        }
        if(isSameDay(date, rowCreatedDate)) {
            return true;
        }
        return false;
    }

    private Date getStartDateByItemsAndAddons() {
        Date start = getStartDateByItems();
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            
        for (CartItem item : cart.getItems()) {
            if (item.priceMatrix != null) {
                for (String date : item.priceMatrix.keySet()) {
                    try {
                        Date checkDate = sdf.parse(date);
                        if(start == null || checkDate.before(start)) {
                            start = checkDate;
                        }
                    } catch (ParseException ex) {
                        System.out.println("failed");
                    }
                }
            }

            if(start == null || (item.startDate != null && item.startDate.before(start))) {
                start = item.startDate;
            }

            if (item.itemsAdded != null) {
                for (PmsBookingAddonItem addon : item.itemsAdded) {
                    if (start == null || start.after(addon.date)) {
                        start = addon.date;
                    }  
                } 
            }
        }
        return start;
    }


    private Date getEndDateByItemsAndAddons() {
        Date end = getEndDateByItems();
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            
        for (CartItem item : cart.getItems()) {
            if (item.priceMatrix != null) {
                for (String date : item.priceMatrix.keySet()) {
                    try {
                        Date checkDate = sdf.parse(date);
                        if(end == null || checkDate.after(end)) {
                            end = checkDate;
                        }
                    } catch (ParseException ex) {
                        System.out.println("failed");
                    }
                }
            }

            if(end == null || (item.endDate != null && item.endDate.after(end))) {
                end = item.endDate;
            }
            
            if (item.itemsAdded != null) {
                for (PmsBookingAddonItem addon : item.itemsAdded) {
                    if (end == null || end.before(addon.date)) {
                        end = addon.date;
                    }  
                } 
            }
        }
        return end;
    }

    private boolean isSameDayByMilliSeconds(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        if(diff < 0) {
            diff *= -1;
        }
        
        if(diff > (86400000*2)) {
            return false;
        }
        
        return true;
    }

    public void clearPeriodisation() {
        periodeDaySleptEnd = null;
        periodeDaySleptStart = null;
    }

    public Double getTotalAmountVat() {
        double total = getTotalAmount();
        double amount = 0.0;
        for(CartItem item : cart.getItems()) {
            amount += item.getTotalEx();
        }
        return total-amount;
    }

    public Date getDueDate() {
        if(dueDays == null) {
            dueDays = 14;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(rowCreatedDate);
        cal.add(Calendar.DAY_OF_YEAR, dueDays);
        return cal.getTime();
    }

    public void registerTransaction(Date date, Double amount, String userId) {
        OrderTransaction transaction = new OrderTransaction();
        transaction.date = date;
        transaction.amount = amount;
        transaction.userId = userId;
        orderTransactions.add(transaction);
    }

    public double getTransactionAmount() {
        double amountPaid = 0.0;
        for(OrderTransaction trans : orderTransactions) {
            amountPaid += trans.amount;
        }
        return amountPaid;
    }

    public boolean isFullyPaid() {
        double transactionAmount = getTransactionAmount();
        double total = getTotalAmount();
        if(total > transactionAmount) {
            return false;
        }
        return true;
    }


    public static class Status  {
        public static int CREATED = 1;
        public static int WAITING_FOR_PAYMENT = 2;
        public static int PAYMENT_FAILED = 3;
        public static int COMPLETED = 4;
        public static int CANCELED = 5;
        public static int SENT = 6;
        public static int PAYMENT_COMPLETED = 7;
        public static int COLLECTION_FAILED = 8;
        public static int NEEDCOLLECTING = 9;
        public static int SEND_TO_INVOICE = 10;
    }
    
    public Date createdDate = new Date();
    
    /**
     * This expiry date is used for recurring orders. Orders that has an expiry
     * date will automatically be renewed with a new order when it expires.
     */
    public Date expiryDate;

    public Integer recurringDays;

    public Integer recurringMonths = 1;

    /**
     * The users id for whom placed the order if order is created without user
     * been logged in, this will be empty
     */
    public String userId;
    
    public int status = Order.Status.CREATED;
    public Cart cart;
    
    public String getDateCreated() {
        if (createdDate == null) {
            return "";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        return sdf.format(createdDate);
    }
    
    public void setStatusCreated() {
        status = Status.CREATED;
    }
    
    public void setStatusPaymentFailed() {
        status = Status.PAYMENT_FAILED;
    }
    
    public void setStatusCompleted() {
        status = Status.COMPLETED;
    }
    
    public void setStatusCanceled() {
        status = Status.CANCELED;
    }
    
    @Override
    public int compareTo(Order o) {
        if(o.createdDate == null || createdDate == null) {
            return 0;
        }
        return createdDate.compareTo(o.createdDate);
    }
    
    public void updateCount(String cartItemId, Integer count) {
        if (cart.getCartItem(cartItemId) == null) {
            return;
        }
        cart.setProductCount(cartItemId, count);
    }
    
    public void removeAllItemsExcept(List<String> ids) {
        List<String> toRemove = new ArrayList();
        for(CartItem item : cart.getItems()) {
            toRemove.add(item.getCartItemId());
        }
        
        toRemove.removeAll(ids);
        
        for(String remove : toRemove) {
            cart.removeItem(remove);
        }
    }
    
    public void updatePrice(String cartItemId, double price) {
        if (cart.getCartItem(cartItemId) == null) {
            return;
        }
        
        double oldPrice = cart.getCartItem(cartItemId).getProduct().price;
        cart.updatePrice(cartItemId, price);
        
        OrderLog log = new OrderLog();
        log.userId = userId;
        log.description = "CartItem price changed price from " + oldPrice + " to " + price;
    }

    public Date getStartDateByItems() {
        Date start = null;
        for(CartItem item : cart.getItems()) {
            if(item != null && item.getStartingDate() != null && (start == null || start.after(item.getStartingDate()))) {
                start = item.getStartingDate();
            }
            if(item != null && item.getEndingDate()!= null && (start == null || start.after(item.getEndingDate()))) {
                start = item.getEndingDate();
            }
        }
        return start;
    }


    public Date getEndDateByItems() {
        Date end = null;
        for(CartItem item : cart.getItems()) {
            if(end == null || (item.getEndingDate() != null && end.before(item.getEndingDate()))) {
                end = item.getEndingDate();
            }
            if(end == null || (item.getStartingDate()!= null && end.before(item.getStartingDate()))) {
                end = item.getStartingDate();
            }
        }
        return end;
    }
    
    public boolean matchOnString(String searchWord) {
        if (searchWord == null || searchWord.isEmpty()) 
            return true;
        
        boolean match = false;
        
        if (cart != null && cart.address != null && cart.address.fullName != null) {
            match = cart.address.fullName.toLowerCase().contains(searchWord.toLowerCase());
        }
        if(searchWord.equals(incrementOrderId + "")) {
            return true;
        }
        
        // Add more search criterias here ? :D
        
        return match;
    }
    
    public boolean paymentDateWithin(Date from, Date to) {
        if (paymentDate == null)
            return false;
        
        long inDate = paymentDate.getTime();
        long fromL = from.getTime();
        long toL = to.getTime();
        
        return (fromL <= inDate &&  inDate <= toL);
    }
    
    public boolean isTransferBefore(Date end) {
        if (transferToAccountingDate != null && transferToAccountingDate.before(end)) {
            return true;
        }
        
        if (transferToAccountingDate != null && transferToAccountingDate.equals(end)) {
            return true;
        }
        
        return false;
    }
    
    public void resetTransferToAccounting() {
        transferredToAccountingSystem = false;
        triedTransferredToAccountingSystem  = false;
    }
    
    public void markAsTransferredToAccounting() {
        transferredToAccountingSystem = true;
        triedTransferredToAccountingSystem  = true;
    }
} 
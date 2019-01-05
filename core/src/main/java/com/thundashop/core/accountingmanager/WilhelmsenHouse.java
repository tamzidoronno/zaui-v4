package com.thundashop.core.accountingmanager;

import com.ibm.icu.util.Calendar;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ForStore;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.paymentmanager.StorePaymentConfig;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;

@ForStore(storeId="123865ea-3232-4b3b-9136-7df23cf896c6")
public class WilhelmsenHouse implements AccountingInterface {

    private UserManager userManager;
    private InvoiceManager invoiceManager;
    private OrderManager orderManager;
    private StoreApplicationPool storeApplicationPool;
    private AccountingManagers managers;

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
        if(type != null && type.equals("invoice")) {
            orders = removeExpediaPrepaid(orders);
            return createInvoiceXledgerFile(orders);
        } else {
            return creategbatfile(orders);
        }
    }
    
    public List<String> creategbatfile(List<Order> orders) {
        List<String> lines = new ArrayList();
        for(Order order : orders) {
            User user = userManager.getUserById(order.userId);
            if(user == null || user.customerId == null) {
                continue;
            }
            
            Date periodeDate = order.getStartDateByItems();
            if(periodeDate == null) {
                periodeDate = order.rowCreatedDate;
            }
            Integer accountingId = 1;
            
            if (order.payment != null && order.payment.paymentType.equals("ns_92bd796f_758e_4e03_bece_7d2dbfa40d7a\\ExpediaPayment")) {
                accountingId = 11072; // Expedia customer id.
            } else if (order.payment != null && order.payment.paymentType.equals("ns_d79569c6_ff6a_4ab5_8820_add42ae71170\\BookingComCollectPayments")) {
                accountingId = 19982; // Bookingcom.
            } else if (order.payment != null && order.payment.paymentType.equals("ns_f1c8301d_9900_420a_ad71_98adb44d7475\\Vipps")) {
                accountingId = 24359; // Vipps customer id.
            } else {
                accountingId = user.customerId; // Kundenr 
            }
            
            List<HashMap<Integer, String>> tmplines = generateLine(order, periodeDate, accountingId);
            for(HashMap<Integer, String> toConvert : tmplines) {
                String line = makeLine(toConvert);
                lines.add(line);
            }
        }

        return lines;
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
    
    private String createLineText(CartItem item, Order order) {
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
            if(mdata != null && mdata.startsWith("114")) {
                mdata = "";
            } else {
                mdata = ", " + mdata;
            }
            lineText = item.getProduct().name + mdata + startEnd;
        }
        
        lineText = lineText.replace("Dobbeltrom", "");
        lineText = lineText.replace("Kjøkken", "kj. ");
        lineText = lineText.replace("kjøkken", "kj. ");
        lineText = lineText.replace("Standard", "Std");
        lineText = lineText.replace("standard", "std");
        lineText = lineText.trim();
    
        if(order.invoiceNote != null && !order.invoiceNote.isEmpty()) {
            lineText = "(x)" + lineText;
        }
        lineText = makeSureIsOkay(lineText);
        return lineText;
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
        this.storeApplicationPool = manager;
    }

    @Override
    public void setManagers(AccountingManagers mgr) {
        this.managers = mgr;
    }

    private String removeSemiColon(String fullName) {
        if(fullName == null || fullName.isEmpty()) {
            return fullName;
        }
        
        return fullName.replaceAll(";", "");
    }

    private List<Order> removeExpediaPrepaid(List<Order> orders) {
        List<Order> toRemove = new ArrayList();
        for(Order order : orders) {
            
            if (order.payment != null && order.payment.paymentType.equals("ns_92bd796f_758e_4e03_bece_7d2dbfa40d7a\\ExpediaPayment")) {
                if(order.getEndDateByItems() != null && order.getEndDateByItems().after(new Date())) {
                    toRemove.add(order);
                }
            }
        }
        orders.removeAll(toRemove);
        return orders;
    }

    
    private List<HashMap<Integer, String>> generateLine(Order order, Date periodeDate, Integer customerId) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(order.rowCreatedDate);
        
        StorePaymentConfig storePaymentConfig = managers.paymentManager.getStorePaymentConfiguration(order.getPaymentApplicationId());

        
        managers.invoiceManager.generateKidOnOrder(order);
        
        List<HashMap<Integer, String>> lines = new ArrayList();
        
        int firstMonth = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);
        int duedays = 14;
        
        if(order.dueDays != null) {
            duedays = order.dueDays;
        }
        
        
        
        BigDecimal total = order.getTotalAmountVatRoundedTwoDecimals(2);
        DecimalFormat df = new DecimalFormat("#.##");    
        User user = managers.userManager.getUserById(order.userId);
        String kid = order.kid;
        if(kid == null) {
            kid = "";
        }
        
        String interimaccount = "1510";
        
        if(storePaymentConfig != null && storePaymentConfig.offsetAccountingId_accrude != null && !storePaymentConfig.offsetAccountingId_accrude.isEmpty()) {
            interimaccount = storePaymentConfig.offsetAccountingId_accrude;
        }
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(order.rowCreatedDate);
        cal2.add(Calendar.DAY_OF_YEAR, duedays);
        Date dueDate = cal2.getTime();

        HashMap<Integer, String> line = new HashMap();
        line.put(0, "GBAT10");
        line.put(1, order.incrementOrderId+ "");
        line.put(2, format.format(order.rowCreatedDate)); //Ordredato
        line.put(3, "1");
        line.put(4, "");
        line.put(5, "");
        line.put(6, interimaccount);
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
        line.put(26, "");
        line.put(27, "");
        line.put(28, "X");
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
            subLine.put(2, format.format(order.rowCreatedDate)); //Ordredato
            subLine.put(3, "1");
            subLine.put(4, "");
            subLine.put(5, "");
            subLine.put(6, product.sku);
            subLine.put(7, product.sku);
            BigDecimal itemamount = item.getTotalExRoundedWithTwoDecimals(2);
            itemamount = itemamount.add(new BigDecimal(-1));
            subLine.put(8, df.format(itemamount));
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
            subLine.put(20, createLineText(item, order)); //Forfallsdato
            subLine.put(21, order.payment.readablePaymentType()); //Forfallsdato
            subLine.put(22, "");
            subLine.put(23, "");
            subLine.put(24, "");
            subLine.put(25, "");
            subLine.put(26, "");
            subLine.put(27, "");
            subLine.put(28, "X");
            
            lines.add(subLine);

        }
        
        return lines;
    }
    
    private String makeSureIsOkay(String text) {
        if(text == null) {
            return "";
        }
        return text.replaceAll(";", " ");
    }    

    private List<String> createInvoiceXledgerFile(List<Order> orders) {
        List<String> lines = new ArrayList();
        boolean first = true;
        for(Order order : orders) {
            User user = userManager.getUserById(order.userId);
            if(user == null || user.customerId == null) {
                continue;
            }
            
            Date periodeDate = order.getStartDateByItems();
            if(periodeDate == null) {
                periodeDate = order.rowCreatedDate;
            }
            Integer accountingId = 1;
            
            accountingId = user.customerId; // Kundenr 
            
            List<HashMap<Integer, String>> tmplines = generateInvoiceLines(order, periodeDate, accountingId, first);
            for(HashMap<Integer, String> toConvert : tmplines) {
                String line = makeLine(toConvert);
                lines.add(line);
            }
            first = false;
        }

        return lines;
    }

    private List<HashMap<Integer, String>> generateInvoiceLines(Order order, Date periodeDate, Integer accountingId, boolean first) {
        User user = userManager.getUserByIdIncludedDeleted(order.userId);
                
        String zipcode = "";
        if(user.address != null) { zipcode = user.address.postCode; }
        if(zipcode == null) { zipcode = ""; }
        String address = "";
        if(user.address != null) { address = user.address.address; }
        if(address == null) { address = ""; }
        String city = "";
        if(user.address != null) { city = user.address.city; }
        if(city == null) { city = ""; }
        String phone = user.cellPhone;
        String email = user.emailAddress;
        if(phone == null) { phone = ""; }
        if(email == null) { email = ""; }
        
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String date = format.format(periodeDate);

        List<HashMap<Integer, String>> lines = new ArrayList();
        
        Integer lineNo = 1;
        for(CartItem item : order.cart.getItems()) {
            Product product = managers.productManager.getProduct(item.getProduct().id);
            if(product == null) {
                product = managers.productManager.getDeletedProduct(item.getProduct().id);
            }
            
            LinkedHashMap<String, String> entries = new LinkedHashMap();
            entries.put("ImpSystem", "");
            entries.put("ImpSystemRef", "");
            entries.put("InvoiceGroup", "");
            entries.put("OrderNo", order.incrementOrderId + "");
            entries.put("LineNo", lineNo+"");
            entries.put("Date", date);
            entries.put("CustomerNo", accountingId + "");
            entries.put("InvoiceTemplate", "");
            entries.put("ReadyToInvoice", "");
            entries.put("Project", "");
            entries.put("Object", "");
            entries.put("ObjectValue", "");
            entries.put("Posting1", "");
            entries.put("Posting2", "");
            entries.put("XGL", "");
            entries.put("Currency", "NOK");
            entries.put("Product", "");
            entries.put("XLG", "");
            entries.put("Text", createLineText(item, order));
            entries.put("Pricelist", "");
            entries.put("PriceGroup", "");
            entries.put("Unit", "døgn");
            entries.put("Quantity", item.getCount() + "");
            entries.put("CostPrice", "");
            entries.put("UnitPriceImp", item.getProduct().price + "");
            entries.put("Discount", "");
            entries.put("DiscountAmount", "");
            entries.put("TaxRule", product.accountingSystemId + "");
            entries.put("PrePaidToBank", "");
            entries.put("PrePaidDate", "");
            entries.put("PaymentRef", "");
            entries.put("Xidentifier", "");
            entries.put("ExternalOrder", "");
            entries.put("SubledgerName", makeSureIsOkay(user.fullName));
            entries.put("CompanyNo", "");
            entries.put("SubledgerGroup", "");
            entries.put("BankAccount", "");
            entries.put("PaymentTerms", "");
            entries.put("UpdateAddress", "");
            entries.put("StreetAddressLine1", makeSureIsOkay(address));
            entries.put("StreetAddressLine2", "");
            entries.put("ZipCode", makeSureIsOkay(zipcode));
            entries.put("City", makeSureIsOkay(city));
            entries.put("State", "");
            entries.put("Country", "");
            entries.put("InvoiceStreetAddress2Line1", makeSureIsOkay(address));
            entries.put("InvoiceStreetAddress2Line2", "");
            entries.put("InvoiceZipCode2", makeSureIsOkay(zipcode));
            entries.put("InvoiceCity2", makeSureIsOkay(city));
            entries.put("InvoiceState2", "");
            entries.put("InvoiceCountry2", "");
            entries.put("Phone", makeSureIsOkay(phone));
            entries.put("Email", makeSureIsOkay(email));
            entries.put("YourRef", "");
            entries.put("Dummy1", "");
            entries.put("Dummy2", "");
            entries.put("Dummy3", "");
            entries.put("Dummy4", "");
            entries.put("Dummy5", "");
            entries.put("EOL", "X");
            
            if(first) {
                HashMap<Integer, String> toAdd = new HashMap();
                int j = 0;
                for(String key : entries.keySet()) {
                    toAdd.put(j, key);
                    j++;
                }
                lines.add(toAdd);
                
                first = false;
            }
            
            HashMap<Integer, String> toAdd = new HashMap();
            int j = 0;
            for(String key : entries.keySet()) {
                toAdd.put(j, entries.get(key));
                j++;
            }
            lines.add(toAdd);
            lineNo++;
        }
        return lines;
    }
}
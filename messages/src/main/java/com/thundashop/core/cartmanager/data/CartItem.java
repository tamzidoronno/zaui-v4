/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.cartmanager.data;

import com.google.gson.Gson;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.TwoDecimalRounder;
import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductPriceOverride;
import com.thundashop.core.productmanager.data.ProductPriceOverrideType;
import com.thundashop.core.productmanager.data.TaxGroup;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class CartItem implements Serializable, Cloneable {
    private String cartItemId = UUID.randomUUID().toString();
    private Map<String, String> variations = new HashMap(); 
   
    private Product product;
    
    private int count = 0;
    public Date startDate;
    public Date endDate;
    public Date newStartDate;
    public Date newEndDate;
    public Date periodeStart = null;
    public String groupedById = "";
    public String addedBy = "";
    public Date addedDate = null;
    public boolean removedAfterDeleted;
    public List<PmsBookingAddonItem> itemsAdded;
    public HashMap<String, Double> priceMatrix;
    public boolean hideDates = false;
    public boolean disabled = false;
    public String addedByGetShopModule = "";
    public String pmsBookingId = "";
    
    public String wareHouseId = "";
    private List<ProductPriceOverride> overridePriceHistory = new ArrayList();
    
    @Transient
    public String orderId;
    
    public Double overridePriceIncTaxes;
    
    /**
     * Indicates what conference this item was connected to previouse.
     */
    public String conferenceId = "";
    
    public CartItem() {
    }
    
    public Double getPriceExForMinutes() {
        if(startDate == null || endDate == null) {
            return 0.0;
        }
        long diff = endDate.getTime() - startDate.getTime();
        long mins = getSeconds();
        if(mins == 0) {
            return 0.0;
        }
        return (getProduct().priceExTaxes * getCount()) / mins;
    }
    
    public Double getPriceIncForMinutes() {
        if(startDate == null || endDate == null) {
            return 0.0;
        }
        long mins = getSeconds();
        if(mins == 0) {
            return 0.0;
        }
        return (getProduct().price * getCount()) / mins;
    }
    
    public boolean isSame(String productId, Map<String, String> variations) {
        if (!this.product.id.equals(productId)) {
            return false;
        }
        if(this.variations == null) {
            return false;
        }
        if (variations.size() != this.variations.size()) {
            return false;
        }
        
        for (String varId : this.variations.values()) {
            if (!variations.values().contains(varId)) {
                return false;
            }
        }
        
        return true;
    }
    
    public String getCartItemId() {
        return cartItemId;
    }

    public void increseCounter() {
        this.count++;
    }
    
    public void decreaseCounter() {
        if (this.count > 1) {
            this.count++;
        }
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setProduct(Product product) {
        this.product = product.clone();
    }

    public void setVariations(Map<String, String> variations) {
        this.variations = variations;
    }

    public Product getProduct() {
        if (product == null)
            return null;
        
        product.doFinalize();
        return product;    
    }

    public String getProductId() {
        if (this.product == null) {
            return "";
        }
        
        return this.product.id;
    }
    
    public Product getProductUnfinalized() {
        return product;
    }
    
    public Map<String, String> getVariations() {
        if(variations == null) {
            return new HashMap();
        }
        return variations;
    }

    public void doFinalize() {
        if (product != null) {
            product.doFinalize();
            //Negative values on the price should never happen, the count should be negative instead.
            if(product.price < 0) {
                count *= -1;
                product.price *= -1;
                product.priceExTaxes *= -1;
            }
        }
        
        if (overridePriceIncTaxes == null || Double.isNaN(overridePriceIncTaxes) ) {
            overridePriceIncTaxes = null;
        }
        
        overridePriceHistory.stream().forEach(o -> o.finalize());
        
        
        if(itemsAdded != null) {
            for(PmsBookingAddonItem toCheck : itemsAdded) {
                
                if(toCheck != null && toCheck.price != null && (toCheck.price.isInfinite() || toCheck.price.isNaN())) {
                    toCheck.price = 0.0;
                }
                
                if(toCheck != null && toCheck.price != null && toCheck.price.equals(Double.NEGATIVE_INFINITY)) {
                    System.err.println("Found a negative infinite ?");
                    toCheck.price = 0.0;
                }
                
                if(toCheck != null && toCheck.price != null && toCheck.price.equals(Double.POSITIVE_INFINITY)) {
                    toCheck.price = 0.0;
                }
            }
        }
        correctIncorrectCalculation();
    }

    public Date getStartingDate() {
        if(newStartDate != null) {
            return newStartDate;
        }
        
        if (startDate == null) {
            return addedDate;
        }
        
        return startDate;
    }
    
    public Date getEndingDate() {
        if(newEndDate != null) {
            return newEndDate;
        }
        
        if (endDate == null) {
            return addedDate;
        }
        
        return endDate;
    }

    public CartItem copy() {
        Gson gson = new Gson();
        String res = gson.toJson(this);
        CartItem newItem = gson.fromJson(res, CartItem.class);
        newItem.cartItemId = UUID.randomUUID().toString();
        return newItem;
    }

    public boolean startsOnDate(Date time, Date rowCreatedDate) {
        Date startToUse = startDate;
        Calendar createdCal = Calendar.getInstance();
        if(startDate != null) {
            createdCal.setTime(startDate);
        }
        if(startToUse == null || createdCal.get(Calendar.YEAR) < 1980) {
            startToUse = rowCreatedDate;
        }
        if(startToUse == null) {
            return false;
        }
        createdCal.setTime(startToUse);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);
        
        if((createdCal.get(Calendar.YEAR) == timeCal.get(Calendar.YEAR)) && 
            (createdCal.get(Calendar.DAY_OF_YEAR) == timeCal.get(Calendar.DAY_OF_YEAR))) {
            return true;
        }
        return false;
    }

    public int getSecondsForDay(Calendar time, boolean shift) {
        if(startDate == null || endDate == null) {
            return -1;
        }
        
        if(startDate.equals(endDate)) {
            return -1;
        }
        
        if(startDate.after(endDate)) {
            return -1;
        }
        
        if(sameDay(startDate, endDate)) {
            return -1;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        if(cal.get(Calendar.YEAR) < 1980) {
            return -1;
        }
        
        Date tmpStartDate = new Date(startDate.getTime());
        Date tmpEndDate = new Date(endDate.getTime());
        if(shift) {
            cal = Calendar.getInstance();
            cal.setTime(tmpStartDate);
            int toShift = cal.get(Calendar.HOUR_OF_DAY);
            cal.add(Calendar.HOUR_OF_DAY, toShift * -1);
            tmpStartDate = cal.getTime();
            
            cal.setTime(tmpEndDate);
            cal.add(Calendar.HOUR_OF_DAY, toShift * -1);
            tmpEndDate = cal.getTime();
        }
        
        Date dayToCheckAgainst = time.getTime();
        int seconds = 0;
        if(sameDay(dayToCheckAgainst, tmpStartDate) && sameDay(dayToCheckAgainst, tmpEndDate)) {
            LocalDateTime start = LocalDateTime.ofInstant(tmpStartDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(tmpEndDate.toInstant(), ZoneId.systemDefault());
            seconds = (int) Duration.between(start, end).getSeconds();
        } else if(sameDay(dayToCheckAgainst, tmpStartDate)) {
            Calendar c = Calendar.getInstance();
            c.setTime(tmpStartDate);
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            
            LocalDateTime start = LocalDateTime.ofInstant(tmpStartDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(c.getTime().toInstant(), ZoneId.systemDefault());

            seconds = (int) Duration.between(start, end).getSeconds();
        } else if(sameDay(dayToCheckAgainst, tmpEndDate)) {
            Calendar c = Calendar.getInstance();
            c.setTime(tmpEndDate);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            
            LocalDateTime start = LocalDateTime.ofInstant(c.getTime().toInstant(), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(tmpEndDate.toInstant(), ZoneId.systemDefault());

            seconds = (int) Duration.between(start, end).getSeconds();
        } else if(dayToCheckAgainst.before(tmpStartDate)) {
            seconds = 0;
        } else if(dayToCheckAgainst.after(tmpEndDate)) {
            seconds = 0;
        } else {
            seconds = (60*60*24);
        }
        
        return seconds;
    }
    
    public boolean sameDay(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate1.equals(localDate2);
    }

    public long getSeconds() {
        
        LocalDateTime start = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
        
        long minutes = Duration.between(start, end).getSeconds();

        return minutes;
    }

    public void refreshCartItemId() {
        cartItemId = UUID.randomUUID().toString();
    }

    public BigDecimal getTotalAmountRoundedWithTwoDecimals(int precision) {
        BigDecimal rounded = TwoDecimalRounder.roundTwoDecimals(getProduct().price, precision);
        rounded = rounded.multiply(new BigDecimal(count));
        return rounded;
    }

    public BigDecimal getAmountRoundedWithTwoDecimals(int precision) {
        BigDecimal rounded = TwoDecimalRounder.roundTwoDecimals(getProduct().price, precision);
        return rounded;
    }
    
    public BigDecimal getTotalAmountRoundedWithTwoDecimalsOverride(int precision) {
        BigDecimal rounded = TwoDecimalRounder.roundTwoDecimals(overridePriceIncTaxes, precision);
        rounded = rounded.multiply(new BigDecimal(count));
        return rounded;
    }
    
    public double getTotalAmount() {
        return count * getProductPrice();
    }
    
    public double getTotalAmountInLocalCurrency() {
        if (product != null && product.priceLocalCurrency != null) {
            return count * product.priceLocalCurrency;
        }
        
        return count * getProductPrice();
    }

    public BigDecimal getTotalExRoundedWithTwoDecimals(int precision) {
        BigDecimal priceExTaxes = TwoDecimalRounder.roundTwoDecimals(getProduct().priceExTaxes, precision);
        return priceExTaxes.multiply(new BigDecimal(count));
    }
    
    public double getTotalEx() {
        if (getProduct() == null) {
            return 0;
        }
        
        return count * getProduct().priceExTaxes;
    }
    
    public double getTotalExLocalCurrency() {
        return count * getProduct().priceExTaxesLocalCurrency;
    }

    public Double getPriceMatrixAmount() {
        if(priceMatrix == null) {
            return 0.0;
        }
        
        Double check = 0.0;
        for(Double toAdd : priceMatrix.values()) {
            check += toAdd;
        }
        return check;
    }

    public double getDiffForFromMeta() {
        double total = getTotalAmount();
        double totalOnMeta = getTotalOnMeta();
        double diff = (total - totalOnMeta);
        if(diff < 1 && diff > -1) {
            diff = 0.0;
        }
        
        return diff;
    }

    private double getTotalOnMeta() {
        double val = 0.0;
        double total = getTotalAmount();
        
        if(priceMatrix != null) {
            for(String day : priceMatrix.keySet()) {
                val += priceMatrix.get(day);
            }
        }
        if(itemsAdded != null) {
            for(PmsBookingAddonItem addonItem : itemsAdded) {
                if (addonItem != null && addonItem.price != null && addonItem.count != null) {
                    val += (addonItem.price * addonItem.count);
                }
            }
        }

        return val;
    }

    public List<PmsBookingAddonItem> getItemsAdded() {
        return itemsAdded;
    }
    
    public void recalculatePriceMatrixAndAddons() {
        Double amount = getProduct().price * count;
        if(priceMatrix != null) {
            int matrixSize = priceMatrix.size();
            if(matrixSize > 0) {
                Double avgPrice = amount / matrixSize;
                List<String> keys = new ArrayList(priceMatrix.keySet());
                for(String key : keys) {
                    priceMatrix.put(key, avgPrice);
                }
            }
        }
        
        if(itemsAdded != null && !itemsAdded.isEmpty()) {
            Double avgPrice = amount / itemsAdded.size();
            for(PmsBookingAddonItem item : itemsAdded) {
                if(item.count > 1) {
                    item.count = 1;
                } else {
                    item.count = -1;
                }
                item.price = avgPrice;
            }
        }
    }
    
    public boolean correctIncorrectCalculation() {
        double total = getTotalAmount();
        double totalOnMeta = getTotalOnMeta();
        double diff = total + totalOnMeta;
        boolean orderIsNull = total > -0.1 && total < 0.1;
        
        if(diff > -0.1 && diff < 0.1 && !orderIsNull) {
            if(itemsAdded != null) {
                for(PmsBookingAddonItem item : itemsAdded) {
                    if (item != null && item.price != null && item.count != null) {
                        item.price = item.price * -1;
                    }
                }
            }
            if(priceMatrix != null) {
                for(String day : priceMatrix.keySet()) {
                    priceMatrix.put(day, priceMatrix.get(day) * -1);
                }
            }
            return true;
        }
        return false;
    }
    

    public void dumpMetaData() {
        if(itemsAdded != null) {
            for(PmsBookingAddonItem item : itemsAdded) {
                System.out.println("itemdump;" + product.name + ";" + item.count + ";" + item.price);
            }
        }
        if(priceMatrix != null) {
            for(String day : priceMatrix.keySet()) {
                System.out.println("itemdumppricematrix;" + product.name + ";" + day + ";" + priceMatrix.get(day));
            }
        }
        dumpItem();
    }

    private void dumpItem() {
        System.out.println(product.price + ";" + count);
    }

    public void recalculateMetaData() {
        if (itemsAdded != null && !itemsAdded.isEmpty() && product != null) {
            double totalPrice = itemsAdded.stream().mapToDouble(i -> i.price * i.count).sum();
            int totalCount = itemsAdded.stream().mapToInt(i -> i.count).sum();
            double average = totalPrice / totalCount;
            this.product.price = average;
            this.count = totalCount;
        }
        
        if (priceMatrix != null && !priceMatrix.isEmpty() && product != null) {
            double totalPrice = priceMatrix.values()
                    .stream()
                    .filter(d -> d != null)
                    .mapToDouble(d -> d.doubleValue())
                    .sum();
            
            double average = totalPrice / priceMatrix.size();
            
            product.price = average;
            
            count = priceMatrix.size();
        }
    }

    public boolean isForPeriode(Date date) {
        return false;
    }

    public boolean isMatrixAndItemsValid() {
        Double total = 0.0;
        boolean found = false;
            correctIncorrectCalculation();
            if(itemsAdded != null && !itemsAdded.isEmpty()) {
               for(PmsBookingAddonItem pmsitem : itemsAdded) {
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
            if(priceMatrix != null && !priceMatrix.isEmpty()) {
                for(Double val : priceMatrix.values()) {
                    total += val;
                    found = true;
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

    public void keepOnlyDateRange(Date start, Date end) {
        if(startDate == null || endDate == null) {
            return;
        }
        if(!sameDay(start, startDate) && startDate.before(start)) {
            startDate = start;
        }
        if(!sameDay(end, endDate) && endDate.after(end)) {
            endDate = end;
        }
        
        int counter = 0;
        double price = 0.0;
        if(priceMatrix != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            List<String> toRemove = new ArrayList();
            for(String dateString : priceMatrix.keySet()) {
                try {
                    Date date = formatter.parse(dateString);
                    if(!sameDay(date, startDate) && date.before(startDate)) {
                        toRemove.add(dateString);
                    } else if(!sameDay(date, endDate) && date.after(endDate)) {
                        toRemove.add(dateString);
                    } else {
                        counter++;
                        price += priceMatrix.get(dateString);
                    }
                }catch(Exception e) {
                    GetShopLogHandler.logStack(e, null);
                }
            }
            for(String remove : toRemove) {
                priceMatrix.remove(remove);
            }
        }
        
        if(itemsAdded != null) {
            List<PmsBookingAddonItem> removeItems = new ArrayList();
            for(PmsBookingAddonItem item : itemsAdded) {
                if(item.date == null) {
                    continue;
                }
                if(!sameDay(item.date, startDate) && item.date.before(startDate)) {
                    removeItems.add(item);
                } else if(!sameDay(item.date, endDate) && item.date.after(endDate)) {
                    removeItems.add(item);
                } else {
                    counter += item.count;
                    price += (item.price * item.count);
                }
            }
            itemsAdded.removeAll(removeItems);
        }
        
        this.count = counter;
        this.product.price = price / counter;
    }

    public boolean isPriceMatrixItem() {
        return priceMatrix != null && !priceMatrix.isEmpty();
    }

    public boolean isPmsAddons() {
        return itemsAdded != null && !itemsAdded.isEmpty();
    }

    public double getPriceMatrixWithoutTax(String dateString) {
        Double price = priceMatrix.get(dateString);
        if (price == null) {
            throw new NullPointerException("Tried to recalculate a price that is null from price matrix.");
        }
        
        double taxDivideFactor = product.taxGroupObject.getTaxRate() + 1; 
        double priceExTaxes = price / taxDivideFactor;
        return priceExTaxes;
    }   

    public double getPriceIncTaxes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addOverridePriceHistory(ProductPriceOverride override, String userId) {
        overridePriceHistory.stream().forEach(o -> {
            o.deleteEntry(userId);
        });
        overridePriceHistory.add(override);
        calculateOverridePrice();
    }

    public int getOverridePriceHistoryCount() {
        return overridePriceHistory.size();
    }

    private void calculateOverridePrice() {
        overridePriceHistory.sort((ProductPriceOverride o1, ProductPriceOverride o2) -> {
            return o1.getDate().compareTo(o2.getDate());
        });
        
        overridePriceHistory.stream()
            .filter(o -> !o.isDeleted())
            .forEach(history -> {
                
                if (history.getType().equals(ProductPriceOverrideType.fixedprice)) {
                    overridePriceIncTaxes = history.getNewValue();
                }

                if (history.getType().equals(ProductPriceOverrideType.discountpercent)) {
                    if (history.getNewValue() == 0) {
                        overridePriceIncTaxes = null;
                    } else {
                        overridePriceIncTaxes = ((100 - history.getNewValue())/100) * getProduct().price;
                    }
                }
                
            });
    }

    public void updateOverridePricesToProduct() {
        if (overridePriceIncTaxes != null) {
            getProduct().price = overridePriceIncTaxes;
        }
    }

    public double getProductPrice() {
        if (overridePriceIncTaxes != null) {
            return overridePriceIncTaxes;
        }
        
        Product retProduct = getProduct();
        
        if (retProduct == null) {
            return 0;
        }
        
        return retProduct.price;
    }

    public void remove(CartItem cartItem) {
        double diff = cartItem.getProductPrice() - getProductPrice();
        
        if (diff == 0) {
            count = count - cartItem.count;
        } else {
            overridePriceIncTaxes = ((getProductPrice() * (double)count) - (cartItem.getProductPrice() * (double)cartItem.count)) / (double)count ;
        }
        
        if (overridePriceIncTaxes != null && overridePriceIncTaxes < 0.00001 && overridePriceIncTaxes > -0.00001) {
            overridePriceIncTaxes = 0D;
        }
        
        if (product.price < 0.00001 && product.price > -0.00001) {
            product.price = 0;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean containsRoom(String pmsBookingRoomId) {
        if(product != null && product.externalReferenceId != null) {
            return product.externalReferenceId.equals(pmsBookingRoomId);
        }
        return false;
    }

    public void changeAllTaxes(TaxGroup taxGroupObject) {
        if (product != null) {
            product.taxgroup = taxGroupObject.groupNumber;
            product.taxGroupObject = taxGroupObject;
            product.price = product.priceExTaxes * (taxGroupObject.getTaxRate()+1);
        }
    }

    public void creditPmsAddonsAndPriceMatrix() {
        if (itemsAdded != null) {
            itemsAdded.stream().forEach(item -> {
                item.price = item.price * -1;
                if (item.priceExTaxes != null) {
                    item.priceExTaxes = item.priceExTaxes * -1;
                }
            });
        }
        
        if (priceMatrix != null) { 
            for (String date : priceMatrix.keySet()) {
                priceMatrix.put(date, priceMatrix.get(date)*-1);
            }
        }
    }


}
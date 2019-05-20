package com.thundashop.core.productmanager.data;

import org.mongodb.morphia.annotations.Transient;
import com.google.gson.Gson;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.Translation;
import com.thundashop.core.common.TwoDecimalRounder;
import com.thundashop.core.listmanager.data.JsTreeList;
import com.thundashop.core.pagemanager.data.Page;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author ktonder
 */
public class Product extends DataCommon implements Comparable<Product>  {
    /* This variable is no longer in use, using only imagesAdded */
    public Map<String, ProductImage> images = new HashMap<String, ProductImage>();    
    
    public List<String> imagesAdded = new LinkedList();
    public List<String> descriptions = new ArrayList();

    public String selectedProductTemplate = "ecommerce_product_template_1";
    
    /* Used to increase performance */
    public String currentSelectedProducTemplate = "";
    
    @Translation
    public String description;
    @Translation
    public String shortDescription;
    public String mainImage = "";
    public double price;
    
    /**
     * This price is in taxes and used when the 
     * order is sold in a foreign currency
     */
    public Double priceLocalCurrency;
    
    public Integer percentagePrice = 0;
    public Double priceBeforeTaxChange;
    public TaxGroup taxGroupBeforeTaxChange;
    
    public Double lastExtraPrice = 0D;

    public double discountedPrice;
    
    public Double overriddenPrice;
    
    public String tag = "";
    
    /**
     * Specified in seconds.
     */
    public long minPeriode = 0;
    public boolean progressivePriceModel = false;
    public boolean dynamicPriceInPercent = false;
    
    public List<ProductDynamicPrice> prices = new ArrayList();
    public double campaign_price;
    public Double original_price;
    @Translation
    public String name;
    
    @Transient
    public String uniqueName;
    
    public int stockQuantity;
    public String pageId;
    public boolean freeShipping = false;
    public boolean promoted = false;
    public boolean hideShippingPrice = false;
    public int taxgroup = -1;
    public boolean privateExcluded = false;
    public Integer incrementalProductId = null;
    
    
    /**
     * This is the product id reflected in the accounting-system
     */
    public String accountingSystemId;
    
    /**
     * This is the accounting-number in the accounting-system.
     * 
     * (Legacy, not in use in the new Accounting Report System (f-report))
     * Uses accountingConfig instaed.
     *
     */
    private String accountingAccount;
    
    /**
     * Used on the new accounting (primitiv) way.
     */
    public List<ProductAccountingInformation> accountingConfig = new ArrayList();
    
    public HashMap<String, AttributeItem> addedAttributes = new HashMap();
    
    public HashMap<String, Double> groupPrice = new HashMap();
    
    public List<String> categories = new ArrayList();
    
    public List<String> subProductIds = new ArrayList();
    
    public TaxGroup taxGroupObject;
    
    public List<TaxGroup> additionalTaxGroupObjects = new ArrayList();
    
    @Transient
    public Page page;
    
    @Transient
    public List<Product> subProducts = new ArrayList();
    
    @Transient
    public HashMap<String, String> attributesToSave = new HashMap();
    
    @Transient
    public ProductAccountingInformation activeAccountingInformation = null;
    
    public boolean isGroupedProduct = false;
    
    public boolean isFood = false;
    
    /**
     * Should always be in gram.
     */
    public int weight;
    
    @Editor
    public String supplier;
    
    @Administrator
    public double cost;
    
    //Inidicated if nessesary appliations has been added in simple mode.
    public boolean simpleAppsAdded;
    
    /**
     * This should always be in percents.
     */
    @Editor
    private double taxes;
    
    public String sku;
    
    public String metaData = "";
    public String additionalMetaData = "";
    /* This we need i case we need to track down more data later on (used for bookings) */
    public String externalReferenceId = "";
    public boolean isNotRecurring = false;
    
    @Transient
    public double priceExTaxes;
    
    @Transient
    public double priceExTaxesLocalCurrency;
    
    public Map<String, String> variationCombinations;

    @Transient
    public JsTreeList variations = null;
    
    public List<ProductExtraConfig> extras = new ArrayList();
    
    public HashMap<String, List<String>> selectedExtras  = new HashMap();
    
    /**
     * This contains a distinct list of taxgroupnumbers that this
     * product has been sold on.
     */
    public List<Integer> soldOnTaxGroups = new ArrayList();
    
    public void setMainImage(String fileId) {
        mainImage = fileId;
    }
    
    
    public void removeImage(String fileId) {
        if (imagesAdded == null) {
            return;
        }
        
        imagesAdded.remove(fileId);
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        int len = id.length();
        for (int i = 0; i < len; i++) {
            h = 31 * h + id.charAt(i);
        }
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.hashCode() == obj.hashCode());
    }
    
    @Override
    public String toString() {
        return id;
    }

    public boolean check(ProductCriteria searchCriteria) {
        if (id != null && searchCriteria.ids.contains(id))
            return true;
        
        if (page != null && page.id != null && searchCriteria.pageIds.contains(page.id))
            return true;
        
        if (searchCriteria.search != null 
                && !searchCriteria.search.equals("")
                    && name != null
                && name.toLowerCase().matches("(.*)"+searchCriteria.search.toLowerCase()+"(.*)"))
            return true;
        
        if (page != null && page.parent != null && searchCriteria.parentPageIds.contains(page.parent.id))
            return true;
        
        return false;
    }


    @Override
    public int compareTo(Product t) {
        return t.rowCreatedDate.compareTo(rowCreatedDate);
    }
    
    public double getPrice(Map<String, String> variations) {
        double retprice = this.price;
        
        if (variations != null && !variations.isEmpty()) {
            // TODO - Maybe add different prices for other variations.
        }
        
        
        if (this.overriddenPrice != null) {
            return overriddenPrice;
        }
        
        if (this.discountedPrice > 0) {
            return this.discountedPrice;
        }
        
        
        return retprice;
    }

    public Product clone() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), this.getClass());
    }
    
    /**
     * We adding the difference between last calculated extra price and now. 
     * This is to avoid that the price is changed back if the price is manually changed in the future
     */
    private void addExtrasPrice() {
        double extraPrice = getExtrasPrice();
        double toAdd = extraPrice - lastExtraPrice;
        lastExtraPrice = extraPrice;
        price += toAdd;
    }

    public void doFinalize() {
        if(Double.isNaN(discountedPrice)) {
            discountedPrice = 0.0;
        }
        
        addExtrasPrice();
        
        double divident = 0.0;
        if(taxGroupObject != null && taxGroupObject.taxRate != null) {
            divident = (1 + (taxGroupObject.taxRate/100));
        }
        if(taxGroupObject != null && taxGroupObject.taxRate != null && divident != 0.0 && price != 0.0) {
            priceExTaxes = price / divident;
            if (priceLocalCurrency != null) {
                priceExTaxesLocalCurrency = priceLocalCurrency / divident;
            }
        } else {
            priceExTaxes = price;
            if (priceLocalCurrency != null) {
                priceExTaxesLocalCurrency = priceLocalCurrency;
            }
        }
        if(Double.isNaN(priceExTaxes)) {
            priceExTaxes = 0.0;
            priceExTaxesLocalCurrency = 0.0;
        }
        
        createEmptyAccountingInformationObjects();
        
        setActiveAccountingInformation();
    }

    public BigDecimal getPriceExTaxesWithTwoDecimals(int precision) {
        return TwoDecimalRounder.roundTwoDecimals(priceExTaxes, precision);
    }

    public void changeToAdditionalTaxCode(String taxGroupId) {
        TaxGroup group = additionalTaxGroupObjects.stream()
                .filter(t -> t.id.equals(taxGroupId))
                .findFirst()
                .orElse(null);
        
        if (priceBeforeTaxChange != null) {
            // Its not possible to change to a different taxgroup without doing a reset by using the resetAdditionalTaxGroup function
            return;
        }
        
        if (group != null) {
            changeToTaxGroup(group);
        }
    }
    
    public void resetAdditionalTaxGroup() {
        if (priceBeforeTaxChange == null) {
            return;
        }
        
        price = priceBeforeTaxChange;
        priceBeforeTaxChange = null;
        taxGroupObject = taxGroupBeforeTaxChange;
        taxGroupBeforeTaxChange = null;
        doFinalize();
    }
    
    private double getExtrasPrice() {
        if (selectedExtras == null || selectedExtras.isEmpty()) {
            return 0D;
        }
        
        double toAdd = 0D;
        for (String optionId : selectedExtras.keySet()) {
            ProductExtraConfig opt = extras.stream()
                    .filter(e -> e.id.equals(optionId))
                    .findAny()
                    .orElse(null);
            if (selectedExtras.get(optionId) == null) {
                continue;
            }
            
            for (String extraId : selectedExtras.get(optionId)) {
                ProductExtraConfigOption val = opt.extras.stream()
                        .filter(e -> e.optionsubid.equals(extraId))
                        .findAny()
                        .orElse(null);
                
                toAdd += val.extraPriceDouble;
            }
        }
        
        return toAdd;
    }

    /**
     * If this product should not be counted in the income-report it will return false.
     * 
     * Example, giftcards and cash withdrawal are not products that should generate any income.
     * 
     * @return 
     */
    public boolean isActuallyIncome() {
        return !id.equals("giftcard");
    }

    private void setActiveAccountingInformation() {
        if (taxGroupObject != null) {
            activeAccountingInformation = accountingConfig.stream()
                    .filter(o -> o.taxGroupNumber != null && o.taxGroupNumber.equals(taxGroupObject.groupNumber))
                    .findAny()
                    .orElse(null);
            
            if (activeAccountingInformation != null) {
                accountingAccount = activeAccountingInformation.accountingNumber;
            } 
        }
    }

    public void createEmptyAccountingInformationObjects() {
        List<Integer> taxGroupNumbers = new ArrayList();
        
        if (taxGroupObject != null) {
            taxGroupNumbers.add(taxGroupObject.groupNumber);
        }
        
        for (TaxGroup taxGroup : additionalTaxGroupObjects) {
            taxGroupNumbers.add(taxGroup.groupNumber);
        }
        
        if (soldOnTaxGroups != null) {
            taxGroupNumbers.addAll(soldOnTaxGroups);
        }
        
        for (Integer taxGroupNumber : taxGroupNumbers) {
            ProductAccountingInformation existing = accountingConfig.stream()
                    .filter(o -> o.taxGroupNumber != null && o.taxGroupNumber.equals(taxGroupNumber))
                    .findAny()
                    .orElse(null);
            
            if (existing == null) {
                existing = new ProductAccountingInformation();
                existing.taxGroupNumber = taxGroupNumber;
                accountingConfig.add(existing);
            }
        }
    }

    public ProductAccountingInformation getAccountingInformation(int taxGroupNumber) {
        return accountingConfig.stream()
                    .filter(o -> o.taxGroupNumber != null && o.taxGroupNumber.equals(taxGroupNumber))
                    .findAny()
                    .orElse(null);
    }

    public String getAccountingAccount() {
        if(accountingAccount != null && !accountingAccount.isEmpty()) {
            return accountingAccount;
        }

        for(ProductAccountingInformation a : accountingConfig) {
            if(a.accountingNumber != null && !a.accountingNumber.isEmpty()) {
                return a.accountingNumber;
            }
        }
        return null;
    }

    public void setAccountingAccount(String account) {
        accountingAccount = account;
    }

    public void changeToTaxGroup(TaxGroup group) {
        taxGroupBeforeTaxChange = taxGroupObject;
        priceBeforeTaxChange = price;
        double multiple = group.getTaxRate()+1;
        BigDecimal newPrice = getPriceExTaxesWithTwoDecimals(2);
        price = newPrice.multiply(new BigDecimal(multiple)).doubleValue();
        this.taxGroupObject = group;
        taxgroup = group.groupNumber;
        doFinalize();
    }
}

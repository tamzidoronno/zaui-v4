package com.thundashop.core.productmanager.data;

import com.google.code.morphia.annotations.Transient;
import com.google.gson.Gson;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.Translation;
import com.thundashop.core.pagemanager.data.Page;
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
    public Map<String, ProductImage> images = new HashMap<String, ProductImage>();    
    public List<String> imagesAdded = new LinkedList();
    public List<ProductVariation> variations = new ArrayList();
    public List<String> descriptions = new ArrayList();
    @Translation
    public String description;
    @Translation
    public String shortDescription;
    public String mainImage = "";
    public double price;
    public double campaign_price;
    public Double original_price;
    @Translation
    public String name;
    public int stockQuantity;
    public String pageId;
    public boolean freeShipping = false;
    public boolean promoted = false;
    public boolean hideShippingPrice = false;
    public int taxgroup = -1;
    
    public long campaing_start_date = 0;
    public long campaing_end_date = 0;
    
    //AttributegroupId, AttributeSelected
    public List<String> attributes = new ArrayList();
    
    @Transient
    public Page page;
    
    public TaxGroup taxGroupObject;
    
    @Transient
    public HashMap<String, String> attributesAdded = new HashMap();
    
    @Transient
    public HashMap<String, String> attributesToSave = new HashMap();
    
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

    public ProductVariation getVariation(String variationId) {
        for (ProductVariation variation : variations) {
            ProductVariation ivariation = variation.get(variationId);
            if (ivariation != null) {
                return ivariation;
            }
        }
        return null;
    }
    
    public double getPrice(List<String> variations, double conversionRate) {
        double retprice = this.price;
        for (String variation : variations) {
            ProductVariation productVariation = getVariation(variation);
            if (productVariation != null) {
                retprice += (productVariation.priceDifference * conversionRate);
                retprice *= 100;
                retprice = Math.round(retprice);
                retprice /= 100;
            }
        }
        
        return retprice;
    }

    public Product clone() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), this.getClass());
    }
}

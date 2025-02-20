/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.cartmanager.data;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pmsmanager.PmsRepeatingData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Coupon extends DataCommon {
    public String code;
    public CouponType type;
    public int amount;
    public HashMap<String, Double> dailyPriceAmountByType = new HashMap<>();
    public int timesLeft;
    public String channel = "";
    public String description = "";
    public PmsRepeatingData whenAvailable = null;
    public String pmsWhenAvailable = "";
    public List<String> productsToSupport = new ArrayList<>();
    public List<AddonsInclude> addonsToInclude = new ArrayList<>();
    public String priceCode = "default";
    public int minDays = 0;
    public int maxDays = 0;
    
    public boolean activeCampaign = false;
    public boolean presentCampaignOnFrontPage = false;
    public HashMap<String, String> campaignTitle = new HashMap<>();
    public HashMap<String, String> campaignDescription = new HashMap<>();
    public boolean excludeDefaultAddons = false;

    public boolean containsAddonProductToInclude(String productId) {
        if(addonsToInclude == null) {
            return false;
        }
        for(AddonsInclude inc : addonsToInclude) {
            if(inc.productId != null && inc.productId.equals(productId)) {
                return true;
            }
        }
        return false;
    }

    public void convertToNewSystem(List<BookingItemType> types) {
        for(BookingItemType type : types) {
            dailyPriceAmountByType.put(type.id+"_1", (double)amount);
        }
    }
    
    public void checkAmount() {
        if(type != null && type.equals(CouponType.FIXEDPRICE)) {
            if(!dailyPriceAmountByType.isEmpty()) {
                for(String key : dailyPriceAmountByType.keySet()) {
                    if(key.equals("_1")) {
                        this.amount = dailyPriceAmountByType.get(key).intValue();
                    }
                    break;
                }
            }
        }
    }

}

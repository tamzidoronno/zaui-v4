package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.HashMap;

public class PmsUserDiscount extends DataCommon {
    public static class PmsUserDiscountType {
        public static Integer percentage = 0;
        public static Integer fixedPrice = 1;
    }
    
    public String userId;
    public Integer discountType = 0;
    public HashMap<String, Double> discounts = new HashMap();
}

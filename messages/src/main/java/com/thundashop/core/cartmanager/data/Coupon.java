/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.cartmanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pmsmanager.PmsRepeatingData;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Coupon extends DataCommon {
    public String code;
    public CouponType type;
    public int amount;
    public int timesLeft;
    public String channel = "";
    public String description = "";
    public PmsRepeatingData whenAvailable = null;
    public String pmsWhenAvailable = "";
    public List<String> productsToSupport = new ArrayList();
    public String priceCode = "default";
}

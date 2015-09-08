/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.cartmanager.data;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class Coupon extends DataCommon {
    public String code;
    public CouponType type;
    public int amount;
    public int timesLeft;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.gsd;

import com.thundashop.core.pdf.data.AccountingDetails;

/**
 *
 * @author ktonder
 */
public class GiftCardPrintMessage extends GetShopDeviceMessage {
    public AccountingDetails accountDetails;
    public Double giftCardValue = 0D;
    public String code = "";
}

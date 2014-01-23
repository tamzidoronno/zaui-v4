/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.bigstock.data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class BigStockOrder implements Serializable {
    public Date purchaseDate;
    public String downloadAddress;
    public String sizeCode;
    public BigStockPurchaseResponse respone;
    public String downloadKey;
    public String imageId;
    public String purchaseUrl;
    public String purchaseSha1String;
    public String accountKey;
    public String accountId;
    public String getShopImageId;
    public int credit;
}

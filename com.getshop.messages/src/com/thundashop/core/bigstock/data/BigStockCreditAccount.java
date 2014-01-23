/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.bigstock.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class BigStockCreditAccount extends DataCommon {
    private List<BigStockOrder> purchasedImages = new ArrayList();
    public int creditAccount = 0;
    
    public void addOrder(BigStockOrder order) {
        this.purchasedImages.add(order);
        creditAccount = creditAccount - order.credit;
    }

    public void addGetShopImageId(String downloadUrl, String imageId) {
        for (BigStockOrder order : purchasedImages) {
            if (order.downloadAddress.equals(downloadUrl)) {
                order.getShopImageId = imageId;
            }
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.bigstock.data;

import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public class BigStockPurchaseResponse implements Serializable {
    public int response_code;
    public String message;
    public BigStockPurchaseData data;
}

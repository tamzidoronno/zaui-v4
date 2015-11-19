/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import com.thundashop.core.productmanager.data.TaxGroup;
import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author ktonder
 */
public class Payment implements Serializable {
    public String paymentType = "";
    public double paymentFee = 0D;
    public TaxGroup paymentFeeTaxGroup = null;
    public HashMap<Long, String> transactionLog = new HashMap();
    public boolean captured = false;
    public HashMap<String, String> callBackParameters = new HashMap();
}

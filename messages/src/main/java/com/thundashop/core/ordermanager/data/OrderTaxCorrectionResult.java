/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.ordermanager.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class OrderTaxCorrectionResult {
    public String orderId;
    public List<OrderTaxCorrectionResultValue> values = new ArrayList();
}

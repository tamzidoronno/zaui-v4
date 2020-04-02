/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.ordermanager.data;

import com.thundashop.core.productmanager.data.TaxGroup;
import java.math.BigDecimal;

/**
 *
 * @author ktonder
 */
public class OrderTaxCorrectionResultValue {
    public Integer taxGroupNumber;
    public BigDecimal originalValue;
    public BigDecimal shouldBeValue;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
 * @author ktonder
 */
public class TwoDecimalRounder {
    
    public static BigDecimal roundTwoDecimals(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String twoDec = df.format(value);
        
        return new BigDecimal(twoDec);
    }
    
}

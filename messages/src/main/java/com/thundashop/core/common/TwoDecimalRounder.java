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
    
    public static BigDecimal roundTwoDecimals(double value, int precision) {
        String format = getFormat(precision);
        
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            return BigDecimal.ZERO;
        }
        
        try {
            DecimalFormat df = new DecimalFormat(format);
            df.setRoundingMode(RoundingMode.CEILING);
            String twoDec = df.format(value);            
            return new BigDecimal(twoDec);
        } catch (Exception ex) {
            ex.printStackTrace();
            return BigDecimal.ZERO;
        }
        
    }

    private static String getFormat(int precision) {
        String format = "#.";
        for (int i=0; i<precision; i++) {
            format += "#";
        }
        return format;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.LinkedList;

/**
 *
 * @author boggi
 */
public class PmsPaymentLinksConfiguration extends DataCommon {
    public String webAdress;
    public LinkedList<PmsProductMessageConfig> productPaymentLinks = new LinkedList();
    
    //Temporary object to make sure usercodes are converted once in a time, can be removed anytime.
    boolean haveConvertedUserDiscountCodes = false;
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author boggi
 */
public class PmsPaymentLinksConfiguration extends DataCommon {
    public String webAdress;
    public LinkedList<PmsProductMessageConfig> productPaymentLinks = new LinkedList();
}

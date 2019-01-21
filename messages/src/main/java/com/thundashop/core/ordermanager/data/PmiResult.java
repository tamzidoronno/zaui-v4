/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class PmiResult implements Serializable {
    public String propertyid;
    public Date transactiondate;
    public String department;
    public String productName;
    public String prodcutId;
    public double revenue = 0;
}

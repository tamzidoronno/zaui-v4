/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class SedoxEvcCreditOrder implements Serializable {
    public int magentoOrderId;
    public double amount;
    public String evcCustomerId;
    public Date dateCreated = new Date();
}

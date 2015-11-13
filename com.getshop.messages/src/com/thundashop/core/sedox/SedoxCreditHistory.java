/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class SedoxCreditHistory implements Serializable {
    public int transactionReference;
    public String description;
    public double amount;
    public Date dateCreated = new Date();
    public double newBalance;
}

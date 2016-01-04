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
public class SedoxOrder implements Serializable {
    public String productId;
    public double creditAmount;
    public Date dateCreated;
}

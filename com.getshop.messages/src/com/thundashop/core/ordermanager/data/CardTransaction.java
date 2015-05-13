/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import java.util.Date;

/**
 *
 * @author ktonder
 */
public class CardTransaction {
    public Date date;
    public double amount = 0;
    public String currency = "";
    public String microTransactionReference = "";
}

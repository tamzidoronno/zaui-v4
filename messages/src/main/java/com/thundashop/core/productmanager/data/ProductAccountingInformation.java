/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.productmanager.data;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class ProductAccountingInformation implements Serializable {
    public String accountingNumber = "";
    public Integer taxGroupNumber;
    public String id = UUID.randomUUID().toString();
}

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
public class OrderLog implements Serializable {
    public Date logDate = new Date();
    public String description = "";
    public String userId = "";
}

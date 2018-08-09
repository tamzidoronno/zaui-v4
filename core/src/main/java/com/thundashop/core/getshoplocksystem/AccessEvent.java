/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class AccessEvent implements Serializable {
    public String groupId;
    public String lockId;
    public Date date;
    /**
     * Can be used later.
     */
    public String type = "open";
}

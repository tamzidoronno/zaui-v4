/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class LockLog extends DataCommon {
    public String lockId;
    
    public Date timestamp = new Date();
    
    /**
     * 2 = open.
     * 10 = close.
     * 27 = usercode changed.
     */
    public int event;
    
    public String nameOfUser;
    
    public LockCode lockCode;
}

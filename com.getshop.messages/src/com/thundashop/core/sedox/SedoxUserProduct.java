/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class SedoxUserProduct extends DataCommon {
    public boolean started;
    public Boolean isFinished = false;
    public String uploadOrigin;
    public Map<String,String> reference = new HashMap();
    public Map<String, Date> states = new HashMap();
    public String useCreditAccount;
    public String uploadedByUserId;
    public String comment;
    public String startedByUserId = "";
    public Date startedDate;
    public String productId;
    
    @Transient
    public SedoxProduct product;
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pullserver.data;

import com.thundashop.core.common.DataCommon;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 *
 * @author ktonder
 */
public class PullMessage extends DataCommon {
    public String postVariables = "";
    public String getVariables = "";
    public String body = "";
    public String keyId = "";
    public String belongsToStore = "";
    public boolean delivered = false;
    public int sequence = 0;
    
    public boolean isInvalidatedDueToTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -7);
        Date sevenDaysAgo = cal.getTime();
        
        return rowCreatedDate.before(sevenDaysAgo);
    }
}

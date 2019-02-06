/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.director;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
public class DailyUsage extends DataCommon {
    public int domesticSmses = 0;
    public int internationalSmses = 0;
    public int ehfs = 0;
    public String belongsToStoreId = "";
    public String systemId;
    public Date start;
    public Date end;

    public boolean isOnDay(Date timeToGet) {
        long startL = start.getTime();
        long endL = end.getTime();
        long check = timeToGet.getTime();
        
        return startL <= check && check < endL;
    }
}

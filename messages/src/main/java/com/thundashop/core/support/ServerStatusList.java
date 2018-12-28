/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.support;

import com.thundashop.core.common.DataCommon;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author boggi
 */
public class ServerStatusList extends DataCommon {
    public HashMap<String, ServerStatusEntry> entries = new HashMap();
    public Date lastSaved;

    boolean needSaving() {
        if(lastSaved == null) {
            return true;
        }
        long diff = System.currentTimeMillis() - lastSaved.getTime();
        diff = diff / 1000;
        return (diff > 600);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

/**
 *
 * @author boggi
 */
public class PmsConferenceEvent extends DataCommon {
    public String pmsConferenceItemId = "";
    public String pmsConferenceId = "";
    public String name = "";
    public Date from = null;
    public Date to = null;
    public String userId = "";
    public Integer status = 0;

    boolean betweenTime(Date start, Date end) {
        if(from.after(start) && from.before(end)) {
            return true;
        }
        if(to.after(start) && to.before(end)) {
            return true;
        }
        return false;
    }
}

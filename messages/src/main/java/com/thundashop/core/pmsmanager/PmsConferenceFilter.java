/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author boggi
 */
public class PmsConferenceFilter {
    /**
     * if this is set to true old events
     * will not be returned
     */
    public boolean onlyNoneExpiredEvents = false;
    
    /**
     * Limit the result to onlye the userIds that are
     * specified in this variable.
     */
    public List<String> userIds = new ArrayList();
    
    public String title = "";
    
    public Date start = null;
    public Date end = null;
}

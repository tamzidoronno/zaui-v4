/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class DriverRouteLog implements Serializable {
    public String userId;
    public Date date;
    public String addedByUserId;
    
    /**
     * True if added to route, false if removed from route.
     */
    public boolean added;
}

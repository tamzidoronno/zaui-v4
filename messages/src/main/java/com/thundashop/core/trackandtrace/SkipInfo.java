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
public class SkipInfo implements Serializable {
    public String skippedReasonId = "";
    
    public Date startedTimeStamp = null;
    public String startedByUserId = "";    
    public double lon;
    public double lat;
}

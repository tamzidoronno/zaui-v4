/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class ReplyMessage extends DataCommon {
    public String driverId;
    public String driverName;
    public String message;
    public List<String> podBarCodes = new ArrayList();
    
    public Date dateFromDevice;
    
    /*
    types available is: general, message, destination
    */
    public String messageSource = "";
    
    public String routeId = "";
    public String companyId;
    public Integer stopSequence;
    public String repliedOnMessageId = "";
}

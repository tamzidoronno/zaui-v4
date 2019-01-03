/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.PermenantlyDeleteData;
import java.util.Date;

/**
 *
 * @author ktonder
 */
@PermenantlyDeleteData
public class DriverMessage extends DataCommon {
    public String driverId;
    public boolean isRead = false;
    public String message = "";
    public Date ackDate;
    public String ackedByUserId;
}

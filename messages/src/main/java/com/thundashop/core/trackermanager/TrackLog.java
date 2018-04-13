/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackermanager;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class TrackLog extends DataCommon {
    public Date date;
    public String sessionId;
    public String type;
    public String value;
    public String textDescription;
    public String applicationName;
}

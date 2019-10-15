/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class CronThreadStartLog extends DataCommon {
    public String threadName;
    public Date started;
    public Long startedMs;
    public Date ended;
    public Long endedMs;
    public String belongsToStoreId;
    public Long timeUsed;
    public String type;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
public class AccessHistory extends DataCommon {
    public String lockId;
    public int userSlot;
    public Date accessTime;
    public String code;
    public String serverId = "";

    AccessHistoryResult toResult(String doorName, String name) {
        AccessHistoryResult ret = new AccessHistoryResult();
        ret.time = accessTime;
        ret.doorName = doorName;
        ret.name = name;
        return ret;
    }
}

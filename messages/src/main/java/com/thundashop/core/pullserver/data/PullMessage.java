/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pullserver.data;

import com.thundashop.core.common.DataCommon;
import java.util.LinkedHashMap;

/**
 *
 * @author ktonder
 */
public class PullMessage extends DataCommon {
    public String postVariables = "";
    public String getVariables = "";
    public String body = "";
    public String keyId = "";
    public String belongsToStore = "";
    public boolean delivered = false;
    public int sequence = 0;
}

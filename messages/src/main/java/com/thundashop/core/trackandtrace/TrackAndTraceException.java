/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class TrackAndTraceException extends DataCommon {
    public String name = "";
    /**
     * Type = common, pickup_{mode}, delivery
     */
    public String type = "";
}

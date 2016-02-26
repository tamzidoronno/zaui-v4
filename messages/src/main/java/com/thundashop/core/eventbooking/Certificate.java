/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class Certificate extends DataCommon {
    public String name = "";
    public Map<String, String> data = new HashMap();
    public Date validFrom;
    public Date validTo;
    public String backgroundImage;
}

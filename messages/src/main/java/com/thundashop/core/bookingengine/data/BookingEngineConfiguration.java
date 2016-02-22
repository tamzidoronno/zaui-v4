/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import java.util.LinkedHashMap;

/**
 *
 * @author ktonder
 */
public class BookingEngineConfiguration extends DataCommon {
    public boolean confirmationRequired = false;
    public RegistrationRules rules = new RegistrationRules();
    public TimeRepeaterData openingHours = null;
    public LinkedHashMap<String, TimeRepeaterData> openingHoursData = new LinkedHashMap();    
}

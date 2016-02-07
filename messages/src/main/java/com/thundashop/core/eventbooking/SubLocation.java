/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class SubLocation implements Serializable {
    public String id = UUID.randomUUID().toString();
    public Map<String, GroupLocationInformation> groupLocationInformation = new HashMap();
    public String name = "";
    public String description = "";
    public String contactPerson = "";
    public String cellPhone = "";
    public String lat = "";
    public String lon = "";
}

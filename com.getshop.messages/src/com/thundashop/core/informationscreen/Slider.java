/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.informationscreen;

import com.thundashop.core.common.DataCommon;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class Slider extends DataCommon {
    public String customerId;
    public String sliderType;
    public String name;
    public Map<String, String> texts = new HashMap();
    public Map<String, String> images = new HashMap();
}

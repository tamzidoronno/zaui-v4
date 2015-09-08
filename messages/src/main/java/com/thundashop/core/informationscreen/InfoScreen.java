/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.informationscreen;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class InfoScreen {
    public String backgroundImage = "";
    public Map<String, Slider> sliders = new HashMap();
    public String infoScreenId;
    public boolean showNewsFeed = false;
    public String name = "";
}

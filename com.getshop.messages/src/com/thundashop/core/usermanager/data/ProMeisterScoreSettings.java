/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class ProMeisterScoreSettings {
    public ProMeisterScoreType type = new ProMeisterScoreType();
    public Map<String, Double> scores = new HashMap();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class GroupRequirement implements Serializable {
    public Map<String, Requirement> requirements = new HashMap();
}

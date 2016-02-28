/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class ResultRequirement extends DataCommon {
    public String testId = "";
    public List<String> catsThatShouldBeUsed = new ArrayList();
    public Map<String, GroupRequirement> groupRequiments = new HashMap();
}

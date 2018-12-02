/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.support;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author boggi
 */
public class FeatureList extends DataCommon {
    public String parentId = "";
    public HashMap<String, String> test = new HashMap();
    public String module = "";
    public List<FeatureListEntry> entries = new ArrayList();
}

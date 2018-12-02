package com.thundashop.core.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author boggi
 */
public class FeatureListEntry {
    HashMap<String, String> text = new HashMap();
    String parent = "";
    String type = "";
    public List<FeatureListEntry> entries = new ArrayList();
    public String id = "";
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.listmanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author boggi
 */
public class EntryList extends DataCommon {
    public List<Entry> entries = new ArrayList();
    public String appId;
    public String name = "";
    public ListType type = null;
    public List<String> extendedLists; 
}

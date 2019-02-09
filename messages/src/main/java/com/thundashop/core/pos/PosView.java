/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class PosView extends DataCommon {
    public String type;
    public String name;
    
    public List<String> productListsIds = new ArrayList();
    public List<String> tableListIds = new ArrayList();
    
    public HashMap<String, PosListConfig> listConfigs = new HashMap();

    public void changeListMode(String listId, boolean showAsGroupButton) {
        PosListConfig config = listConfigs.get(listId);
        
        if (config == null) {
            config = new PosListConfig();
            config.listId = listId;
            listConfigs.put(listId, config);
        }
        
        config.showAsGroupButton = showAsGroupButton;
    }
}

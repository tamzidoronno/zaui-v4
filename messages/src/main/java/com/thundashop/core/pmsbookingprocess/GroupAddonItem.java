/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsbookingprocess;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class GroupAddonItem {
    
    public String type;
    public AddonItem mainItem;
    public List<AddonItem> items = new ArrayList();
    
}

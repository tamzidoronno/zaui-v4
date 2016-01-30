/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.listmanager.data;

import com.thundashop.core.common.DataCommon;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class Menu extends DataCommon {
    public String entryListId = "";
    public String appId = "";
    
    public String name = "";
    
    @Transient
    public EntryList entryList;
    
}

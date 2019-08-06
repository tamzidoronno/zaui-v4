/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.warehousemanager;

import com.thundashop.core.common.DataCommon;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class WareHouse extends DataCommon {
    public String name;
    
    /**
     * If this is set to true all cartitems that has not specified 
     * warehouse will be stock managed towards this warehouse.
     */
    public boolean isDefault = false;
    
    @Transient
    public List<WareHouseLocation> wareHouseLoccations;
}

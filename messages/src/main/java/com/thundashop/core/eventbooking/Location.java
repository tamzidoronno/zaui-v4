/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class Location extends DataCommon {
    public List<SubLocation> locations = new ArrayList();
    public String name = "";
    
    @Transient
    public boolean isFiltered = false;

    void deleteSubLocation(String subLocationId) {
        locations.removeIf(o -> o.id.equals(subLocationId));
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.calendarmanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.Comment;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Location extends DataCommon {
    public List<GroupLocationInformation> groupLocationInformation = new ArrayList();
    public List<Comment> comments = new ArrayList();
    public List<Location> subLocations = new ArrayList();
    public boolean isSubLocation = false;
    public String location = "";
    public String locationExtra = "";
    public String contactPerson = "";
    public String emailAddress = "";
    public String mobile = "";
    public String other = "";
    public Double lon;
    public Double lat;

    public void replaceSubLocation(Location location) {
        List<Location> newLocations = new ArrayList();
        for (Location subLoc : subLocations) {
            if (subLoc.id.equals(location.id)) {
                continue;
            }
            newLocations.add(subLoc);
        }
        
        newLocations.add(location);
        subLocations = newLocations;
    }

    public Location cloneMe(Location masterLocation) {
        Location loc = new Location();
        loc.groupLocationInformation = groupLocationInformation;
        loc.subLocations = subLocations;
        loc.isSubLocation = isSubLocation;
        loc.location = masterLocation.location;
        loc.locationExtra = locationExtra;
        loc.contactPerson = contactPerson;
        loc.emailAddress = emailAddress;
        loc.mobile = mobile;
        loc.other = other;
        loc.lon = lon;
        loc.lat = lat;
        
        // Extended data
        loc.id = id;
        loc.storeId = storeId;
        loc.deleted = deleted;
        loc.className = className;
        loc.rowCreatedDate = rowCreatedDate;
        loc.lastModified = lastModified;
        
        return loc;
    }

    public boolean deleteSubLocation(String locationId) {
        Location toRemove = null;
        for (Location subLoc : subLocations) {
            if (subLoc.id.equals(locationId)) {
                toRemove = subLoc;
            }
        }
        
        
        if (toRemove != null) {
            subLocations.remove(toRemove);
            return true;
        }
        
        return false;
    }
}
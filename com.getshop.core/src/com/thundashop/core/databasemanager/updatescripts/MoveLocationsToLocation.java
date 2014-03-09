/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.calendar.CalendarManager;
import com.thundashop.core.calendarmanager.data.Day;
import com.thundashop.core.calendarmanager.data.Entry;
import com.thundashop.core.calendarmanager.data.Location;
import com.thundashop.core.calendarmanager.data.Month;
import com.thundashop.core.common.DataCommon;

import com.thundashop.core.storemanager.data.Store;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class MoveLocationsToLocation extends UpgradeBase {

    private HashMap<String, Location> locations;

    private void start() throws UnknownHostException {
        List<Store> stores = getAllStores();
        for (Store store : stores) {
            clear();
            List<Month> months = new ArrayList();
            for (DataCommon dataObject : getDataFromDatabase(CalendarManager.class, store.id)) {
                
                if (dataObject instanceof Location) {
                    Location location = (Location)dataObject;
                    locations.put(location.location, location);
                }
                
                if (dataObject instanceof Month) {
                    Month month = (Month) dataObject;
                    months.add(month);
                }
            }
            
            for (Month month : months) {
                doMonth(month);
            }
            
            save(store);
        }
    }

    private void clear() {
        locations = new HashMap();
    }

    public static void main(String args[]) throws UnknownHostException {
        MoveLocationsToLocation start = new MoveLocationsToLocation();
        start.start();
    }

    private String toCamelCase(String s) {
        if (s == null) {
            return "";
        }
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString.trim();
    }

    private String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase()
                + s.substring(1).toLowerCase();
    }

    private void doMonth(Month month) throws UnknownHostException {
        for (Day day : month.days.values()) {
            for (Entry entry : day.entries) {
                if (entry.location == null || entry.location.equals("")) {
                    continue;
                }
                String locationString = toCamelCase(entry.location);
                Location location = locations.get(locationString);
                
                if (location == null) {
                    location = new Location();
                    location.id = UUID.randomUUID().toString();
                    location.location = locationString;
                    location.locationExtra = entry.locationExtended;
                    locations.put(locationString, location);
                }

                entry.locationId = location.id;
            }
        }
        
        saveObject(month, CalendarManager.class.getSimpleName());
    }

    private void save(Store store) throws UnknownHostException {
        for (Location location : locations.values()) {
            location.storeId = store.id;
            saveObject(location, CalendarManager.class.getSimpleName());
        }
    }
}
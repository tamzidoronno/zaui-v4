/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackermanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ITrackerManager {
    public void logTracking(String applicationName, String type, String value, String textDescription);
    @Administrator
    public List<TrackLog> getActivities(Date start, Date end);
}

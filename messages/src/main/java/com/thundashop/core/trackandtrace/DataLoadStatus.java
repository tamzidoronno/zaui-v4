/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;
import java.util.Set;

/**
 *
 * @author ktonder
 */
public class DataLoadStatus extends DataCommon {
    public String fileName;
    public int numberOfRoutes;
    public int numberOfDestinations;
    public int numberOfOrders;
    public long millisecondsToLoad;
    public Set<String> routeIds;
    public int numberOfPickupTasks;
    public int numberOfDeliveryTasks;
}

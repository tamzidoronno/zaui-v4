/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.Customer;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ITrackAndTraceManager {
    
    @Customer
    public List<Route> getMyRoutes();
    
    @Customer
    public void startRoute(Date startRouteDate, String routeId, double lon, double lat);
    
    @Customer
    public Route getRouteById(String routeId);
    
    @Customer
    public Destination getDestination(String destinationId);
    
    @Customer
    public Destination markHasArrived(Date dateArrived, String destinationId, double lon, double lat);
}
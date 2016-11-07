/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.Administrator;
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
    public Route getRouteById(String routeId);
    
    @Customer
    public Destination saveDestination(Destination destination);
    
    @Customer
    public void saveTask(Task task);
    
    @Customer
    public void saveRoute(Route route);
    
    public List<TrackAndTraceException> getExceptions();
    
    @Administrator
    public void saveException(TrackAndTraceException exception);
}
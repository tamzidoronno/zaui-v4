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
    public List<Route> getRoutesById(String routeId);
    
    @Customer
    public Destination saveDestination(Destination destination);
    
    @Customer
    public void markAsDeliverd(String taskId);
    
    @Customer
    public void markTaskWithExceptionDeliverd(String taskId, String exceptionId);
    
    @Customer
    public void saveRoute(Route route);
    
    public List<TrackAndTraceException> getExceptions();
    
    @Administrator
    public void saveException(TrackAndTraceException exception);
    
    @Administrator
    public List<Route> getAllRoutes();
    
//    @Administrator
//    public void addCompanyToRoute(String routeId, String companyId);
    
    public void addDeliveryTaskToDestionation(String destionatId, DeliveryTask task);
    
    @Administrator
    public void loadData(String base64, String fileName);
    
    @Administrator
    public List<DataLoadStatus> getLoadStatuses();
    
    @Administrator
    public DataLoadStatus getLoadStatus(String statusId);
    
    @Administrator
    public void addDriverToRoute(String userId, String routeId);
    
    @Customer
    public void changeQuantity(String taskId, String orderReference, int quantity);
    
    @Customer
    public void changeCountedDriverCopies(String taskId, String orderReference, int quantity);
    
    @Customer
    public void setDesitionationException(String destinationId, String exceptionId, double lon, double lat);
    
    @Customer
    public void unsetSkippedReason(String destinationId);
    
    @Customer
    public List<AcculogixExport> getExport(String routeId);
}
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
    public void markOrderWithException(String taskId, String orderReferenceNumber, String exceptionId);
    
    @Customer
    public void saveRoute(Route route);
    
    public List<TrackAndTraceException> getExceptions();
    
    @Administrator
    public void deleteRoute(String routeId);
    
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
    public List<AcculogixExport> getExport(String routeId, boolean currentState);
    
    @Administrator
    public void setSequence(String exceptionId, int sequence);
    
    @Administrator
    public Destination getDestinationById(String destinationId);
    
    @Customer
    public void setCagesOrPalletCount(String taskId, int quantity);
    
    /**
     * Returns a list of all the pooled destinations.
     * 
     * @param routeId
     * @param destinationId
     * @return 
     */
    @Customer
    public Route moveDesitinationToPool(String routeId, String destinationId);
    
    @Administrator
    public void moveDestinationFromPoolToRoute(String destId, String routeId);
    
    @Customer
    public List<PooledDestionation> getPooledDestiontions();
    
    @Customer
    public void setScannedBarcodes(String taskId, String orderReference, List<String> barcodes, boolean barcodeEnteredManually);
    
    @Administrator
    public DriverMessage sendMessageToDriver(String driverId, String message);
    
    @Administrator
    public void setInstructionOnDestination(String routeId, String destinationId, String message);
    
    @Customer
    public List<DriverMessage> getDriverMessages(String userId);
    
    @Customer
    public void acknowledgeDriverMessage(String msgId);
    
    @Administrator
    public DriverMessage getDriverMessage(String msgId);
    
    @Customer
    public List<Route> addPickupOrder(String destnationId, PickupOrder order);
}
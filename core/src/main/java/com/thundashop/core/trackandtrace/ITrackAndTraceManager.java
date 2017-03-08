/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
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
    
    @Editor
    public void deleteRoute(String routeId);
    
    @Editor
    public void saveException(TrackAndTraceException exception);
    
    @Editor
    public List<Route> getAllRoutes();
    
//    @Editor
//    public void addCompanyToRoute(String routeId, String companyId);
    
    public void addDeliveryTaskToDestionation(String destionatId, DeliveryTask task);
    
    @Editor
    public void loadData(String base64, String fileName);
    
    @Editor
    public List<DataLoadStatus> getLoadStatuses();
    
    @Editor
    public DataLoadStatus getLoadStatus(String statusId);
    
    @Editor
    public void addDriverToRoute(String userId, String routeId);

    @Editor
    public void removeDriverToRoute(String userId, String routeId);
    
    @Customer
    public void changeQuantity(String taskId, String orderReference, int parcels, int containers);
    
    @Customer
    public void changeCountedDriverCopies(String taskId, String orderReference, int quantity);
    
    @Customer
    public void setDesitionationException(String destinationId, String exceptionId, double lon, double lat);
    
    @Customer
    public void unsetSkippedReason(String destinationId);
    
    @Customer
    public List<AcculogixExport> getExport(String routeId, boolean currentState);
    
    @Editor
    public void setSequence(String exceptionId, int sequence);
    
    @Editor
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
    
    @Customer
    public Route moveDestinationFromPoolToRoute(String destId, String routeId);
    
    @Customer
    public List<PooledDestionation> getPooledDestiontions();
    
    @Customer
    public List<PooledDestionation> getPooledDestiontionsByUsersDepotId();
    
    @Customer
    public void setScannedBarcodes(String taskId, String orderReference, List<String> barcodes, boolean barcodeEnteredManually);
    
    @Editor
    public DriverMessage sendMessageToDriver(String driverId, String message);
    
    @Editor
    public String setInstructionOnDestination(String routeId, String destinationId, String message);
    
    @Customer
    public List<DriverMessage> getDriverMessages(String userId);
    
    @Customer
    public void acknowledgeDriverMessage(String msgId);
    
    @Editor
    public DriverMessage getDriverMessage(String msgId);
    
    @Customer
    public TaskAdded addPickupOrder(String destnationId, PickupOrder order);
    
    @Customer
    public void markAsCompleted(String routeId, double lat, double lon);
    
    @Administrator
    public void checkRemovalOfRoutes();
    
    @Administrator
    public List<Route> getRoutesCompletedPast24Hours();
}
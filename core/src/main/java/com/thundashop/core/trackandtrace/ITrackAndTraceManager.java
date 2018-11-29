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
import java.io.Serializable;
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
    public void loadDataBase64(String base64, String fileName);
    
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
    
    @Customer
    public List<AcculogixExport> getExportedData(Date start, Date end);
    
    @Customer
    public List<AcculogixExport> getAllExportedDataForRoute(String routeId);
    
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
    public List<Route> moveDesitinationToPool(String routeId, String destinationId);
    
    @Customer
    public List<Route> moveDestinationFromPoolToRoute(String destId, String routeId);
    
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
    public TaskAdded addPickupOrder(String destnationId, PickupOrder order, PickupTask inTask);
    
    @Customer
    public void markAsCompleted(String routeId, double lat, double lon);
    
    @Customer
    public void markAsCompletedWithTimeStamp(String routeId, double lat, double lon, Date date);
    
    @Customer
    public boolean markAsCompletedWithTimeStampAndPassword(String routeId, double lat, double lon, Date date, String password);
    
    @Administrator
    public void checkRemovalOfRoutes();
    
    @Administrator
    public List<Route> getRoutesCompletedPast24Hours();
    
    @Customer
    public void markRouteAsStarted(String routeId, Date startedTimeStamp, double lon, double lat);
    
    @Customer
    public String markRouteAsStartedWithCheck(String routeId, Date startedTimeStamp, double lon, double lat);
    
    @Customer
    public void markAsArrived(String destinationId, Date startedTimeStamp, double lon, double lat);
    
    @Customer
    public void acceptTodaysInstruction(String routeId);
    
    @Customer
    public void markDeparting(String destinationId, double latitude, double longitude, Date timeStamp, String signatureImage, String typedSignature);
    
    @Customer
    public void markInstructionAsRead(String destinationId, Date date);
   
    @Editor
    public List<String> getRouteIdsThatHasNotCompleted();
    
    @Customer
    public void replyMessage(String messageId, String text, Date date);
    
    @Customer
    public void replyMessageForDestionation(String destinationId, String text, Date date);
    
    @Customer
    public void replyGeneral(String routeId, String text, Date date);
    
    @Administrator
    public void deleteReplyMessage(String replyMessageId);
    
    @Administrator
    public List<ReplyMessage> getReplyMessages();
    
    @Administrator
    public void setSortingOfRoutes(String sortingName);
    
    @Customer
    public List<Serializable> getMyQueueMessages();
}
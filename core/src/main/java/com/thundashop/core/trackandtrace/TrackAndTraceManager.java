/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.utils.ImageManager;
import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.CheckConsistencyCron;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.socket.WebSocketServerImpl;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class TrackAndTraceManager extends ManagerBase implements ITrackAndTraceManager {

    public HashMap<String, Route> routes = new HashMap();
    
    public HashMap<String, Destination> destinations = new HashMap();
    
    public HashMap<String, PooledDestionation> pooledDestinations = new HashMap();
    
    public HashMap<String, Task> tasks = new HashMap();
    
    public HashMap<String, DataLoadStatus> loadStatuses = new HashMap();
    
    public HashMap<String, TrackAndTraceException> exceptions = new HashMap();
    
    public HashMap<String, ExportedData> exports = new HashMap();
    
    public HashMap<String, DriverMessage> driverMessages = new HashMap();

    public ExportCounter exportCounter = null;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private ImageManager imageManager;
    
    @Autowired
    public WebSocketServerImpl webSocketServer;
    
    private List<AcculogixExport> sortedExports = new ArrayList();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        
        for (DataCommon common : data.data) {
            if (common instanceof Route) {
                Route route = (Route)common;
                routes.put(route.id, route);
            }
            
            if (common instanceof ExportCounter) {
                exportCounter = (ExportCounter)common;
            }

            if (common instanceof Destination) {
                Destination dest = (Destination)common;
                destinations.put(dest.id, dest);
            }

            if (common instanceof Task) {
                Task task = (Task)common;
                tasks.put(task.id, task);
            }
            
            if (common instanceof PooledDestionation) {
                PooledDestionation pooled = (PooledDestionation)common;
                pooledDestinations.put(pooled.id, pooled);
            }
            
            if (common instanceof ExportedData) {
                ExportedData export = (ExportedData)common;
                exports.put(export.id, export);
            }

            if (common instanceof TrackAndTraceException) {
                TrackAndTraceException exception = (TrackAndTraceException)common;
                exceptions.put(exception.id, exception);
            }

            if (common instanceof DataLoadStatus) {
                DataLoadStatus loadStatus = (DataLoadStatus)common;
                loadStatuses.put(loadStatus.id, loadStatus);
            }
            
            if (common instanceof DriverMessage) {
                DriverMessage driverMessage = (DriverMessage)common;
                driverMessages.put(driverMessage.id, driverMessage);
            }
        }
        
        createScheduler("checkRemovalOfRoutes", "0 * * * *", CheckRemovalOfFinishedRoutes.class);
    }
    
    private void ensureRemoval(PooledDestionation dest) {
        moveDesitinationToPool(dest.originalRouteId, dest.destionationId);
    }
    
    @Override
    public List<Route> getMyRoutes() {
        List<Route> retList = routes.values().stream()
                .filter(route -> route.userIds.contains(getSession().currentUser.id))
                .collect(Collectors.toList());
        
        retList.stream().forEach(route -> finalize(route));
        retList.sort(Route.getSortedById());
        return retList;
    }

    @Override
    public List<Route> getRoutesById(String routeId) {
        Route foundroute = getRouteById(routeId);
        
        if (foundroute == null) {
            return new ArrayList();
        }
        
        ArrayList<Route> retList = new ArrayList();
        retList.add(foundroute);
        retList.stream().forEach(route -> finalize(route));
        return retList;
    }
    
    private Route getRouteById(String routeId) {
        String routeIdModified = routeId.replaceAll("\n", "\r");
        Route retRoute = routes.get(routeIdModified);
        
        finalize(retRoute);
        return retRoute;
    }
    
    private void sortExportedData() {
        List<AcculogixExport> acculogixExports = new ArrayList();
        exports.values().stream().forEach(o -> o.exportedData.stream().forEach( ex -> acculogixExports.add(ex)));
        
        sortedExports = acculogixExports.stream().sorted((AcculogixExport o1, AcculogixExport o2) -> {
            Long l1 = o1.TNTUID;
            Long l2 = o2.TNTUID;
            return l2.compareTo(l1);
        }).collect(Collectors.toList());
        
    }
    
    
    
    public boolean alreadyExported(AcculogixExport inExp) {
        String md5Sum = inExp.md5sum;
        
        sortExportedData();
        
        for (AcculogixExport exp : sortedExports) {
            if (exp.routeId != null && exp.routeId.equals(inExp.routeId) && exp.PODBarcodeID != null &&  exp.PODBarcodeID.equals(inExp.PODBarcodeID) &&  exp.ORReferenceNumber != null && exp.ORReferenceNumber.equals(inExp.ORReferenceNumber) && exp.md5sum != null && !exp.md5sum.equals(md5Sum)) {
                return false;
            }
            
            if (exp.md5sum.equals(md5Sum))
                return true;
        }
        
        return false;
    }

    private Destination getDestination(String destinationId) {
        Destination dest = destinations.get(destinationId);
        finalize(dest);
        return dest;
    }
    
    private void finalize(Route retRoute) {
        if (retRoute == null)
            return;
    
        retRoute.makeSureUserIdsNotDuplicated();
        
        retRoute.clearDestinations();
        
        retRoute.destinationIds.stream()
            .forEach(destinationId -> retRoute.addDestination(destinations.get(destinationId)));
        
        retRoute.getDestinations().stream().forEach(dest -> finalize(dest));

        retRoute.setPodBarcodeStringToTasks();
        
        retRoute.sortDestinations();
    }

    private void finalize(Destination dest) {
        if (dest == null)
            return;
        
        if (dest.tasks != null) {
            dest.tasks.clear();
        }
        
        dest.taskIds.stream()
                .forEach(id -> dest.tasks.add(tasks.get(id)));
     
        if (dest.companyIds.size() > 0)
            dest.company = userManager.getCompany(dest.companyIds.get(0));
    }

    @Override
    public Destination saveDestination(Destination inDestination) {
        saveObjectInternal(inDestination);
        destinations.put(inDestination.id, inDestination);
        
        return inDestination;
    }

    public Destination saveDestinationDirect(Destination inDestination) {
        saveObject(inDestination);
        destinations.put(inDestination.id, inDestination);
        return inDestination;
    }
    
    
    public void saveRouteDirect(Route inRoute) {
        saveObjectInternal(inRoute);
        routes.put(inRoute.id, inRoute);
    }
    
    @Override
    public void saveRoute(Route inRoute) {
        if (inRoute.startInfo.started && inRoute.startInfo.startedByUserId.isEmpty()) {
            inRoute.startInfo.startedByUserId = getSession().currentUser.id;
        }
        
        saveObjectInternal(inRoute);
        routes.put(inRoute.id, inRoute);
    }

    @Override
    public List<TrackAndTraceException> getExceptions() {
        return new ArrayList(exceptions.values().stream().sorted(TrackAndTraceException.getSortBySequence()).collect(Collectors.toList()));
    }

    @Override
    public void saveException(TrackAndTraceException exception) {
        saveObjectInternal(exception);
        exceptions.put(exception.id, exception);
    }

    @Override
    public List<Route> getAllRoutes() {
        ArrayList<Route> retList = new ArrayList(routes.values());
        retList.stream().forEach(route -> finalize(route));
        retList.sort(Route.getSortedById());
        return retList;
    }

//    @Override
//    public void addCompanyToRoute(String routeId, String companyId) {
//        Route route = getRouteById(routeId);
//        if (route != null) {
//            if (route.hasCompanyInDestionations(companyId)) {
//                return;
//            }
//            
//            Destination destination = new Destination();
//            destination.companyId = companyId;
//            saveObjectInternal(destination);
//            
//            destinations.put(destination.id, destination);
//            route.destinationIds.add(destination.id);
//            saveObjectInternal(route);
//        }
//    }

    @Override
    public void addDeliveryTaskToDestionation(String destionatId, DeliveryTask task) {
        Destination dest = getDestination(destionatId);
        
        if (dest != null) {
            saveObjectInternal(task);
            tasks.put(task.id, task);
            dest.taskIds.add(task.id);
            dest.ensureUniqueTaskIds();
            saveObjectInternal(dest);
        }
    }

    @Override
    public void markAsDeliverd(String taskId) {
        Task task = tasks.get(taskId);
        
        if (task != null) {
            task.completed = true;
            saveObjectInternal(task);
        }
    }

    @Override
    public void markTaskWithExceptionDeliverd(String taskId, String exceptionId) {
        Task task = tasks.get(taskId);
        
        if (task != null) {
//            task.exceptionId = exceptionId;
            task.completed = false;
            saveObjectInternal(task);
        }
    }

    @Override
    public void loadData(String base64, String fileName) {
        if (fileName.contains("drivers")) {
            new AcculogixDriverImporter(userManager, base64);
            return;
        }
        
        if (fileName.toLowerCase().contains("virtualroutes")) {
            new AcculogixVirtualRoutesImporter(this, base64);
            return;
        }
        
        AcculogixDataImporter ret = new AcculogixDataImporter(base64, userManager, this, fileName);
        ret.getRoutes().stream().forEach(route -> notifyRoute(getRouteById(route.id)));
        
    }

    void saveTask(DeliveryTask task) {
        saveObjectInternal(task);
        tasks.put(task.id, task);
    }

    void saveTaskGeneral(Task task) {
        saveObjectInternal(task);
        tasks.put(task.id, task);
    }
    
    public void  saveTaskGeneralDirect(Task task) {
        saveObject(task);
        tasks.put(task.id, task);
    }

    void saveLoadStatus(DataLoadStatus loadStatus) {
        saveObjectInternal(loadStatus);
        loadStatuses.put(loadStatus.id, loadStatus);
    }

    @Override
    public List<DataLoadStatus> getLoadStatuses() {
        ArrayList<DataLoadStatus> returnlist = new ArrayList(loadStatuses.values());
        Collections.sort(returnlist, (o1, o2) -> o2.rowCreatedDate.compareTo(o1.rowCreatedDate));
        return returnlist;
    }

    @Override
    public DataLoadStatus getLoadStatus(String statusId) {
        return loadStatuses.get(statusId);
    }

    @Override
    public void addDriverToRoute(String userId, String routeId) {
        Route route = getRouteById(routeId);
        if (route != null) {
            User user = userManager.getUserById(userId);
            if (user != null) {
                userManager.checkUserAccess(user);
                route.userIds.add(userId);
                route.makeSureUserIdsNotDuplicated();
                saveObjectInternal(route);
                
                notifyRoute(route);
            }
        }
    }

    @Override
    public void changeQuantity(String taskId, String orderReference, int parcels, int containers) {
        Task task = tasks.get(taskId);
        if (task instanceof PickupTask) {
            ((PickupTask)task).changeCountedCopies(orderReference, parcels, containers);
            saveObjectInternal(task);
        }
        if (task instanceof DeliveryTask) {
            ((DeliveryTask)task).changeQuantity(orderReference, parcels);
            saveObjectInternal(task);
        }
    }

    @Override
    public void setDesitionationException(String destinationId, String exceptionId, double lon, double lat) {
        Destination dest = destinations.get(destinationId);
        if (dest != null) {
            SkipInfo skipInfo = new SkipInfo();
            skipInfo.lat = lat;
            skipInfo.lon = lon;
            skipInfo.skippedReasonId = exceptionId;
            skipInfo.startedTimeStamp = new Date();
            skipInfo.startedByUserId = getSession().currentUser.id;
            dest.skipInfo = skipInfo;
            saveObjectInternal(dest);
        }
    }

    @Override
    public void unsetSkippedReason(String destinationId) {
        Destination dest = destinations.get(destinationId);
        if (dest != null) {
            dest.skipInfo.skippedReasonId = "";
            saveObjectInternal(dest);
        }
    }

    @Override
    public void changeCountedDriverCopies(String taskId, String orderReference, int quantity) {
        Task task = tasks.get(taskId);
        if (task instanceof DeliveryTask) {
            ((DeliveryTask)task).changeDriverQuantity(orderReference, quantity);
            saveObjectInternal(task);
        }
    }

    @Override
    public List<AcculogixExport> getAllExportedDataForRoute(String routeId) {
        List<AcculogixExport> dats = new ArrayList();
        
        exports.values().stream()
                .filter(data -> data.routeId != null && data.routeId.equals(routeId))
                .forEach(data -> {
                    dats.addAll(data.exportedData);
                });
        
        Collections.sort(dats, (o1, o2) -> {
            if (o1.TNTUID < o2.TNTUID) {
                return 1;
            }
            if (o1.TNTUID > o2.TNTUID) {
                return -1;
            }

            return 0;
        });
        
        return dats;
    }
    
    @Override
    public List<AcculogixExport> getExportedData(Date start, Date end) {
        List<AcculogixExport> datas = new ArrayList();
        
        exports.values().stream()
                .filter(exp -> exp.createdBetween(start, end))
                .forEach(exp -> datas.addAll(exp.exportedData));
        
        Collections.sort(datas, (o1, o2) -> {
            if (o1.TNTUID < o2.TNTUID) {
                return 1;
            }
            if (o1.TNTUID > o2.TNTUID) {
                return -1;
            }

            return 0;
        });
        
        return datas;
    }
    
    @Override
    public List<AcculogixExport> getExport(String routeId, boolean currentState) {
        long time = System.currentTimeMillis();
        
        Route route = getRouteById(routeId);
        
        List<AcculogixExport> everything = new ArrayList();
        
        if (route != null) {
            everything = getExportInternal(route, currentState, true);
        } else {
            
            for (Route route1 : routes.values()) {
                finalize(route1);
                everything.addAll(getExportInternal(route1, currentState, false));   
            }
            
            if (!everything.isEmpty()) {
                ExportedData exported = new ExportedData();
                exported.exportSequence = exports.size() + 1;
                exported.exportedData = new ArrayList(everything);
                saveObject(exported);    
                exports.put(exported.id, exported);

                sortExportedData();
                
            }
            
            addLastExports(null, everything, everything.isEmpty() ? 4 : 3, !everything.isEmpty());
            
        }
        
        if (!currentState) {
            addUnassignedDestinations(everything, currentState);
        }
        
        Collections.sort(everything, (o1, o2) -> {
            if (o1.TNTUID < o2.TNTUID) {
                return 1;
            }
            if (o1.TNTUID > o2.TNTUID) {
                return -1;
            }

            return 0;
        });
        
        System.out.println("Time used : " + (System.currentTimeMillis()-time));
        return everything;
        
    }

    private List<AcculogixExport> getExportInternal(Route route, boolean currentState, boolean addLastExports) throws ErrorException {
        AcculogixDataExporter exporter = new AcculogixDataExporter(route, exceptions, getStoreDefaultAddress(), imageManager, getStartNumber().exportCounter, currentState, this);
        List<AcculogixExport> exportedData = exporter.getExport();
        
        if (!exportedData.isEmpty() && addLastExports) {
            ExportedData exported = new ExportedData();
            exported.routeId = route.id;
            exported.exportSequence = getExports(route.id).size() + 1;
            exported.exportedData = new ArrayList(exportedData);
            saveObject(exported);    
            exports.put(exported.id, exported);
            
            sortExportedData();
        }
        
        if (!exportedData.isEmpty()) {
            exportCounter.exportCounter += exportedData.size();
            saveObject(exportCounter);
            
            markRouteAsClean(route);
        }
        
        if (!currentState && addLastExports) {
            addLastExports(route.id, exportedData, exportedData.isEmpty() ? 4 : 3, !exportedData.isEmpty());
        }
        
        return exportedData;
    }
    
    private ExportCounter getStartNumber() {
        if (exportCounter == null) {
            exportCounter = new ExportCounter();
        }
                
        return exportCounter;
    }
    
    public List<ExportedData> getExports(String routeId) {
        if (routeId == null) {
            return exports.values().stream()
                .collect(Collectors.toList());
        } else {
            return exports.values().stream()
                .filter(exp -> exp.routeId != null && exp.routeId.equals(routeId))
                .collect(Collectors.toList());    
        }
        
    }

    @Override
    public void setSequence(String exceptionId, int sequence) {
        TrackAndTraceException exp = exceptions.get(exceptionId);
        if (exp != null) {
            exp.sequence = sequence;
            saveObjectInternal(exp);
        }
    }

    @Override
    public void setCagesOrPalletCount(String taskId, int quantity) {
        Task task = tasks.get(taskId);
        if (task instanceof DeliveryTask) {
            ((DeliveryTask)task).containerCounted = quantity;
            saveObjectInternal(task);
        }
    }

    @Override
    public List<Route> moveDesitinationToPool(String routeId, String destinationId) {
        Route route = getRouteById(routeId);
        if (route != null) {
            boolean removed = route.removeDestination(destinationId);
            if (removed) {
                PooledDestionation dest = new PooledDestionation();
                dest.destionationId = destinationId;
                
                if (getSession() != null && getSession().currentUser != null)
                    dest.pooledByUserId = getSession().currentUser.id;
                
                dest.originalRouteId = routeId;
                saveObjectInternal(dest);
                pooledDestinations.put(dest.id, dest);
                saveObjectInternal(route);
                
                
                Route virtualRoute = createVirtualRouteBasedOnRouteId(dest);
                virtualRoute.dirty = true;
                virtualRoute.destinationIds.add(destinationId);
                createExport(virtualRoute);                
            }
        }
        
        finalize(route);
        
        notifyRoute(route);
        
        List<Route> retRoutes = new ArrayList();
        retRoutes.add(route);
        return retRoutes;
    }

    @Override
    public List<PooledDestionation> getPooledDestiontions() {
        return new ArrayList(pooledDestinations.values());
    }

    @Override
    public Destination getDestinationById(String destinationId) {
        Destination dest = destinations.get(destinationId);
        
        if (dest != null) {
            finalize(dest);
        }
        
        return dest;
    }

    @Override
    public List<Route> moveDestinationFromPoolToRoute(String destId, String routeId) {
        PooledDestionation pooledDest = pooledDestinations.remove(destId);
        
        List<Route> retRoutes = new ArrayList();
        
        if (pooledDest != null) {
            deleteObject(pooledDest);
            Route route = getRouteById(routeId);
            if (route != null) {
                Destination dest = getDestination(pooledDest.destionationId);
                dest.movedFromPool = new Date();
                route.destinationIds.add(dest.id);
                saveObjectInternal(route);
                notifyRoute(route);
                finalize(route);
                retRoutes.add(route);
            }
        }
        
        return retRoutes;
    }

    private void notifyRoute(Route route) {
        finalize(route);
        
        webSocketServer.sendMessage(route);
    }
    
    private void createExport(Route route) {
        if (route == null)
            return;
        
        finalize(route);
        
        AcculogixDataExporter exporter = new AcculogixDataExporter(route, exceptions, getStoreDefaultAddress(), imageManager, getStartNumber().exportCounter, false, this);
        List<AcculogixExport> exportedData = exporter.getExport();
        
        if (!exportedData.isEmpty()) {
            ExportedData exported = new ExportedData();
            exported.routeId = route.id;
            exported.exportSequence = getExports(route.id).size() + 1;
            exported.exportedData = new ArrayList(exportedData);
            saveObject(exported);    
            exports.put(exported.id, exported);
            
            exportCounter.exportCounter += exportedData.size();
            saveObject(exportCounter);
            
            sortExportedData();
        }
    }
    
    private void saveObjectInternal(DataCommon data) {
        if (data instanceof Route) {
            ((Route)data).dirty = true;
            createExport(((Route)data));
        }
        
        if (data instanceof Destination) {
            ((Destination)data).dirty = true;
            Route route = getRouteByDestination(((Destination)data));
            createExport(route);
        }
        
        if (data instanceof Task) {
            ((Task)data).dirty = true;
            Route route = getRouteByTask(((Task)data));
            createExport(route);
        }
        
        saveObject(data);
    }

    private void markRouteAsClean(Route route) {
        if (route != null) {
            route.dirty = false;
            for (Destination dest : route.getDestinations()) {
                dest.dirty = false;
                for (Task task : dest.tasks) {
                    task.dirty = false;
                    saveObject(task);
                }
                saveObject(dest);
            }
            
            if (!route.isVritual) {
                saveObject(route);    
            }
            
        }
    }

    private void addLastExports(String routeId, List<AcculogixExport> exportedData, int max, boolean skipFirst) {
        
        
        List<ExportedData> sortedRoutes = getExports(routeId).stream().sorted((o1, o2) -> {
            if (o1.exportSequence == o2.exportSequence) {
                return 0;
            }
            if (o1.exportSequence < o2.exportSequence) {
                return 1;
            }
            if (o1.exportSequence > o2.exportSequence) {
                return -1;
            }
            
            return 0;
        }).collect(Collectors.toList());
        
        int i = 0;
        
        for (ExportedData iExp : sortedRoutes) {
            if (skipFirst) {
                skipFirst = false;
                continue;
            }
            
            i++;
            if (i > max) {
                break;
            }
            
            exportedData.addAll(iExp.exportedData);
        }
    }

    @Override
    public void deleteRoute(String routeId) {
        String routeIdModified = routeId.replaceAll("\n", "\r");
        Route route = getRouteById(routeIdModified);
        
        if (route != null) {
            deleteObject(route);
            routes.remove(route.id);
        }
        
        for (ExportedData exportData : exports.values()) {
            if (exportData.routeId != null && exportData.routeId.equals(route.id)) {
                deleteObject(exportData);
            }
        }
        
        if (route != null) {
            if (route.destinationIds != null) {
                route.destinationIds.stream().forEach(destId -> {
                    deleteDestination(destId);
                });
            }
        }
        
        if (route != null) {
            route.userIds.stream()
                .forEach(userId -> sendRouteRemovedMessage(route.id, userId));
        }
        
        exports.values().removeIf(o -> o.routeId != null && o.routeId.equals(route.id));
    }
    
    private void deleteDestination(String destId) {
        Destination dest = destinations.remove(destId);
        if (dest != null) {
            deleteObject(dest);
            dest.taskIds.stream().forEach(taskId -> { 
                deleteTask(taskId);
            });
        }
    }
    
    private void deleteTask(String taskId) {
        Task task = tasks.remove(taskId);
        if (task != null) {
            deleteObject(task);
        }
    }

    @Override
    public void markOrderWithException(String taskId, String orderReferenceNumber, String exceptionId) {
        Task task = tasks.get(taskId);
        if (task instanceof PickupTask) {
            ((PickupTask)task).setOrderException(orderReferenceNumber, exceptionId);
            saveObjectInternal(task);
        }
    }

    @Override
    public void setScannedBarcodes(String taskId, String orderReference, List<String> barcodes, boolean barcodeEnteredManually) {
        Task task = tasks.get(taskId);
        if (task instanceof PickupTask) {
            PickupOrder order = ((PickupTask)task).getOrder(orderReference);
            if (order != null) {
                order.barcodeScanned = barcodes;
                order.barcodeEnteredManually = barcodeEnteredManually;
            }
            
            saveObjectInternal(task);
        }
    }

    @Override
    public DriverMessage sendMessageToDriver(String driverId, String message) {
        DriverMessage driverMsg = new DriverMessage();
        driverMsg.message = message;
        driverMsg.driverId = driverId;
        saveObject(driverMsg);
        driverMessages.put(driverMsg.id, driverMsg);
        
        webSocketServer.sendMessage(driverMsg);
        return driverMsg;
    }

    @Override
    public String setInstructionOnDestination(String routeId, String destinationId, String message) {
        Destination dest = getDestinationById(destinationId);
        if (dest == null) {
            return "Destination not found";
        }
        
        dest.extraInstructions = message;
        saveObject(dest);
        
        Route route = getRouteById(routeId);
        
        if (route == null) {
            return "Route not found";
        }
        
        
        finalize(route);
        notifyRoute(route);
        
        return "Received";
    }

    @Override
    public List<DriverMessage> getDriverMessages(String userId) {
        if (userId == null || userId.isEmpty())
            return new ArrayList();
        
        return driverMessages.values()
                .stream()
                .filter(msg -> msg.driverId != null && msg.driverId.equals(userId) && !msg.isRead)
                .collect(Collectors.toList());
    }

    @Override
    public void acknowledgeDriverMessage(String msgId) {
        DriverMessage msg = driverMessages.get(msgId);
        if (msg != null) {
            msg.isRead = true;
            msg.ackDate = new Date();
            msg.ackedByUserId = getSession().currentUser.id;
            saveObject(msg);
        }
    }

    @Override
    public DriverMessage getDriverMessage(String msgId) {
        return driverMessages.get(msgId);
    }

    private void addUnassignedDestinations(List<AcculogixExport> everything, boolean currentState) {
        List<PooledDestionation> localPooledDestinations = getPooledDestiontions();
        
        if (localPooledDestinations.isEmpty()) {
            return;
        }
        
        Map<String, Route> usingRoutes = new HashMap();
        
        for (PooledDestionation dest : localPooledDestinations) {
            Route virtualRoute = createVirtualRouteBasedOnRouteId(dest);
            
            if (!usingRoutes.containsKey(virtualRoute.id)) {
                usingRoutes.put(virtualRoute.id, virtualRoute);
            } else {
                virtualRoute = usingRoutes.get(virtualRoute.id);
            }
            
            virtualRoute.destinationIds.add(dest.destionationId);
        }
        
        for (Route route1 : usingRoutes.values()) {
            finalize(route1);
            everything.addAll(getExportInternal(route1, currentState, false));   
        }
    }

    private Route createVirtualRouteBasedOnRouteId(PooledDestionation dest) {
        Route route = new Route();
        route.id = dest.originalRouteId.substring(0, 2) + "99";
        route.originalId = route.id;
        route.rowCreatedDate = new Date();
        route.isVritual = true;
        route.dirty = false;
        return route;
    }

    public DeliveryTask getDeliveryTaskForDestination(String id) {
        Destination dest = getDestinationById(id);
        
        if (dest != null) {
            for (String taskId : dest.taskIds) {
                Task task = tasks.get(taskId);
                if (task instanceof DeliveryTask) {
                    return (DeliveryTask)task;
                }
            };
        }
        
        return null;
    }

    public PickupTask getPickupTask(String id) {
        Destination dest = getDestinationById(id);
        
        if (dest != null) {
            for (String taskId : dest.taskIds) {
                Task task = tasks.get(taskId);
                if (task instanceof PickupTask) {
                    return (PickupTask)task;
                }
            };
        }
        
        return null;
    }

    @Override
    public TaskAdded addPickupOrder(String destnationId, PickupOrder order, PickupTask inTask) {
        Destination dest = getDestination(destnationId);
        PickupTask task = dest.getPickupTask();
        
        if (task == null) {
            task = inTask;
            saveObject(task);
            tasks.put(task.id, task);
            
            if (!dest.taskIds.contains(task.id)) {
                dest.taskIds.add(task.id);
                saveObject(dest);
            }
        }
        
        order.source = "tnt";
        
        PickupOrder existingOrder = task.getOrder(order.referenceNumber);
        
        if (existingOrder == null) {
            task.orders.add(order);
        } else {
            order = existingOrder;
        }
        
        task.completed = false;
        saveObjectInternal(task);
        saveObjectInternal(dest);
        
        
        TaskAdded ret = new TaskAdded();
        ret.route = getFirstRouteForDestination(destnationId); 
        ret.orderReferenceNumber = order.referenceNumber;
        ret.task = task;
        finalize(dest);
        ret.destination = dest;
        
        return ret;
    }

    @Override
    public void markAsCompleted(String routeId, double lat, double lon) {
        Route route = getRouteById(routeId);
        if (route != null) {
            route.completedInfo.completed = true;
            route.completedInfo.completedByUserId = getSession().currentUser.id;
            route.completedInfo.completedTimeStamp = new Date();
            route.completedInfo.completedLat = lat;
            route.completedInfo.completedLon = lon;
            saveObjectInternal(route);
        }
    }

    @Override
    public void removeDriverToRoute(String userId, String routeId) {
        Route route = getRouteById(routeId);
        if (route != null) {
            route.userIds.remove(userId);
            saveObjectInternal(route);
            
            finalize(route);
            
            sendRouteRemovedMessage(routeId, userId);
        }
    }

    private void sendRouteRemovedMessage(String routeId, String userId) {
        DriverRemoved driverRemoved = new DriverRemoved();
        driverRemoved.routeId = routeId;
        driverRemoved.userId = userId;
        
        webSocketServer.sendMessage(driverRemoved);
    }

    @Override
    public void checkRemovalOfRoutes() {
        List<Route> routesToDelete = routes.values().stream()
                .filter(r -> r.shouldBeDeletedDueToOverdue())
                .collect(Collectors.toList());
        
        routesToDelete.forEach(r -> deleteRoute(r.id));
    }

    @Override
    public List<PooledDestionation> getPooledDestiontionsByUsersDepotId() {
        User user = userManager.getUserById(getSession().currentUser.id);
        
        String depotId = user.metaData.get("depotId");
        
        if (depotId == null)
            return new ArrayList();
        
        List<PooledDestionation> dests = getPooledDestiontions().stream()
                .filter(dest -> getRouteById(dest.originalRouteId) != null)
                .filter(dest -> getRouteById(dest.originalRouteId).depotId.equals(depotId))
                .collect(Collectors.toList());
        
        dests.stream().forEach(dest -> finalize(dest));
        return dests;
    }
    
    private void finalize(PooledDestionation dest) {
        dest.destination = getDestinationById(dest.destionationId);
        dest.originalRoute = getRouteById(dest.originalRouteId);
    }

    private Route getFirstRouteForDestination(String destnationId) {
        for (Route route : this.routes.values()) {
            if (route.destinationIds.contains(destnationId)) {
                finalize(route);
                return route;
            }
        }
        
        return null;
    }

    @Override
    public List<Route> getRoutesCompletedPast24Hours() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -204);
        Date hoursAgo = cal.getTime();
        
        List<Route> retRoutes = routes.values().stream()
                .filter(r -> r.completedInfo != null)
                .filter(r -> r.completedInfo.completed)
                .filter(r -> r.completedInfo.completedTimeStamp.after(hoursAgo))
                .collect(Collectors.toList());
        
        return retRoutes;
    }

    @Override
    public void markRouteAsStarted(String routeId, Date startedTimeStamp, double lon, double lat) {
        Route route = getRouteById(routeId);
        if (route == null) {
            return;
        }
        
        route.startInfo.started = true;
        route.startInfo.startedTimeStamp = startedTimeStamp;
        route.startInfo.lon = lon;
        route.startInfo.lat = lat;
        route.startInfo.startedByUserId = getSession().currentUser.id;
        saveObjectInternal(route);
        
        notifyRoute(route);
    }

    @Override
    public void acceptTodaysInstruction(String routeId) {
        Route route = getRouteById(routeId);
        if (route == null) {
            return;
        }
        
        route.instructionAccepted = true;
        saveObjectInternal(route);
        
        notifyRoute(route);
    }

    @Override
    public void markDeparting(String destinationId, double latitude, double longitude, Date timeStamp, String signatureImage, String typedSignature) {
        Destination destination = getDestination(destinationId);
        
        if (destination == null)
            return;
        
        String imageId = imageManager.saveImageLocally(signatureImage);
            
        if (imageId != null && !imageId.isEmpty()) {
            TrackAndTraceSignature signature = new TrackAndTraceSignature();
            signature.imageId = imageId;
            signature.operatorUserId = getSession().currentUser.id;
            signature.sigutureAddedDate = timeStamp;
            signature.typedName = typedSignature.toUpperCase();
            destination.signatures.add(signature);
        }

        destination.startInfo.completed = true;
        destination.startInfo.completedTimeStamp = timeStamp;
        destination.startInfo.completedLon = longitude;
        destination.startInfo.completedLat = latitude; 
        
        saveObjectInternal(destination);
    }

    @Override
    public void markAsArrived(String destinationId, Date startedTimeStamp, double lon, double lat) {
        Destination destination = getDestination(destinationId);
        if (destination == null)
            return;
        
        destination.startInfo.started = true;
        destination.startInfo.startedTimeStamp = startedTimeStamp;
        destination.skipInfo.skippedReasonId = "";
        destination.startInfo.lon = lon;
        destination.startInfo.lat = lat;  
        
        saveObjectInternal(destination);
    }

    private Route getRouteByDestination(Destination destination) {
        return routes.values().stream()
                .filter(route -> route.destinationIds.contains(destination.id))
                .findFirst()
                .orElse(null);
    }

    private Route getRouteByTask(Task task) {
        Destination dest = getDestinationByTask(task);
        if (dest != null) {
            return getRouteByDestination(dest);
        }
        
        return null;
    }

    private Destination getDestinationByTask(Task task) {
        return destinations.values().stream()
                .filter(dest -> dest.taskIds.contains(task.id))
                .findFirst()
                .orElse(null);
    }

}
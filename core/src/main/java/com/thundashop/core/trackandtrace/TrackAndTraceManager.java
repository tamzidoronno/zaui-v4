/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.utils.ImageManager;
import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.socket.WebSocketServerImpl;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private UserManager userManager;
    
    @Autowired
    private ImageManager imageManager;
    
    @Autowired
    public WebSocketServerImpl webSocketServer;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        
        for (DataCommon common : data.data) {
            if (common instanceof Route) {
                Route route = (Route)common;
                routes.put(route.id, route);
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
        }
        
        new ArrayList(pooledDestinations.values()).stream().forEach(pool -> ensureRemoval((PooledDestionation)pool));
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
        
        return retList;
    }

    @Override
    public List<Route> getRoutesById(String routeId) {
        Route foundroute = getRouteById(routeId);
        
        ArrayList<Route> retList = new ArrayList();
        retList.add(foundroute);
        retList.stream().forEach(route -> finalize(route));
        return retList;
    }
    
    private Route getRouteById(String routeId) {
        Route retRoute = routes.get(routeId);
        
        finalize(retRoute);
        return retRoute;
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
        storeAndSaveSignatureImage(inDestination);
        saveObjectInternal(inDestination);
        destinations.put(inDestination.id, inDestination);
        
        return inDestination;
    }

    private void storeAndSaveSignatureImage(Destination inDestination) {
        Destination memDest = destinations.get(inDestination.id);
        if (memDest != null)
            inDestination.signatures = memDest.signatures;
        
        if (inDestination.signatureImage != null && !inDestination.signatureImage.isEmpty()) {
            String imageId = imageManager.saveImageLocally(inDestination.signatureImage);
            
            
            if (imageId != null && !imageId.isEmpty()) {
                TrackAndTraceSignature signature = new TrackAndTraceSignature();
                signature.imageId = imageId;
                signature.operatorUserId = getSession().currentUser.id;
                signature.sigutureAddedDate = new Date();
                signature.typedName = inDestination.typedNameForSignature.toUpperCase();
                inDestination.signatures.add(signature);
            }
            
            inDestination.typedNameForSignature = "";
            inDestination.signatureImage = "";
        }
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
    public void changeQuantity(String taskId, String orderReference, int quantity) {
        Task task = tasks.get(taskId);
        if (task instanceof DeliveryTask) {
            ((DeliveryTask)task).changeQuantity(orderReference, quantity);
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
    public List<AcculogixExport> getExport(String routeId) {
        Route route = getRouteById(routeId);
        
        if (route == null)
            return new ArrayList();
        
        AcculogixDataExporter exporter = new AcculogixDataExporter(route, exceptions, getStoreDefaultAddress(), imageManager, getStartNumber(routeId));
        List<AcculogixExport> exportedData = exporter.getExport();
        
        if (!exportedData.isEmpty()) {
            ExportedData exported = new ExportedData();
            exported.routeId = routeId;
            exported.exportSequence = getExports(routeId).size() + 1;
            exported.exportedData = new ArrayList(exportedData);
            saveObject(exported);    
            exports.put(exported.id, exported);
        }
        
        addLastExports(routeId, exportedData, exportedData.isEmpty() ? 4 : 3, !exportedData.isEmpty());
        
        markRouteAsClean(routeId);
        
        exportedData = exportedData.stream().sorted((o1, o2) -> {
            if (o1.TNTUID == o2.TNTUID) {
                return 0;
            }
            if (o1.TNTUID < o2.TNTUID) {
                return 1;
            }
            if (o1.TNTUID > o2.TNTUID) {
                return -1;
            }
            
            return 0;
        }).collect(Collectors.toList());
        return exportedData;
    }
    
    private int getStartNumber(String routeId) {
        List<ExportedData> retVal = getExports(routeId);
        
        return retVal
                .stream()
                .mapToInt(exp -> exp.exportedData.size())
                .sum();
                
    }
    
    public List<ExportedData> getExports(String routeId) {
        return exports.values().stream()
                .filter(exp -> exp.routeId != null && exp.routeId.equals(routeId))
                .collect(Collectors.toList());
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
    public Route moveDesitinationToPool(String routeId, String destinationId) {
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
            }
        }
        
        finalize(route);
        
        notifyRoute(route);
        return route;
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
    public void moveDestinationFromPoolToRoute(String destId, String routeId) {
        PooledDestionation pooledDest = pooledDestinations.remove(destId);
        if (pooledDest != null) {
            deleteObject(pooledDest);
            Route route = getRouteById(routeId);
            if (route != null) {
                Destination dest = getDestination(pooledDest.destionationId);
                route.destinationIds.add(dest.id);
                saveObjectInternal(route);
                notifyRoute(route);
            }
        }
        
        
    }

    private void notifyRoute(Route route) {
        finalize(route);
        webSocketServer.sendMessage(route);
    }
    
    private void saveObjectInternal(DataCommon data) {
        if (data instanceof Route)
            ((Route)data).dirty = true;
        
        if (data instanceof Destination)
            ((Destination)data).dirty = true;
        
        if (data instanceof Task)
            ((Task)data).dirty = true;
        
        saveObject(data);
    }

    private void markRouteAsClean(String routeId) {
        Route route = getRouteById(routeId);
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
            saveObject(route);
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
            
            System.out.println("Adding: " + iExp.exportSequence);
            exportedData.addAll(iExp.exportedData);
        }
    }

    @Override
    public void deleteRoute(String routeId) {
        Route route = getRouteById(routeId);
        if (route != null) {
            deleteObject(route);
            routes.remove(route.id);
        }
        
        for (ExportedData exportData : exports.values()) {
            if (exportData.routeId != null && exportData.routeId.equals(routeId)) {
                deleteObject(exportData);
            }
        }
        
        exports.values().removeIf(o -> o.routeId != null && o.routeId.equals(routeId));
    }
    
}
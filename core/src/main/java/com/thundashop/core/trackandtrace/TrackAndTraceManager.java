/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
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
    
    public HashMap<String, Task> tasks = new HashMap();
    
    public HashMap<String, DataLoadStatus> loadStatuses = new HashMap();
    
    public HashMap<String, TrackAndTraceException> exceptions = new HashMap();

    @Autowired
    private UserManager userManager;
    
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

            if (common instanceof TrackAndTraceException) {
                TrackAndTraceException exception = (TrackAndTraceException)common;
                exceptions.put(exception.id, exception);
            }

            if (common instanceof DataLoadStatus) {
                DataLoadStatus loadStatus = (DataLoadStatus)common;
                loadStatuses.put(loadStatus.id, loadStatus);
            }
        }
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
        
        retRoute.destinationIds.stream()
            .forEach(destinationId -> retRoute.addDestination(destinations.get(destinationId)));
        
        retRoute.getDestinations().stream().forEach(dest -> finalize(dest));

        retRoute.setPodBarcodeStringToTasks();
    }

    private void finalize(Destination dest) {
        dest.tasks.clear();
        
        dest.taskIds.stream()
                .forEach(id -> dest.tasks.add(tasks.get(id)));
     
        if (dest.companyIds.size() > 0)
            dest.company = userManager.getCompany(dest.companyIds.get(0));
    }

    @Override
    public Destination saveDestination(Destination inDestination) {

        saveObject(inDestination);
        destinations.put(inDestination.id, inDestination);
        
        return inDestination;
    }

    @Override
    public void saveRoute(Route inRoute) {
        saveObject(inRoute);
        routes.put(inRoute.id, inRoute);
    }

    @Override
    public List<TrackAndTraceException> getExceptions() {
        return new ArrayList(exceptions.values().stream().sorted(TrackAndTraceException.getSortBySequence()).collect(Collectors.toList()));
    }

    @Override
    public void saveException(TrackAndTraceException exception) {
        saveObject(exception);
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
//            saveObject(destination);
//            
//            destinations.put(destination.id, destination);
//            route.destinationIds.add(destination.id);
//            saveObject(route);
//        }
//    }

    @Override
    public void addDeliveryTaskToDestionation(String destionatId, DeliveryTask task) {
        Destination dest = getDestination(destionatId);
        
        if (dest != null) {
            saveObject(task);
            tasks.put(task.id, task);
            dest.taskIds.add(task.id);
            dest.ensureUniqueTaskIds();
            saveObject(dest);
        }
    }

    @Override
    public void markAsDeliverd(String taskId) {
        Task task = tasks.get(taskId);
        
        if (task != null) {
            task.completed = true;
            saveObject(task);
        }
    }

    @Override
    public void markTaskWithExceptionDeliverd(String taskId, String exceptionId) {
        Task task = tasks.get(taskId);
        
        if (task != null) {
//            task.exceptionId = exceptionId;
            task.completed = false;
            saveObject(task);
        }
    }

    @Override
    public void loadData(String base64, String fileName) {
        if (fileName.contains("drivers")) {
            new AcculogixDriverImporter(userManager, base64);
            return;
        }
        
        new AcculogixDataImporter(base64, userManager, this, fileName);
    }

    void saveTask(DeliveryTask task) {
        saveObject(task);
        tasks.put(task.id, task);
    }

    void saveTaskGeneral(Task task) {
        saveObject(task);
        tasks.put(task.id, task);
    }

    void saveLoadStatus(DataLoadStatus loadStatus) {
        saveObject(loadStatus);
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
                saveObject(route);
            }
        }
    }

    @Override
    public void changeQuantity(String taskId, String orderReference, int quantity) {
        Task task = tasks.get(taskId);
        if (task instanceof DeliveryTask) {
            ((DeliveryTask)task).changeQuantity(orderReference, quantity);
            saveObject(task);
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
            saveObject(dest);
        }
    }

    @Override
    public void unsetSkippedReason(String destinationId) {
        Destination dest = destinations.get(destinationId);
        if (dest != null) {
            dest.skipInfo.skippedReasonId = "";
            saveObject(dest);
        }
    }

    @Override
    public void changeCountedDriverCopies(String taskId, String orderReference, int quantity) {
        Task task = tasks.get(taskId);
        if (task instanceof DeliveryTask) {
            ((DeliveryTask)task).changeQuantity(orderReference, quantity);
            saveObject(task);
        }
    }

    @Override
    public List<AcculogixExport> getExport(String routeId) {
        Route route = getRouteById(routeId);
        AcculogixDataExporter exporter = new AcculogixDataExporter(route, exceptions);
        return exporter.getExport();
    }

    @Override
    public void setSequence(String exceptionId, int sequence) {
        TrackAndTraceException exp = exceptions.get(exceptionId);
        if (exp != null) {
            exp.sequence = sequence;
            saveObject(exp);
        }
    }

    @Override
    public void setCagesOrPalletCount(String taskId, int quantity) {
        Task task = tasks.get(taskId);
        if (task instanceof DeliveryTask) {
            ((DeliveryTask)task).containerCounted = quantity;
            saveObject(task);
        }
    }
    
}
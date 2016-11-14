/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.UserManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
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
    
    public HashMap<String, TrackAndTraceException> exceptions = new HashMap();

    @Autowired
    private UserManager userManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        
        boolean refreshDemoData = false;
        for (DataCommon common : data.data) {
            if (refreshDemoData) {
                deleteObject(common);
            } else {
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
            }
        }
        
        if (refreshDemoData) {
            addDemoRoutes();
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

    private void addDemoRoutes() {
        if (routes.size() > 0) {
            return;
        }
        
        for (int i=0;i<5;i++) {
            Route route = new Route();
            route.id = UUID.randomUUID().toString();
            routes.put(route.id, route);
            
            for (int j=0; j<3; j++) {
                Destination dest = new Destination();
                destinations.put(dest.id, dest);
                route.destinationIds.add(dest.id);
                saveObject(dest);
                
                createPickupTask(dest, "parcels");
                createPickupTask(dest, "money");
                createPickupTask(dest, "valuedparcel");
                
                Task task = new DeliveryTask();
                saveObject(task);
                dest.taskIds.add(task.id);
                tasks.put(task.id, task);
            }
            
            saveObject(route);
        }
    }

    private void createPickupTask(Destination dest, String type) throws ErrorException {
        PickupTask task = new PickupTask();
        task.type = type;
        saveObject(task);
        dest.taskIds.add(task.id);
        tasks.put(task.id, task);
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

    }

    private void finalize(Destination dest) {
        dest.tasks.clear();
        
        dest.taskIds.stream()
                .forEach(id -> dest.tasks.add(tasks.get(id)));
        
        dest.company = userManager.getCompany(dest.companyId);
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
    public void saveTask(Task task) {
        tasks.put(task.id, task);
        saveObject(task);
    }

    @Override
    public List<TrackAndTraceException> getExceptions() {
        return new ArrayList(exceptions.values());
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

    @Override
    public void addCompanyToRoute(String routeId, String companyId) {
        Route route = getRouteById(routeId);
        if (route != null) {
            if (route.hasCompanyInDestionations(companyId)) {
                return;
            }
            
            Destination destination = new Destination();
            destination.companyId = companyId;
            saveObject(destination);
            
            destinations.put(destination.id, destination);
            route.destinationIds.add(destination.id);
            saveObject(route);
        }
    }

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
    
}
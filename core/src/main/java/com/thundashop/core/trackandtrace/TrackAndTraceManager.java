/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
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

    @Override
    public void dataFromDatabase(DataRetreived data) {
        addDemoRoutes();
    }
    
    @Override
    public List<Route> getMyRoutes() {
        ArrayList<Route> retList = new ArrayList(routes.values());
        retList.stream().forEach(route -> finalize(route));
        return retList;
    }

    private void addDemoRoutes() {
        for (int i=0;i<5;i++) {
            Route route = new Route();
            route.id = UUID.randomUUID().toString();
            routes.put(route.id, route);
            
            for (int j=0; j<3; j++) {
                Destination dest = new Destination();
                destinations.put(dest.id, dest);
                route.destinationIds.add(dest.id);
            }
        }
    }

    @Override
    public void startRoute(Date startRouteDate, String routeId, double lon, double lat) {
        Route route = routes.get(routeId);
        if (route != null) {
            route.startInfo.started = true;
            route.startInfo.startedTimeStamp = startRouteDate;
            route.startInfo.startedByUserId = getSession().currentUser.id;
            route.startInfo.lon = lon;
            route.startInfo.lat = lat;   
            //saveObject(route);
        }
    }

    @Override
    public Route getRouteById(String routeId) {
        Route retRoute = routes.get(routeId);
        
        finalize(retRoute);
        return retRoute;
    }

    @Override
    public Destination getDestination(String destinationId) {
        Destination dest = destinations.get(destinationId);
        finalize(dest);
        return dest;
    }

    private void finalize(Route retRoute) {
        retRoute.destinationIds.stream()
            .forEach(destinationId -> retRoute.addDestination(destinations.get(destinationId)));
    }

    private void finalize(Destination dest) {
        dest.calculateDestinationState();
    }

    @Override
    public Destination markHasArrived(Date dateArrived, String destinationId, double lon, double lat) {
        Destination dest = destinations.get(destinationId);
        if (dest != null) {
            dest.markHasArrived(getSession().currentUser, lon, lat, dateArrived);
//            saveObject(dest);
        }
        
        return dest;
    }
    
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.Company;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class AcculogixDataImporter {
    private final List<String[]> datas = new ArrayList();
    private final HashMap<String, Destination> destinations = new HashMap();
    private final HashMap<String, Company> companies = new HashMap();
    private final HashMap<String, Route> routes = new HashMap();
    private final HashMap<String, Task> tasks = new HashMap();
    private final UserManager userManager;
    private final TrackAndTraceManager trackAndTraceManager;
    private final String rawData;
    private final String fileName;
    private final long start;

    public AcculogixDataImporter(String rawData, UserManager userManager, TrackAndTraceManager trackAndTraceManager, String fileName) {
        this.rawData = rawData;
        this.fileName = fileName;
        this.userManager = userManager;
        this.trackAndTraceManager = trackAndTraceManager;
        
        start = System.currentTimeMillis();
        loadData();
        
        start();
    }
    

    private void loadData() {
        
        for (String line : rawData.split("\n")) {
            String[] values = line.split("\\t", -1);
            datas.add(values);
        }
    }

    private void extractCompanies() {
        
        for (String[] row : datas) {
            Company company = new Company();
            company.id = row[50];
            company.name = row[12];
            company.address = new Address();
            company.address.address = row[13];
            company.address.city = row[14];
            company.address.province = row[15];
            company.address.postCode = row[16];
            company.address.phone = row[18];
            companies.put(company.id, company);
        }
    }

    private void start()  {
        extractCompanies();
        saveCompanies();
        createRoutes();
        addDestinationsToRoutes();
        addDeliveryTasksToDestinations();
        addPickupTasksToDestinations();
        makeLogEntry();
    }

    private void saveCompanies() {
        for (Company comp : companies.values()) {
            userManager.saveCompany(comp);
        }
    }

    private void createRoutes() {
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        for (String[] row : datas) {
            Route route = new Route();
            route.id = row[49];
            route.name = route.id;
            
            try {    
                route.deliveryServiceDate = sdf.parse(row[30].split(" ")[1]);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
            
            routes.put(route.id, route);
        }
    }

    private void addDestinationsToRoutes() {
        for (Route route : routes.values()) {
            List<Destination> rows = datas.stream()
                    .filter(row -> row[49].equals(route.id))
                    .filter(distinctByKey(d -> d[20].trim()))
                    .map(row -> createDestionation(row))
                    .sorted((o1, o2) -> o1.seq.compareTo(o2.seq))
                    .collect(Collectors.toList());
            
            for (Destination dest : rows) {
                route.destinationIds.add(dest.id);
                trackAndTraceManager.saveRoute(route);
            }
        }
    }
    
    private Destination createDestionation(String[] args) {
        Destination destination = new Destination();
        destination.companyIds.addAll(getCompanyIdsForRouteSeq(args[20]));
        destination.seq = Integer.parseInt(args[20]);
        destination.podBarcode = args[34];
        destination.note = args[21];
        destination.onDemandInstructions = args[23];
        trackAndTraceManager.saveDestination(destination);
        destinations.put(destination.id, destination);
        return destination;
    }
    
    public <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>(); 
       return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private void addDeliveryTasksToDestinations() {
        for (Destination destination : destinations.values()) {
            List<String[]> deliveryOrderDatas = datas.stream()
                    .filter(row -> destination.companyIds.contains(row[50]))
                    .filter(row -> row[64].equals("DELIVERY"))
                    .collect(Collectors.toList());
            
            List<DeliveryOrder> deliveryOrders = deliveryOrderDatas.stream()
                    .map(task -> createDeliveryOrder(task))
                    .collect(Collectors.toList());
            
            if (deliveryOrders.isEmpty()) {
                continue;
            }
            
            DeliveryTask task = new DeliveryTask();
            task.taskType = Integer.parseInt(deliveryOrderDatas.get(0)[65]);
            task.orders = deliveryOrders;
            task.podBarcode = deliveryOrderDatas.get(0)[34];
            
            trackAndTraceManager.saveTaskGeneral(task);
            tasks.put(task.id, task);
            destination.taskIds.add(task.id);
            trackAndTraceManager.saveObject(destination);
        }
    }
    
    private DeliveryOrder createDeliveryOrder(String[] data) {
        DeliveryOrder order = new DeliveryOrder();
        order.comment = data[59];
        order.orderOdds = !data[54].isEmpty() ? Integer.parseInt(data[54]) : 0;
        order.orderFull = !data[55].isEmpty() ? Integer.parseInt(data[55]) : 0;
        order.orderLargeDisplays = !data[56].isEmpty() ? Integer.parseInt(data[56]) : 0;
        order.orderDriverDeliveries = !data[57].isEmpty() ? Integer.parseInt(data[57]) : 0;
        order.quantity = !data[58].isEmpty() ? Integer.parseInt(data[58]) : 0;
        order.originalQuantity = order.quantity;
        order.referenceNumber = data[33];
        order.cage = !data[61].isEmpty();
        order.orderType = data[31];
        return order;
    }

    private void makeLogEntry() {
        DataLoadStatus loadStatus = new DataLoadStatus();
        loadStatus.routeIds = routes.keySet();
        loadStatus.fileName = fileName;
        loadStatus.numberOfRoutes = routes.size();
        loadStatus.numberOfDestinations = companies.size();
        loadStatus.numberOfOrders = tasks.values().stream().mapToInt(task -> task.getOrderCount()).sum();
        loadStatus.numberOfPickupTasks = (int)tasks.values().stream().filter(task -> task instanceof PickupTask).count();
        loadStatus.numberOfDeliveryTasks = (int)tasks.values().stream().filter(task -> task instanceof DeliveryTask).count();
        
        loadStatus.millisecondsToLoad = System.currentTimeMillis() - start;
        trackAndTraceManager.saveLoadStatus(loadStatus);
    }

    private void addPickupTasksToDestinations() {
        for (Destination destination : destinations.values()) {
            List<String[]> pickupTasksDatas = datas.stream()
                    .filter(row -> destination.companyIds.contains(row[50]))
                    .filter(row -> row[64].equals("PICKUP RETURNS"))
                    .collect(Collectors.toList());
            
            List<PickupOrder> pickupTasks;
            pickupTasks = pickupTasksDatas.stream()
                    .map(task -> createPickupOrder(task))
                    .collect(Collectors.toList());
            
            if (pickupTasks.isEmpty()) {
                continue;
            }
            
            PickupTask task = new PickupTask();
            task.taskType = Integer.parseInt(pickupTasksDatas.get(0)[65]);
            task.orders = pickupTasks;
            task.cage = !pickupTasksDatas.get(0)[60].isEmpty();
            task.podBarcode = pickupTasksDatas.get(0)[34];
            
            trackAndTraceManager.saveTaskGeneral(task);
            tasks.put(task.id, task);
            destination.taskIds.add(task.id);
            trackAndTraceManager.saveObject(destination);
        }
    }

    public PickupOrder createPickupOrder(String[] data) {
        PickupOrder order = new PickupOrder();
        order.comment =  data[59];
        order.instruction = data[22] + " " + data[53];
        order.referenceNumber = data[33];
        return order;
    }

    private List<String> getCompanyIdsForRouteSeq(String stopSeq) {
        return datas.stream().filter(d -> d[20].equals(stopSeq))
                .map(d -> d[50])
                .collect(Collectors.toList());
    }
}

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
import java.util.UUID;
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
            String id = row[49] + " " + row[30];
            List<Route> alreadyExistingRotues = trackAndTraceManager.getRoutesById(id);
            if (!alreadyExistingRotues.isEmpty()) {
                routes.put(id, trackAndTraceManager.getRoutesById(id).get(0));
                continue;
            }
            
            Route route = new Route();
            route.id = id;
            route.name = route.id;
            route.originalId = row[49];
            route.userIds.add(row[63]);
            route.depotId = row[2];
            
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
                    .filter(row -> row[49].equals(route.originalId))
                    .filter(distinctByKey(d -> d[20].trim()))
                    .map(row -> createDestionation(row, route))
                    .sorted((o1, o2) -> o1.seq.compareTo(o2.seq))
                    .collect(Collectors.toList());
            
            for (Destination dest : rows) {
                if (!route.destinationIds.contains(dest.id)) {
                    route.destinationIds.add(dest.id);
                    trackAndTraceManager.saveRoute(route);
                }
            }
        }
    }
    
    private Destination createDestionation(String[] args, Route route) {
        String id = route.id + "_" + args[50] + "_" + Integer.parseInt(args[20]);
        
        Destination destination = trackAndTraceManager.getDestinationById(id);
        
        if (destination != null) {
            destinations.put(destination.id, destination);
            return destination;
        }
        
        destination = new Destination();    
        destination.id = id;
        destination.companyIds.addAll(getCompanyIdsForRouteSeq(args[20]));
        destination.seq = Integer.parseInt(args[20]);
        destination.podBarcode = args[34];
        destination.stopWindow = args[24];
        destination.onDemandInstructions = args[23];
        destination.pickupInstruction = args[22] + args[53];
        destination.deliveryInstruction = args[21];

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
                    .filter(row -> destination.seq.equals(Integer.parseInt(row[20])))
                    .filter(row -> row[64].equals("DELIVERY"))
                    .collect(Collectors.toList());
            
            List<DeliveryOrder> deliveryOrders = deliveryOrderDatas.stream()
                    .map(task -> createDeliveryOrder(task))
                    .collect(Collectors.toList());
            
            if (deliveryOrders.isEmpty()) {
                continue;
            }
            
            DeliveryTask task = trackAndTraceManager.getDeliveryTaskForDestination(destination.id);
            
            if (task == null) {
                task = new DeliveryTask();
                task.id = UUID.randomUUID().toString();
                task.taskType = Integer.parseInt(deliveryOrderDatas.get(0)[65]);
                task.orders = deliveryOrders;
                task.podBarcode = deliveryOrderDatas.get(0)[34];
                destination.taskIds.add(task.id);
                task.completed = false;
            } else {
                task.orders.addAll(deliveryOrders);
                task.completed = false;
                destination.unStart();
            }
            
            trackAndTraceManager.saveTaskGeneral(task);
            tasks.put(task.id, task);
            
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
        order.originalQuantity = !data[32].isEmpty() ? Integer.parseInt(data[32]) : 0;
        order.referenceNumber = data[33];
        order.podBarcode = data[34];
        
        System.out.println(data[32] + " | " + order.podBarcode);
        if (!data[61].isEmpty()) {
            if (data[62].equalsIgnoreCase("CAGE LG"))
                order.containerType = ContainerType.CAGE_LG;
            
            if (data[62].equalsIgnoreCase("CAGE SM"))
                order.containerType = ContainerType.CAGE_SM;
            
            if (data[62].equalsIgnoreCase("PALLET"))
                order.containerType = ContainerType.PALLET;
        }
        
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
                    .filter(row -> destination.seq.equals(Integer.parseInt(row[20])))
                    .filter(row -> row[64].equals("PICKUP RETURNS"))
                    .collect(Collectors.toList());
            
            List<PickupOrder> pickupOrders;
            pickupOrders = pickupTasksDatas.stream()
                    .map(task -> createPickupOrder(task))
                    .collect(Collectors.toList());
            
            if (pickupOrders.isEmpty()) {
                continue;
            }
            
            PickupTask task = trackAndTraceManager.getPickupTask(destination.id);
            
            if (task == null) {
                task = new PickupTask();
                task.id = UUID.randomUUID().toString();
                task.taskType = Integer.parseInt(pickupTasksDatas.get(0)[65]);
                task.orders = pickupOrders;
                task.cage = !pickupTasksDatas.get(0)[60].isEmpty();
                task.podBarcode = pickupTasksDatas.get(0)[34];
                destination.taskIds.add(task.id);
            } else {
                task.orders.addAll(pickupOrders);
                task.completed = false;
                destination.unStart();
            }
            
            trackAndTraceManager.saveTaskGeneral(task);
            tasks.put(task.id, task);
            trackAndTraceManager.saveObject(destination);
        }
    }

    public PickupOrder createPickupOrder(String[] data) {
        PickupOrder order = new PickupOrder();
        order.comment =  data[59];
        order.instruction = data[22] + " " + data[53];
        order.referenceNumber = data[33];
        order.podBarcode = data[34];
        order.container = !data[60].isEmpty();
        order.mustScanBarcode = data[67].toLowerCase().equals("scan");
        order.originalQuantity = !data[32].isEmpty() ? Integer.parseInt(data[32]) : 0;
        System.out.println(data[32] + " " + order.podBarcode);
        return order;
    }

    private List<String> getCompanyIdsForRouteSeq(String stopSeq) {
        return datas.stream().filter(d -> d[20].equals(stopSeq))
                .map(d -> d[50])
                .collect(Collectors.toList());
    }

    public List<Route> getRoutes() {
        return new ArrayList(routes.values());
    }
}

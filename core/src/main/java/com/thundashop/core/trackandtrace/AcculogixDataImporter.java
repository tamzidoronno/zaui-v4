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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        addCollectionOrders();
        addPickupTasksToDestinations();
        makeLogEntry();
        saveRoutes();
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
            
            id = cleanupIdProblems(id);
            Route route = new Route();
            route.id = id;
            route.name = route.id;
            route.originalId = row[49];
            route.userIds.add(row[63]);
            route.depotId = row[2];
            
            if (row[63] != null && !row[63].isEmpty()) {
                route.addLogEntryForDriver(row[63], "import", new Date(), true);
            }
            
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
                }
            }
            
            trackAndTraceManager.saveRouteDirect(route);
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
        destination.customerNumber = args[76];
        
        if (args.length > 68) {
            destination.signatureRequired = !args[68].contains("N");
        }
        
        trackAndTraceManager.saveDestinationDirect(destination);
        destinations.put(destination.id, destination);
        return destination;
    }
    
    public <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>(); 
       return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private void addCollectionOrders() {
        
        for (Destination destination : destinations.values()) {
            List<String[]> collectionDatas = datas.stream()
                    .filter(row -> destination.companyIds.contains(row[50]))
                    .filter(row -> destination.seq.equals(Integer.parseInt(row[20])))
                    .filter(row -> row[64].equals("DELIVERY"))
                    .filter(row -> isCollectionRow(row))
                    .collect(Collectors.toList());
            
            List<CollectionTask> collectionTasks = collectionDatas
                    .stream()
                    .map(o -> createCollectionTask(o))
                    .collect(Collectors.toList());

            collectionTasks.removeIf(o -> !o.isCod && !o.isOptional && !o.isCos);
            
            Map<String, List<CollectionTask>> groupedByCollectionType = collectionTasks
                    .stream()
                    .collect(Collectors.groupingBy(CollectionTask::getCollectionType));
            
            destination.collectionPayType = String.join(",", 
                    collectionDatas.stream()
                        .map(o -> o[69])
                        .distinct()
                        .collect(Collectors.toList())
                );
            
            List<CollectionTasks> taskList = new ArrayList();
            
            for (String type : groupedByCollectionType.keySet()) {
                CollectionTasks task = new CollectionTasks();
                task.collectionTasks = groupedByCollectionType.get(type);
                task.type = type;
                taskList.add(task);
            }
            
            destination.collectionTasks = taskList;
            
            trackAndTraceManager.saveObject(destination);
        }
    }
    
    private void addDeliveryTasksToDestinations() {
        for (Destination destination : destinations.values()) {
            List<String[]> deliveryOrderDatas = datas.stream()
                    .filter(row -> destination.companyIds.contains(row[50]))
                    .filter(row -> destination.seq.equals(Integer.parseInt(row[20])))
                    .filter(row -> !row[33].isEmpty())
                    .filter(row -> row[64].equals("DELIVERY"))
                    .collect(Collectors.toList());
            
            deliveryOrderDatas.removeIf(row -> rowIsDeliveryButNotDelivery(row));
            
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
                boolean added = false;
                for (DeliveryOrder delivery : deliveryOrders) {
                    if (!inOrders(task.orders, delivery)) {
                        task.orders.add(delivery);
                        added = true;
                    }
                }
                if (added) {
                    task.completed = false;
                    destination.unStart();
                }
            }
            
            trackAndTraceManager.saveTaskGeneralDirect(task);
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
        order.referenceNumber = data[33].trim();
        order.podBarcode = data[34];
        
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
                boolean added = false;
                for (PickupOrder pickupOrder : pickupOrders) {
                    if (!inOrders(task.orders, pickupOrder)) {
                        task.orders.add(pickupOrder);
                        added = true;
                    }
                }
                
                if (added) {
                    task.completed = false;
                    destination.unStart();
                }
            }
            
            trackAndTraceManager.saveTaskGeneralDirect(task);
            tasks.put(task.id, task);
            trackAndTraceManager.saveObject(destination);
        }
    }

    public PickupOrder createPickupOrder(String[] data) {
        PickupOrder order = new PickupOrder();
        order.comment =  data[59];
        order.instruction = data[22] + " " + data[53];
        order.referenceNumber = data[33].trim();
        order.podBarcode = data[34];
        order.container = !data[60].isEmpty();
        order.mustScanBarcode = data[67].toLowerCase().equals("scan");
        order.originalQuantity = !data[32].isEmpty() ? Integer.parseInt(data[32]) : 0;
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

    private void saveRoutes() {
        routes.values().stream()
                .forEach(route -> trackAndTraceManager.saveRoute(route));
    }

    private String cleanupIdProblems(String id) {
        id = id.replaceAll("\r", "");
        id = id.replaceAll("\n", "");
        id = id.replaceAll("\t", "");
        return id;
    }

    private boolean inOrders(List<DeliveryOrder> orders, DeliveryOrder delivery) {
        for (DeliveryOrder or : orders) {
            if ( or.referenceNumber != null && or.referenceNumber.equals(delivery.referenceNumber)) {
                return true;
            }
        }
        
        return false;
    }

    private boolean inOrders(List<PickupOrder> orders, PickupOrder pickupOrder) {
        
        for (PickupOrder or : orders) {
            if ( or.referenceNumber != null && or.referenceNumber.equals(pickupOrder.referenceNumber)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Well....
     * 
     * Instead of just adding new records for a destionation that was clearly 
     * COD and COS features, there was simply added more data to the Delivery tasks.
     * 
     * It turns out that its not enough with using only the extra rows for the collections,
     * so they needed to add extra rows for delivery that really is only for pickup.
     * 
     * @param row
     * @return 
     */
    private boolean rowIsDeliveryButNotDelivery(String[] row) {
        String orderReference = row[33].trim().toLowerCase();
        
        /**
         * Ref email with brent 12.01.2019 14:31
         * 
         * "On the delivery task you can hide the cod only transactions by filtering 
         * out any transactions references that start with a S, T, or X 
         * (do this from memory out of office at moment)"
         * 
         */
        boolean filterByOrderReference = orderReference.trim().startsWith("s") 
                || orderReference.trim().startsWith("x") 
                || orderReference.trim().startsWith("t");
        
        return filterByOrderReference;
    }

    private boolean isCollectionRow(String[] row) {
        return row[72].toLowerCase().equals("true") || row[70].toLowerCase().equals("true") || row[72].toLowerCase().equals("true");
    }

    private CollectionTask createCollectionTask(String[] data) {
        CollectionTask task = new CollectionTask();
        task.isCod = data[70].toLowerCase().trim().equals("true");
        task.isCos = data[72].toLowerCase().trim().equals("true");
        task.isOptional = data[73].toLowerCase().trim().equals("true");
        task.podBarcode = data[34];
        
        if (!data[71].isEmpty()) {
            task.amount = Double.parseDouble(data[71].replace(",", "."));
        }
        
        task.collectPreviouseInvoiceCredit = data[74].toLowerCase().equals("true");
        
        if (!data[75].isEmpty()) {
            task.previouseCreditAmount = Double.parseDouble(data[75].replace(",", "."));
        }
        
        task.customerNumber = data[76]; 
        
        task.referenceNumber = data[33].trim();
        
        return task;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.Company;
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

    private void start() {
        extractCompanies();
        saveCompanies();
        createRoutes();
        addDestinationsToRoutes();
        addTasksToDestionations();
        makeLogEntry();
    }

    private void saveCompanies() {
        for (Company comp : companies.values()) {
            userManager.saveCompany(comp);
        }
    }

    private void createRoutes() {
        for (String[] row : datas) {
            Route route = new Route();
            route.id = row[49];
            route.name = route.id;
            routes.put(route.id, route);
        }
    }

    private void addDestinationsToRoutes() {
        for (Route route : routes.values()) {
            List<Destination> rows = datas.stream()
                    .filter(row -> row[49].equals(route.id))
                    .map(row -> createDestionation(row))
                    .filter(distinctByKey(d -> d.companyId))
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
        destination.companyId = args[50];
        destination.seq = Integer.parseInt(args[20]);
        destination.podBarcode = args[34];
        destination.note = args[21];
        trackAndTraceManager.saveDestination(destination);
        destinations.put(destination.id, destination);
        return destination;
    }
    
    public <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private void addTasksToDestionations() {
        for (Destination destination : destinations.values()) {
            List<Task> tasks = datas.stream()
                    .filter(row -> row[50].equals(destination.companyId))
                    .map(task -> createTask(task))
                    .collect(Collectors.toList());
            
            for (Task task : tasks) {
                destination.taskIds.add(task.id);
            }        
            
            trackAndTraceManager.saveObject(destination);
        }
    }
    
    private Task createTask(String[] data) {
        
        Task retTask = null;
        
        if (data[64].equals("DELIVERY")) {
            DeliveryTask task = new DeliveryTask();
            task.cage = !data[62].isEmpty();
            task.quantity = Integer.parseInt(data[58]);
            retTask = task;
        }
        
        if (data[64].trim().equals("PICKUP RETURNS")) {
            PickupTask pickupTask = new PickupTask();
            pickupTask.type = "parcels";
            retTask = pickupTask;
        }
        
        if (retTask == null) {
            throw new NullPointerException("Unkown task type: " + data[64]);
        }
        
        retTask.id = data[33];
        retTask.comment = data[61]; 
        trackAndTraceManager.saveTaskGeneral(retTask);
        
        tasks.put(retTask.id, retTask);
        return retTask;
    }

    private void makeLogEntry() {
        DataLoadStatus loadStatus = new DataLoadStatus();
        loadStatus.fileName = fileName;
        loadStatus.numberOfRoutes = routes.size();
        loadStatus.numberOfDestinations = companies.size();
        loadStatus.numberOfOrders = tasks.size();
        loadStatus.millisecondsToLoad = System.currentTimeMillis() - start;
        trackAndTraceManager.saveLoadStatus(loadStatus);
    }

}

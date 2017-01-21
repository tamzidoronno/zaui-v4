/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class AcculogixDataExporter {
    private final Route route;
    private final Map<String, TrackAndTraceException> exceptions;

    public AcculogixDataExporter(Route route, Map<String, TrackAndTraceException> exceptions) {
        this.route = route;
        this.exceptions = exceptions;
    }
    
    public List<AcculogixExport> getExport() {
        List<AcculogixExport> exports = new ArrayList();
        
        for (Destination dest : route.getDestinations()) {
            for (Task task : dest.tasks) {
                if (task instanceof PickupTask) {
                    exports.addAll(createExports(route,dest,(PickupTask)task));
                }
                
                if (task instanceof DeliveryTask) {
                    exports.addAll(createExports(route,dest,(DeliveryTask)task));
                }
            }
        }
        
        return exports;
    }

    private AcculogixExport createExport(Route route, Destination dest, Task task) {
        AcculogixExport exp = new AcculogixExport();
        exp.ArrivalDateTime = dest.startInfo.started ? formatDate(dest.startInfo.startedTimeStamp) : "";
        exp.PODBarcodeID = task.podBarcode;
        exp.RDDriver$ID = route.startInfo.startedByUserId;
        exp.ReceiverName = dest.typedNameForSignature;
        exp.routeId = route.originalId;
        
        if (route.startInfo.started) {
            exp.TaskStatus = "AF";
        }
        
        if (task.completed) {
            exp.TaskStatus = "D1";
        }
        
        if (dest.skipInfo.skippedReasonId != null && !dest.skipInfo.skippedReasonId.isEmpty()) {
            exp.TaskStatus = exceptions.get(dest.skipInfo.skippedReasonId).name;
        }
        
        if (dest.skipInfo.startedTimeStamp != null) {
            exp.StatusDateTimeCompleted = formatDate(dest.skipInfo.startedTimeStamp);
            exp.Latitude = dest.skipInfo.lat;
            exp.Longitude = dest.skipInfo.lon;
        }
        
        if (dest.startInfo.started) {
            exp.StatusDateTimeCompleted = formatDate(dest.startInfo.startedTimeStamp);
            exp.Latitude = dest.startInfo.completedLat;
            exp.Longitude =dest.startInfo.completedLon;
        }
        
        if (dest.startInfo.completed) {
            exp.StatusDateTimeCompleted = formatDate(dest.startInfo.completedTimeStamp);
            exp.Latitude = dest.startInfo.completedLat;
            exp.Longitude = dest.startInfo.completedLon;
        }

        exp.SignatureObtained = dest.signatureImage != null && !dest.signatureImage.isEmpty() ? "Yes" : "No";
        return exp;
    }
    
    private List<AcculogixExport> createExports(Route route, Destination dest, PickupTask task) {
        List<AcculogixExport> toAdd = new ArrayList();
        
        for (PickupOrder order : task.orders) {
            AcculogixExport exp = createExport(route, dest, task);
            exp.BarcodeValidated = task.barcodeValidated ? "Yes" : "No";
            setOrderInfo(exp, order);

            /** 
             * TODO: Set fields: 
             * exp.TaskContainerCount
             */
            
            toAdd.add(exp);
        }
        
        return toAdd;
    }

    private List<AcculogixExport> createExports(Route route, Destination dest, DeliveryTask task) {
        List<AcculogixExport> toAdd = new ArrayList();
        
        for (DeliveryOrder order : task.orders) {
            AcculogixExport exp = createExport(route, dest, task);
            setOrderInfo(exp, order);
            
            if (order.originalQuantity > order.quantity) {
                exp.TaskType = "SHORT # PACKAGES";
            }
            
            if (order.originalQuantity < order.quantity) {
                exp.TaskType = "OVER # PACKAGES";
            }
            
            if (!order.exceptionId.isEmpty()) {
                exp.TaskType = exceptions.get(order.exceptionId).name;
            }
            
            exp.ORPieceCount = order.quantity;
            
            if (order.driverDeliveryCopiesCounted> 0) {
                exp.ORPieceCount = order.quantity;
            }
            
            exp.TotalPieces = order.originalQuantity;
            
            toAdd.add(exp);
        }
        
        return toAdd;
    }
    
    private String formatDate(Date date) {
        // YYYYMMDDHHMMSS
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        return sdf.format(date);
    }

    private void setOrderInfo(AcculogixExport exp, TntOrder order) {
        exp.ORReferenceNumber = order.referenceNumber;
        exp.TaskType = order instanceof DeliveryOrder ? "DELIVERY" : "";
        exp.TaskType = order instanceof PickupOrder ? "PICKUP RETURNS" : exp.TaskType;
        exp.TaskComments = order.comment;
        
        /**
         * TODO: Set order status.
         */
    }
}


//20170107085958
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.utils.ImageManager;
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
    private final String address;
    private final Route route;
    private final Map<String, TrackAndTraceException> exceptions;
    private final ImageManager imageManager;
    private int startId = 0;
    
    public AcculogixDataExporter(Route route, Map<String, TrackAndTraceException> exceptions, String storeAddress, ImageManager imageManager, int startId) {
        this.route = route;
        this.startId = startId;
        this.exceptions = exceptions;
        this.address = storeAddress;
        this.imageManager = imageManager;
    }
    
    public List<AcculogixExport> getExport() {
        List<AcculogixExport> exports = new ArrayList();
        
        for (Destination dest : route.getDestinations()) {
            String base64Sigature = null;
            
            if (dest.getLatestSignatureImage() != null) {
                base64Sigature = imageManager.getBase64EncodedImageLocally(dest.getLatestSignatureImage().imageId);
            }
            
            for (Task task : dest.tasks) {
                if (task instanceof PickupTask) {
                    exports.addAll(createExports(route,dest,(PickupTask)task, base64Sigature));
                }
                
                if (task instanceof DeliveryTask) {
                    exports.addAll(createExports(route,dest,(DeliveryTask)task, base64Sigature));
                }
            }
        }
        
        return exports;
    }

    private AcculogixExport createExport(Route route, Destination dest, Task task, String base64Signature) {
        if (!route.dirty && !dest.dirty && !task.dirty) {
//            return null;
        }
        
        startId++;
        
        AcculogixExport exp = new AcculogixExport();
        exp.ArrivalDateTime = dest.startInfo.started ? formatDate(dest.startInfo.startedTimeStamp) : "";
        exp.PODBarcodeID = task.podBarcode;
        exp.RDDriver$ID = route.startInfo.startedByUserId;
        exp.ReceiverName = dest.typedNameForSignature;
        exp.routeId = route.originalId;
        exp.RTRouteStopSeq = dest.seq;
        exp.TNTUID = startId;
        
        exp.TaskStatus = "DL";
        
        if (route.startInfo.started) {
            exp.TaskStatus = "AF";
        }
        
        if (dest.startInfo.completed) {
            exp.TaskStatus = "D1";
        }
        
        if (dest.skipInfo.skippedReasonId != null && !dest.skipInfo.skippedReasonId.isEmpty()) {
            exp.TaskStatus = exceptions.get(dest.skipInfo.skippedReasonId).name;
        }
        
        if (route.startInfo.started) {
            exp.StatusDateTimeCompleted = formatDate(route.startInfo.startedTimeStamp);
            exp.Latitude = route.startInfo.lat;
            exp.Longitude = route.startInfo.lon;
        }
        
        if (dest.skipInfo.startedTimeStamp != null) {
            exp.StatusDateTimeCompleted = formatDate(dest.skipInfo.startedTimeStamp);
            
            exp.Latitude = dest.skipInfo.lat;
            exp.Longitude = dest.skipInfo.lon;
        }
        
        if (dest.startInfo.started) {
            exp.StatusDateTimeCompleted = formatDate(dest.startInfo.startedTimeStamp);
            exp.Latitude = dest.startInfo.lat;
            exp.Longitude =dest.startInfo.lon;
        }
        
        if (dest.startInfo.completed) {
            exp.StatusDateTimeCompleted = formatDate(dest.startInfo.completedTimeStamp);
            exp.Latitude = dest.startInfo.completedLat;
            exp.Longitude = dest.startInfo.completedLon;
        }

        boolean anySignatures = dest.signatures != null && !dest.signatures.isEmpty();
        exp.SignatureObtained = anySignatures ? "Yes" : "No";
        
        if (anySignatures) {
            exp.signatureBase64 = base64Signature;
            exp.signatureUuid = dest.getLatestSignatureImage().imageId;
        }
        
        
        return exp;
    }
    
    private List<AcculogixExport> createExports(Route route, Destination dest, PickupTask task, String base64Signature) {
        List<AcculogixExport> toAdd = new ArrayList();
        
        for (PickupOrder order : task.orders) {
            AcculogixExport exp = createExport(route, dest, task, base64Signature);
            if (exp == null)
                continue;
            
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

    private List<AcculogixExport> createExports(Route route, Destination dest, DeliveryTask task, String base64Sigature) {
        List<AcculogixExport> toAdd = new ArrayList();
        
        for (DeliveryOrder order : task.orders) {
            AcculogixExport exp = createExport(route, dest, task, base64Sigature);
            if (exp == null)
                continue;
            
            boolean hasDriverCopies = order.driverDeliveryCopiesCounted != null && order.driverDeliveryCopiesCounted > 0;
            
            exp.ORPieceCount = order.originalQuantity;
            setOrderInfo(exp, order);
            
            if (task.completed) {
                exp.TaskType = "DELIVERED";
            }
            
            if (order.originalQuantity > order.quantity && !hasDriverCopies) {
                exp.TaskType = "SHORT # PACKAGES";
            }
            
            if (order.originalQuantity < order.quantity && !hasDriverCopies) {
                exp.TaskType = "OVER # PACKAGES";
            }
            
            if (!order.exceptionId.isEmpty()) {
                exp.TaskType = exceptions.get(order.exceptionId).name;
            }
            
            exp.TotalPieces = order.quantity;
            
            toAdd.add(exp);
        }
        
        return toAdd;
    }
    
    private String formatDate(Date date) {
        // YYYYMMDDHHMMSS
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
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
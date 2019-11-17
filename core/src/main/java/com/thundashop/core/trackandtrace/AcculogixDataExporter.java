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
    private long startId = 0;
    private TrackAndTraceManager trackAndTraceManager;
    private final boolean currentState;
    
    public AcculogixDataExporter(Route route, Map<String, TrackAndTraceException> exceptions, String storeAddress, ImageManager imageManager, long startId, boolean currentState, TrackAndTraceManager trackAndTraceManager) {
        this.route = route;
        this.trackAndTraceManager = trackAndTraceManager;
        this.startId = startId;
        this.exceptions = exceptions;
        this.address = storeAddress;
        this.imageManager = imageManager;
        this.currentState = currentState;
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
        
        exports.stream().forEach(exp -> { exp.createMd5Sum(); });
        
        exports.removeIf(exp -> trackAndTraceManager.alreadyExported(exp));
        exports.removeIf(exp -> trackAndTraceManager.afInArray(exp, exports));
        
        exports.stream().forEach(exp -> {
            startId++;
            exp.TNTUID = startId;
        });
        
        return exports;
    }

    private AcculogixExport createExport(Route route, Destination dest, Task task, String base64Signature) {
        if (!route.dirty && !dest.dirty && !task.dirty && !currentState) {
            return null;
        }
        
        if (route.rowCreatedDate == null)
            return null;
        
        AcculogixExport exp = new AcculogixExport();
        exp.ArrivalDateTime = dest.startInfo.started ? formatDate(dest.startInfo.startedTimeStamp) : "";
        exp.RDDriver$ID = route.startInfo.startedByUserId;
        exp.routeId = route.originalId;
        exp.RTRouteStopSeq = dest.seq;
        
        setTaskStatus(exp, route, task, dest);
        
        exp.StatusDateTimeCompleted = formatDate(route.rowCreatedDate);
        
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
            exp.Longitude = dest.startInfo.lon;
        }
        
        if (dest.startInfo.completed) {
            exp.StatusDateTimeCompleted = formatDate(dest.startInfo.completedTimeStamp);
            exp.Latitude = dest.startInfo.completedLat;
            exp.Longitude = dest.startInfo.completedLon;
        }

        // Set correct timestamp when status is "Route Assigned".
        if (dest.movedFromPool != null) {
            exp.StatusDateTimeCompleted = formatDate(new Date());
        }
        
        boolean anySignatures = dest.signatures != null && !dest.signatures.isEmpty();
        exp.SignatureObtained = anySignatures ? "Yes" : "No";
        
        TrackAndTraceSignature latestSignature = dest.getLatestSignatureImage();
        if (anySignatures && latestSignature != null) {
            exp.signatureBase64 = base64Signature;
            exp.signatureUuid = latestSignature.imageId;
            exp.ReceiverName = latestSignature.typedName;
        }
        
        
        return exp;
    }

    private void setTaskStatus(AcculogixExport exp, Route route1, Task task, Destination dest) {
        exp.TaskStatus = "DL";
        
        if (dest.movedFromPool != null) {
            exp.TaskStatus = "Route Assigned";
        }
        
        if (route1.startInfo.started) {
            exp.TaskStatus = "AF";
        }
        
        if (dest.startInfo.started) {
            exp.TaskStatus = "Arrive";
        }
        
        if (task instanceof DeliveryTask && task.completed) {
            exp.TaskStatus = "DeliveryTaskComplete";
        }
        
        if (task instanceof DeliveryTask && task.completed && dest.startInfo.completed) {
            exp.TaskStatus = "D1";
        }
        
        if (task instanceof PickupTask && task.completed) {
            exp.TaskStatus = "PickupTaskComplete";
        }
        
        if (task instanceof PickupTask && task.completed && dest.startInfo.completed) {
            exp.TaskStatus = "RT";
        }
        
        if (dest.skipInfo.skippedReasonId != null && !dest.skipInfo.skippedReasonId.isEmpty()) {
            exp.TaskStatus = exceptions.get(dest.skipInfo.skippedReasonId).name;
        }
        
        if (route1.isVritual) {
            exp.TaskStatus = "UNASSIGNED";
        }
    }
    
    private List<AcculogixExport> createExports(Route route, Destination dest, PickupTask task, String base64Signature) {
        List<AcculogixExport> toAdd = new ArrayList();
        
        for (PickupOrder order : task.orders) {
            AcculogixExport exp = createExport(route, dest, task, base64Signature);
            if (exp == null)
                continue;
            
            setOrderInfo(exp, order, dest);
            
            exp.PODBarcodeID = order.podBarcode;
            
            if (order.source != null && order.source.toLowerCase().equals("tnt")) {
                exp.PODBarcodeID = findFirstPODBarcode(dest);
            }
            
            exp.BarcodeValidated = task.barcodeValidated ? "Yes" : "No";
            setOrderInfo(exp, order, dest);
            
            if (dest.signatures.size() > 0) {
                exp.ORStatus = "PICKUP RETURN";
            }
           
            toAdd.add(exp);
            
            if (order.exceptionId != null && !order.exceptionId.isEmpty()) {
                exp.TaskStatus = exceptions.get(order.exceptionId).name;
                exp.ORStatus = exp.TaskStatus;
            }
            
            if(order.countedBundles > 0) {
                exp.ORPieceCount = order.countedBundles;
            } else {
                exp.ORPieceCount = order.barcodeScanned.size();
            }
            
            if (order.countedContainers > 0) {
                exp.ORPieceCount = 0;
                exp.TaskContainerCount = order.countedContainers;
            }
            
            exp.BarcodeValidated = order.barcodeEnteredManually ? "NO" : "YES";
            
            if (order.barcodeScanned.isEmpty()) {
                exp.BarcodeValidated = "";
            }
            
            exp.ORPieceCorrect = "NO";
        }
        
        return toAdd;
    }

    private List<AcculogixExport> createExports(Route route, Destination dest, DeliveryTask task, String base64Sigature) {
        List<AcculogixExport> toAdd = new ArrayList();
        
        for (DeliveryOrder order : task.orders) {
            AcculogixExport exp = createExport(route, dest, task, base64Sigature);
            if (exp == null)
                continue;
            
            setOrderInfo(exp, order, dest);
            
            exp.PODBarcodeID = order.podBarcode;
            
            if (task.completed) {
                exp.ORPieceCount = order.quantity;
            }

            if (order.containerType != null) {
                exp.TaskContainerCount = task.containerCounted;
            }
                        
            exp.ORPieceCorrect = "NO";
            
            setDeliveryStatusWhenDelivered(task, exp, order);
            
            exp.TotalPieces = order.originalQuantity;
            
            toAdd.add(exp);
            
            if (order.exceptionId != null && !order.exceptionId.isEmpty()) {
                exp.TaskStatus = exceptions.get(order.exceptionId).name;
                exp.ORStatus = exp.TaskStatus;
            }
        }
        
        return toAdd;
    }

    private void setDeliveryStatusWhenDelivered(DeliveryTask task, AcculogixExport exp, DeliveryOrder order) {
        int diff = order.originalQuantity - order.quantity;
        boolean hasDriverCopies = order.driverDeliveryCopiesCounted != null && order.driverDeliveryCopiesCounted > 0;
        
        if (task.completed) {
            exp.ORStatus = "DELIVERED";
            
            if (order.originalQuantity > order.quantity && !hasDriverCopies) {
                exp.ORStatus = "SHORT # PACKAGES";
            }
            
            if (order.originalQuantity < order.quantity && !hasDriverCopies) {
                exp.ORStatus = "OVER # PACKAGES";
            }
            
            if (hasDriverCopies) {
                int total = order.orderOdds + order.orderFull + order.orderLargeDisplays + order.driverDeliveryCopiesCounted;
                if (total > order.quantity) {
                    exp.ORStatus = "SHORT # PACKAGES";
                } else if (total < order.quantity) {
                    exp.ORStatus = "OVER # PACKAGES";
                } else {
                    exp.ORStatus = "DELIVERED";
                }
                
                diff = order.quantity - total;
            }
            
            if (diff != 0) {
                exp.ORStatus = exp.ORStatus.replaceAll("#", ""+Math.abs(diff));
                exp.ORPieceCorrect = "YES";
            }
            
            if (order.exceptionId != null && !order.exceptionId.isEmpty()) {
                exp.ORStatus = exceptions.get(order.exceptionId).name;
            }
        }
    }
    
    private String formatDate(Date date) {
        // YYYYMMDDHHMMSS
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date);
    }

    private void setOrderInfo(AcculogixExport exp, TntOrder order, Destination dest) {
        exp.ORReferenceNumber = order.referenceNumber;
        exp.TaskType = order instanceof DeliveryOrder ? "DELIVERY" : "";
        exp.TaskType = order instanceof PickupOrder ? "PICKUP RETURNS" : exp.TaskType;
        exp.TaskComments = order.comment;
        exp.taskSource = order.source;
    }

    private String findFirstPODBarcode(Destination dest) {
        for (Task iTak : dest.tasks) {
            if (iTak instanceof PickupTask) {
                PickupTask task = (PickupTask)iTak;
                for (PickupOrder order : task.orders) {
                    if (order.podBarcode != null && !order.podBarcode.isEmpty()) {
                        return order.podBarcode;
                    }
                }
            }
            
            if (iTak instanceof DeliveryTask) {
                DeliveryTask task = (DeliveryTask)iTak;
                for (DeliveryOrder order : task.orders) {
                    if (order.podBarcode != null && !order.podBarcode.isEmpty()) {
                        return order.podBarcode;
                    }
                }
            }
        }
        
        return "";
    }
}
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.TaskController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.exceptions = [];
    $scope.i = 1;
    
    $scope.doTheBack = function() {
        $state.transitionTo('base.destination', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId });
    }
    
    $scope.exceptionSelected = function(exceptionid) {
        $scope.task.exceptionId = exceptionid;
        $scope.task.completed = false;
        $api.getApi().TrackAndTraceManager.markTaskWithExceptionDeliverd($scope.task.id, exceptionid);
        datarepository.save();
        $state.transitionTo('base.destination', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId });
    }
    
    $scope.pickupExceptionSelected = function(exceptionid) {
        for (var i in $scope.task.orders) {
            var order = $scope.task.orders[i];
            if (order.referenceNumber === $stateParams.orderId) {
                order.exceptionId = exceptionid;
                order.countedBundles = -1;
                $api.getApi().TrackAndTraceManager.markOrderWithException($scope.task.id, order.referenceNumber, exceptionid);
                datarepository.save();
            }
        }
        
        $scope.goBackToOrderView();
    }
    
    $scope.anyOrderExceptions = function() {
        for (var i in $scope.task.orders) {
            var order = $scope.task.orders[i];
            if (order.exceptionId)
                return true;
        }
        
        return false;
    }
    
    $scope.showExceptions = function() {
        $state.transitionTo('base.taskexceptions', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId, taskId : $scope.task.id });
    }
    
    $scope.goBackToOrderView = function() {
        $state.transitionTo('base.task', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId, taskId : $scope.task.id });
    }
    
    $scope.setPickupType = function() {
        $scope.task = datarepository.getTaskById($stateParams.taskId);
        
        if (!$scope.task)
            return;
        
        if ($scope.task.className == "com.thundashop.core.trackandtrace.PickupTask") {
            $scope.taskType = "pickup_parcels";
        }
        
        if ($scope.task.className == "com.thundashop.core.trackandtrace.DeliveryTask") {
            $scope.taskType = "delivery";
        }
    }
    
    $scope.loadExceptions = function() {
        for (var i in datarepository.exceptions) {
            var ex = datarepository.exceptions[i];
            console.log($scope.taskType, ex.type) ;
            if (ex.type ===  $scope.taskType) {
                $scope.exceptions.push(ex);
            }
        }
    }
    
    $scope.shouldBeVisible = function(order) {
        return datarepository.currentVissibleReferenceNumber == order.referenceNumber;
    }
    
    $scope.toggleActionButton = function(task) {
        if ($('[order="'+task.referenceNumber+'"] .deliverytaskaction').is(':visible')) {
            $('[order="'+task.referenceNumber+'"] .deliverytaskaction').hide();
            datarepository.currentVissibleReferenceNumber = null;
        } else {
            datarepository.currentVissibleReferenceNumber = task.referenceNumber;
            $('.deliverytaskaction').hide();
            $('[order="'+task.referenceNumber+'"] .deliverytaskaction').show();
        }
    }
    
    $scope.isAllDriverCopiedOrdersCounted = function() {
        for (var i in $scope.task.orders) {
            var order = $scope.task.orders[i];
            if (order.orderDriverDeliveries && typeof(order.driverDeliveryCopiesCounted) === "undefined") {
                return false;
            }
        }
        
        return true;
    }
    
    $scope.markAsDeliverd = function() {
        if (!$scope.isAllDriverCopiedOrdersCounted()) {
            alert("You need to count all the orders with driver copies");
            return;
        }
        $scope.task.completed = true;
        $api.getApi().TrackAndTraceManager.markAsDeliverd($scope.task.id);
        datarepository.save();
        $state.transitionTo('base.destination', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId });
    }
    
    $scope.setPickupType();
    
    $scope.getTotalBundles = function(task) {
        var count = 0;
        
        for (var i in task.orders) {
            count += task.orders[i].quantity;
        }
        
        return count;
    }
    
    $scope.isCageOrder = function(order) {
        return order.containerType === "CAGE_LG" || order.containerType === "CAGE_SM";
    }
    
    $scope.isPalletOrder = function(order) {
        return order.containerType === "PALLET";
    }
    
    $scope.getCages = function() {
        var orders = [];
        for (var i in $scope.task.orders) {
            var order = $scope.task.orders[i];
            
            if ($scope.isCageOrder(order)) {
                orders.push(order);
            }
        }
        
        return orders;
    }
    
    $scope.getPallets = function() {
        var orders = [];
        for (var i in $scope.task.orders) {
            var order = $scope.task.orders[i];
            
            if ($scope.isPalletOrder(order)) {
                orders.push(order);
            }
        }
        
        return orders;
    }
    
    $scope.getContainerBundles = function() {
        var pallets = $scope.getPallets();
        var cages = $scope.getCages();
        var combined = pallets.concat(cages);
        return combined;
    }
    
    $scope.getBundleCount = function(orders) {
        var q = 0;
        for (var i in orders) {
            var order = orders[i];
            q += order.quantity;
        }
        return q;
    }
    
    $scope.isLooseOrder = function(order) {
        return !$scope.isPalletOrder(order) && !$scope.isCageOrder(order);
    }
    
    $scope.getLooseOrders = function() {
        var orders = [];
        for (var i in $scope.task.orders) {
            var order = $scope.task.orders[i];
            if ($scope.isLooseOrder(order)) {
                orders.push(order);
            }
        }
        return orders;
    }
    
    $scope.getPalletCount = function() {
        var orders = $scope.getPallets()();
        return $scope.countIt(orders);
    }
    
    $scope.getCageCount = function() {
        var orders = $scope.getCages();
        return $scope.countIt(orders);
    }
    
    $scope.resetPickupOrder = function(referenceNumber) {
        var task = datarepository.getTaskById($stateParams.taskId);
        for (var i in task.orders) {
            var order = task.orders[i];
            if (order.referenceNumber === referenceNumber) {
                order.barcodeScanned = [];
                order.countedBundles = -1;
                
                $api.getApi().TrackAndTraceManager.changeQuantity($scope.task.id, referenceNumber, -1, -1);
                $api.getApi().TrackAndTraceManager.setScannedBarcodes($scope.task.id, referenceNumber, [], false);                
                datarepository.save();
            }
        }
        
    }

    $scope.orderFinished = function(order) {
        if ($scope.taskType === "pickup_parcels" && order.container && order.countedBundles < 0 && order.countedContainers < 0 &&!order.exceptionId) {
            return false;
        }
        
        if ($scope.taskType === "pickup_parcels" && order.countedBundles < 0 && order.barcodeScanned.length == 0 &&!order.exceptionId) {
            return false;
        }
        
        
        if (order.orderDriverDeliveries && (!order.hasOwnProperty("driverDeliveryCopiesCounted") || order.driverDeliveryCopiesCounted == null)) {
            return false;
        }
        
        return true;
    }
    
    $scope.getContainerCount = function() {
        if (!$scope.isContainerCounted()) {
            return 0;
        }
        
        return $scope.task.containerCounted;
    }
    
    $scope.isAnyContainers = function() {
        var anyCont = false;
        for (var i in $scope.task.orders) {
            var order = $scope.task.orders[i];
            if (!order.exceptionId && ($scope.isPalletOrder(order) || $scope.isCageOrder(order))) {
                anyCont = true;
            }
        }
       
        return anyCont;
    }
    
    $scope.isContainerCounted = function() {
        var anyCont = $scope.isAnyContainers();
        
        if (!anyCont) {
            return true;
        }

        if ($scope.task.hasOwnProperty('containerCounted') && $scope.task.containerCounted > 0) 
            return true;
        
        return false;
    }
    
    $scope.allOrdersFinished = function() {
        for (var i in $scope.task.orders) {
            var order = $scope.task.orders[i];
            if (!$scope.orderFinished(order))
                return false;
        }
        
        return true;
    }
    
    $scope.openCorrection = function(order) {
        $state.transitionTo('base.ordercorrection', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: $scope.task.id, orderId: order.referenceNumber,
            type: 'normal'
        });
    }
    
    $scope.openCountedReturn = function(order) {
        $state.transitionTo('base.ordercorrection', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: $scope.task.id, orderId: order.referenceNumber,
            type: 'returncountingnormal'
        });
    }
    
    $scope.openCountPalletsOrCages = function(order) {
        $state.transitionTo('base.ordercorrection', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: $scope.task.id, orderId: null,
            type: 'cagecount'
        });
    }
    
    $scope.showKeyReference = function() {
        $state.transitionTo('base.keyreference', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: $scope.task.id
        });
    }
    
    $scope.openDriverCopies = function(order) {
        $state.transitionTo('base.ordercorrection', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: $scope.task.id, orderId: order.referenceNumber,
            type: 'driverCopies'
        });
    }
    
    $scope.openDeliveryException = function(order) {
        var params = {
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId : $scope.task.id,
            orderId: order.referenceNumber
        };
        
        $state.transitionTo('base.deliveryexception', params);
    }
    
    $scope.openPickupException = function(order) {
        var params = {
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId : $scope.task.id,
            orderId: order.referenceNumber
        };
        
        $state.transitionTo('base.pickupexception', params);
    }
    
    $scope.barcodeReceived = function(barcode, barcodeEnteredManually, keyedBarcode) {
        var labelNumber = barcode.substr(barcode.length - 3);
        var orderReference = barcode.substr(barcode.length - 13, 10);
        
        var orderFound = false;
        for (var i in $scope.task.orders) {
            var order = $scope.task.orders[i];
            if (order.referenceNumber.trim() == orderReference) {
                orderFound = order;
            }
        }
        
        if (barcodeEnteredManually && keyedBarcode !== null && keyedBarcode.length === 4 && !orderFound) {
            for (var i in $scope.task.orders) {
               var order = $scope.task.orders[i];
                if (order.referenceNumber.trim().substr(order.referenceNumber.length - 4) == keyedBarcode) {
                    orderFound = order;
                }
            }
        }
        
        if (orderFound) {
            if (!orderFound.barcodeScanned) {
                orderFound.barcodeScanned = [];
            }
            
            if ($.inArray(barcode, orderFound.barcodeScanned) < 0) {
                orderFound.barcodeScanned.push(barcode);
            }
            
            
            if (!orderFound.mustScanBarcode) {
                $scope.openCountedReturn(orderFound);
            }
            
            var tast = datarepository.getTaskById($stateParams.taskId);
            for (var i in tast.orders) {
                var order = tast.orders[i];
                if (order.referenceNumber.trim() == orderReference) {
                    tast.orders[i] = orderFound;
                }
            }
            
            var isBarcodeEnteredManually = !barcodeEnteredManually ? false : true;
            
            $api.getApi().TrackAndTraceManager.setScannedBarcodes($scope.task.id, orderFound.referenceNumber, orderFound.barcodeScanned, isBarcodeEnteredManually);
            datarepository.save();
            
            return;
        }
        
        if (barcodeEnteredManually) {
            alert("Unscheduled or invalid reference keyed.");
        } else {
            alert("Please check the return label");
        }
    }
    
    $scope.stopScanner = function() {
        if (typeof(cordova) === "undefined") {
            return;
        }
        
        cordova.exec(function() {}, function() {}, "HoneyWellBarcodeReaderE75", "stop", ["test"])
    }
    
    $scope.startCameraScanner = function() {
        cordova.plugins.barcodeScanner.scan(
           function (result) {
               $scope.barcodeReceived(result.text, true, null);
           },
           function (error) {
               alert("Scanning failed: " + error);
           },
           {
               preferFrontCamera : false, // iOS and Android
               showFlipCameraButton : true, // iOS and Android
               showTorchButton : true, // iOS and Android
               torchOn: true, // Android, launch with the torch switched on (if available)
               saveHistory: true, // Android, save scan history (default false)
               prompt : "Place a barcode inside the scan area", // Android
               resultDisplayDuration: 500, // Android, display scanned text for X ms. 0 suppresses it entirely, default 1500
               formats : "all", // default: all but PDF_417 and RSS_EXPANDED
               orientation : "landscape", // Android only (portrait|landscape), default unset so it rotates with the device
               disableAnimations : true, // iOS
               disableSuccessBeep: false // iOS and Android
           }
        );
    }

    $scope.startScanner = function() {
        if (typeof(cordova) === "undefined") {
            $scope.barcodeReceived('721148692910'+$scope.i);
            $scope.i++;
            return;
        }
        
        var deviceName = "";
        
        if (typeof(device) !== "undefined") {
            deviceName = device.model;
        }
        
        if (deviceName === "S40" && cordova.plugins && cordova.plugins.barcodeScanner) {
            $scope.startCameraScanner();
            return;
        }
        
        $scope.stopScanner();
        cordova.exec(function(a) { $scope.barcodeReceived(a); }, function(fail) {}, "HoneyWellBarcodeReaderE75", "echo", ["test"])
    }
    
    if ($state.current.name === "base.taskexceptions" || $state.current.name === "base.pickupexception" ||  $state.current.name === "base.deliveryexception") {
        $scope.loadExceptions();
    }
    
    if ($stateParams.action && $stateParams.action.state === "keyedReference") {
        var barcode = datarepository.getDestinationById($stateParams.destinationId).company.id + $stateParams.action.keyReference + "000";
        $scope.barcodeReceived(barcode, true, $stateParams.action.keyReference);
    }
    
    $scope.$on('refreshRoute', function(msg, route) {
        $scope.setPickupType();
        $scope.$evalAsync();
    });
    
}
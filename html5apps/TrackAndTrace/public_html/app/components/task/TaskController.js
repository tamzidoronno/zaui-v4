/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

controllers.TaskController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.exceptions = [];
    
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
    
    $scope.showExceptions = function() {
        $state.transitionTo('base.taskexceptions', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId, taskId : $scope.task.id });
    }
    
    $scope.setPickupType = function() {
        $scope.task = datarepository.getTaskById($stateParams.taskId);
        console.log($scope.task);
        
        if ($scope.task.className == "com.thundashop.core.trackandtrace.PickupTask") {
            $scope.taskType = "pickup";
        }
        
        if ($scope.task.className == "com.thundashop.core.trackandtrace.DeliveryTask") {
            $scope.taskType = "delivery";
        }
    }
    
    $scope.loadExceptions = function() {
        for (var i in datarepository.exceptions) {
            var ex = datarepository.exceptions[i];
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
            if (order.orderDriverDeliveries && !order.driverDeliveryCopiesCounted) {
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

    $scope.orderFinished = function(order) {
        if ($scope.taskType === "pickup" && order.countedBundles < 0) {
            return false;
        }
        
        if (order.orderDriverDeliveries && !order.hasOwnProperty("driverDeliveryCopiesCounted")) {
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
    
    $scope.isContainerCounted = function() {
        var anyCont = false;
        for (var i in $scope.task.orders) {
            var order = $scope.task.orders[i];
            if ($scope.isPalletOrder(order) || $scope.isCageOrder(order)) {
                anyCont = true;
            }
        }
        
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
    
    $scope.openDriverCopies = function(order) {
        $state.transitionTo('base.ordercorrection', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: $scope.task.id, orderId: order.referenceNumber,
            type: 'driverCopies'
        });
    }
    
    $scope.barcodeReceived = function(barcode) {
        var labelNumber = barcode.substr(barcode.length - 3);
        var orderReference = barcode.substr(barcode.length - 13, 10);
        
        var orderFound = false;
        for (var i in $scope.task.orders) {
            var order = $scope.task.orders[i];
            if (order.referenceNumber.trim() == orderReference) {
                orderFound = order;
            }
        }
        
        if (orderFound) {
            $scope.openCountedReturn(orderFound);
            return;
        }
        
        alert("Please check the return label");
    }
    
    $scope.stopScanner = function() {
        if (typeof(cordova) === "undefined") {
            alert("No cordova");
            return;
        }
        
        cordova.exec(function() {}, function() {}, "HoneyWellBarcodeReaderE75", "stop", ["test"])
    }
    
    $scope.startScanner = function() {
        if (typeof(cordova) === "undefined") {
            alert("No cordova");
            return;
        }
        
        $scope.stopScanner();
        cordova.exec(function(a) { $scope.barcodeReceived(a); }, function(fail) {}, "HoneyWellBarcodeReaderE75", "echo", ["test"])
    }
    
    if ($state.current.name === "base.taskexceptions") {
        $scope.loadExceptions();
    }

}
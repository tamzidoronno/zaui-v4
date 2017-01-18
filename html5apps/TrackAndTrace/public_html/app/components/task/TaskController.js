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
        
        if ($scope.task.className == "com.thundashop.core.trackandtrace.PickupTask") {
            $scope.taskType = "pickup_"+$scope.task.type;
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
        return datarepository.currentVissibleReferenceNumber === order.referenceNumber;
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
    
    $scope.cancelCorrection = function(a, b) {
        debugger;
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
    
    $scope.getCagesAndPallet = function() {
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
    
    $scope.countIt = function(orders) {
        var count = 0;
        
        for (var i in orders) {
            var order = orders[i];
            if (order.palletsOrCagesDelivered) {
                count += order.palletsOrCagesDelivered;
            }
        }
        
        return count;
    }
    
    $scope.orderFinished = function(order) {
        
        if ($scope.isLooseOrder(order)) {

            if (order.orderDriverDeliveries && !order.hasOwnProperty("driverDeliveryCopiesCounted")) {
                return false;
            }
            
            return true;
        }
        
        if ($scope.isCageOrder(order) || $scope.isPalletOrder(order)) {

            if (order.orderDriverDeliveries && !order.hasOwnProperty("driverDeliveryCopiesCounted")) {
                return false;
            }
            
            if (!order.hasOwnProperty("palletsOrCagesDelivered"))
                return false;
            
            return true;
        }
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
    
    $scope.openCountPalletsOrCages = function(order) {
        $state.transitionTo('base.ordercorrection', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: $scope.task.id, orderId: order.referenceNumber,
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
    
    if ($state.current.name === "base.taskexceptions") {
        $scope.loadExceptions();
    }
    
}
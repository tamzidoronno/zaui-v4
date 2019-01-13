/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.TaskCorrectionController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.numbers = 10;
    $scope.api = $api;
    $scope.countPickupContainerType = "";
    $scope.showOldValue = true;
    
    $scope.setOrder = function() {
        $scope.task = datarepository.getTaskById($stateParams.taskId);
        
        if (!$scope.task)
            return;
        
        for (var i in $scope.task.orders) {
            var order = $scope.task.orders[i];
            if (order.referenceNumber === $stateParams.orderId) {
                $scope.order = order;
                $scope.numbers = order.quantity;
            }
        }
    }
    
    $scope.needToPreselectType = function() {
        if ($scope.order && $scope.order.container && !$scope.countPickupContainerType)
            return true;
        
        return false;
    }
    
    $scope.getOldQuantity = function(order) {
        
        if ($stateParams.type === "collection") {
            $scope.showOldValue = false;
            return 0;
        }
        
        if ($stateParams.type === "collectionChequeAmount") {
            var destination = datarepository.getDestinationById($stateParams.destinationId);
            destination.collectionChequeAmount;
            return destination.codAdjustment;
        }
        
        if ($stateParams.type === "normal") {
            return order.quantity;
        }
        
        if ($stateParams.type === "returncountingnormal") {
            if (order.countedBundles < 0)
                return 0;
            
            return order.countedBundles;
        }
        
        if ($stateParams.type === "driverCopies") {
            return order.driverDeliveryCopiesCounted;
        }
        
        if ($stateParams.type === "cagecount") {
            return $scope.task.containerCounted;
        }
        
        return 0;
    }
    
    $scope.getOldQuantityText = function() {
         if ($stateParams.type === "collection")
            return "Old amount";
        
        return "Old quantity:";
    }
    
    $scope.getNewQuantityText = function() {
        console.log($stateParams);
        if ($stateParams.type === "collection" && $stateParams.collectionData.action === "registerchequenumber")
            return "Cheque number";
        
        if ($stateParams.type === "collection")
            return "New amount";
        
        return "New quantity:";
    }
    
    $scope.makeCorrection = function(a, b) {
        var newQuantity = parseFloat(b, 10);
        
        if (isNaN(newQuantity)) {
            newQuantity = 0;
        }
        
        if ($stateParams.type === "collection") {
            var collectionData = $stateParams.collectionData;
            collectionData.newAmount = newQuantity;
            
            $state.transitionTo('base.collection', { 
                destinationId: $stateParams.destinationId,  
                routeId: $stateParams.routeId, 
                action : {}, 
                collectionData: collectionData, 
                collectionType: $stateParams.collectionData.collectionType, 
                collectionSubType: $stateParams.collectionData.collectionSubType 
            });
            return;
        }
        
        if ($stateParams.type === "normal") {
            $scope.order.quantity = newQuantity;
            datarepository.save();
            $scope.api.getApi().TrackAndTraceManager.changeQuantity($scope.task.id, $scope.order.referenceNumber, b, -1);
        }
        
        if ($stateParams.type === "cagecount") {
            $scope.task.containerCounted = newQuantity;
            datarepository.save();
            $scope.api.getApi().TrackAndTraceManager.setCagesOrPalletCount($scope.task.id, b);
        }
        
        if ($stateParams.type === "returncountingnormal") {
            
            if ($scope.order.container) {
                if ($scope.countPickupContainerType === "container") {
                    $scope.order.countedBundles = -1;
                    $scope.order.countedContainers = newQuantity;
                } else {
                    $scope.order.countedBundles = newQuantity;
                    $scope.order.countedContainers = -1;    
                }
            } else {
                $scope.order.countedBundles = newQuantity;
                $scope.order.countedContainers = -1;
            }
            
            $scope.order.exceptionId = null;
            
            $scope.api.getApi().TrackAndTraceManager.changeQuantity($scope.task.id, $scope.order.referenceNumber, $scope.order.countedBundles, $scope.order.countedContainers);
            $scope.api.getApi().TrackAndTraceManager.markOrderWithException($scope.task.id, $scope.order.referenceNumber, "");
            datarepository.save();
        }
        
        if ($stateParams.type === "driverCopies") {
            var order = $scope.order;
            $scope.order.driverDeliveryCopiesCounted = newQuantity;
            
            var totalBundles = order.driverDeliveryCopiesCounted + order.orderOdds + order.orderFull + order.orderLargeDisplays;
            $scope.order.quantity = totalBundles;
            
            datarepository.save();
            $scope.api.getApi().TrackAndTraceManager.changeCountedDriverCopies($scope.task.id, $scope.order.referenceNumber, b);
            $scope.api.getApi().TrackAndTraceManager.changeQuantity($scope.task.id, $scope.order.referenceNumber, $scope.order.quantity, -1);
        }
        
        $state.transitionTo('base.task', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId, taskId: $stateParams.taskId });
    }
    
    $scope.setContainerType = function(type) {
        $scope.countPickupContainerType = type;
    }
    
    $scope.goBack = function(a, b) {
        if ($stateParams.type === "collection") {
            
            $state.transitionTo('base.collection', { 
                destinationId: $stateParams.destinationId,  
                routeId: $stateParams.routeId, 
                action : {}, 
                collectionData: $stateParams.collectionData, 
                collectionType: $stateParams.collectionData.collectionType, 
                collectionSubType: $stateParams.collectionData.collectionSubType 
            });
            
            return;
        }
        
        $state.transitionTo('base.task', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId, taskId: $stateParams.taskId });
    }
    
    $scope.showExtraButtons = function() {
        $('.numpadcomma').hide();
        $('.plusminus').hide();
        
        if ($stateParams.type === "collection") {
            setTimeout(function() {
                $('.plusminus').attr('style', 'display: inline-block !important;');
                $('.numpadcomma').attr('style', 'display: inline-block !important;');
            }, 0);
        }
    }
    
    $scope.setOrder();
    $scope.showExtraButtons();
}
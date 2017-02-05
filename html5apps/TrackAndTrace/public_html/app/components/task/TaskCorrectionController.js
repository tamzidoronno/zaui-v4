/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.TaskCorrectionController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.numbers = 10;
    $scope.api = $api;
    
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
    
    $scope.getOldQuantity = function(order) {
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
    
    $scope.makeCorrection = function(a, b) {
        var newQuantity = parseInt(b, 10);
        if ($stateParams.type === "normal") {
            $scope.order.quantity = newQuantity;
            datarepository.save();
            $scope.api.getApi().TrackAndTraceManager.changeQuantity($scope.task.id, $scope.order.referenceNumber, b);
        }
        
        if ($stateParams.type === "cagecount") {
            $scope.task.containerCounted = newQuantity;
            datarepository.save();
            $scope.api.getApi().TrackAndTraceManager.setCagesOrPalletCount($scope.task.id, b);
        }
        
        if ($stateParams.type === "returncountingnormal") {
            $scope.order.countedBundles = newQuantity;
            $scope.order.exceptionId = null;
            
            $scope.api.getApi().TrackAndTraceManager.changeQuantity($scope.task.id, $scope.order.referenceNumber, newQuantity);
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
            $scope.api.getApi().TrackAndTraceManager.changeQuantity($scope.task.id, $scope.order.referenceNumber, $scope.order.quantity);
        }
        
        $state.transitionTo('base.task', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId, taskId: $stateParams.taskId });
    }
    
    $scope.goBack = function(a, b) {
        $state.transitionTo('base.task', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId, taskId: $stateParams.taskId });
    }
    
    $scope.setOrder();
}
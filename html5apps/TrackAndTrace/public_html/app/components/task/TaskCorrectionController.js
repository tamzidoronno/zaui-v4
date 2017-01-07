/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


controllers.TaskCorrectionController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.numbers = 10;
    $scope.api = $api;
    
    $scope.setOrder = function() {
        $scope.task = datarepository.getTaskById($stateParams.taskId);
        
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
        
        if ($stateParams.type === "driverCopies") {
            return order.driverDeliveryCopiesCounted;
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
        
        if ($stateParams.type === "driverCopies") {
            var order = $scope.order;
            var totalBundles = order.driverDeliveryCopiesCounted + order.orderOdds + order.orderFull + order.orderLargeDisplays;
            
            $scope.order.driverDeliveryCopiesCounted = newQuantity;
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
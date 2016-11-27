/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

controllers.TaskController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.exceptions = [];
    
    $scope.doTheBack = function() {
        window.history.back();
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
    
    $scope.toggleActionButton = function(task) {
        $('.deliverytaskaction').hide();
        $('[order="'+task.referenceNumber+'"] .deliverytaskaction').show();
    }
    
    $scope.markAsDeliverd = function() {
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
    
    if ($state.current.name === "base.taskexceptions") {
        $scope.loadExceptions();
    }
}


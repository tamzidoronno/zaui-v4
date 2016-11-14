/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.DestinationController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.route = datarepository.getRouteById($stateParams.routeId);
    $scope.destination = datarepository.getDestinationById($stateParams.destinationId);
    
    $scope.doTheBack = function() {
        $state.transitionTo('base.routeoverview', { routeId : $stateParams.routeId });
    };
    
    $scope.destinationState = function(state) {
        if ($scope.destination.startInfo.started) {
            return "started";
        }
        
        return "unkown";
    };
    
    $scope.getTaskName = function(task) {
        if (task.className == "com.thundashop.core.trackandtrace.PickupTask")
            return "Pickup - " + task.type;
        
        if (task.className == "com.thundashop.core.trackandtrace.DeliveryTask")
            return "Delivery";
    };
    
    $scope.markAsArrived = function() {
        navigator.geolocation.getCurrentPosition(function(position) {
            $scope.destination.startInfo.started = true;
            $scope.destination.startInfo.startedTimeStamp = new Date();
            $scope.destination.startInfo.lon = position.coords.longitude;
            $scope.destination.startInfo.lat = position.coords.latitude;  
            $scope.$apply();

            $api.getApi().TrackAndTraceManager.saveDestination($scope.destination);
            datarepository.save();
        });
    }
    
    $scope.getStatus = function(task) {
        if (task.completed)
            return "completed";
        
        if (task.exceptionId) {
            return "exception";
        }
        
        return "unknown";
    }
    
    $scope.allTaskCompleted = function() {
        for (var i in $scope.destination.tasks) {
            var task = $scope.destination.tasks[i];
            
            if ($scope.getStatus(task) === "unknown") {
                return false;
            }
        }
        
        return true;
    },
            
    $scope.departedTapped = function() {
        $state.transitionTo('base.destinationsignature', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId });
    }
    
    $scope.openTask = function(destinationId, routeId, taskId) {
        $state.transitionTo('base.task', { destinationId: destinationId,  routeId: routeId, taskId: taskId });
    }
}
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.DestinationController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.route = datarepository.getRouteById($stateParams.routeId);
    $scope.destination = datarepository.getDestinationById($stateParams.destinationId);
    $scope.showReplyMessage = false;

    if (!$scope.destination) {
        $state.transitionTo("base.home");
        return;
    }
    
    $scope.doTheBack = function() {
        $state.transitionTo('base.routeoverview', { routeId : $stateParams.routeId });
    };
    
    $scope.instructionRead = function() {
        $scope.destination.extraInstractionsRead = true;
        $scope.destination.extraInstractionsReadDate = new Date();
        datarepository.save();
        $api.getApi().TrackAndTraceManager.markInstructionAsRead($scope.destination.id, new Date()); 
    }
    
    $scope.destinationState = function(state) {
        if ($scope.destination.startInfo.started) {
            return "started";
        }
        
        return "unkown";
    };
    
    
    $scope.showAddPickupTask = function() {
        $state.transitionTo("base.addPickupTask",  { 
            destinationId: $stateParams.destinationId,
            routeId: $stateParams.routeId 
        });
    },
    
      $scope.getCurrentException = function() {
        var exceptions = datarepository.getDestionationsExceptions();
        for (var i in exceptions) {
            if (exceptions[i].id === $scope.destination.skipInfo.skippedReasonId) {
                return exceptions[i].name;
            }
        }
    }
    
    $scope.getTaskName = function(task) {
        if (task.className == "com.thundashop.core.trackandtrace.PickupTask")
            return "Pickup";
        
        if (task.className == "com.thundashop.core.trackandtrace.DeliveryTask")
            return "Delivery";
    };
    
    $scope.markAsArrived = function() {
        $scope.destination.startInfo.started = true;
        $scope.destination.startInfo.startedTimeStamp = new Date();
        $scope.destination.skipInfo.skippedReasonId = "";
        
        navigator.geolocation.getCurrentPosition(function(position) {
            
            $scope.destination.startInfo.lon = position.coords.longitude;
            $scope.destination.startInfo.lat = position.coords.latitude;  
            $scope.$apply();

            $api.getApi().TrackAndTraceManager.markAsArrived($scope.destination.id, new Date(), position.coords.longitude, position.coords.latitude);

            datarepository.save();
        }, function(failare, b, c) {
            $api.getApi().TrackAndTraceManager.markAsArrived($scope.destination.id, new Date(), 0, 0);
            datarepository.save();
        }, {maximumAge:60000, timeout:60000, enableHighAccuracy:true});
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
    
    $scope.showDestinationExceptions = function() {
        $state.transitionTo('base.destinationexception', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId });
    }
    
    $scope.cancelSendBackMessage = function() {
        $scope.showReplyMessage = false;
    }
    
    $scope.sendMessageBack = function() {
        var message = $('#replyMessageArea').val();
        var me = $scope;
        $scope.showReplyMessage = false;
        $api.getApi().TrackAndTraceManager.replyMessageForDestionation($scope.destination.id, message, new Date()).done(function(res) {
            me.instructionRead();
        });
    }
    
    $scope.$on('refreshRoute', function(msg, route) {
        for (var i in route.destinations) {
            var dest = route.destinations[i];
            if (dest.id == $stateParams.destinationId) {
                $scope.destination = datarepository.getDestinationById($stateParams.destinationId);
                $scope.$apply();
            }
        }
    });
    
    $scope.doShowReplyMessage = function() {
        $scope.showReplyMessage = true;
    }
}
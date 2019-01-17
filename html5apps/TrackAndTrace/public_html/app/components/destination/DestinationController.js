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
    
    $scope.hasOptionalTasks = function() {
        for (var i in $scope.destination.collectionTasks) {
            var groupedTask = $scope.destination.collectionTasks[i];
            if (groupedTask.type === "optional") {
                return true;
            }
        }
        
        return false;
    }
    
    $scope.openCollectionTask = function(destionationId, routeId, tasks) {        
        var collectionSubType = tasks.type === "codmandatory" ? 'normal' : 'payment';
        $state.transitionTo('base.collection', { destinationId: destionationId,  routeId: routeId, collectionType: tasks.type, collectionSubType : collectionSubType });
    }
    
    $scope.isOptinalAndNot = function(tasks) {
        if (tasks.type === "optional" && tasks.date)
            return false;
        
        if (tasks.type === "optional")
            return true;
        
        return false;
    }
    
    $scope.isCollected = function(type) {
        for (var i in $scope.destination.collectionTasks) {
            var groupedTask = $scope.destination.collectionTasks[i];
            if (groupedTask.type === type) {
                return groupedTask.date;
            }
        }
        
        return false;
    }
    
    $scope.getCollectionTypeName = function(type) {
        if (type == "codmandatory") {
            return "COD";
        }
        
        if (type == "cosmandatory") {
            return "COS";
        }
        
        if (type == "optional") {
            return "Optional";
        }
    },
            
    $scope.allTaskCompleted = function() {
        for (var i in $scope.destination.tasks) {
            var task = $scope.destination.tasks[i];
            
            if ($scope.getStatus(task) === "unknown") {
                return false;
            }
        }
        
        for (var i in $scope.destination.collectionTasks) {
            var groupedTask = $scope.destination.collectionTasks[i];
            
            if (groupedTask.type !== "optional" && !groupedTask.date) {
                console.log("fase", groupedTask);
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
    
    $scope.hasMandatoryCodTasks = function() {
        console.log($scope.destination);
        for (var i in $scope.destination.collectionTasks) {
            var task = $scope.destination.collectionTasks[i];
            if (task.isCod && !task.isOptional) {
                return true;
            }
        }
        
        return false;
    }
    
    $scope.hasMandatoryCosTask = function() {
        var task = $scope.destination.collectionTasks[i];
        for (var i in $scope.destination.collectionTasks) {
            if (task.isCos && !task.isOptional) {
                return true;
            }
        }
        
        return false;
    }
}
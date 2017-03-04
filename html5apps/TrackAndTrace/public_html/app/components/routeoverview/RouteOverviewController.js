/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.RouteOverviewController = function($scope, datarepository, $rootScope, $stateParams, $state, $api) {
    $scope.route = datarepository.getRouteById($stateParams.routeId);
    $scope.showFinished = datarepository.showFinished;
    
    
    $scope.getFinishedState = function(destination) {
        
        if (destination.skipInfo.skippedReasonId) {
            return "yellow";
        }
        
        if (!destination.signatureImage && !destination.startInfo.started) {
            return "red";
        }
        if (destination.signatures.length == 0  && destination.startInfo.started) {
            return "yellow";
        }
        if (destination.signatures.length > 0 && destination.startInfo.started) {
            return "green";
        }
    }
    
    $scope.toggleFinished = function() {
        $scope.showFinished = !$scope.showFinished;   
        datarepository.showFinished = $scope.showFinished;   
    }
    
    $scope.acceptTodaysInstruction = function() {
        $scope.route.instructionAccepted = true;
        $api.getApi().TrackAndTraceManager.saveRoute($scope.route);
        datarepository.save();
    }
    
    $scope.goBack = function() {
        $state.transitionTo("base.home");
    }
    
    $scope.updateTimeStamp = function(destination) {
        $scope.destination = destination;
        $scope.destination.startInfo.startedTimeStamp = new Date();
        
        navigator.geolocation.getCurrentPosition(function(position) {
            
            $scope.destination.startInfo.lon = position.coords.longitude;
            $scope.destination.startInfo.lat = position.coords.latitude;  
            $scope.$apply();

            $api.getApi().TrackAndTraceManager.saveDestination($scope.destination);
            $api.getApi().TrackAndTraceManager.unsetSkippedReason($scope.destination.id);
            
            datarepository.save();
        }, function(failare, b, c) {
            $api.getApi().TrackAndTraceManager.saveDestination($scope.destination);
            $api.getApi().TrackAndTraceManager.unsetSkippedReason($scope.destination.id);
            
            datarepository.save();
        }, {maximumAge:60000, timeout:60000, enableHighAccuracy:true});
    }
    
    $scope.showDestination = function(destinationId, routeId) {
        var destination = datarepository.getDestinationById(destinationId);
        
        if ($scope.getFinishedState(destination) === "yellow") {
            $scope.updateTimeStamp(destination);
        }
        
        $state.transitionTo("base.destination", {destinationId: destinationId, routeId: routeId});
    }
    $scope.isBoth = function(destination) {
        var foundPick = false;
        var foundDelivery = false;

        for (var i in destination.tasks) {
            if (destination.tasks[i].className === "com.thundashop.core.trackandtrace.DeliveryTask") {
                foundPick = true;
            }
            if (destination.tasks[i].className === "com.thundashop.core.trackandtrace.PickupTask") {
                foundDelivery = true;
            }
        }

        return foundPick && foundDelivery;
    }
    
    $scope.isTaskType = function(destination, pickup, delivery) {
        var isBoth = $scope.isBoth(destination);
        
        if (pickup && delivery) {
            return isBoth;
        }
        
        if (pickup) {
            for (var i in destination.tasks) {
                if (destination.tasks[i].className === "com.thundashop.core.trackandtrace.PickupTask") {
                    return !isBoth;
                }
            }
        }
        
        if (delivery) {
            for (var i in destination.tasks) {
                if (destination.tasks[i].className === "com.thundashop.core.trackandtrace.DeliveryTask") {
                    return !isBoth;
                }
            }
        }
        
        
        return false;
    }
    
    $scope.markRouteCompleted = function() {
        $state.transitionTo("base.completeRoute", {
            routeId: $stateParams.routeId
        });
    }
};
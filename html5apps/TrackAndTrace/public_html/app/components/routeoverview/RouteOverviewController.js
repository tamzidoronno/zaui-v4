/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.RouteOverviewController = function($scope, datarepository, $rootScope, $stateParams, $state, $api) {
    $scope.route = datarepository.getRouteById($stateParams.routeId);
    $scope.showFinished = true;
    
    $scope.getFinishedState = function(destination) {
        if (!destination.signatureImage && !destination.startInfo.started) {
            return "red";
        }
        if (!destination.signatureImage && destination.startInfo.started) {
            return "yellow";
        }
        if (destination.signatureImage && destination.startInfo.started) {
            return "green";
        }
    }
    
    $scope.toggleFinished = function() {
        $scope.showFinished = !$scope.showFinished;   
    }
    
    $scope.acceptTodaysInstruction = function() {
        $scope.route.instructionAccepted = true;
        $api.getApi().TrackAndTraceManager.saveRoute($scope.route);
        datarepository.save();
    }
    
    $scope.goBack = function() {
        $state.transitionTo("base.home");
    }
    
    $scope.showDestination = function(destinationId, routeId) {
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
};
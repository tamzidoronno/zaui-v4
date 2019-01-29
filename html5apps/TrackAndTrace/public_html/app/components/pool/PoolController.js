/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.PoolController = function($scope, $api, $rootScope, datarepository, $state) {
    $scope.datarepository = datarepository;

    $scope.pooledDestinations = [];
    
    $scope.selectRoute = function(route) {
        datarepository.selectedRouteForPoolController = route;
    }
    
    $scope.moveToPool = function(destination) {
        var loggedInCall = $api.getApi().UserManager.isLoggedIn();
        loggedInCall.fail(function(res) {
            alert("This function is not available offline, please make sure you are connected to internet.");
        });
        
        loggedInCall.done(function(res) {
            var conf = confirm("Are you sure you want to move " + destination.company.name + " to pool?");
            if (conf) {
                $api.getApi().TrackAndTraceManager.moveDesitinationToPool(datarepository.selectedRouteForPoolController.id, destination.id).done(function(route) {
                    datarepository.updateRoute(route[0]);
                    datarepository.selectedRouteForPoolController = route[0];
                    $scope.$evalAsync();
                    $scope.fetchPooledDestination();
                });
            }    
        });
    }
    
    $scope.moveFromPool = function(destination, id) {
        var loggedInCall = $api.getApi().UserManager.isLoggedIn();
        loggedInCall.fail(function(res) {
            alert("This function is not available offline, please make sure you are connected to internet.");
        });
        
        loggedInCall.done(function(res) {
            var conf = confirm("Are you sure you want to move " + destination.company.name + " from pool?");
            if (conf) {
                $api.getApi().TrackAndTraceManager.moveDestinationFromPoolToRoute(id, datarepository.selectedRouteForPoolController.id).done(function(route) {
                    datarepository.updateRoute(route[0]);
                    datarepository.selectedRouteForPoolController = route[0];
                    $scope.$evalAsync();
                    $scope.fetchPooledDestination();
                });
            }
        });
            
    }
    
    $scope.fetchPooledDestination = function() {
        var me = $scope;
        $api.getApi().TrackAndTraceManager.getPooledDestiontionsByUsersDepotId().done(function(res) {
            me.pooledDestinations = res;
            me.$evalAsync();
        });
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
    
    $scope.fetchPooledDestination();
};


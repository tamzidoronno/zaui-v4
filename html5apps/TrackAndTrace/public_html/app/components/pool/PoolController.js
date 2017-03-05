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
        var conf = confirm("Are you sure you want to move " + destination.company.name + " to pool?");
        if (conf) {
            $api.getApi().TrackAndTraceManager.moveDesitinationToPool(datarepository.selectedRouteForPoolController.id, destination.id).done(function(route) {
                datarepository.updateRoute(route);
                datarepository.selectedRouteForPoolController = route;
                $scope.$apply();
                $scope.fetchPooledDestination();
            });
            
        }
    }
    
    $scope.moveFromPool = function(destination, id) {
        
        var conf = confirm("Are you sure you want to move " + destination.company.name + " from pool?");
        if (conf) {
            $api.getApi().TrackAndTraceManager.moveDestinationFromPoolToRoute(id, datarepository.selectedRouteForPoolController.id).done(function(route) {
                datarepository.updateRoute(route);
                datarepository.selectedRouteForPoolController = route;
                $scope.$apply();
                $scope.fetchPooledDestination();
            });
        }
            
    }
    
    $scope.goBack = function() {
        if (datarepository.selectedRouteForPoolController) {
            datarepository.selectedRouteForPoolController = null;
            return;
        }
        
        $state.transitionTo("base.home");
    }
    
    $rootScope.$on('refreshRoute', function(msg, route) {
        if ($scope.datarepository.selectedRouteForPoolController && $scope.datarepository.selectedRouteForPoolController.id == route.id) {
            $scope.datarepository.selectedRouteForPoolController = route;
        }
    });
    
    $scope.fetchPooledDestination = function() {
        $api.getApi().TrackAndTraceManager.getPooledDestiontionsByUsersDepotId().done(function(res) {
            $scope.pooledDestinations = res;
            $scope.$apply();
        });
    }
    
    $scope.fetchPooledDestination();
};


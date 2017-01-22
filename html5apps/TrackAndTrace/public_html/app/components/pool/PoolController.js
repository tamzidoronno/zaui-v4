/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.PoolController = function($scope, $api, $rootScope, datarepository, $state) {
    $scope.datarepository = datarepository;
    
    $scope.selectRoute = function(route) {
        datarepository.selectedRouteForPoolController = route;
    }
    
    $scope.moveToPool = function(destination) {
        var conf = confirm("Are you sure you want to move " + destination.company.name + " to pool?");
        if (conf) {
            $api.getApi().TrackAndTraceManager.moveDesitinationToPool(datarepository.selectedRouteForPoolController.id, destination.id).done(function(route) {
                datarepository.updateRoute(route);
                console.log(route.destinations);
                datarepository.selectedRouteForPoolController = route;
                $scope.$apply();
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
};


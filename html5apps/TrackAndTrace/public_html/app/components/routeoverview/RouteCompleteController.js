/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.RouteCompleteController = function($scope, datarepository, $rootScope, $stateParams, $state, $api) {
    $scope.route = datarepository.getRouteById($stateParams.routeId);
    $scope.password = "";
    
    $scope.setOverviewVariables = function() {
        $scope.uncompletedStops = 0;
        
        var d = new Date();
        $scope.time = ("0" + d.getDate()).slice(-2) + "/" + ("0"+(d.getMonth()+1)).slice(-2) + "-" + d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);

        if ($scope.route && $scope.route.destinations) {
            for (var i in $scope.route.destinations) {
                var destination = $scope.route.destinations[i];
                var completed = destination.signatures.length > 0 && destination.startInfo.started;
                if (!completed) {
                    $scope.uncompletedStops++;
                }
            }
        }
    }
    
    $scope.goBack = function() {
        $state.transitionTo('base.routeoverview', {
            'routeId' : $stateParams.routeId
        });
    }
    
    $scope.markCompleted = function() {
        if (!$scope.password) {
            alert("Please enter your password");
            return;
        }
        
        var routeId = $stateParams.routeId;
        var $routeToUse = datarepository.getRouteById(routeId);        

        navigator.geolocation.getCurrentPosition(function(position) {
            $api.getApi().TrackAndTraceManager.markAsCompletedWithTimeStampAndPassword(routeId, position.coords.latitude, position.coords.longitude, new Date(), $scope.password).done(function(res) {
                if (res) {
                    $routeToUse.completedInfo.completed = true;
                    datarepository.save();
                    $state.transitionTo("base.home", {});
                } else {
                    alert("Please check your password");
                }
            });
        }, function(failare, b, c) {
            $api.getApi().TrackAndTraceManager.markAsCompletedWithTimeStampAndPassword(routeId, 0, 0, new Date(), $scope.password).done(function(res) {
                if (res) {
                    $routeToUse.completedInfo.completed = true;
                    datarepository.save();
                    $state.transitionTo("base.home", {});
                } else {
                    alert("Please check your password");
                }
            });
        }, {maximumAge:60000, timeout:60000, enableHighAccuracy:true});
    }
    
    $scope.setOverviewVariables();
}
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.HomeController = function($scope, $api, $rootScope, datarepository, $state) {
    $scope.name = "";
    $scope.datarepository = datarepository;
    
    $scope.init = function($api) {
        $scope.name = $api.getLoggedOnUser().fullName;
    }
    
    $scope.loadData = function() {
        if ($api.getApi().getUnsentMessageCount() > 0) {
            alert("You can not load routes until you have sent all your stored date, please make sure your device is connected to internet");
            return;
        }
        
        datarepository.loadAllData($api, $scope);
    }
    
    $scope.openPool = function() {
        $scope.loadData();
        $state.transitionTo("base.pool", {});
    }
    
    $scope.logOut = function() {
        localStorage.setItem("username", "");
        localStorage.setItem("password", "");
        $api.reconnect();

        $rootScope.$broadcast("loggedOut", "");
    }
    
    $scope.openRoute = function(routeId) {
        $state.transitionTo("base.routeoverview", {routeId: routeId});
    }
    
    $scope.getUncompletedTasksNumber = function(route) {
        var count = 0;
        
        for (var i in route.destinations) {
            var dest = route.destinations[i];
            if (dest.signatures.length > 0) {
                count++;
            }
        }
        
        return count;
    }
    
    $scope.startRoute = function(routeId) {
        var confirmed = confirm("Are you sure you want to start this route?");
        
        if (confirmed) {
            var $routeToUse = datarepository.getRouteById(routeId);        
            $routeToUse.startInfo.started = true;
            $routeToUse.startInfo.startedTimeStamp = new Date();

            $state.transitionTo("base.routeoverview", {routeId: $routeToUse.id});
            
            navigator.geolocation.getCurrentPosition(function(position) {
                $routeToUse.startInfo.lon = position.coords.longitude;
                $routeToUse.startInfo.lat = position.coords.latitude;  
                $scope.$apply();
               
                $api.getApi().TrackAndTraceManager.saveRoute($routeToUse);
                datarepository.save();
            }, function(failare, b, c) {
                $routeToUse.startInfo.started = true;
                $routeToUse.startInfo.startedTimeStamp = new Date();
                $scope.$apply();
               
                $api.getApi().TrackAndTraceManager.saveRoute($routeToUse);
                datarepository.save();
            }, {maximumAge:60000, timeout:60000, enableHighAccuracy:true});
        }
    }
    
    $scope.init($api);
};

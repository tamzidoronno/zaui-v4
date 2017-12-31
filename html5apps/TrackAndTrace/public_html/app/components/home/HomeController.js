/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.HomeController = function($scope, $api, $rootScope, datarepository, $state, $stateParams) {
    $scope.name = "";
    $scope.datarepository = datarepository;
    $scope.routes = [];
    
    $scope.init = function($api) {
        $scope.name = $api.getLoggedOnUser().fullName;
        
        for (var i in datarepository.routes) {
            var route = datarepository.routes[i];
            if (!route.completedInfo.completed) {
                $scope.routes.push(route);
            }
        }
    }
    
    $scope.loadData = function() {
        if ($api.getApi().getUnsentMessageCount() > 0) {
            alert("You can not load routes until you have sent all your stored date, please make sure your device is connected to internet");
            return;
        }
        
        datarepository.loadAllData($api, $scope);
    }
    
    $scope.openPool = function() {
        if (!$api.getApi().connectionEstablished) {
            alert("This function is not available offline, please make sure you are connected to internet.");
            return;
        }
        
        $scope.loadData();
        $state.transitionTo("base.pool", {});
    }
    
    $scope.logOut = function() {
        var conf = confirm("Are you sure you want to logout?");
        
        if (!conf)
            return;
        
        localStorage.setItem("loggedInUserId", "");
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
    
    $scope.refresh = function() {
        $('.refreshicon').addClass('fa-spin');
        var completed = function() {
            $('.refreshicon').removeClass('fa-spin'); 
            $state.go($state.current, { 'action': { frefreshData : false }}, {reload: true});
        }
        datarepository.loadAllData($api, $scope, completed);
    }
    
    $scope.startRoute = function($routeToUse) {
        var confirmed = confirm("Are you sure you want to start this route?");
        
        var inputDate = new Date($routeToUse.deliveryServiceDate);
        var todaysDate = new Date();
        
        var sameDay = inputDate.setHours(0,0,0,0) == todaysDate.setHours(0,0,0,0);
        var later = todaysDate.getTime() > inputDate.getTime();
        
        if (!sameDay && !later) {
            alert("This route is not scheduled for today");
            return;
        }
        
        if (confirmed) {
            $routeToUse.startInfo.started = true;
            $routeToUse.startInfo.startedTimeStamp = new Date();

            $state.transitionTo("base.routeoverview", {routeId: $routeToUse.id});
            
            navigator.geolocation.getCurrentPosition(function(position) {
                $routeToUse.startInfo.lon = position.coords.longitude;
                $routeToUse.startInfo.lat = position.coords.latitude;  
                $scope.$evalAsync();
               
                $api.getApi().TrackAndTraceManager.markRouteAsStarted($routeToUse.id, new Date(), $routeToUse.startInfo.lon, $routeToUse.startInfo.lat);
                datarepository.save();
            }, function(failare, b, c) {
                $routeToUse.startInfo.started = true;
                $routeToUse.startInfo.startedTimeStamp = new Date();
                $scope.$evalAsync();
               
                $api.getApi().TrackAndTraceManager.markRouteAsStarted($routeToUse.id, new Date(), 0, 0);
                datarepository.save();
            }, {maximumAge:60000, timeout:60000, enableHighAccuracy:true});
        }
    }
    
    $scope.init($api);
    
    if ($stateParams.action.refreshData) {
        $scope.refresh();
    }
};

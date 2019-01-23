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
        $scope.name = "";
        
        if ($api.getLoggedOnUser()) {
            $scope.name = $api.getLoggedOnUser().fullName;
        }
        
        for (var i in datarepository.routes) {
            var route = datarepository.routes[i];
            if (!route.completedInfo.completed) {
                $scope.routes.push(route);
            }
        }
    }
    
    $scope.loadData = function(completeFunction) {
        if ($api.getApi().getUnsentMessageCount() > 0) {
            alert("You can not load routes until you have sent all your stored date, please make sure your device is connected to internet");
            return;
        }
        
        datarepository.loadAllData($api, $scope, completeFunction);
    }
    
    $scope.openPool = function() {
        var loggedInCall = $api.getApi().UserManager.isLoggedIn();
        loggedInCall.fail(function(res) {
            alert("This function is not available offline, please make sure you are connected to internet.");
        })
        
        loggedInCall.done(function(res) {
            
            $('.loaderbox_home_gps').show();
            $('.loaderbox_home_gps span').html('Loading data, please wait');

            if ($api.getApi().getUnsentMessageCount() > 0) {
                alert("You can not load routes until you have sent all your stored date, please make sure your device is connected to internet");
                return;
            }
            
            $scope.loadData(function(res) {
                $('.loaderbox_home_gps').hide();
                $state.transitionTo("base.pool", {});    
            });
        });
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
        
        if (confirmed) {
            $routeToUse.startInProgress = true;
            $('.loaderbox_home_gps').show();
            $('.loaderbox_home_gps span').html('Fetching GPS coordinates, please wait');
            
            navigator.geolocation.getCurrentPosition(function(position) {
                
                var start = $api.getApi().TrackAndTraceManager.markRouteAsStartedWithCheck($routeToUse.id, new Date(), $routeToUse.startInfo.lon, $routeToUse.startInfo.lat, false, true);
                
                $('.loaderbox_home_gps span').html('Starting route... please wait..');
                
                start.done(function(res) {
                    $('.loaderbox_home_gps').hide();
                    
                    $routeToUse.startInProgress = false;
                    
                    if (res === "SERVICEDATE_IN_FUTURE") {
                        alert("Can not start the route as the service date is in the future");
                    }
                   
                    if (res === "true") {
                        $routeToUse.startInfo.started = true;
                        $routeToUse.startInfo.startedTimeStamp = new Date();

                        $routeToUse.startInfo.lon = position.coords.longitude;
                        $routeToUse.startInfo.lat = position.coords.latitude;  
                        $state.transitionTo("base.routeoverview", {routeId: $routeToUse.id});

                        datarepository.save();
                    }
                    
                    $scope.$evalAsync();
                });
                
                start.fail(function() {
                    $('.loaderbox_home_gps').hide();
                    $routeToUse.startInProgress = false;
                    alert('You need to be online to start a route');
                    $scope.$evalAsync();
                });
                
            }, function(failare, b, c) {
                $('.loaderbox_home_gps span').html('<span style="color: red;">Failed to fetch coordiantes!</span>');
                setTimeout(function() {
                    $('.loaderbox_home_gps').hide();
                }, 2000)
                
                var start = $api.getApi().TrackAndTraceManager.markRouteAsStartedWithCheck($routeToUse.id, new Date(), 0, 0, false, true);
                
                start.done(function() {
                    $routeToUse.startInProgress = false;
                    
                    if (res === "SERVICEDATE_IN_FUTURE") {
                        alert("Can not start the route as the service date is in the future");
                        return;
                    }
                    
                    if (res === "true") {
                        $routeToUse.startInfo.started = true;
                        $routeToUse.startInfo.startedTimeStamp = new Date();

                        $routeToUse.startInfo.lon = position.coords.longitude;
                        $routeToUse.startInfo.lat = position.coords.latitude;  
                        $state.transitionTo("base.routeoverview", {routeId: $routeToUse.id});

                        datarepository.save();
                    }
                    
                    $scope.$evalAsync();
                });
                
                start.fail(function() {
                    $routeToUse.startInProgress = false;
                    alert('You need to be online to start a route');
                    $scope.$evalAsync();
                });
        
            }, {maximumAge:60000, timeout:60000, enableHighAccuracy:true});
        }
    }
    
    $scope.init($api);
    
    if ($stateParams.action.refreshData) {
        $scope.refresh();
    }
};

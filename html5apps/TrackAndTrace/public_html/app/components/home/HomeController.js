/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.HomeController = function($scope, $api, $rootScope, datarepository) {
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
    
    $scope.logOut = function() {
        localStorage.setItem("username", "");
        localStorage.setItem("password", "");
        $api.reconnect();

        $rootScope.$broadcast("loggedOut", "");
    }
    
    $scope.startRoute = function(routeId) {
        var confirmed = confirm("Are you sure you want to start this route?");
        
        $routeToUse = datarepository.getRouteById(routeId);
        
        if (confirmed) {
            navigator.geolocation.getCurrentPosition(function(position) {
                $routeToUse.startInfo.started = true;
                $routeToUse.startInfo.startedTimeStamp = new Date();
                $routeToUse.startInfo.lon = position.coords.longitude;
                $routeToUse.startInfo.lat = position.coords.latitude;  
                $scope.$apply();
               
                $api.getApi().TrackAndTraceManager.saveRoute($routeToUse);
                datarepository.save();
            });
        }
    }
    
    $scope.init($api);
};

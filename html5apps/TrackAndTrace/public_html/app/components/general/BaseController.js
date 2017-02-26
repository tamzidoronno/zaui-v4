/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.BaseController = function($scope, $rootScope, datarepository, $api) {
    $scope.messages = datarepository.driverMessages;
    
    if($api.getApi()) {
        $scope.counter = $api.getApi().getUnsentMessageCount();
    }
    
    
    $rootScope.$on('refreshRouteEven1', function(msg, data) {
        var loggedOnUserId = $api.getLoggedOnUser().id;

        for (var i in data.userIds) {
            if (data.userIds[i] === loggedOnUserId) {
                datarepository.updateRoute(data, $api);
                $rootScope.$broadcast('refreshRoute', data);
            }
        }
        
        
    });
    
    $rootScope.$on('messageReceived', function(msg, data) {
        if (data.driverId === $api.getLoggedOnUser().id) {
            datarepository.driverMessages.push(data);
            datarepository.save();
            
            $scope.messages = datarepository.driverMessages;
            $scope.$apply();
            $rootScope.$apply();
        }
    });
    
    $rootScope.$on('refreshData', function() {
        $('.loadingData').show();
        datarepository.loadAllData($api, $scope);
    });
    
    $rootScope.$on('messageCountChanged', function() {
        $scope.counter = $api.getApi().messagesToSendJson.length;
        if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
            $scope.$apply();
        }
    });
  
    $scope.ackMessage = function(message) {
        $api.getApi().TrackAndTraceManager.acknowledgeDriverMessage(message.id);
        
        for (var i in datarepository.driverMessages) {
            var drv = datarepository.driverMessages[i];
            if (drv.id == message.id) {
                drv.isRead = true;
            }
        }
        
        message.isRead = true;
        datarepository.save();
    }
};
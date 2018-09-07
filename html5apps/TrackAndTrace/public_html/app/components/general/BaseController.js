/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.BaseController = function($scope, $rootScope, $state, datarepository, $api) {
    $scope.messages = datarepository.driverMessages;
    $scope.sentMessages = [];
    $scope.showReply = false;
    $scope.currentMesage = null;
    $scope.apiOptions = false;
    
    if (!$api.getApi() || !$api.getLoggedOnUser()) {
        $scope.messages = [];
    }
    
    if($api.getApi()) {
        $scope.counter = $api.getApi().getUnsentMessageCount();
    }
    
    $scope.$on('refreshRouteEven1', function(msg, data) {
        var loggedOnUserId = null; 
        
        if ($api.getLoggedOnUser()) {
            loggedOnUserId = $api.getLoggedOnUser().id;
        }

        for (var i in data.userIds) {
            if (data.userIds[i] === loggedOnUserId) {
                datarepository.updateRoute(data, $api);
                $state.go($state.current, {}, {reload: true});
            }
        }
    });
    
    $scope.$on('messageReceived', function(msg, data) {
        if ($api.getLoggedOnUser()) {
            if (data.driverId === $api.getLoggedOnUser().id) {
                datarepository.driverMessages.push(data);
                datarepository.save();

                $scope.messages = datarepository.driverMessages;
                $scope.$evalAsync();
                $rootScope.$apply();
            }
        }
    });
    
    $scope.$on('driverRemoved', function(msg, data) {
        if ($api.getLoggedOnUser()) {
            if (data.userId === $api.getLoggedOnUser().id) {
                datarepository.removeRoute(data.routeId);
                $state.transitionTo('base.home', {}, {reload: true});

                $scope.$evalAsync();
                $rootScope.$apply();
            }
        }
    });
    
    $scope.$on('refreshData', function() {
        datarepository.loadAllData($api, $scope, null, $state);
    });
    
    $scope.groupBy = function(xs, key) {
        
    }
    
    $scope.$on('messageCountChanged', function() {
        $scope.counter = $api.getApi().getUnsentMessageCount();
        $scope.$evalAsync();
    });
  
    $scope.ackMessage = function(message) {
        if ($api.getApi() != null && typeof($api.getApi().TrackAndTraceManager) !== "undefined") {
            $api.getApi().TrackAndTraceManager.acknowledgeDriverMessage(message.id);
        }
        
        for (var i in datarepository.driverMessages) {
            var drv = datarepository.driverMessages[i];
            if (drv.id == message.id) {
                drv.isRead = true;
            }
        }
        
        message.isRead = true;
        datarepository.save();
    };
    
    $scope.replyMessage = function(message) {
        $('#commonMessageReplyBox').val("");
        $scope.currentMessage = message;
        $scope.showReply = true;
    }
    
    $scope.cancelSendMessage = function() {
        $scope.showReply = false;
    }
    
    $scope.sendMessageBack = function() {
        var message = $scope.currentMessage;
        var textMessage = $('#commonMessageReplyBox').val();
        var me = $scope;
        $scope.showReply = false;
        
        $api.getApi().TrackAndTraceManager.replyMessage(message.id, textMessage, new Date()).done(function(res) {
            me.ackMessage(message);
        });
    }
    
    $scope.toggleOptions = function() {
        $scope.apiOptions = !$scope.apiOptions;
        
        if (typeof(messagePersister) !== "undefined" && messagePersister) {
            var toLoop = messagePersister.getAllUnsentMessages();
            var test = [];
            
            for (var i in toLoop) {
                var msg = toLoop[i];
                var meth = msg.interfaceName+"."+msg.method;
                if (!test[meth]) {
                    test[meth] = 1;
                } else {
                    test[meth] += 1;
                }
            }
            
            $scope.sentMessages = [];
            
            for (var i in test) {
                var count = test[i];
                var toAdd = {
                    method: i,
                    count: count
                };
                $scope.sentMessages.push(toAdd);
            }
            
            console.log(test);
        }
        
        $scope.$evalAsync();
    }
    
    $scope.resetApi = function() {
        $api.getApi().resetConnection();
    }
    
    $scope.goOffline = function() {
        $api.getApi().disconnect();
    }
    
    $scope.goOnline = function() {
        $api.getApi().connect();
    }
};
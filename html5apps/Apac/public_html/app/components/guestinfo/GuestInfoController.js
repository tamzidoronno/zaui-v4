/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.GuestInfoController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    $scope.access = "";
    $scope.loadingMessages = true;
    $scope.textMessageToSend = "";
    $scope.loadingUserInfo = true;
    
    $scope.messageReceived = function(res) {
        $scope.messages = res;
        $scope.loadingMessages = false;
        $scope.$evalAsync();
    }
    
    $scope.init = function() {
        $api.getApi().ApacManager.getApacAccess(datarepository.domainname, $stateParams.accessId).done(function(res) {
            $scope.access = res;
            $scope.loadingUserInfo = false;
            $scope.startFetchingMessages();
            $scope.$evalAsync();
        });
    }
    
    $scope.startFetchingMessages = function() {
        $scope.loadingMessages = true;
        $api.getApi().MessageManager.getSmsMessagesSentTo($scope.access.prefix, $scope.access.phoneNumber, $scope.access.rowCreatedDate, new Date()).done($scope.messageReceived);
    }
    
    
    $scope.sendMessage = function() {
        $scope.loadingMessages = true;
        $api.getApi().ApacManager.sendSms(datarepository.domainname, $scope.access.id, $scope.textMessageToSend).done($scope.startFetchingMessages);
    }
 
    $scope.init();
}
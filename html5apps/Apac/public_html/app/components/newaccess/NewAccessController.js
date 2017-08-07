/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.NewAccessController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    $scope.doors = [];
    $scope.guestName = "";
    $scope.guestPhoneNumber = "";
    $scope.doorId = "";
    $scope.guestInfo = "";
    $scope.guestPhonePrefix = localStorage.getItem("guestPhonePrefix");
    
    $scope.init = function($api) {
        $scope.name = $api.getApi().ApacManager.getAllDoors(datarepository.domainname).done(function(doors) {
            $scope.doors = doors;
            $scope.$evalAsync();
        })
    }
    
    $scope.goBack = function($api) {
        $state.transitionTo("base.home");
    }
    
    $scope.createAccess = function() {
        
        
        var grantAccess = {
            deviceId : $scope.doorId,
            name : $scope.guestName,
            phoneNumber : $scope.guestPhoneNumber,
            prefix : $scope.guestPhonePrefix
        };
        
        localStorage.setItem("guestPhonePrefix", $scope.guestPhonePrefix);
        var apacManager = $api.getApi().ApacManager;
        
        apacManager.grantAccess(datarepository.domainname, grantAccess).done(function(res) {
            $state.transitionTo('base.guestinfo', {accessId: res.id});
        });
    }
    
    $scope.init($api);
};

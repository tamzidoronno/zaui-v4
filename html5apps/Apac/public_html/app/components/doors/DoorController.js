/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.DoorController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    $scope.door = "";
    $scope.loadinglogs = true;
    $scope.logs = [];
    
    $scope.doorsLoaded = function(doors) {
        for (var i in doors) {
            var door = doors[i];
            if (door.externalId === $stateParams.externalId) {
                $scope.door = door;
                $scope.$evalAsync();
            }
        }
    }
    
    $scope.logsLoaded = function(logs) {
        $scope.logs = logs;
        $scope.loadinglogs = false;
        $scope.$evalAsync();
    }
    
    $scope.loadLogs = function() {
        var end = (new Date).getTime();
        var start = end - (7*24*60*60*1000);
        $api.getApi().DoorManager.getLogForDoor(datarepository.domainname, $stateParams.externalId, start, end).done($scope.logsLoaded);
    }
    
    $scope.convertTimestamp = function(time) {
        
    }
    
    $scope.goToDoor = function(door) {
        $state.transitionTo('base.door', {externalId:  door.externalId});
    }
    
    $scope.init = function() {
        $api.getApi().DoorManager.getAllDoors(datarepository.domainname).done($scope.doorsLoaded);
        $scope.loadLogs();
    };
    
    $scope.convertTimestamp = function(timestamp) {
        var d = new Date(timestamp);
        return d.toLocaleString();
    }
    
    $scope.goBack = function() {
        $state.transitionTo('base.doors');
    }
    
    $scope.init();
};
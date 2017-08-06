/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.DoorsController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    $scope.loadingDoors = true;
    $scope.doors = [];  
    
    $scope.doorsLoaded = function(doors) {
        $scope.doors = doors;
        $scope.loadingDoors = false;
        $scope.$evalAsync();
    }
    
    
    $scope.goToDoor = function(door) {
        $state.transitionTo('base.door', {externalId:  door.externalId});
    }
    
    $scope.init = function() {
        $api.getApi().DoorManager.getAllDoors(datarepository.domainname).done($scope.doorsLoaded);
    };
    
    $scope.goBack = function() {
        $state.transitionTo('base.home');
    }
    
    $scope.init();
}
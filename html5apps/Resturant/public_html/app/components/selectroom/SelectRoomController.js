/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.SelectRoomController = function($scope, $rootScope, $api, datarepository, $state) {
    $scope.rooms = datarepository.rooms;
    
    $scope.selectRoom = function(roomId) {
        datarepository.setSelectedRoom(roomId);
        $scope.goBack();
    }
    
    $scope.goBack = function() {
        $state.transitionTo("base.home", {});
    }
};
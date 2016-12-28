/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.HomeController = function($scope, $api, $rootScope, $state, datarepository) {
    $scope.name = "";
    
    $scope.init = function($api) {
        $scope.name = $api.getLoggedOnUser().fullName;
        $scope.room = datarepository.getSelectedRoom();
    }
    
    $scope.goToSettings = function() {
        $state.transitionTo("base.settings", {});
    }
    
    $scope.logOut = function() {
        localStorage.setItem("username", "");
        localStorage.setItem("password", "");
        $api.reconnect();

        $rootScope.$broadcast("loggedOut", "");
    }
    
    $scope.goToRoomSelection = function() {
        $state.transitionTo('base.selectroom', {});
    }
    
    $scope.goToTable = function(tableId) {
        $state.transitionTo('base.tableoverview', {tableId : tableId});
    }
    
    $scope.inUse = function(table) {
        var cartItems = datarepository.getAllCartItems(table.id);
        if (cartItems.length > 0) {
            return true;
        }
        
        return false;
    }
    
    
    
    $scope.init($api);
};

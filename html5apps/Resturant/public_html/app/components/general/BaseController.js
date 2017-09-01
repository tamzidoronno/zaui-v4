/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.BaseController = function($scope, $rootScope, $api, datarepository) {

    $scope.refreshTable = function(event, tableId) {
        $api.getApi().ResturantManager.getCurrentTableData(tableId).done(function(res) {
            datarepository.setCartItems(res.cartItems, tableId);
            $scope.$evalAsync();
        }) 
    }
    
    $scope.connectionEstablished = function() {
        for (var i in datarepository.rooms) {
            var room = datarepository.rooms[i];
            for (var j in room.tables) {
                var table = room.tables[j];
                table.refreshing = true;
                $api.getApi().ResturantManager.getCurrentTableData(table.id).done(function(res) {
                    datarepository.setCartItems(res.cartItems, res.tableId);
                    $scope.$evalAsync();
                });
                
                $scope.$evalAsync();
            }
        }
    }
    
    $rootScope.$on('refreshTable', $scope.refreshTable);
    $rootScope.$on('connectionEstablished', $scope.connectionEstablished);
    
    $api.reconnect();
};
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.SettingsController = function($scope, $rootScope, $api, datarepository, $state) {
    $scope.datarepository = datarepository;
    
    $scope.reloadData = function() {
        var resturantManager = $api.getApi().ResturantManager;
        var productManager = $api.getApi().ProductManager;
        
        productManager.getAllProducts().done(function(res) { datarepository.setProducts(res); });
        productManager.getProductLists().done(function(res) { datarepository.setProductLists(res); });
        resturantManager.getRooms().done(function(res) { datarepository.setRooms(res); } )
        
    }
    
    $scope.goBack = function() {
        $state.transitionTo("base.home", {});
    }
};
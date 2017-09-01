/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.SettingsController = function($scope, $rootScope, $api, datarepository, $state) {
    $scope.datarepository = datarepository;
    
    $scope.standalone = datarepository.isStandAlone();
    $scope.shouldAskForPincode = datarepository.shouldAskForPincode();
    
    $scope.starteda = false;
    $scope.startedc = false;
    $scope.startedb = false;
    
    $scope.savePaymentMethods = function() {
        datarepository.save();
    }
    
    $scope.saveActiveLists = function() {
        datarepository.save();
    }
    
    $scope.reloadData = function() {
        var resturantManager = $api.getApi().ResturantManager;
        var productManager = $api.getApi().ProductManager;
        var storeApplicationPool = $api.getApi().StoreApplicationPool;
        var storePrintManager = $api.getApi().StorePrintManager;
        
        $scope.starteda = true; 
        $scope.startedb = true; 
        $scope.startedc = true; 
        
        productManager.getAllProducts().done(function(res) {  $scope.starteda = false; datarepository.setProducts(res); $scope.$evalAsync(); });
        productManager.getProductLists().done(function(res) { $scope.startedb = false; datarepository.setProductLists(res); $scope.$evalAsync(); });
        resturantManager.getRooms().done(function(res) { $scope.startedc = false; datarepository.setRooms(res); $scope.$evalAsync(); } )
        storeApplicationPool.getActivatedPaymentApplications().done(function(res) { datarepository.setActivatedPaymentMethods(res); $scope.$evalAsync(); } )
        storePrintManager.getPrinters().done(function(res) { datarepository.setPrinters(res); $scope.$evalAsync(); } )
    }
    
    $scope.togglePincodeAsking = function() {
        datarepository.togglePincodeAsking();
    }
    
    $scope.goBack = function() {
        $state.transitionTo("base.home", {});
    }
    
    $scope.toggleStandalone = function() {
        datarepository.toggleStandAlone();
    }
};
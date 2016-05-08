/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.CarOverviewController = function($scope, $api) {
    $scope.car = null;
    
    $scope.carsFetched = function(cars) {
        $scope.car = cars[0];
        $scope.$apply();
    }
    
    $scope.fetchCar = function() {
        var cellPhone = localStorage.getItem("cellphone");
        $getshopApi = $api.api;
        $getshopApi.MecaManager.getCarsByCellphone(cellPhone).done($scope.carsFetched)
    }
    
    $scope.answerNo = function() {
        $getshopApi = $api.api;
        $getshopApi.MecaManager.answerServiceRequest($scope.car.id, false).done(function(car) {
            $scope.car = car;
            $scope.$apply();
        });
    }
        
    $scope.answerYes = function() {
        $getshopApi = $api.api;
        $getshopApi.MecaManager.answerServiceRequest($scope.car.id, true).done(function(car) {
            $scope.car = car;
            $scope.$apply();
        });
    }
    
    $scope.answerNoEU = function() {
        $getshopApi = $api.api;
        $getshopApi.MecaManager.answerControlRequest($scope.car.id, false).done(function(car) {
            $scope.car = car;
            $scope.$apply();
        });
    }
    
    $scope.answerYesEU = function() {
        $getshopApi = $api.api;
        $getshopApi.MecaManager.answerControlRequest($scope.car.id, true).done(function(car) {
            $scope.car = car;
            $scope.$apply();
        });
    }
    
    $scope.fetchCar();
}

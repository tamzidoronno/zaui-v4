/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.KilometersController = function($scope, $api) {
    
    $scope.cars = function($cars) {
        if ($cars.length > 0) {
            $scope.car = $cars[0];
            $scope.$apply();
        }
    }
    
    $scope.loadCarData = function() {
        $api.getApi().MecaManager.getCarsByCellphone(localStorage.getItem("cellphone")).done($scope.cars);
    }
    
    $scope.sent = function() {
        alert("Takk, vi har nå registrert den nye kilometerstanden");
    }
    
    $scope.sendKilomters = function() {
        $api.getApi().MecaManager.sendKilometers(localStorage.getItem("cellphone"), $scope.car.kilometers).done($scope.sent);
    }
    
    $scope.loadCarData();
}
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
        console.log($scope.car);
        $scope.$apply();
    }
    
    $scope.fetchCar = function() {
        var cellPhone = localStorage.getItem("cellphone");
        $getshopApi = $api.getApi();
        $getshopApi.MecaManager.getCarsByCellphone(cellPhone).done($scope.carsFetched)
    }
    
    $scope.fetchCar();
}

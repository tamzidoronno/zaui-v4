/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.RouteOverviewController = function($scope, datarepository, $rootScope, $stateParams, $state) {
    $scope.route = datarepository.getRouteById($stateParams.routeId);
    $scope.showFinished = true;
    
    $scope.getFinishedState = function(destination) {
        if (!destination.signatureImage && !destination.startInfo.started) {
            return "red";
        }
        if (!destination.signatureImage && destination.startInfo.started) {
            return "yellow";
        }
        if (destination.signatureImage && destination.startInfo.started) {
            return "green";
        }
    }
    
    $scope.toggleFinished = function() {
        $scope.showFinished = !$scope.showFinished;   
    }
};
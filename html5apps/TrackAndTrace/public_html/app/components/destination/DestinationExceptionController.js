/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.DestinationExceptionController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.exceptions = datarepository.getDestionationsExceptions();
    $scope.destination = datarepository.getDestinationById($stateParams.destinationId);
    $scope.api = $api.getApi();
    
    $scope.goBackToDestionation = function() {
        $state.transitionTo("base.destination", {destinationId: $stateParams.destinationId, routeId: $stateParams.routeId});
    }
  
    $scope.setDesitinationException = function(ex) {
        $scope.destination.skipInfo.skippedReasonId = ex.id;
        $scope.destination.skipInfo.startedTimeStamp = new Date();
        datarepository.save();
        
        $state.transitionTo("base.routeoverview", {routeId: $stateParams.routeId});
        
        navigator.geolocation.getCurrentPosition(function(position) {
            $scope.destination.skipInfo.lon = position.coords.longitude;
            $scope.destination.skipInfo.lat = position.coords.latitude;  
            
            $scope.api.TrackAndTraceManager.setDesitionationException($scope.destination.id, ex.id, position.coords.longitude, position.coords.latitude);
            datarepository.save();
            
        }, function(failare, b, c) {
            $scope.api.TrackAndTraceManager.setDesitionationException($scope.destination.id, ex.id, 0, 0);
        }, {maximumAge:60000, timeout:60000, enableHighAccuracy:true});
    }
}

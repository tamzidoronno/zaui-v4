/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.AddPickupTaskController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.taskType = false;
    
    $scope.goBack = function() {
        $state.transitionTo("base.destination",  { 
            destinationId: $stateParams.destinationId,
            routeId: $stateParams.routeId 
        });
    }
    
    $scope.setType = function(type) {
        $scope.taskType = type;
    }
    
    $scope.createTask = function($event, numbers) {
        if (numbers.length < 13) {
            alert("Must be atleast 13 digits");
            return;
        }
        
        var pickupOrder = {
            mustScanBarcode: false,
            referenceNumber: numbers,
            container: $scope.taskType === "container"
        };
        
        $('.loadingData').show();
        $api.getApi().TrackAndTraceManager.addPickupOrder($stateParams.destinationId, pickupOrder).done(function(res) {
            datarepository.updateRoute(res[0], $api);
            $('.loadingData').hide();
            $state.transitionTo("base.destination",  { 
                destinationId: $stateParams.destinationId,
                routeId: $stateParams.routeId 
            });
        });
    }
}

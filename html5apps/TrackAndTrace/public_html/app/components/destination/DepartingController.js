/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.DepartingController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.route = datarepository.getRouteById($stateParams.routeId);
    $scope.destination = datarepository.getDestinationById($stateParams.destinationId);
    $scope.api = $api;
    $scope.typedName = "";
    
    $scope.initSignaturePad = function() {
        
        var element = document.getElementById('signature-pad');
        $(element).attr("width", $('.clearButton').outerWidth());
        
        $scope.signaturePad = new SignaturePad(element, {
            backgroundColor: 'rgba(255, 255, 255, 0)',
            penColor: 'rgb(0, 0, 255)'
        });
    }
    
    $scope.initSignaturePad();
    
    $scope.clearSignature = function() {
        this.signaturePad.clear();
    }
    
    $scope.saveSignature = function() {
        if (!$scope.typedName) {
            alert("The type named can not be blank");
            return;
        }
        var data = $scope.signaturePad.toDataURL("image/png");
        $scope.destination.signatureImage = data;
        $scope.destination.typedNameForSignature = $scope.typedName;
        $scope.api.getApi().TrackAndTraceManager.saveDestination($scope.destination);
        $scope.api.getApi().TrackAndTraceManager.unsetSkippedReason($scope.destination.id);
        $scope.destination.skipInfo.skippedReasonId = "";
        datarepository.save();
        
        $state.transitionTo("base.routeoverview", { routeId : $stateParams.routeId } )
    }
}
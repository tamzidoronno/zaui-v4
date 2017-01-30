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
        $scope.startedSignature = false;
        this.signaturePad.clear();
    }
    
    $scope.saveSignature = function() {
        if (!$scope.typedName) {
            alert("The type named can not be blank");
            return;
        }
        
        var data = $scope.signaturePad.toDataURL("image/png");
        
        if (data === "data:image/png;base64,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") {
            alert("The signature can not be blank");
            return;
        }
        
        $scope.destination.signatureImage = data;
        $scope.destination.typedNameForSignature = $scope.typedName;
        
        $scope.destination.signatures.push( {
            typedName: $scope.typedName
        });
        datarepository.save();

        navigator.geolocation.getCurrentPosition(function(position) {
            $scope.saveDeparting(data, position.coords.longitude, position.coords.latitude);
        }, function(failare, b, c) {
            $scope.saveDeparting(data, 0, 0);
        }, {maximumAge:60000, timeout:60000, enableHighAccuracy:true});
        
        $state.transitionTo("base.routeoverview", { routeId : $stateParams.routeId } )
    }
    
    $scope.saveDeparting = function(data, longitude, latitude) {
        $scope.destination.startInfo.completed = true;
        $scope.destination.startInfo.completedTimeStamp = new Date();
        $scope.destination.startInfo.completedLon = longitude;
        $scope.destination.startInfo.completedLat = latitude;  
            
        $scope.api.getApi().TrackAndTraceManager.saveDestination($scope.destination);
        $scope.api.getApi().TrackAndTraceManager.unsetSkippedReason($scope.destination.id);
        $scope.destination.skipInfo.skippedReasonId = "";
        datarepository.save();
    }
}
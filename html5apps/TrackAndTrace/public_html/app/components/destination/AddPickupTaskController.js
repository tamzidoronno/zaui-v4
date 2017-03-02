/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.AddPickupTaskController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.taskType = false;
    $scope.numbers = "";
    
    $scope.goBack = function() {
        $state.transitionTo("base.destination",  { 
            destinationId: $stateParams.destinationId,
            routeId: $stateParams.routeId 
        });
    }
    
    $scope.setType = function(type) {
        $scope.taskType = type;
    }
    
    $scope.stopScanner = function() {
        if (typeof(cordova) === "undefined") {
            return;
        }
        
        cordova.exec(function() {}, function() {}, "HoneyWellBarcodeReaderE75", "stop", ["test"])
    }
    
    $scope.barcodeReceived = function(barcode) {
        var labelNumber = barcode.substr(barcode.length - 3);
        var orderReference = barcode.substr(barcode.length - 13, 10);
        
        $scope.numbers = orderReference;
        $scope.createTask(null, $scope.numbers);
    }
    
    $scope.startScanner = function() {
        
        if (typeof(cordova) === "undefined") {
            $scope.barcodeReceived('651817211486929100');
            $scope.i++;
            return;
        }
        
        $scope.stopScanner();
        cordova.exec(function(a) { $scope.barcodeReceived(a); }, function(fail) {}, "HoneyWellBarcodeReaderE75", "echo", ["test"])
    }
    
    $scope.createTask = function($event, numbers) {
        if (numbers.length !== 10) {
            alert("Must be 10 digits");
            return;
        }
        
        var pickupOrder = {
            mustScanBarcode: false,
            referenceNumber: numbers,
            container: $scope.taskType === "container"
        };
        
        $('.loadingData').show();
        $api.getApi().TrackAndTraceManager.addPickupOrder($stateParams.destinationId, pickupOrder).done(function(res) {
            datarepository.updateTask(res.destination, res.task, $api);
            $('.loadingData').hide();
//            $state.transitionTo("base.destination",  { 
//                destinationId: $stateParams.destinationId,
//                routeId: $stateParams.routeId 
//            });
            $state.go('base.task', { 
                destinationId: $stateParams.destinationId,  
                routeId: $stateParams.routeId, 
                taskId: res.task.id,
                'action' : { 
                    state: 'keyedReference',
                    keyReference : res.orderReferenceNumber
                }
            });
        });
    }
}

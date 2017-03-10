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
    
    $scope.getPickupTask = function() {
        var dest = datarepository.getDestinationById($stateParams.destinationId);
        for (var i in dest.tasks) {
            if (dest.tasks[i].className === "com.thundashop.core.trackandtrace.PickupTask") {
                return dest.tasks[i];
            }
        }
        
        return null;
    }
    
    $scope.generateUUID = function() {
        var d = new Date().getTime();
        var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = (d + Math.random()*16)%16 | 0;
            d = Math.floor(d/16);
            return (c=='x' ? r : (r&0x3|0x8)).toString(16);
        });
        return uuid;
    };

    $scope.createTask = function($event, numbers) {
        if (numbers.length !== 10) {
            alert("Must be 10 digits");
            return;
        }
        
        var pickupOrder = {
            mustScanBarcode: false,
            referenceNumber: numbers,
            container: $scope.taskType === "container",
            source: "tnt"
        };
        
        var pickupTask = $scope.getPickupTask();
        
        if (pickupTask === null) {
            pickupTask = {
                orders : [],
                cage : false,
                completed: false,
                barcodeValidated: false,
                dirty: true,
                id: $scope.generateUUID(),
                className: "com.thundashop.core.trackandtrace.PickupTask",
                rowCreatedDate: new Date(),
            }
        }
        
        pickupTask.orders.push(pickupOrder);
        
        $api.getApi().TrackAndTraceManager.addPickupOrder($stateParams.destinationId, pickupOrder, pickupTask);
        var dest = datarepository.getDestinationById($stateParams.destinationId);
        
        datarepository.updateTask(dest, pickupTask, $api);
        
        $state.go('base.task', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: pickupTask.id,
            'action' : { 
                state: 'keyedReference',
                keyReference : pickupOrder.referenceNumber
            }
        });
    }
}

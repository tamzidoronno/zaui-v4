/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

function sortByReferenceNumber(a,b) {
  if (a.referenceNumber < b.referenceNumber)
    return -1;
  if (a.referenceNumber > b.referenceNumber)
    return 1;
  return 0;
}

controllers.CollectionController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.destination = datarepository.getDestinationById($stateParams.destinationId);
    
    $scope.paymentView = $stateParams.collectionSubType === "payment";
    $scope.collectionType = $stateParams.collectionType;
    
    $scope.getGroupedTasks = function() {
        for (var i in $scope.destination.collectionTasks) {
            var tasks = $scope.destination.collectionTasks[i];
            
            if (tasks.type === $scope.collectionType) {
                return tasks;   
            }
        }
        
        return null;
    },
            
    $scope.getTasks = function() {
        var retList = [];
        
        var groupTasks = $scope.getGroupedTasks();
        
        for (var j in groupTasks.collectionTasks) {

            var colTask = groupTasks.collectionTasks[j];
            if ($scope.collectionType === "codmandatory" && colTask.isCod && !colTask.isOptional) {
                retList.push(colTask);
            }

            if ($scope.collectionType === "cosmandatory" && colTask.isCos && !colTask.isOptional) {
                retList.push(colTask);
            }
        }

        retList.sort(sortByReferenceNumber);
        
        return retList;
    }
    
    $scope.setDeliveryTask = function() {
        for (var i in $scope.destination.tasks) {
            var task = $scope.destination.tasks[i];
            if (task.className === 'com.thundashop.core.trackandtrace.DeliveryTask') {
                $scope.task = task;
            }
        }
    }
    
    $scope.getReturnCredit = function() {
        var groupedTask = $scope.getGroupedTasks();
        return groupedTask.adjustedReturnCredit;
    }
    
    $scope.getAdjustment = function() {
        var groupTasks = $scope.getGroupedTasks();
        if (!groupTasks.adjustment) {
            return 0;
        }
        return groupTasks.adjustment;
    }
    
    $scope.getPreviouseInvoiceCredit = function() {
        var groupedTask = $scope.getGroupedTasks();
        
        if (groupedTask.adjustmentPreviouseCredit) {
            return groupedTask.adjustmentPreviouseCredit;
        }
        
        var tasks = $scope.getTasks();
        var retValue = 0;
        
        for (var i in tasks) {
            var task = tasks[i];
            retValue += task.previouseCreditAmount;
        }
        
        return retValue;
    }
    
    $scope.getTotalCredit = function() {
        var total = $scope.getReturnCredit() + $scope.getAdjustment() + $scope.getPreviouseInvoiceCredit();
        return (Math.round(total*100)/100);
        return total;
    }
    
    $scope.getTotalPaymentSelected = function() {
        var value = $scope.getTotalPayment() - $scope.getRegisteredCashAmount() - $scope.getRegisteredChequeAmount();
        
        return (Math.round(value*100)/100);
    }
    
    $scope.paymentAmountValid = function() {
        if ($stateParams.collectionType === "optional") {
            return true;
        }
        
        var total = $scope.getTotalPaymentSelected();
        return !total;
    }
    
    $scope.getTotalPayment = function() {
        var total = $scope.getSubTotal() + $scope.getTotalCredit();
        
        if (total < 0)
            total = 0;
        
        return (Math.round(total*100)/100);
    }
    
    $scope.getSubTotal = function() {
        var subTotal = 0;
        var tasks = $scope.getTasks();
        for (var i in tasks) {
            subTotal += tasks[i].amount;
        }
        
        return subTotal;
    }
    
    $scope.openAdjustment = function(action) {
        $state.transitionTo('base.ordercorrection', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: "",
            type: 'collection',
            collectionData: {
                collectionType : $stateParams.collectionType,
                collectionSubType : 'normal',
                action: action
            }
        });
    }
    
    $scope.registerCashPayment = function() {
        $state.transitionTo('base.ordercorrection', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: "",
            type: 'collection',
            collectionData: {
                collectionType : $stateParams.collectionType,
                collectionSubType : 'payment',
                action: 'registercashpayment'
            }
        });
    }
    
    $scope.registerChequePayment = function() {
        $state.transitionTo('base.ordercorrection', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: "",
            type: 'collection',
            collectionData: {
                collectionType : $stateParams.collectionType,
                collectionSubType : 'payment',
                action: 'registerchequeamount'
            }
        });
    }
    
    $scope.registerChequeNumber = function() {
        $state.transitionTo('base.ordercorrection', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: "",
            type: 'collection',
            collectionData: {
                collectionType : $stateParams.collectionType,
                collectionSubType : 'payment',
                action: 'registerchequenumber'
            }
        });
    }
    
    $scope.showPayment = function() {
        $stateParams.collectionSubType = "normal";
        $state.transitionTo('base.collection', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId, collectionSubType :  'payment', collectionType: $stateParams.collectionType });
    }
    
    $scope.doTheBack = function() {
        if ($stateParams.collectionSubType === "payment" && $stateParams.collectionType === "codmandatory") {
            $state.transitionTo('base.collection', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId, collectionType: $stateParams.collectionType, collectionSubType : 'normal' });
            return;
        }
        
        $state.transitionTo('base.destination', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId });
    }
    
    $scope.setDeliveryTask();
    
    if ($stateParams.action.state === "chequeAmount") {
        $state.transitionTo('base.ordercorrection', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: "",
            type: 'collectionChequeNumber'
        });
    }
    
    $scope.collectionCompleted = function() {
        var groupedTask = $scope.getGroupedTasks();
        groupedTask.date = new Date();
        
        $api.getApi().TrackAndTraceManager.registerCollectionData($scope.destination.id, groupedTask);
        datarepository.save();
        
        $state.transitionTo('base.destination', { 
            destinationId: $stateParams.destinationId,
            routeId: $stateParams.routeId, 
        });
    }
    
    $scope.setAdjustmentValues = function() {
        var groupsTask = $scope.getGroupedTasks();
        if ($stateParams.collectionData.action === "adjustment") {
            groupsTask.adjustment = $stateParams.collectionData.newAmount;
            datarepository.save();
        }
        
        if ($stateParams.collectionData.action === "adjustedReturnCredit") {
            groupsTask.adjustedReturnCredit = $stateParams.collectionData.newAmount;
            
            if (groupsTask.adjustedReturnCredit > 0) {
                groupsTask.adjustedReturnCredit = groupsTask.adjustedReturnCredit * -1;
            }
            
            datarepository.save();
        }
        
        if ($stateParams.collectionData.action === "adjustmentPreviouseCredit") {
            groupsTask.adjustmentPreviouseCredit = $stateParams.collectionData.newAmount;
            if (groupsTask.adjustmentPreviouseCredit > 0) {
                groupsTask.adjustmentPreviouseCredit = groupsTask.adjustmentPreviouseCredit * -1;
            }
            datarepository.save();
        }
        
        if ($stateParams.collectionData.action === "registercashpayment") {
            groupsTask.cashAmount = $stateParams.collectionData.newAmount;
            datarepository.save();
        }
        
        if ($stateParams.collectionData.action === "registerchequeamount") {
            groupsTask.chequeAmount = $stateParams.collectionData.newAmount;
            if (groupsTask.chequeAmount) {
                $scope.registerChequeNumber();
            }
        }
        
        if ($stateParams.collectionData.action === "registerchequenumber") {
            groupsTask.chequeNumber = $stateParams.collectionData.newAmount;
            datarepository.save();
        }
    }
    
    $scope.getRegisteredCashAmount = function() {
        var groupedTask = $scope.getGroupedTasks();
        if (!groupedTask.cashAmount) {
            return 0;
        }
        return groupedTask.cashAmount;
    }
    
    $scope.getRegisteredChequeAmount = function() {
        var groupedTask = $scope.getGroupedTasks();
        return groupedTask.chequeAmount;
    }
    
    $scope.getRegisteredChequeNubmer = function() {
        var groupedTask = $scope.getGroupedTasks();
        return groupedTask.chequeNumber;
    }
    
    $scope.getDistinctPodCodes = function() {
        var groupedTasks = $scope.getGroupedTasks();
        var podBarcodes = [];
        
        for (var i in groupedTasks.collectionTasks) {
            var task = groupedTasks.collectionTasks[i];
            podBarcodes.push(task.podBarcode);
        }
        
        podBarcodes = podBarcodes.filter(function(value, index, self) {
            return self.indexOf(value) === index;
        });
        
        return podBarcodes.join(',');
    }
    
    $scope.setAdjustmentValues();
}
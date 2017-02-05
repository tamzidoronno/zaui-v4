/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.KeyReferenceController = function($scope, datarepository, $stateParams, $api, $state) {
    $scope.numbers = 10;
    $scope.api = $api;
    $scope.task = datarepository.getTaskById($stateParams.taskId);
    
    
    $scope.makeCorrection = function(a, b) {
        $state.go('base.task', { 
            destinationId: $stateParams.destinationId,  
            routeId: $stateParams.routeId, 
            taskId: $scope.task.id,
            'action' : { 
                state: 'keyedReference',
                keyReference : b
            }
        });
    }
    
    $scope.doTheBack = function() {
        $state.transitionTo('base.task', { destinationId: $stateParams.destinationId,  routeId: $stateParams.routeId, taskId: $stateParams.taskId });
    }
}
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.PoolControllerFooter =  function($scope, $api, $rootScope, datarepository, $state) {
    $scope.goBack = function() {
        if (datarepository.selectedRouteForPoolController) {
            datarepository.selectedRouteForPoolController = null;
            return;
        }
        
        $state.transitionTo("base.home");
    }
}
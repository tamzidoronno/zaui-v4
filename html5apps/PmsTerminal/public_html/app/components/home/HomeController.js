/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.HomeController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    
    $scope.init = function($api) {
    }
    $scope.goToBooking = function() {
        $state.transitionTo('base.existingbooking');
    }
    
    $scope.init($api);
};

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.GuestInfoFooterController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    $scope.goToNewAccess = function() {
        $state.transitionTo('base.newaccess');
    }
    $scope.goToHome = function() {
        $state.transitionTo('base.home');
    }
};
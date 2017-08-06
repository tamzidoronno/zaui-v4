/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.MasterCodesController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    $scope.loadingMasterCodes = true;
    $scope.masterCodes = null;
    
    $scope.masterCodesLoaded = function(res) {
        $scope.masterCodes = res;
        $scope.loadingMasterCodes = false;
        $scope.$evalAsync();
    }
    
    $scope.init = function() {
        $api.getApi().DoorManager.getMasterCodes(datarepository.domainname).done($scope.masterCodesLoaded);
    }
    
    $scope.goToHome = function() {
        $state.transitionTo('base.home');
    }
    
    $scope.masterCodesSaved = function(res) {
        $('.mastercodes .savingspinner').hide();
        $('.mastercodes .savedmastercodes').show();
        setTimeout(function() {
            $('.mastercodes .savedmastercodes').fadeOut();
        }, 1000);
    }
    
    $scope.saveMasterCodes = function() {
        $('.mastercodes .savingspinner').show();
        
        if ($scope.masterCodes) {
            $scope.masterCodes.codes = {};
        }
        
        $('input.mastercodeinput').each(function() {
            var slot = $(this).attr('slot');
            var code = $(this).val();
            $scope.masterCodes.codes[slot] = code;
        });
        
        $api.getApi().DoorManager.saveMasterCodes(datarepository.domainname, $scope.masterCodes).done($scope.masterCodesSaved);
    }
    
    $scope.init();
}
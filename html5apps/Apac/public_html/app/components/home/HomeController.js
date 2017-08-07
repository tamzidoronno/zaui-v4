/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.HomeController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    $scope.name = "";
    $scope.loadingList = true;
    
    $scope.init = function($api) {
        $scope.name = $api != null && $api.getLoggedOnUser() != null && $api.getLoggedOnUser().fullName; 
        $api.getApi().ApacManager.getAccessList(datarepository.domainname).done(function(result) {
            $scope.accessList = result;
            $scope.loadingList = false;
            $scope.$evalAsync();
        });
    }
    
    $scope.goToNewAccess = function() {
        $state.transitionTo('base.newaccess');
    }
    
    $scope.deleteAccess = function(access) {
        var result = confirm("Are you sure?");
        if (!result)
            return;
        
        $api.getApi().ApacManager.removeAccess(datarepository.domainname, access.id).done(function() {
            $scope.init($api);
        });
    }
    
    $scope.showMore = function() {
        if ($('.moremenu').is(':visible')) {
            $('.moremenu').slideUp();
        } else {
            $('.moremenu').slideDown();
        }
    }
    
    $scope.showAccess = function(access) {
        $state.transitionTo('base.guestinfo', {accessId: access.id});
    }
    
    $scope.logOut = function() {
        var conf = confirm("Are you sure you want to logout?");
        
        if (!conf)
            return;
        
        localStorage.setItem("username", "");
        localStorage.setItem("password", "");
        $api.reconnect();

        $rootScope.$broadcast("loggedOut", "");
    }
    
    $scope.showDoors = function() {
        $state.transitionTo('base.doors');
    }
    
    $scope.goToMasterCodes = function() {
        $state.transitionTo('base.mastercodes');
    }
    
    $scope.showMessages = function() {
        $state.transitionTo('base.incomingmessages');
    }
    
    $scope.init($api);
};

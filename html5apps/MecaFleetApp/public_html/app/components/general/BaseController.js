/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.BaseController = function($scope, $api) {
    $scope.alias = "";
    var api = $api.getApi();
    
    $scope.fetchTitle = function() {
        if (!api || !api.StoreApplicationPool)
            return;
        
        api.StoreApplicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41").done(function(res) {
            if (res && res.settings && res.settings.title && res.settings.title.value) {
                $scope.alias = res.settings.title.value;
                $scope.$apply();
            }
        });
    }
    
    
    $scope.connectionEstablised = function() {
        $scope.fetchTitle();
    }
    
    $scope.loggedOut = function() {
         $scope.alias = "";
    }
    
    $scope.$on('loggedOut', $scope.loggedOut);
    $scope.$on('connectionEstablished', $scope.connectionEstablised);
    $scope.fetchTitle();
};


/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.ErrorHandlerController = function($scope, $rootScope, $state, datarepository, $api) {
    if (datarepository.lastError) {
        $scope.message = datarepository.lastError.message + "\n" + datarepository.lastError.stack;
    }
    $scope.errorReportedDone = false;
    
    $scope.error = datarepository.lastError;
    
    $scope.errorReported = function() {
        $scope.errorReportedDone = true;
        $scope.$evalAsync();
    }
    
    $scope.reportError = function() {
        $api.getApi().MessageManager.sendMail('post@getshop.com', 'GetShop Support', 'Track And Trace Device Error', $scope.message, 'post@getshop.com', 'GetShop Support').done($scope.errorReported);
    }
    
    $scope.clearDevice = function() {
        datarepository.clear();
        $scope.logOut();
    }
    
    $scope.logOut = function() {
        localStorage.setItem("username", "");
        localStorage.setItem("password", "");
        $api.reconnect();

        $rootScope.$broadcast("loggedOut", "");
    }
}
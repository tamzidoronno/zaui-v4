/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.HomeController = function($scope, $api, $rootScope) {
    $scope.agreeDate = false;
    $scope.sendKilometer = false;
    
    $scope.doLogout = function() {
        localStorage.setItem("cellphone", "");
        localStorage.setItem("identifier", "");
        localStorage.setItem("loggedInUserId", "");
        $api.reconnect();
        
        $rootScope.$broadcast("loggedOut", "");
    };
    
    $scope.sendRegistrationId = function() {
        if (PushNotificationSettings.registrationId) {
            $api.api.MecaManager.registerDeviceToCar(PushNotificationSettings.registrationId, localStorage.getItem("cellphone"));
        }   
    };
    
    $scope.getCar = function() {
        $api.api.MecaManager.getCarsByCellphone(localStorage.getItem("cellphone")).done(function(res) {
            $scope.agreeDate = res[0].agreeDate || res[0].agreeDate === "true" || res[0].agreeDate === true || res[0].agreeDateControl;
            $scope.sendKilometer = res[0].dateRequestedKilomters != null;

            $scope.$apply();
        })
    };
    
    $scope.$on('pushNotificationReceived', $scope.getCar)
    
    $scope.sendRegistrationId();
    $scope.getCar();
}


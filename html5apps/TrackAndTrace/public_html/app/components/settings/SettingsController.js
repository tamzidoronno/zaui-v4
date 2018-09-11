/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.SettingsController = function($scope, $api, datarepository, $state) {
    $scope.loggedIn = false;
    
    $scope.test = function() {
    }
    
    $scope.showSettings = function() {
        var password = $('#themagicpassword').val();
        if (password == "6589") {
            $scope.loggedId = true;
        } else {
            $scope.loggedId = false;
        }
    }

    $scope.clearDb = function() {
        if (messagePersister) {
            messagePersister.clearSentMessages();
        }
    }
    
    $scope.goHome = function() {
        $state.transitionTo('base.login', { });
    }
};

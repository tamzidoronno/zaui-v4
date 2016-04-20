/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.ContactController = function($scope, $api) {
    $scope.sendMessage = function() {
        $api.getApi().MessageManager.sendMessageToStoreOwner($scope.message, "Message from mobile app").done(function() {
            alert("thank you");
        });
    }
};

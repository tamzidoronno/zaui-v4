/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.LoginController = function($scope, $api) {
    $scope.username = localStorage.getItem("username");
    $scope.password = localStorage.getItem("password");
    
    $scope.doLogin = function() {
        localStorage.setItem("username", $scope.username);
        localStorage.setItem("password", $scope.password);
        $api.reconnect(true);
    }
    
}

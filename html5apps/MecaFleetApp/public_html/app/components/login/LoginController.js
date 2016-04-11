/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.LoginController = function($scope, $api) {
    $scope.identifier = localStorage.getItem("identifier");
    $scope.cellphone = localStorage.getItem("cellphone");
    
    $scope.doLogin = function() {
        localStorage.setItem("identifier", $scope.identifier);
        localStorage.setItem("cellphone", $scope.cellphone);
        $api.reconnect(true);
    }
    
}

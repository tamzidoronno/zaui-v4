/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.LoginController = function($scope, $api) {
    $scope.username = localStorage.getItem("username");
    $scope.password = localStorage.getItem("password");
    $scope.company = localStorage.getItem("company");
    
    $scope.doLogin = function() {

        if ($('.loginbutton').find('.login-shower').length) {
            return;
        }
        
        $('.loginbutton').prepend('<i class="fa fa-spin fa-spinner login-shower"></i>');
        localStorage.setItem("company", $scope.company);
        localStorage.setItem("username", $scope.username);
        localStorage.setItem("password", $scope.password);
        $api.reconnect(true);
    }

};

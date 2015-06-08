'use strict';

var GetShopLoginController = function($scope, $http, $location) {
    $scope.login = {
        webaddress : "alternativenergi.3.0.local.getshop.com",
        email : "ktonder@gmail.com",
        password : "gakkgakk",
    };

    $scope.loggedIn = function(user) {
        if (user.errorCode == 13) {
            alert('wrong username or password');
            return;
        }

        GetShop.loggedInUserId = user.id;
        sessionStorage.setItem("user.id", user.id);
        $location.path( "/mainview" );
        $scope.$apply();
    };

    $scope.submit = function() {
        sessionStorage.setItem("login.webaddress", $scope.login.webaddress);            
        GetShop.connect($scope.connected);
    };

    $scope.connected = function() {
        GetShop.api.UserManager.logOn($scope.login.email, $scope.login.password).done( $scope.loggedIn );
    }
}
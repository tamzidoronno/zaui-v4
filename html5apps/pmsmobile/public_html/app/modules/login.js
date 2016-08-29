if(getshop === undefined) { getshop = {}; }
getshop.loginController = function($scope, $state, $location) {
    $scope.login = {
        "address" : localStorage.getItem("address"),
        "username" : localStorage.getItem("username"),
        "password" : localStorage.getItem("password")
    };
    $scope.doLogin = function(user) {
        localStorage.setItem("address", user.address);
        localStorage.setItem("username", user.username);
        localStorage.setItem("password", user.password);
        localStorage.setItem("multilevelname", "");
        delete getshopclient;
        createGetshopClient();
        $scope.loading=true;
        var logon = getshopclient.UserManager.logOn(user.username, user.password);
        
        getshopclient.setInitConnectionFailed(function() {
            $scope.loading=false;
            alert('Logon failed');
            $scope.$apply();
        });
        
        logon.done(function(res) {
            $scope.loading=false;
            if(res.errorCode) {
                alert('failed to logon');
            } else {
                $state.go("mainpage");
            }
            $scope.$apply();
        });
        
        logon.fail(function(res) {
            $scope.loading=false;
            alert('Logon failed');
            $scope.$apply();
        });
        return logon;
    };

    var arguments = $location.search();
    if(arguments.username) {
        
        $scope.login = {
            "address" : arguments.address,
            "username" : arguments.username,
            "password" : arguments.password
        };
        
        $scope.doLogin(arguments);
    }
};
if(getshop === undefined) { getshop = {}; }
getshop.loginController = function($scope, $state) {
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
        logon.done(function(res) {
            $scope.loading=false;
            if(res.errorCode === 13) {
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
        })
        return logon;
    }
}
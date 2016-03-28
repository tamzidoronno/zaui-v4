if(getshop === undefined) { getshop = {}; }
getshop.loginController = function($scope, $state) {
    $scope.login = {
        "address" : "partybox.3.0.local.getshop.com",
        "username" : "post@getshop.com",
        "password" : "gakkgakk"
    };
    $scope.doLogin = function(user) {
        localStorage.setItem("address", user.address);
        localStorage.setItem("username", user.username);
        localStorage.setItem("password", user.password);
        createGetshopClient();
        var logon = getshopclient.UserManager.logOn(user.username, user.password);
        logon.done(function(res) {
            if(res.errorCode === 13) {
                alert('failed to logon');
            } else {
                $state.go("mainpage");
            }
        });
        logon.fail(function(res) {
            alert('Logon failed');
        })
        return logon;
    }
}
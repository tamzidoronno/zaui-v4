if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.mainpageController = function($scope, $state) {
    $scope.logout = function() {
        getshopclient.UserManager.logout();
        $state.go("login");
    };
};
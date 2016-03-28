if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.mainpageController = function($scope, $state) {
    $scope.address = localStorage.getItem("address");
    var loadNames = getshopclient.StoreManager.getMultiLevelNames();
    loadNames.done(function(data) {
        var names = [];
        var current = getMultilevelName();
        for(var key in data) {
            if(!current) {
                current = data[key];
                localStorage.setItem("multilevelname", current);
            }
            names.push({"id": data[key], "name" : data[key]});
        }
        $scope.multilevelnames = names;
        $scope.dataSelected = current;
        $scope.$apply();
    })
    $scope.updateMultilevelName = function(name) {
        localStorage.setItem("multilevelname", name);
    };
    $scope.logout = function() {
        getshopclient.UserManager.logout();
        $state.go("login");
    };
};
if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.fireController = function($scope, $state) {
    var getconfig = getshopclient.PmsManager.getConfiguration(getMultilevelName());
    getconfig.done(function(res) {
        $scope.msg = res.fireinstructions;
        $scope.$apply();
    });
}
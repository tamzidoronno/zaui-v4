if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.otherInstructionController = function($scope, $state) {
    var getconfig = getshopclient.PmsManager.getConfiguration(getMultilevelName());
    getconfig.done(function(res) {
        $scope.msg = res.otherinstructions;
        $scope.$apply();
    });
}
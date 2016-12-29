if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.addonviewController = function ($scope, $state, $stateParams) {
    $scope.selectedDay = new Date();
    $scope.addonsCount = 0;
    $scope.viewid = $stateParams.viewid;
    
    $scope.loadAddonsList = function() {
        console.log($scope.viewid);
        var loading = getshopclient.PmsManager.getItemsForView(getMultilevelName(), $scope.viewid, $scope.selectedDay);
        loading.done(function(res) {
            $scope.addonslist = res;
            $scope.notfound = false;
            if(res.length === 0) {
                $scope.notfound = true;
            }
            $scope.$apply();
        });
    }
    $scope.loadAddonsList();
}
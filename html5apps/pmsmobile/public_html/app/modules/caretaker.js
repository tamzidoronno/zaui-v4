if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.careTakerController = function ($scope, $state, $stateParams) {
    $scope.careTakerList = [];
    
    $scope.completeTask = function(object) {
        var completing = getshopclient.PmsManager.completeCareTakerJob(getMultilevelName(), object.id);
        completing.done(function() {
            $scope.loadCareTakerTasks();
        });
    },
    
    $scope.loadCareTakerTasks = function() {
        var loading = getshopclient.PmsManager.getCareTakerJobs(getMultilevelName());
        loading.done(function(res) {
            console.log(res);
            var result = [];
            for(var k in res) {
                if(!res[k].completed) {
                    result.push(res[k]);
                }
            }
            $scope.careTakerList = result;
            $scope.$apply();
        });
    };
            
    $scope.loadCareTakerTasks();
};
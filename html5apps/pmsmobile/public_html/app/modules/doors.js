if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.doorsController = function($scope, $state) {
    var loadDoors = getshopclient.DoorManager.getAllDoors(getMultilevelName());
    $scope.loading = true;
    loadDoors.done(function(res) {
        $scope.loading = false;
        $scope.doors = res;
        $scope.$apply();
    });
    $scope.pulseOpen = function(id, state) {
        var action = getshopclient.DoorManager.doorAction(getMultilevelName(), id, state);
        action.done(function() {
            alert('Action sent');
        });
    }
};
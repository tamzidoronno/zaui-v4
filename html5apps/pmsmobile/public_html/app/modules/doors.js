if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.doorsController = function($scope, $state) {
    $scope.loadDoors = function() {
        var loadDoors = getshopclient.DoorManager.getAllDoors(getMultilevelName());
        $scope.loading = true;
        loadDoors.done(function(res) {
            $scope.loading = false;
            $scope.doors = res;
            $scope.$apply();
        });
    },
    $scope.clearCache = function() {
        $scope.loading = true;
        var clearing = getshopclient.DoorManager.clearDoorCache(getMultilevelName());
        clearing.done(function() {
            $scope.loadDoors();
        });
    },
    $scope.pulseOpen = function(id, state) {
        var action = getshopclient.DoorManager.doorAction(getMultilevelName(), id, state);
        action.done(function() {
            alert('Action sent');
        });
    }
    $scope.loadDoors();
};
if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.cleaningCheckoutController = function ($scope, $state, $stateParams) {
    $scope.roomName = $stateParams.roomName;
    $scope.date = new Date();
    
    $scope.convertToDate = function(time) {
        var d = new Date();
        d.setTime(time);
        return d.toLocaleString();
    }
    
    $scope.loadGuestList = function() {
        var filter = {};
        filter.filterType = "";
        filter.searchWord = $scope.roomName;
        filter.sorting = "periode_desc";
        
        var loadguests = getshopclient.PmsManager.getSimpleRooms(getMultilevelName(), filter);
        var tonight = new Date();
        tonight.setHours(20);
        var tonightTime = tonight.getTime();
        
        loadguests.done(function(res) {
            var newList = [];
            for(var k in res) {
                if(res[k].end > tonightTime) {
                    continue;
                }
                newList.push(res[k]);
            }
            
            $scope.guestlist = newList;
            $scope.selectedGuest = newList[0];
            $scope.$apply();
        });
    }
    
    $scope.loadGuestList();
};
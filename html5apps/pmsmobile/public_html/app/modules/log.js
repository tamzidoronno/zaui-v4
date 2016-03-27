if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.logController = function($scope, $state) {
    var filter = {};
    filter.includeAll = true;
    var loading = getshopclient.PmsManager.getLogEntries(getMultilevelName(), filter);
    loading.done(function(res) {
        $scope.logs = res;
        $scope.$apply();
    });
    
    var loaditems = getshopclient.BookingEngine.getBookingItems(getMultilevelName());
    loaditems.done(function(res) {
        $scope.items = res;
        $scope.$apply();
    });
    
    $scope.updateLogList = function(item) {
        var filter = {};
        filter.bookingItemId = item;
        var loading = getshopclient.PmsManager.getLogEntries(getMultilevelName(), filter);
        loading.done(function(res) {
            $scope.logs = res;
            $scope.$apply();
        });
    }
    
}
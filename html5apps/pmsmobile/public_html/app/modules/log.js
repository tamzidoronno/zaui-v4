if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.logController = function($scope, $state) {
    var filter = {};
    filter.includeAll = true;
    filter.tag = "mobileapp";
    var loading = getshopclient.PmsManager.getLogEntries(getMultilevelName(), filter);
    $scope.loading = true;
    loading.done(function(res) {
        $scope.loading = false;
        $scope.logs = res;
        $scope.$apply();
    });
    
    var loaditems = getshopclient.BookingEngine.getBookingItems(getMultilevelName());
    loaditems.done(function(res) {
        var first = {};
        first.id = "";
        first.bookingItemName = "General";
        res.unshift(first);
        $scope.items = res;
        $scope.$apply();
    });
    $scope.addEntry = function() {
        var text = $scope.logentrytext;
        var item = $scope.selectedroom;
        var object = {};
        object.bookingItemId = item;
        object.tag = "mobileapp";
        object.logText = text;
        $scope.logentrytext = "";
        var logging = getshopclient.PmsManager.logEntryObject(getMultilevelName(), object);
        logging.done(function() {
            $scope.updateLogList(item);
        });
        
    }
    $scope.updateLogList = function(item) {
        var filter = {};
        if(!item) {
            filter.includeAll = true;
        } else {
            filter.bookingItemId = item;
        }
        filter.tag="mobileapp";
        $scope.loading = true;
        var loading = getshopclient.PmsManager.getLogEntries(getMultilevelName(), filter);
        loading.done(function(res) {
            $scope.loading = false;
            $scope.logs = res;
            $scope.$apply();
        });
    }
}
if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.addonviewController = function ($scope, $state, $stateParams) {
    $scope.selectedDay = new Date();
    $scope.addonsCount = 0;
    $scope.viewid = $stateParams.viewid;
    
    $scope.loadAddonsList = function() {
        var loading = getshopclient.PmsManager.getItemsForView(getMultilevelName(), $scope.viewid, $scope.selectedDay);
        loading.done(function(res) {
            $scope.addonsCount = 0;
            $scope.addonslist = res;
            $scope.notfound = false;
            if(res.length === 0) {
                $scope.notfound = true;
            }
            for(var k in res) {
                $scope.addonsCount += res[k].count;
            }
            $scope.loadDeliveryLog();
            $scope.$apply();
        });
    }
    
    $scope.loadDeliveryLog = function() {
        var start = new Date($scope.selectedDay);
        start.setHours(0,0,0,0);

        var end = new Date($scope.selectedDay);
        end.setHours(23,59,59,999);

        var loadList = getshopclient.PmsManager.getDeliveryLogByView(getMultilevelName(), $scope.viewid, start, end);
        loadList.done(function(res) {
            $scope.entryLog = res;
            $scope.deliveryCount = res.length;
            $scope.$apply();
        });
    }
    
    $scope.showAddon = function(showAddon) {
        return showAddon.count > showAddon.delivered;
    }
    
    $scope.deleteLogEntry = function(id) {
        var deleting = getshopclient.PmsManager.deleteDeliveryLogEntry(getMultilevelName(), id);
        deleting.done(function() {
           $scope.loadAddonsList();
        });
    }
    
    $scope.markAddonDelivered = function(addon) {
       var delivery = getshopclient.PmsManager.markAddonDelivered(getMultilevelName(), addon.addonId);
       delivery.done(function() {
           $scope.loadAddonsList();
       });
    }
    
    $scope.loadAddonsList();
}
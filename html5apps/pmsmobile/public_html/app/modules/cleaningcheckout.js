if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.cleaningCheckoutController = function ($scope, $state, $stateParams, $location) {
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
                if(res[k].start < tonightTime && res[k].end > tonightTime) {
                    newList.push(res[k]);
                }
                if(res[k].end > tonightTime) {
                    continue;
                }
                newList.push(res[k]);
            }
            
            $scope.guestlist = newList;
            $scope.selectedGuest = newList[0];
            $scope.loadAddedServices();
            $scope.$apply();
        });
    }
    
    $scope.markRoomReady = function() {
        var markReady = getshopclient.PmsManager.markRoomAsCleaned(getMultilevelName(), $scope.selectedGuest.bookingItemId);
        markReady.done(function() {
             $location.path("/cleaning");
             $scope.$apply();
        });
    }
    
    $scope.removeAddon = function(addonId) {
        var adding = getshopclient.PmsManager.addAddonsToBookingById(getMultilevelName(), addonId, $scope.selectedGuest.pmsRoomId, true);
        adding.done(function() {
            $scope.loadAddedServices();
        });
    }
    
    $scope.loadAddedServices = function() {
        var loading = getshopclient.PmsManager.getAddonsForRoom(getMultilevelName(), $scope.selectedGuest.pmsRoomId);
        loading.done(function(list) {
            $scope.guestAddonList = [];
            for(var k in list) {
                if(list[k].isAvailableForCleaner) {
                    $scope.guestAddonList.push(list[k]);
                }
            }
            $scope.$apply();
        });
    },
    
    $scope.addServiceToRoom = function() {
        var adding = getshopclient.PmsManager.addAddonsToBookingById(getMultilevelName(), $scope.selectedService.addonId, $scope.selectedGuest.pmsRoomId, false);
        adding.done(function() {
            $scope.loadAddedServices();
        });
    },
    
    $scope.loadServiceList = function() {
        var config = getshopclient.PmsManager.getAddonsAvailable(getMultilevelName());
        var addonList = [];
        config.done(function(list) {
            for(var k in list) {
                if(list[k].isAvailableForCleaner) {
                    addonList.push(list[k]);
                }
            }
            $scope.serviceList = addonList;
            $scope.$apply();
        });
        
    }
    
    $scope.loadGuestList();
    $scope.loadServiceList();
};
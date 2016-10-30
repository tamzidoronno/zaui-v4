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
        var adding = getshopclient.PmsManager.removeAddonFromRoomById(getMultilevelName(), addonId, $scope.selectedGuest.pmsRoomId);
        adding.done(function() {
            $scope.loadAddedServices();
        });
    }
    
    $scope.loadAddedServices = function() {
        var loading = getshopclient.PmsManager.getAddonsForRoom(getMultilevelName(), $scope.selectedGuest.pmsRoomId);
        loading.done(function(list) {
            $scope.guestAddonList = [];
            for(var k in list) {
                $scope.guestAddonList.push(list[k]);
            }
            $scope.$apply();
        });
    },
    $scope.reportInventory = function() {
        var reporting = getshopclient.PmsManager.reportMissingInventory(getMultilevelName(), $scope.inventoryList, $scope.selectedGuest.bookingItemId, $scope.selectedGuest.pmsRoomId);
        reporting.done(function() {
            $scope.loadAddedServices();
        });
    },
    $scope.increaseCounter = function(productId) {
        for(var key in $scope.inventoryList) {
            if($scope.inventoryList[key].productId === productId) {
                $scope.inventoryList[key].missingCount++;
                if($scope.inventoryList[key].missingCount > $scope.inventoryList[key].originalCount) {
                    $scope.inventoryList[key].missingCount = $scope.inventoryList[key].originalCount;
                }
            }
        }
        $scope.reportInventory();
    },
    $scope.decreaseCounter = function(productId) {
        for(var key in $scope.inventoryList) {
            if($scope.inventoryList[key].productId === productId) {
                $scope.inventoryList[key].missingCount--;
                if($scope.inventoryList[key].missingCount < 0) {
                    $scope.inventoryList[key].missingCount = 0;
                }
            }
        }
        $scope.reportInventory();
    },
    $scope.addServiceToRoom = function() {
        var adding = getshopclient.PmsManager.addProductToRoom(getMultilevelName(), $scope.selectedService.productId, $scope.selectedGuest.pmsRoomId, 1);
        adding.done(function() {
            $scope.loadAddedServices();
        });
    },
            
    $scope.loadInventoryList = function() {
        var loading = getshopclient.PmsManager.getSimpleInventoryList(getMultilevelName(), $scope.roomName);
        loading.done(function(res) {
            $scope.inventoryList = res;
            $scope.$apply();
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
    $scope.loadInventoryList();
};
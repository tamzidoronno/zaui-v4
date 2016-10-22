if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.cleaningController = function ($scope, $state, $stateParams) {
    $scope.checkInGuests = [];
    $scope.checkOutGuests = [];
    $scope.loadGuest = function(type) {
        var filter = {};
        if(type == "checkin") {
            filter.filterType = "checkin";
            filter.sorting = "room";
            filter.startDate = new Date();
            filter.endDate = new Date();
        }
        if(type == "checkout") {
            filter.filterType = "checkout";
            filter.sorting = "room";
            filter.startDate = new Date();
            filter.endDate = new Date();
        }
        if(type == "other") {
            filter.filterType = "checkout";
            filter.sorting = "room";
            filter.startDate = new Date();
            filter.endDate = new Date();
        }
        filter.includeCleaningInformation = true;
        var loadGuests = getshopclient.PmsManager.getSimpleRooms(getMultilevelName(), filter);
        loadGuests.done(function(data) {
            for(var key in data) {
                data[key].cleaningState = "notclean";
                if(data[key].hasBeenCleaned == true) {
                    data[key].cleaningState = "clean";
                }
            }
            if(type == "checkin") { $scope.checkInGuests = data; }
            if(type == "checkout") { $scope.checkOutGuests = data; }
            $scope.$apply();
        });
    };
    
    $scope.loadRooms = function() {
        var additional = getshopclient.PmsManager.getAllAdditionalInformationOnRooms(getMultilevelName());
        additional.done(function(addinfo) {
            console.log(addinfo);
            var loadrooms = getshopclient.BookingEngine.getBookingItems(getMultilevelName());
            loadrooms.done(function(rooms) {
                console.log(rooms);
                for(var roomk in rooms) {
                    var room = rooms[roomk];
                    for(var addk in addinfo) {
                        var addinfoitem = addinfo[addk];
                        if(addinfoitem.itemId === room.id) {
                            room.inUseState = "notclean";
                            if(addinfoitem.inUse) {
                                room.inUseState = "inuse";
                            } else if(addinfoitem.isClean) {
                                room.inUseState = "clean";
                            }
                        }
                    }
                }
               $scope.rooms = rooms;
               $scope.$apply();
            });
        });
    };
    
    $scope.loadGuest("checkin");
    $scope.loadGuest("checkout");
    $scope.loadGuest("other");
    $scope.loadRooms();
};
if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.cleaningController = function ($scope, $state, $stateParams) {
    $scope.checkInGuests = [];
    $scope.checkOutGuests = [];
    $scope.defaultStart = 14;
    $scope.defaultEnd = 12;
    
    $scope.loadList = function(wtf) {
        $scope.guestList = $scope.checkInGuests;
    }
    $scope.cleaningLists = {
        checkin : { type : "checkin", list:[] },
        interval :  { type : "interval", list:[] },
        checkout :  { type : "checkout", list:[] }
    };
    $scope.getCheckinTime = function(time, guest, checkout) {
        var date = new Date();
        date.setTime(time);
        var hour = date.getHours();
        if(hour < 10) {
            hour = "0" + hour;
        }
        var minute = date.getMinutes();
        if(minute < 10) {
            minute = "0" + minute;
        }
        
        if(checkout && hour > $scope.defaultEnd) {
            guest.latecheckout = true;
        }
        if(!checkout && hour < $scope.defaultStart) {
            guest.earlycheckin = true;
        }
        return hour + ":" + minute;
    }
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
        filter.includeCleaningInformation = true;
        var loadGuests = getshopclient.PmsManager.getSimpleRooms(getMultilevelName(), filter);
        loadGuests.done(function(data) {
            for(var key in data) {
                data[key].cleaningState = "notclean";
                if(data[key].hasBeenCleaned == true) {
                    data[key].cleaningState = "clean";
                }
            }
            if(type == "checkin") { $scope.cleaningLists.checkin.list = data; }
            if(type == "checkout") { $scope.cleaningLists.checkout.list = data; }
            $scope.$apply();
        });
    };
    
    $scope.loadRooms = function() {
        var additional = getshopclient.PmsManager.getAllAdditionalInformationOnRooms(getMultilevelName());
        additional.done(function(addinfo) {
            var loadrooms = getshopclient.BookingEngine.getBookingItems(getMultilevelName());
            loadrooms.done(function(rooms) {
                for(var roomk in rooms) {
                    var room = rooms[roomk];
                    for(var addk in addinfo) {
                        var addinfoitem = addinfo[addk];
                        if(addinfoitem.itemId === room.id) {
                            if(addinfoitem.inUse) {
                                room.inUseState = "inuse";
                            } else if(addinfoitem.isClean) {
                                room.inUseState = "clean";
                            } else {
                                room.inUseState = "notclean";
                            }
                        }
                    }
                }
               $scope.rooms = rooms;
               $scope.$apply();
            });
        });
    };
    
    $scope.toggleCleaningOnRoom = function(room) {
        var confirmed = confirm("Are you sure you want to toggle cleaning on this room?");
        if(confirmed) {
            getshopclient.PmsManager.forceMarkRoomAsCleaned(getMultilevelName(), room.id);
            $scope.loadRooms();
        }
    }
    
    $scope.loadIntervalCleaning = function() {
        var loader = getshopclient.PmsManager.getRoomsNeedingIntervalCleaningSimple(getMultilevelName(), new Date());
        loader.done(function(data) {
            for(var key in data) {
                data[key].cleaningState = "notclean";
                if(data[key].hasBeenCleaned == true) {
                    data[key].cleaningState = "clean";
                }
            }
            
            $scope.cleaningLists.interval.list = data;
            $scope.$apply();
        });
    }
    
    var loadConfig = getshopclient.PmsManager.getConfiguration(getMultilevelName());
    loadConfig.done(function(config) {
        $scope.defaultStart = parseInt(config.defaultStart.split(":")[0]);
        $scope.defaultEnd = parseInt(config.defaultEnd.split(":")[0]);

        $scope.config = config;
        $scope.loadGuest("checkin");
        $scope.loadGuest("checkout");
        $scope.loadRooms();
        $scope.loadIntervalCleaning();
    });
    
};
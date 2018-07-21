if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.cleaningController = function ($scope, $state, $stateParams, $sce) {
    $scope.checkInGuests = [];
    $scope.checkOutGuests = [];
    $scope.defaultStart = 14;
    $scope.reportPanel = false;
    $scope.defaultEnd = 12;
    $scope.instruction = "";
    
    $scope.loadInstruction = function() {
        getshopclient.PmsManager.getConfiguration(getMultilevelName()).done(function(res) {
            $scope.instruction = $sce.trustAsHtml(res.cleaninginstruction);
            $scope.$applyAsync();
        });
    },
    $scope.formatDate = function(date) {
        var today = date;
        var dd = today.getDate();
        var mm = today.getMonth()+1; //January is 0!

        var yyyy = today.getFullYear();
        if(dd<10){
            dd='0'+dd;
        } 
        if(mm<10){
            mm='0'+mm;
        } 
        
        var today = dd+'/'+mm+'/'+yyyy;
        
        return today;
    },
    $scope.loadFutureCleanings = function() {
        var start = new Date();
        var end = new Date();
        end.setDate(end.getDate()+7);
        var loading = getshopclient.PmsManager.getSimpleCleaningOverview(getMultilevelName(), start,end);
        loading.done(function(res) {
            for(var k in res) {
                res[k].dateFormatted = $scope.formatDate(new Date(res[k].date));
            }
            $scope.cleaningstats = res;
        });
    };
    
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
    $scope.showReportPanel = function() {
        $scope.reportPanel = !$scope.reportPanel;
    },
            
    $scope.printCheckedOut = function(guest) {
        console.log(guest);
        if(guest.end > time()) {
            return true;
        }
        return false;
    },
    $scope.loadGuest = function(type) {
        var filter = {};
        var start = new Date();
        start.setHours(0,0,0,0);

        var end = new Date();
        end.setHours(23,59,59,999);
        
        if(type == "checkin") {
            filter.filterType = "checkin";
            filter.sorting = "room";
            filter.startDate = start;
            filter.endDate = end;
        }
        if(type == "checkout") {
            filter.filterType = "checkout";
            filter.sorting = "room";
            filter.startDate = start;
            filter.endDate = end;
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
        var additional = getshopclient.PmsManager.getAllRoomsNeedCleaningToday(getMultilevelName());
        additional.done(function(addinfo) {
            var loadrooms = getshopclient.BookingEngine.getBookingItems(getMultilevelName());
            loadrooms.done(function(rooms) {
                var allRoomsToPrint = [];
                for(var roomk in rooms) {
                    var room = rooms[roomk];
                    for(var addk in addinfo) {
                        var addinfoitem = addinfo[addk];
                        if(addinfoitem.roomId === room.id) {
                            if(addinfoitem.cleaningState === 2) {
                                room.inUseState = "inuse";
                            } else if(addinfoitem.cleaningState === 1) {
                                room.inUseState = "clean";
                            } else if(addinfoitem.cleaningState === 4) {
                                room.inUseState = "needInterval";
                            } else if(addinfoitem.cleaningState === 5) {
                                room.inUseState = "notcleancheckedout";
                            } else {
                                room.inUseState = "notclean";
                            }
                            
                            if(!addinfoitem.hideFromCleaningProgram) {
                                allRoomsToPrint.push(room);
                            }
                        }
                    }
                }
               $scope.rooms = allRoomsToPrint;
               $scope.$apply();
            });
        });
    };
    
    $scope.toggleCleaningOnRoom = function(room) {
        var confirmed = false;
        if(room.inUseState === "inuse") {
            console.log(room);
            $state.go('cleaningCheckout', {  roomName : room.bookingItemName });
        } else if(room.inUseState === "clean") {
            confirmed = confirm("Are you sure you want to mark this room as DIRTY?");
        } else {
            confirmed = confirm("Are you sure you want to mark this room as CLEAN?");
        }
        if(confirmed) {
            if(room.inUseState === "clean") {
                getshopclient.PmsManager.markRoomDirty(getMultilevelName(), room.id);
            } else {
                getshopclient.PmsManager.forceMarkRoomAsCleaned(getMultilevelName(), room.id);
            }
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
    
    $scope.loadBookingItems = function() {
        var loading = getshopclient.BookingEngine.getBookingItems(getMultilevelName());
        loading.done(function(res){
            $scope.items = res;
        });
    }
    
    $scope.reportToCareTaker = function() {
        var job = {};
        job['description'] = $scope.caretakermsg;
        job['roomId'] = $scope.selectedRoom.id;
        
        var saving = getshopclient.PmsManager.saveCareTakerJob(getMultilevelName(), job);
        saving.done(function() {
            $scope.reportPanel = false;
            $scope.caretakermsg = "";
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
    
    $scope.loadBookingItems();
    $scope.loadFutureCleanings();
    $scope.loadInstruction();
};
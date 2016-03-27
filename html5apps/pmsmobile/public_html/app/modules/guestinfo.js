if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.guestInfoController = function($scope, $state, $stateParams) {
    var bookingid = $stateParams.bookingid;
    var roomid = $stateParams.roomid;
    
    $scope.saveUser = function() {
        var saving = getshopclient.UserManager.saveUser($scope.user);
        saving.done(function() {
            alert('saved');
        });
    }
    
    $scope.changeRoom = function(newroom) {
        var bookingid = $scope.booking.id;
        var roomid = $scope.room.pmsBookingRoomId;
        var changing = getshopclient.PmsManager.setBookingItem(getMultilevelName(), roomid, bookingid, newroom);
        changing.done(function(res) {
            if(!res) {
                alert('Updated');
            } else {
                alert(res);
            }
        });
    };
    
    $scope.changeStay = function() {
        var bookingid = $scope.booking.id;
        var roomid = $scope.room.pmsBookingRoomId;
        var changed = getshopclient.PmsManager.changeDates(getMultilevelName(), roomid, bookingid, $scope.room.date.start, $scope.room.date.end);
        changed.done(function(res) {
            console.log(res);
        });
    }

    $scope.saveGuests = function() {
        var bookingid = $scope.booking.id;
        var roomid = $scope.room.pmsBookingRoomId;
        var updating = getshopclient.PmsManager.setGuestOnRoom(getMultilevelName(), $scope.room.guests, bookingid, roomid);
        updating.done(function() {
            alert('updated');
        });
    };
    
    $scope.changeRoomCount = function(count) {
        var guests = [];
        for(var i = 0; i < count; i++) {
            if($scope.room.guests[i]) {
                guests.push($scope.room.guests[i]);
            } else {
                guests.push({});
            }
        }
        $scope.room.guests = guests;
    }
    
    $scope.convertToDate = function(time) {
        var timed = Date.parse(time);
        var d = new Date();
        d.setTime(timed);
        return d;
    }
    
    var booking = getshopclient.PmsManager.getBooking(getMultilevelName(), bookingid);
    booking.done(function(res) {
        for(var key in res.rooms) {
            var room = res.rooms[key];
            if(room.pmsBookingRoomId === roomid) {
                console.log(room);
                room.date.start = $scope.convertToDate(room.date.start);
                room.date.end = $scope.convertToDate(room.date.end);
                $scope.numberofguests = room.numberOfGuests;
                $scope.guests = room.guests;
                $scope.room = room;
                $scope.selectedroom = room.bookingItemId;
                
                var loadItems = getshopclient.BookingEngine.getAllAvailbleItems(getMultilevelName(), new Date(), room.date.end);
                loadItems.done(function(items) {
                    var newres = {};
                    for(var k in items) {
                        newres[items[k].id] = items[k];
                    }
                    var loadItems2 = getshopclient.BookingEngine.getBookingItems(getMultilevelName());
                    loadItems2.done(function(allitems) {
                        console.log(allitems);
                        for(var key in allitems) {
                            if(!newres[allitems[key].id] && allitems[key].id != $scope.selectedroom) {
                                allitems[key].bookingItemName += " (not available)";
                            }
                        }
                        $scope.items = allitems;
                        $scope.$apply();
                    });
                });
                
            }
        }
        $scope.booking = res;
        $scope.$apply();
        var loadingUser = getshopclient.UserManager.getUserById(res.userId);
        loadingUser.done(function(user) {
            $scope.user = user;
            $scope.$apply();
        });
        
    });
    
}
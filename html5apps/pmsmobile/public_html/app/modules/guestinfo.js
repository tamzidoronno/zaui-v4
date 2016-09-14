if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.guestInfoController = function($scope, $state, $stateParams) {
    var bookingid = $stateParams.bookingid;
    var roomid = $stateParams.roomid;
    $scope.doPayOrder = false;
    
    $scope.saveUser = function() {
        var saving = getshopclient.UserManager.saveUser($scope.user);
        saving.done(function() {
            alert('saved');
        });
    }
    
    $scope.changeRoom = function(newroom) {
        var bookingid = $scope.booking.id;
        var roomid = $scope.room.pmsBookingRoomId;
        var changing = getshopclient.PmsManager.setBookingItem(getMultilevelName(), roomid, bookingid, newroom, true);
        changing.done(function(res) {
            if(!res) {
                alert('Updated');
            } else {
                console.log(res);
                alert(res);
            }
        });
    };
    $scope.doRecreateCode = function() {
        var roomId = $scope.room.pmsBookingRoomId;
        var confirmed = confirm("Are you sure you want to generate a new code for this room?");
        if(!confirmed) {
            return;
        }
        var generated = getshopclient.PmsManager.generateNewCodeForRoom(getMultilevelName(), roomId);
        generated.done(function(res) {
            $scope.room.code = res;
        });
    },
    $scope.forceGrantAccess = function() {
        $scope.booking.forceGrantAccess = true;
        getshopclient.PmsManager.saveBooking(getMultilevelName(), $scope.booking);
    },
    
    $scope.markOrderAsInvoice = function() {
        var orderId = $scope.payOrder.id;
        var marking = getshopclient.OrderManager.markAsInvoicePayment(orderId);
        marking.done(function() {
            getshopclient.OrderManager.saveOrder($scope.payOrder);
            $scope.changeOrderStatus(7);
        });
    };
    
    $scope.changeOrderStatus = function(status) {
        var orderId = $scope.payOrder.id;
        var changing = getshopclient.OrderManager.changeOrderStatus(orderId, status);
        changing.done(function() {
            alert('Order has been updated');
            $scope.payOrder.status = status;
            $scope.payOrderProcess = false;
            $scope.$apply();
        });
    };
    
    $scope.doSendReciept = function(order) {
        $scope.payOrderProcess = false;
        if($scope.sendRecieptProcess) {
            $scope.sendRecieptProcess = false;
            return;
        }
        $scope.recieptEmail = $scope.user.emailAddress;
        $scope.sendRecieptProcess = true;
        $scope.recieptOrder = order;
    }
    
    $scope.roomIsStarted = function() {
        if($scope.room && $scope.room.addedToArx) {
            return true;
        }
        if($scope.booking && !$scope.booking.payedFor) {
            return false;
        }
        if($scope.room && !$scope.room.addedToArx) {
            return false;
        }
        return $scope.isStarted;
    };
    
    $scope.completeSendReciept = function() {
        var orderId = $scope.recieptOrder.id;
        var email = $scope.recieptEmail;
        getshopclient.OrderManager.sendReciept(orderId, email);
        alert('Email has been sent');
        $scope.sendRecieptProcess = false;
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
    $scope.doResendCode = function() {
        if($scope.resendcode) {
            $scope.resendcode = false;
            return;
        }
        $scope.numbertosendcodeto = "+" + $scope.room.guests[0].prefix + " " + $scope.room.guests[0].phone;
        $scope.resendcode = true;
    },
    $scope.resendCode = function() {
        var number = $scope.numbertosendcodeto;
        var roomId = $scope.room.pmsBookingRoomId;
        var sending = getshopclient.PmsManager.sendCode(getMultilevelName(), number, roomId);
        sending.done(function() {
            alert('Code has been sent');
            $scope.resendcode = false;
            $scope.$apply();
        });
    },
    
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
    
    $scope.doPayOrder = function(order) {
        if($scope.payOrderProcess) {
            $scope.payOrderProcess = false;
            return;
        }
        $scope.sendRecieptProcess = false;
        $scope.payOrder = order;
        $scope.payOrderProcess = true;
    }
    $scope.loading = true;
    var booking = getshopclient.PmsManager.getBooking(getMultilevelName(), bookingid);
    booking.done(function(res) {
        $scope.loading = false;
        $scope.orders = [];
        for(var key in res.orderIds) {
            var orderId = res.orderIds[key];
            var loadOrder = {};
            loadOrder[orderId] = getshopclient.OrderManager.getOrder(orderId);
            loadOrder[orderId].done(function(orderres) {
                var total = getshopclient.OrderManager.getTotalAmount(orderres);
                total.done(function(amount) {
                    orderres.amountInc = Math.round(amount);
                })
                $scope.orders.push(orderres);
            });
        }
            
        for(var key in res.rooms) {
            var room = res.rooms[key];
            if(room.pmsBookingRoomId === roomid) {
                room.date.start = $scope.convertToDate(room.date.start);
                room.date.end = $scope.convertToDate(room.date.end);
                $scope.numberofguests = room.numberOfGuests;
                $scope.guests = room.guests;
                $scope.room = room;
                $scope.isStarted = room.date.start.getTime() < new Date().getTime();
                $scope.selectedroom = room.bookingItemId;
                var loadItems = getshopclient.BookingEngine.getAllAvailbleItems(getMultilevelName(), new Date(), room.date.end);
                loadItems.done(function(items) {
                    var newres = {};
                    for(var k in items) {
                        newres[items[k].id] = items[k];
                    }
                    var loadItems2 = getshopclient.BookingEngine.getBookingItems(getMultilevelName());
                    loadItems2.done(function(allitems) {
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
if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.guestInfoController = function($scope, $state, $stateParams) {
    var bookingid = $stateParams.bookingid;
    var roomid = $stateParams.roomid;
    $scope.sendToInvoiceButton = false;
    $scope.displaySendPaymentLink = false;
    $scope.unsettledAmount = 0;
    
    $scope.createOrderOnUnsettledAmount = function() {
        var loading = getshopclient.PmsInvoiceManager.createOrderOnUnsettledAmount(getMultilevelName(), $scope.booking.id);
        loading.done(function() {
            $scope.loadGuest();
        });
    }
    
    $scope.saveUser = function() {
        var saving = getshopclient.UserManager.saveUser($scope.user);
        saving.done(function() {
            alert('saved');
        });
    }
    
    $scope.makePayment = function(order) {
        $scope.hideAll();
        $scope.showMakePayment = true;
        $scope.payOrder = order;
        var loading = getshopclient.StoreApplicationPool.getActivatedPaymentApplications();
        loading.done(function(res) {
            $scope.pmethods = res;
            $scope.$apply();
        });
    }
    
    $scope.showSendPaymentLink = function(order) {
        $scope.hideAll();
        $scope.displaySendPaymentLink = true;
        $scope.selectedOrder = order;
    }
    
    $scope.sendPaymentLink = function(order) {
        var orderId = $scope.selectedOrder.id;
        var bookingId = $scope.booking.id;
        var email = $scope.selectedEmail;
        var prefix = $scope.selectedPrefix;
        var phone = $scope.selectedPhone;
        
        var sending = getshopclient.PmsManager.sendPaymentLink(getMultilevelName(), orderId, bookingId, email, prefix, phone);
        sending.done(function() {
            alert('Payment link has been sent');
            $scope.displaySendPaymentLink = false;
            $scope.$apply();
        });
    },
    
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
        $scope.waitingForCode = true;
        generated.done(function(res) {
            $scope.room.code = res;
            $scope.room.addedToArx = true;
            $scope.waitingForCode = false;
            $scope.$apply();
        });
    },
    $scope.forceGrantAccess = function() {
        $scope.room.forceAccess = true;
        getshopclient.PmsManager.saveBooking(getMultilevelName(), $scope.booking);
    },
    
    $scope.markOrderAsInvoice = function() {
        var orderId = $scope.payOrder.id;
        var marking = getshopclient.OrderManager.markAsInvoicePayment(orderId);
        marking.done(function() {
            var newOrder = getshopclient.OrderManager.getOrder(orderId);
            newOrder.done(function(order) {
                order.invoiceNote = $scope.payOrder.invoiceNote;
                $scope.payOrder = order;
                getshopclient.OrderManager.saveOrder($scope.payOrder);
                $scope.changeOrderStatus(7);
            });
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
            if(res.length != 0) {
                alert('Stay has been changed');
                $scope.loadGuest();
            } else {
                alert('Unable to update stay, room is not available.');
            }
        });
    }
    $scope.saveAddons = function() {
        var updating = getshopclient.PmsManager.updateAddons(getMultilevelName(), $scope.room.addons, $scope.booking.id);
        updating.done(function() {
            $scope.loadGuest();
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
        $scope.prefixtosendcodeto = $scope.room.guests[0].prefix;
        $scope.numbertosendcodeto = $scope.room.guests[0].phone;
        $scope.resendcode = true;
    },
    $scope.resendCode = function() {
        var number = $scope.numbertosendcodeto;
        var roomId = $scope.room.pmsBookingRoomId;
        var prefix = $scope.prefixtosendcodeto;
        var sending = getshopclient.PmsManager.sendCode(getMultilevelName(), prefix, number, roomId);
        sending.done(function() {
            alert('Code has been sent');
            $scope.resendcode = false;
//            $scope.room.addedToArx = false;
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
    
    $scope.blockAccess = function() {
        var confirmed = confirm("Are you sure you want to block access for this room?");
        if(confirmed) {
            for(var offset in $scope.booking.rooms) {
                var room = $scope.booking.rooms[offset];
                if(room.pmsBookingRoomId === roomid) {
                    room.blocked = true;
                }
            }
            getshopclient.PmsManager.saveBooking(getMultilevelName(), $scope.booking);
        }
    }
    
    $scope.unblockAccess = function() {
        var confirmed = confirm("Are you sure you want to unblock access for this room?");
        if(confirmed) {
            for(var offset in $scope.booking.rooms) {
                var room = $scope.booking.rooms[offset];
                if(room.pmsBookingRoomId === roomid) {
                    room.blocked = false;
                }
            }
            getshopclient.PmsManager.saveBooking(getMultilevelName(), $scope.booking);
        }
    },
    $scope.markOrderAsPaid = function() {
        var changed = getshopclient.OrderManager.changeOrderType($scope.payOrder.id, $scope.selectedPaymentApp);
        changed.done(function(res) {
            var getorder = getshopclient.OrderManager.getOrder($scope.payOrder.id);
            getorder.done(function(res) {
                res.invoiceNote = $scope.noteOnInvoice;
                res.status = 7;
                var saved = getshopclient.OrderManager.saveOrder(res);
                saved.done(function() {
                    alert('Action completed');
                    $scope.loadGuest();
                    $scope.hideAll();
                    $scope.$apply();
                });
            });
        });
    },
    $scope.hideAll = function() {
        $scope.displaySendPaymentLink = false;
        $scope.showMakePayment = false;
        $scope.sendRecieptProcess  = false;
        $scope.payOrderProcess = false;
    },
            
    $scope.sendToInvoiceButton = function(order) {
        $scope.hideAll();
        if($scope.payOrderProcess) {
            $scope.payOrderProcess = false;
            return;
        }
        $scope.sendRecieptProcess = false;
        $scope.payOrder = order;
        $scope.payOrderProcess = true;
    },
    
    $scope.addAddon = function() {
        var type = $scope.selectedAddon.type;
        var roomId = $scope.room.pmsBookingRoomId;

        var add = getshopclient.PmsManager.addAddonsToBooking(getMultilevelName(), type, roomId, false);
        add.done(function() {
            $scope.loadGuest();
        });
    }
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
    }
    $scope.removeAddon = function(addon) {
        var confirmed = confirm("Are you sure you want to remove this addon?");
        if(confirmed) {
            var remove = getshopclient.PmsManager.removeAddonFromRoom(getMultilevelName(), addon.addonId, $scope.room.pmsBookingRoomId);
            remove.done(function() {
                $scope.loadGuest();
            });
        }
    }
    $scope.loadAddonsList = function() {
        var productsLoading = getshopclient.ProductManager.getAllProductsLight(getMultilevelName());
        productsLoading.done(function(products) {
            var prodMap = {};
            for(var k in products) {
                var prod = products[k];
                prodMap[prod.id] = prod;
            }

            var loading = getshopclient.PmsManager.getAddonsWithDiscount(getMultilevelName(), $scope.room.pmsBookingRoomId);
            var simpleAddonsList = {};
            loading.done(function(test) {
                for(var k in test) {
                    var addon = test[k];
                    simpleAddonsList[addon.addonId] = {};
                    if(prodMap[addon.productId]) {
                        simpleAddonsList[addon.addonId].name = prodMap[addon.productId].name;
                        simpleAddonsList[addon.addonId].type = addon.addonType;
                        simpleAddonsList[addon.addonId].productId = addon.productId;
                    }
                }
                console.log(simpleAddonsList);
                $scope.addonsList = simpleAddonsList;
                $scope.$apply();
            });
        });
    }
    
    $scope.loadGuest = function() {
        $scope.loading = true;
        var booking = getshopclient.PmsManager.getBooking(getMultilevelName(), bookingid);
        
        booking.done(function(res) {
            $scope.loading = false;
            $scope.orders = [];
            $scope.unsettledAmount = res.unsettled;
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
                    var loadTypes = getshopclient.BookingEngine.getBookingItemTypes(getMultilevelName());
                    loadTypes.done(function(alltypes) {
                        var typesIndexed = {};
                        for(var k in alltypes) {
                            var type = alltypes[k];
                            typesIndexed[type.id] = type.name;
                        }
                        
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
                                    allitems[key].bookingItemName += " | " + typesIndexed[allitems[key].bookingItemTypeId];
                                }
                                $scope.items = allitems;
                                $scope.$apply();
                            });
                        });
                    });

                }
            }
            $scope.noAddons = false;
            if($scope.room.addons.length === 0) {
                $scope.noAddons = true;
            } else {
                var products = getshopclient.ProductManager.getAllProducts();
                products.done(function(res) {
                    $scope.products = res;
                    for(var key in $scope.room.addons) {
                        var item = $scope.room.addons[key];
                        item.dateFormatted = $scope.formatDate(new Date(item.date));
                        for(var prodKey in res) {
                            var product = res[prodKey];
                            if(product.id == item.productId) {
                                item.productName = product.name;
                            }
                        }
                    }
                    $scope.$apply();
                })

            }

            $scope.booking = res;
            $scope.loadAddonsList();
            $scope.$apply();
            var loadingUser = getshopclient.UserManager.getUserById(res.userId);
            loadingUser.done(function(user) {
                $scope.user = user;

                $scope.selectedEmail = user.emailAddress;
                $scope.selectedPrefix = user.prefix;
                $scope.selectedPhone = user.cellPhone;

                $scope.$apply();
            });

        });
    };
    $scope.loadGuest();
};
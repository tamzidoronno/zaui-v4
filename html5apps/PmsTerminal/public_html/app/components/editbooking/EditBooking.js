if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.EditBookingController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    
    $scope.types = {};
    $scope.doChangeRoomType = {};
    
    $scope.init = function($api) {
        if(jQuery.isEmptyObject(datarepository)) {
            datarepository = JSON.parse(localStorage.getItem("datarep"));
        }
        localStorage.setItem("datarep", JSON.stringify(datarepository));
        $scope.loadBooking();
        $scope.privatePersonSelected = true;
        $scope.showPrivatePersonInformation();
    };
    
    $scope.findCity = function() {
        if($scope.user.address.postCode.length === 4) {
            $.get("https://api.bring.com/shippingguide/api/postalCode.json?clientUrl=insertYourClientUrlHere&country=no&pnr="+$scope.user.address.postCode, null, function(res) {
                if(res.valid) {
                    $scope.user.address.city = res.result;
                    $scope.$apply();
                }
            });
        }
    };
    
    $scope.updateUserInfo = function(event, index,room, type) {
        if(index !== 0) {
            return;
        }
        if(room.pmsBookingRoomId !== $scope.booking.rooms[0].pmsBookingRoomId) {
            return;
        }
        if(type !== "prefix") {
            var val = $(event.srcElement).val();
        }
        if(type === "guest") { $scope.user.fullName = val; }
        if(type === "email") { $scope.user.emailAddress = val; }
        if(type === "prefix") { $scope.user.prefix = $('select[identifier="0_'+room.pmsBookingRoomId+'"]').val(); }
        if(type === "phone") { $scope.user.cellPhone = val; }
    };
    
    $scope.changeRoomTypeOnRoom = function(room, type) {
        showWaitingOverLay();
        var change = $api.getApi().PmsPaymentTerminal.changeRoomTypeOnRoom($api.getDomainName(), room.pmsBookingRoomId, type.id);
        change.done(function(res) {
            hideWaitingOverLay();
            $scope.doChangeRoomType = {};
            room.bookingItemTypeId = type.id;
            room.totalCost = res.totalCost;
            room.maxNumberOfGuests = res.maxNumberOfGuests;
            if(room.numberOfGuests > room.maxNumberOfGuests) {
                room.numberOfGuests = res.numberOfGuests;
            }
            $scope.$apply();
        });
    };
    
    $scope.changeTypeOnRoom = function(room) {
        var loadAvailableTypes = $api.getApi().PmsPaymentTerminal.getRoomTypesThatRoomCanBeChangedTo($api.getDomainName(), room.pmsBookingRoomId);
        $scope.availableMap = {};
        $scope.doChangeRoomType = {};
        loadAvailableTypes.done(function(res) {
            for(var k in res) {
                $scope.availableMap[k] = {
                    "name": $scope.types[k].name,
                    "count" : res[k],
                    "id" : k,
                    "size" : $scope.types[k].size
                }
            }
            $scope.doChangeRoomType[room.pmsBookingRoomId] = true;
            $scope.$apply();
        });
    };
    
    $scope.showPrivatePersonInformation = function() {
        $scope.profileselected = "private";
        $scope.privateSelected = "profileSelected";
        $scope.companySelected = "";
    };
    
    $scope.showCompanyInformation = function() {
        $scope.profileselected = "company";
        $scope.privateSelected = "";
        $scope.companySelected = "profileSelected";
    };
    
    $scope.loadBooking = function() {
        showWaitingOverLay();
        var loadTypes = $api.getApi().BookingEngine.getBookingItemTypes($api.getDomainName());
        loadTypes.done(function(res) {
            for(var k in res) {
                var type = res[k];
                $scope.types[type.id] = type;
            }
            
            var loadbooking = $api.getApi().PmsManager.getBooking($api.getDomainName(), datarepository.bookingid);
            loadbooking.done(function(res) {
                $scope.booking = res;
                
                if(!$scope.booking.countryCode) {
                    $scope.booking.countryCode = "NO";
                }
                
                $scope.$apply();
                if($scope.booking.userId) {
                    var loadUser = $api.getApi().UserManager.getUserById($scope.booking.userId);
                    loadUser.done(function(res) {
                        $scope.user = res;
                        $scope.user.cellPhone = parseInt($scope.user.cellPhone);
                        if(!res.isCompanyMainContact) {
                            $scope.showPrivatePersonInformation();
                        } else {
                            $scope.showCompanyInformation();
                        }
                        hideWaitingOverLay();
                        $scope.$apply();
                    });
                } else {
                    hideWaitingOverLay();
                }
            });
        });
    };
    
    $scope.decrementGuestCount = function(room) {
        room.numberOfGuests--;
        if(room.numberOfGuests < 1) {
            room.numberOfGuests = 1;
        }
        
        var loadNewPrice = $api.getApi().PmsPaymentTerminal.changeGuestCountOnRoom($api.getDomainName(), room.pmsBookingRoomId, room.numberOfGuests);
        loadNewPrice.done(function(res) {
            room.totalCost = res;
            $scope.$apply();
        });
    };
    
    $scope.getRoomType = function(typeId) {
        return $scope.types[typeId].name;
    };
    
    
    $scope.incrementGuestCount = function(room) {
        room.numberOfGuests++;
        if(room.numberOfGuests > room.maxNumberOfGuests) {
            room.numberOfGuests = room.maxNumberOfGuests;
        }
        var loadNewPrice = $api.getApi().PmsPaymentTerminal.changeGuestCountOnRoom($api.getDomainName(), room.pmsBookingRoomId, room.numberOfGuests);
        loadNewPrice.done(function(res) {
            console.log(res);
            room.totalCost = res;
            $scope.$apply();
        });
    };
    
    $scope.numberOfGuests= function(room) {
        return new Array(room.numberOfGuests);
    };
    
    $scope.continueToCheckout = function() {
        showWaitingOverLay();
        var company = $scope.company;
        if(!company) {
            company = null;
        }
        var user = $scope.user;
        if(!user) {
            user = null;
        }
        var updateBooking = $api.getApi().PmsPaymentTerminal.updateBooking($api.getDomainName(), $scope.booking, user, company);
        updateBooking.done(function(success) {
            if(success) {
                $state.transitionTo('base.checkout');
            } else {
                $scope.failed = true;
            }
            hideWaitingOverLay();
        });
    };
    
    $scope.init($api);
};

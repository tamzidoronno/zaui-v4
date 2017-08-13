if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.EditBookingController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    
    $scope.types = {};
    
    $scope.init = function($api) {
        if(jQuery.isEmptyObject(datarepository)) {
            datarepository = JSON.parse(localStorage.getItem("datarep"));
        }
        localStorage.setItem("datarep", JSON.stringify(datarepository));
        $scope.loadBooking();
        $scope.privatePersonSelected = false;
        $scope.companySelected = false;
        
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
        var loadTypes = $api.getApi().BookingEngine.getBookingItemTypes($api.getDomainName());
        loadTypes.done(function(res) {
            for(var k in res) {
                var type = res[k];
                $scope.types[type.id] = type;
            }
            
            var loadbooking = $api.getApi().PmsManager.getBooking($api.getDomainName(), datarepository.bookingid);
            loadbooking.done(function(res) {
                $scope.booking = res;
                $scope.$apply();
                if($scope.booking.userId) {
                    var loadUser = $api.getApi().UserManager.getUserById($scope.booking.userId);
                    loadUser.done(function(res) {
                        $scope.user = res;
                        if(!res.isCompanyMainContact) {
                            $scope.showPrivatePersonInformation();
                        } else {
                            $scope.showCompanyInformation();
                        }
                        $scope.$apply();
                    });
                }
            });
        });
    };
    
    $scope.decrementGuestCount = function(room) {
        room.numberOfGuests--;
        if(room.numberOfGuests < 1) {
            room.numberOfGuests = 1;
        }
    };
    
    $scope.getRoomType = function(typeId) {
        return $scope.types[typeId].name;
    };
    
    
    $scope.incrementGuestCount = function(room) {
        room.numberOfGuests++;
        if(room.numberOfGuests > room.maxNumberOfGuests) {
            room.numberOfGuests = room.maxNumberOfGuests;
        }
    };
    
    $scope.numberOfGuests= function(room) {
        return new Array(room.numberOfGuests);
    };
    
    $scope.continueToCheckout = function() {
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
        });
    };
    
    $scope.init($api);
};

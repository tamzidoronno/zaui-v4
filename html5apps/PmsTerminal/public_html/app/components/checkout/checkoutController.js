/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.checkoutController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    
    $scope.init = function($api) {
        if(jQuery.isEmptyObject(datarepository)) {
            datarepository = JSON.parse(localStorage.getItem("datarep"));
        }
        localStorage.setItem("datarep", JSON.stringify(datarepository));
        $scope.load();
    };
    $scope.load = function() {
        $scope.loadSummary();
        $scope.loadOrders();
    }
    
    $scope.loadOrders = function() {
        var loadingorders = $api.getApi().PmsPaymentTerminal.getOrderSummary($api.getDomainName(), datarepository.bookingid);
        loadingorders.done(function(res) {
            $scope.orders = res;
        });
    };
    
    $scope.startPaymentProcess = function() {
        $('.paymentprocess').fadeIn();
    };
    
    $scope.cancelPayment = function() {
        $('.paymentprocess').fadeOut();
    };
    
    $scope.doPayIndividually = function() {
       $scope.payIndividually = true;
    };
    
    $scope.loadAdditionalServices = function() {
        var booking = $scope.booking;
        $scope.addons = {};
        for(var key in booking.rooms) {
            var room = booking.rooms[key];
                (function (room) {
                    var loadAddon = $api.getApi().PmsManager.getAddonsWithDiscountForBooking($api.getDomainName(), room.pmsBookingRoomId);
                    loadAddon.done(function(res) {
                        $scope.addons[room.pmsBookingRoomId] = res;
                        $scope.$apply();
                        console.log($scope.addons);
                    });
            })(room);
        }
    };
    
    $scope.loadUser = function(userId) {
        var loadUser = $api.getApi().UserManager.getUserById(userId);
        loadUser.done(function(res) {
            $scope.user = res;
            $scope.$apply();
        });
    };
    
    $scope.removeAddonFromRoom = function(addon, room) {
        console.log(room.pmsBookingRoomId);
        $api.getApi().PmsPaymentTerminal.removeProductFromRoom($api.getDomainName(), addon.productId, room.pmsBookingRoomId);
        $scope.load();
    };
    
    $scope.addAddonToRoom = function(addon, room) {
        console.log($api.getApi().PmsPaymentTerminal);
        $api.getApi().PmsPaymentTerminal.addProductToRoom($api.getDomainName(), addon.productId, room.pmsBookingRoomId);
        $scope.load();
    };
    
    $scope.loadSummary = function() {
        var loadTypes = $api.getApi().BookingEngine.getBookingItemTypes($api.getDomainName());
        loadTypes.done(function(types) {
            var typesToLoad = {};
            for(var k in types) {
                var type = types[k];
                typesToLoad[type.id] = type;
            }
            $scope.types = typesToLoad;
            var bookingid = datarepository.bookingid;
            var loadbooking = $api.getApi().PmsManager.getBooking($api.getDomainName(), bookingid);
            loadbooking.done(function(booking) {
                console.log(booking.rooms);
                $scope.numberOfRoomsBooked = booking.rooms.length;
                $scope.booking = booking;
                $scope.loadUser(booking.userId);
                $scope.loadAdditionalServices();
            });
        });
    };
    
    $scope.init();
};

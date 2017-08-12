/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.StartBookingController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    $scope.roomcount = 0;
    $scope.errorOccured = false;
    $scope.nightcount = 0;
    
    $scope.init = function($api) {
        $scope.guestcount = {};
        for(var i = 0; i < 100; i++) {
            $scope.guestcount[i] = 0;
        }
    };
    
    $scope.incrementRooms = function() {
        $scope.roomcount++;
    };
    
    $scope.decrementRooms = function() {
        $scope.roomcount--;
        if($scope.roomcount < 0) {
            $scope.roomcount = 0;
        }
    };
    
    $scope.getRoomCount = function() {
        return new Array($scope.roomcount);
    };
    
    $scope.incrementNights = function() {
        $scope.nightcount++;
    };
    
    $scope.incrementGuests = function(index) {
        $scope.guestcount[index]++;
    };
    
    $scope.decrementNights = function() {
        $scope.nightcount--;
        if($scope.nightcount < 0) {
            $scope.nightcount = 0;
        }
    };
    
    $scope.decrementGuests = function(index) {
        $scope.guestcount[index]--;
        if($scope.guestcount[index] < 0) {
            $scope.guestcount[index] = 0;
        }
    };
    
    
    $scope.startBooking = function() {
        var startObject = {};
        
        $scope.errorOccured = false;
        if($scope.roomcount === 0) {
            $scope.errorOccured = true;
        };
        if($scope.nightcount === 0) {
            $scope.errorOccured = true;
        };
        for(var i = 0; i < $scope.roomcount; i++) {
            if($scope.guestcount[i] === 0) {
                $scope.errorOccured = true;
            }
        };
        
        if(!$scope.errorOccured) {
            startObject.numberOfRooms = $scope.roomcount;
            startObject.numberOfNights = $scope.nightcount;
            startObject.guestPerRoom = {};
            for(var i = 0; i < $scope.roomcount; i++) {
                startObject.guestPerRoom[i] = $scope.guestcount[i];
            }
            var startbooking = $api.getApi().PmsPaymentTerminal.startBooking($api.getDomainName(), startObject);
            startbooking.done(function(res) {
                datarepository.bookingid = res.id;
                $state.transitionTo('base.editbooking');
            });
        };
    };
    
    $scope.init($api);
};

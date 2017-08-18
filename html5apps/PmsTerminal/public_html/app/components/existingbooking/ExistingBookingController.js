/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.ExistingBookingController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    
    $scope.init = function($api) {
    }
    
    
    
    $scope.continueToCheckout = function(searcresult) {
        datarepository.bookingid = searcresult.id;
        $state.transitionTo('base.checkout');
    };
    
    $scope.searchForBooking = function() {
        var finding = $api.getApi().PmsPaymentTerminal.findBookings($api.getDomainName(), $scope.phonenumber);
        finding.done(function(res) {
            $('.step2').hide();
            if(res.length === 0) {
                $('.step2notfound').show();
            } else {
                $('.step2found').show();
                $scope.searchresult = res;
                $scope.$apply();
            }
        });
    }
    
    $scope.init($api);
};

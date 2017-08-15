if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.HomeController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    
    $scope.init = function($api) {
    };
    
    $scope.goToBooking = function() {
        $state.transitionTo('base.existingbooking');
    };
    
    $scope.startNewBooking = function() {
        $state.transitionTo('base.startbooking');
    };
    
    $scope.init($api);
};

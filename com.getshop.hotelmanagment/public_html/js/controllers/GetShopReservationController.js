var GetShopReservationController = function($scope, $routeParams) {
    $scope.domainId = $routeParams.domainId;
    
    $scope.countries = gs_countries;
    
    $scope.init = function() {
        GetShop.api.HotelBookingManager.getDomain($scope.domainId).done($scope.domainFetched);  
    };
    
    $scope.domainFetched = function(domain) {
        $scope.domainObj = domain;
        $scope.$apply();
    }
    
    $scope.init();
};
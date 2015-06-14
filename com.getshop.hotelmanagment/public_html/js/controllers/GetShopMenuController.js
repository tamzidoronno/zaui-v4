var GetShopMenuController = function($scope, $http, $location) {
    $scope.domainsForTopMenu = [{
        name : 'test'
    }];
        
    $scope.refreshDomains = function() {
        GetShop.api.HotelBookingManager.getDomains().done($scope.domainsFetched);
    };
    
    $scope.domainsFetched  = function(result) {
        $scope.domainsForTopMenu = result;
        $scope.$apply();
        console.log($scope.domains);
//        $scope.$apply();
    }
    
    $scope.$on('refreshDomains', $scope.refreshDomains);
    $scope.refreshDomains();
}
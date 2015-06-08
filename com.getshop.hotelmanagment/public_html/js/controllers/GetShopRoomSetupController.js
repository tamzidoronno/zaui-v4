var GetShopRoomSetupController = function($scope, $http, $location) {
    $scope.domain = {};
    $scope.domains = [];
    $scope.active_room_setup = "active";
    var loading = null;
    
        
    $scope.$on('$viewContentLoaded', function(){
        loading = false;
    });
    
    $scope.activateTab = function(value) {
        $scope.tabToShow = value;
        console.log(value);
    }

    $scope.domainCreated = function() {
        $scope.getDomains();
    };
    
    $scope.getDomains = function() {
        GetShop.api.HotelBookingManager.getDomains($scope.domain.name, $scope.domain.description).done($scope.domainsReceived);
    }
    
    $scope.domainsReceived = function(domains) {
        $scope.domains = domains;
        $scope.$apply(); 
    };
    
    $scope.createDomain = function() {
        GetShop.api.HotelBookingManager.createDomain($scope.domain.name, $scope.domain.description).done($scope.domainCreated);
    }; 
    
    $scope.createRoomType = function() {
        debugger;
    };
   
    $scope.getDomains();
};
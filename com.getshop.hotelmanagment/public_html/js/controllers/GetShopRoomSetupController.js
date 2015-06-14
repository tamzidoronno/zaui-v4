var GetShopRoomSetupController = function($scope, $routeParams) {
    $scope.domain = {};
    $scope.domains = [];
    $scope.active_room_setup = "active";
    $scope.tabToShow = $routeParams.domainId;
    
    $scope.activateTab = function(value) {
        $scope.tabToShow = value;
        
        $scope.fetchRooms();
    }
    
    $scope.fetchRooms = function() {
        GetShop.api.HotelBookingManager.getRooms($scope.tabToShow).done($scope.roomFetched);
    }
    
    $scope.roomFetched = function(rooms) {
        $scope.rooms = rooms;
        $scope.$apply();
        console.log($scope.rooms);
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
        
        if (!$scope.tabToShow && $scope.domains.length > 0) {
            $scope.activateTab($scope.domains[0].id);
        }
    };
    
    $scope.createDomain = function() {
        GetShop.api.HotelBookingManager.createDomain($scope.domain.name, $scope.domain.description).done($scope.domainCreated);
    }; 
    
    $scope.getRoomTypes = function() {
        GetShop.api.HotelBookingManager.getRoomTypes()
    };
   
       
    $scope.deleteRoom = function(room) {
        GetShop.api.HotelBookingManager.deleteRoom($scope.tabToShow, room.id).done($scope.activateTab($scope.tabToShow));
    }
    
    $scope.deleteDomain = function(domainId) {
        GetShop.api.HotelBookingManager.deleteDomain(domainId).done($scope.domainDeleted);
    }
    
    $scope.domainDeleted = function() {
        window.location = '#/roomsetup';
    }
    
    $scope.getDomains();
    if ($scope.tabToShow) {
        $scope.activateTab($scope.tabToShow);
    }
};
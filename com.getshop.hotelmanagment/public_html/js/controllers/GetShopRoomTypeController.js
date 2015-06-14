var GetShopRoomTypeController = function($scope, $routeParams) {
    
    $scope.domainId = $routeParams.id;
    $scope.roomTypeId = $routeParams.roomTypeId;
    
    $scope.roomtype = {
        name : null,
        domain : null,
        price : null,
        size : null
    };
    
    $scope.createRoomType = function() {
        GetShop.api.HotelBookingManager.createRoomType($scope.domainId, $scope.roomtype.name, $scope.roomtype.price, $scope.roomtype.size).done($scope.fetchRoomTypes);
    };
    
    
    $scope.init = function() {
        GetShop.api.HotelBookingManager.getDomain($scope.domainId).done($scope.domainFetched);  
    };
    
    $scope.fetchRoomTypes = function() {
        GetShop.api.HotelBookingManager.getRoomTypes($scope.domainId).done($scope.roomTypesFetched);
    };
    
    $scope.roomTypesFetched = function(roomTypes) {
        $scope.roomTypes = roomTypes;
        $scope.$apply();
    }
    
    $scope.domainFetched = function(domain) {
        $scope.domainObj = domain;
        $scope.$apply();
    }
    
    $scope.deleteRoomType = function(roomTypeId) {
        GetShop.api.HotelBookingManager.deleteRoomType($scope.domainId, roomTypeId).done($.proxy($scope.fetchRoomTypes, $scope));
    }
    
    $scope.fetchRoomType = function() {
        GetShop.api.HotelBookingManager.getRoomType($scope.domainId, $scope.roomTypeId).done(function(result) {
            $scope.roomtype = result;
            $scope.$apply();
        });
        
    }
    
    $scope.init();
    $scope.fetchRoomTypes();
    
    
    
    if ($scope.roomTypeId) {
        $scope.fetchRoomType();
    }
}
var GetShopRoomController = function($scope, $routeParams) {
    $scope.room = {
        roomNumber : ""
    };
    
    $scope.domainId = $routeParams.domainId;
    
    $scope.createRoom = function() {
        var room = {
            name : this.room.roomNumber,
            domainId : this.domainId,
            roomTypeId : $scope.room.roomType.id
        }
        
        GetShop.api.HotelBookingManager.saveRoom(room).done(function() {
            window.location = '#/roomsetup/activetab/'+$scope.domainId;
        });
    };
    
    $scope.fetchRoomTypes = function() {
        GetShop.api.HotelBookingManager.getRoomTypes($scope.domainId).done($scope.roomTypesFetched);
    }

    
    $scope.roomTypesFetched = function(result) {
        $scope.roomTypes = result;
        $scope.$apply();
    }
    
    $scope.fetchRoomTypes();    
};
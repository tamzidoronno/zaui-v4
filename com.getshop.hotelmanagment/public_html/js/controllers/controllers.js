

/* Controllers */
var getShopControllers = angular.module('getShopHotelManagementControllers', []);

getShopControllers.controller('GetShopLoginController', ['$scope', '$http', '$location', GetShopLoginController]);
getShopControllers.controller('GetShopMainViewController', ['$scope', '$http', '$location', GetShopMainViewController]);
getShopControllers.controller('GetShopRoomSetupController', ['$scope', '$routeParams', GetShopRoomSetupController]);
getShopControllers.controller('GetShopReportsController', ['$scope', '$http', '$location', GetShopReportsController]);
getShopControllers.controller('GetShopRoomTypeController', ['$scope', '$routeParams', GetShopRoomTypeController]);
getShopControllers.controller('GetShopRoomController', ['$scope', '$routeParams', GetShopRoomController]);
getShopControllers.controller('GetShopMenuController', ['$scope', '$routeParams', GetShopMenuController]);
getShopControllers.controller('GetShopReservationController', ['$scope', '$routeParams', GetShopReservationController]);
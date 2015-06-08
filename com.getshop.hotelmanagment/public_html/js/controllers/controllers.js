

/* Controllers */
var getShopControllers = angular.module('getShopHotelManagementControllers', []);

getShopControllers.controller('GetShopLoginController', ['$scope', '$http', '$location', GetShopLoginController]);
getShopControllers.controller('GetShopMainViewController', ['$scope', '$http', '$location', GetShopMainViewController]);
getShopControllers.controller('GetShopRoomSetupController', ['$scope', '$http', '$location', GetShopRoomSetupController]);
getShopControllers.controller('GetShopReportsController', ['$scope', '$http', '$location', GetShopReportsController]);
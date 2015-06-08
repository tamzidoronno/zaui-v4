GetShop = {
    api : null,
    loggedInUserId: sessionStorage.getItem("user.id"),
    
    connect: function(connected) {
        if (GetShop.api) {
            if (GetShop.api.connectionEstablished) {
                connected();
            }
            return;
        }
        
        var webAddress = sessionStorage.getItem("login.webaddress");
        if (webAddress) {
            $('.waitingforconenction').show();
            GetShop.api = new GetShopApiWebSocket(webAddress);
            GetShop.api.setConnectedEvent(function() {
                $('.waitingforconenction').hide();
                connected();
            });    
            
            GetShop.api.connect();
        } else {
            connected();
        }
    }
};

GetShop.connect(function() {
    
}); 

var getShopHotelManagement = angular.module('GetShopHotelManagement', [
  'ngRoute',
  'getShopHotelManagementControllers'
]);

getShopHotelManagement.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'GetShopLoginController'
      }).
      when('/mainview', {
        templateUrl: 'partials/mainview.html',
        controller: 'GetShopMainViewController'
      }).
      when('/reports', {
        templateUrl: 'partials/reports.html',
        controller: 'GetShopReportsController'
      }).
      when('/roomsetup', {
        templateUrl: 'partials/roomsetup.html',
        controller: 'GetShopRoomSetupController'
      }).
      when('/roomsetup/manageroomtypes/:id', {
        templateUrl: 'partials/roomtypes.html',
        controller: 'GetShopRoomSetupController'
      }).
      otherwise({
        redirectTo: '/mainview'
      });
  }]
)
.run( function($rootScope, $location) {
    $rootScope.$on( "$routeChangeStart", function(event, next, current) {
        if (GetShop.api == null || GetShop.loggedInUserId == false) {
            $location.path( "/login" );
        }    
    });
    
    $rootScope.$on('$routeChangeSuccess', function(ev,data) {   
        $('.headnavbar li').removeClass('active');
    })
 });

// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
angular.module('arxapp', ['ionic', 'ArxAppControllers'])


.run(function($ionicPlatform, GetshopService) {
  $ionicPlatform.ready(function() {
    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)
    if(window.cordova && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
    }
    if(window.StatusBar) {
      StatusBar.styleDefault();
    }
  });

  GetshopService.connectToGetshop();
})


// Setup view states
.config(function($stateProvider, $urlRouterProvider) {

  $stateProvider
    .state('login', {
      url: "/login",
      templateUrl: "login.html",
      controller: 'LoginCtrl'
    })
    .state('menu', {
      url: "/menu",
      templateUrl: "menu.html",
      controller: 'MenuCtrl'
    })
    .state('users', {
      url: "/users",
      templateUrl: "users.html",
      controller: 'UsersCtrl'
    })
    .state('user-detail', {
      url: "/users/:userId",
      templateUrl: "user-detail.html",
      controller: 'UserDetailCtrl'
    })
    .state('doors', {
      url: "/doors",
      templateUrl: "doors.html",
      controller: 'DoorsCtrl'
    })
    .state('door-detail', {
      url: "/door?id&from&to",
      templateUrl: "door-detail.html",
      controller: "DoorDetailCtrl"
    });

    $urlRouterProvider.otherwise("/login");

});


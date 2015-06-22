// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
angular.module('starter', ['ionic'])


.run(function($ionicPlatform) {
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
    .state('doors', {
      url: "/doors",
      templateUrl: "doors.html",
      controller: 'DoorsCtrl'
    });

    $urlRouterProvider.otherwise("/menu");

})

.controller('LoginCtrl', function($scope, LoginService, $ionicPopup, $state) {
  $scope.data = {};
 
  $scope.login = function() {
    LoginService.loginUser($scope.data.username, $scope.data.password).success(function(data) {
      $state.go('menu');
    }).error(function(data) {
      var alertPopup = $ionicPopup.alert({
        title: 'Login failed!',
        template: 'Please check your credentials!'
      });
    });
  }
})

.controller('MenuCtrl', function($scope) {})

.controller('UsersCtrl', function($scope) {
  $scope.users = [{
    firstName: "Ola",
    lastName: "Nordman",
    streetAddress: "Strandgaten 21",
    postNumber: "4830",
    post: "Hauge I Dalane",
    group: "16"
  },{
    firstName: "Kai",
    lastName: "Nordman",
    streetAddress: "Strandgaten 21",
    postNumber: "4830",
    post: "Hauge I Dalane",
    group: "5"
  },{
    firstName: "Emil",
    lastName: "Nordman",
    streetAddress: "Strandgaten 21",
    postNumber: "4830",
    post: "Hauge I Dalane",
    group: "5"
  },{
    firstName: "Ola",
    lastName: "Nordman",
    streetAddress: "Strandgaten 22",
    postNumber: "4830",
    post: "Hauge I Dalane",
    group: "5"
  },{
    firstName: "Ola",
    lastName: "Nordman",
    streetAddress: "Strandgaten 23",
    postNumber: "4830",
    post: "Hauge I Dalane",
    group: "5"
  },{
    firstName: "Ola",
    lastName: "Nordman",
    streetAddress: "Strandgaten 24",
    postNumber: "4830",
    post: "Hauge I Dalane",
    group: "5"
  },{
    firstName: "Ola",
    lastName: "Nordman",
    streetAddress: "Strandgaten 25",
    postNumber: "4830",
    post: "Hauge I Dalane",
    group: "5"
  },{
    firstName: "Ola",
    lastName: "Nordman",
    streetAddress: "Strandgaten 26",
    postNumber: "4830",
    post: "Hauge I Dalane",
    group: "5"
  },{
    firstName: "Ola",
    lastName: "Nordman",
    streetAddress: "Strandgaten 26",
    postNumber: "4830",
    post: "Hauge I Dalane",
    group: "5"
  }
  ];
})

.controller('DoorsCtrl', function($scope) {
  $scope.doors = [{
    name: "Room 1"
  },{
    name: "Room 2"
  },{
    name: "Room 3"
  },{
    name: "Door to the kitchen"
  },{
    name: "Back door"
  },{
    name: "Enterance"
  }
  ];
})

.service('LoginService', function($q) {
    return {
        loginUser: function(name, pw) {
            var deferred = $q.defer();
            var promise = deferred.promise;
 
            if (name == 'user' && pw == 'secret') {
                deferred.resolve('Welcome ' + name + '!');
            } else {
                deferred.reject('Wrong credentials.');
            }
            promise.success = function(fn) {
                promise.then(fn);
                return promise;
            }
            promise.error = function(fn) {
                promise.then(null, fn);
                return promise;
            }
            return promise;
        }
    }
});
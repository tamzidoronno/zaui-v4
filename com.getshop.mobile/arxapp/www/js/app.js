// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
angular.module('starter', ['ionic'])


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
    .state('doors', {
      url: "/doors",
      templateUrl: "doors.html",
      controller: 'DoorsCtrl'
    });

    $urlRouterProvider.otherwise("/login");

})

.controller('LoginCtrl', function($scope, LoginService, LocalStorage, $ionicPopup, $state) {

  function Credentials(host, username, password) {
    this.host = host;
    this.username = username;
    this.password = password;
  }

  // load credentials from local storage
  $scope.userData = LocalStorage.getObject('userData');
  if ($scope.userData.credentials === undefined) {
    $scope.userData.credentials = [];
  }
 
  // create data object that will store values from the view
  $scope.data = {};


  // define a function to login with stored credentials
  $scope.loginWithSavedCredentials = function() {
    // find credentials for selected host
    var i = -1;
    for (var index = 0; index < $scope.userData.credentials.length; index++) {
      if ($scope.userData.credentials[index].host == $scope.data.selectedHost) {
        i = index;
        break;
      }
    }
    if (i == -1) {
      var alertPopup = $ionicPopup.alert({
        title: 'Login failed!',
        template: "Can't find credentials for this host. Please re-enter it."
      });      
    } else {
      // try to login using saved credentials
      LoginService.loginUser($scope.userData.credentials[i].host, $scope.userData.credentials[i].username, $scope.userData.credentials[i].password).success(function(data) {
        // go to menu
        $state.go('menu');
      }).error(function(data) {
        var alertPopup = $ionicPopup.alert({
          title: 'Login failed!',
          template: "Those credentials don't work anymore!"
        });
      });      
    }
  }
 
  // define a function to login with new credentials given by the user
  $scope.login = function() {
    LoginService.loginUser($scope.data.enteredHost, $scope.data.username, $scope.data.password).success(function(data) {
      var index = 0;
      // check if we already have credentials for this host
      for (index; index < $scope.userData.credentials.length; index++) {
        if ($scope.userData.credentials[index].host == $scope.data.host)
          break; // credentials will be updated at this index
      }
      // add or update credentials
      $scope.userData.credentials[index] = new Credentials($scope.data.host, $scope.data.username, $scope.data.password);
      // save to local storage
      LocalStorage.setObject('userData', $scope.userData);
      // go to menu
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

.service('LoginService', ['GetshopService', '$q', function(getshop, $q) {
    return {
        loginUser: function(host, name, pw) {
            var deferred = $q.defer();
            var promise = deferred.promise;
 
            if (getshop.client.ArxManager.logonToArx(host, name, pw, false)) {
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
}])

.factory('GetshopService', ['$window', function($window) {
  return {

    client: null,

    connectToGetshop: function() {
      // Connect to getshop web api
      this.client = new GetShopApiWebSocket("arx.getshop.com");
      this.client.setConnectedEvent(function () {
        
      });
      this.client.connect();

    }
  }
}])

.factory('LocalStorage', ['$window', function($window) {
  return {
    set: function(key, value) {
      $window.localStorage[key] = value;
    },
    get: function(key, defaultValue) {
      return $window.localStorage[key] || defaultValue;
    },
    setObject: function(key, value) {
      $window.localStorage[key] = JSON.stringify(value);
    },
    getObject: function(key) {
      return JSON.parse($window.localStorage[key] || '{}');
    }
  }
}]);
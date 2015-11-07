function formatDate(d) {
    var month = d.getMonth()+1;
        if(month < 10) {
            month = "0" + month;
        }
        var day = d.getDate();
        if(day < 10) {
            day = "0" + day;
        }
        var hour = d.getHours();
        if(hour < 10) {
            hour = "0" + hour;
        }
        var min = d.getMinutes();
        if(min < 10) {
            min = "0" + min;
        }
        var secs = d.getSeconds();
        if(secs < 10) {
            secs = "0" + secs;
        }


        var dformat = [day,month,d.getFullYear()].join('.')+' '+
              [hour,min,secs].join(':');

        return dformat;
}

function formatDateEnglish(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}

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
    .state('about', {
      url: "/about",
      templateUrl: "about.html",
      controller: 'AboutCtrl'
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


angular.module('app', ['ui.router'])
.config(function($stateProvider, $urlRouterProvider) {
  $urlRouterProvider.otherwise("/login");
  //
  // Now set up the states
  $stateProvider
    .state('login', {
      url: "/login",
      templateUrl: "pages/login.html",
      controller: getshop.loginController
    })
    .state('mainpage', {
      url: "/mainpage",
      templateUrl: "pages/mainpage.html",
      controller: getshop.mainpageController
    })
    .state('guestinfo', {
        url: '/guestinfo/:bookingid/:roomid',
        templateUrl: "pages/guestinfo.html",
        controller: getshop.guestInfoController
    })
    .state('findguest', {
      url: "/findguest",
      templateUrl: "pages/findguest.html",
      controller: getshop.findguestController
    })
    .state('fire', {
      url: "/fire",
      templateUrl: "pages/fire.html",
      controller: getshop.fireController
    })
    .state('sms', {
      url: "/sms",
      templateUrl: "pages/sms.html",
      controller: getshop.smsController
    })
    .state('log', {
      url: "/log",
      templateUrl: "pages/log.html",
      controller: getshop.logController
    })
    .state('doors', {
      url: "/doors",
      templateUrl: "pages/doors.html",
      controller: getshop.doorsController
    })
    .state('doorlog', {
      url: "/doorlog/:externalId/:name",
      templateUrl: "pages/doorlog.html",
      controller: getshop.doorlogController
    })
    .state('otherinstruction', {
      url: "/otherinstruction",
      templateUrl: "pages/otherinstruction.html",
      controller: getshop.otherInstructionController
    });
})
.value('$routerRootComponent', 'app')

.component('app', {
  template:
    '<nav>\n' +
    '  <a ui-sref="state1">Crisis Center</a>\n' +
    '  <a ui-sref="state2">Heroes</a>\n' +
    '</nav>\n' +
    '<ng-outlet></ng-outlet>\n',
  $routeConfig: [
    {path: '/crisis-center/...', name: 'CrisisCenter', component: 'crisisCenter', useAsDefault: true},
    {path: '/heroes/...', name: 'Heroes', component: 'heroes' }
  ]
});

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
    .state('addonview', {
        url: '/addonview/:viewid',
        templateUrl: "pages/addonview.html",
        controller: getshop.addonviewController
    })
    .state('cleaningCheckout', {
        url: '/cleaning/checkout/:roomName',
        templateUrl: "pages/cleaningcheckout.html",
        controller: getshop.cleaningCheckoutController
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
    .state('cleaning', {
      url: "/cleaning",
      templateUrl: "pages/cleaning.html",
      controller: getshop.cleaningController
    })
    .state('caretaker', {
      url: "/caretaker",
      templateUrl: "pages/caretaker.html",
      controller: getshop.careTakerController
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
    .state('breakfast', {
      url: "/breakfast",
      templateUrl: "pages/breakfast.html",
      controller: getshop.breakfastController
    })
    .state('otherinstruction', {
      url: "/otherinstruction",
      templateUrl: "pages/otherinstruction.html",
      controller: getshop.otherInstructionController
    })
    .state('conference', {
      url: "/conference",
      templateUrl: "pages/conference.html",
      controller: getshop.conferenceController
    });
})
.value('$routerRootComponent', 'app')


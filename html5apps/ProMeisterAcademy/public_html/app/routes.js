angular.module('ProMeisterAcademy')
.config(['$stateProvider', function ($stateProvider) {
    $stateProvider.state('base', {
        url: '',
        templateUrl: 'components/general/main.html',
        abstract: true,
        controller : controllers.BaseController
    });
    
    $stateProvider.state('base.home', {
        url: '/home',
        views: {
            main: {
                templateUrl : 'components/home/home.html',
                controller : controllers.HomeController
            }
        }
    }),
    
    $stateProvider.state('base.login', {
        url: '/login',
        
        views: {
            main: {
                templateUrl : 'components/login/login.html',
                controller : controllers.LoginController
            }
        }
    });
    
    $stateProvider.state('base.signup', {
        url: '/signup',
        
        views: {
            main: {
                templateUrl : 'components/signup/signup.html',
                controller : controllers.SignupController
            }
        }
    });
    
    $stateProvider.state('base.signupToEvent', {
        url: '/signup/:eventId',
        
        views: {
            main: {
                templateUrl : 'components/signup/signupEvent.html',
                controller : controllers.SignupController
            }
        }
    });
   
    $stateProvider.state('base.overview', {
        url: '/overview',
        
        views: {
            main: {
                templateUrl : 'components/overview/overview.html',
                controller : controllers.OverviewController
            }
        }
    });
    
    $stateProvider.state('base.contact', {
        url: '/contact',
        
        views: {
            main: {
                templateUrl : 'components/contact/contact.html',
                controller : controllers.ContactController
            }
        }
    });
    
    $stateProvider.state("base.pagenotfound", {
        url: "*path",
        views: {
            main: {
                templateUrl: "components/general/pagenotfound.html"
            }
        }
    });
}]);

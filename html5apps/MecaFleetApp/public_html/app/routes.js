angular.module('MecaFleetApp')
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
    
    $stateProvider.state('base.caroverview', {
        url: '/car',
        
        views: {
            main: {
                templateUrl : 'components/caroverview/caroverview.html',
                controller : controllers.CarOverviewController
            }
        }
    });
    
    $stateProvider.state('base.contact', {
        url: '/contact',
        
        views: {
            main: {
                templateUrl : 'components/contact/main.html',
                controller : controllers.ContactController
            }
        }
    });
    
    $stateProvider.state('base.veihjelp', {
        url: '/contact/veihjelp',
        
        views: {
            main: {
                templateUrl : 'components/contact/veihjelp.html',
                controller : controllers.ContactController
            }
        }
    });
    
    $stateProvider.state('base.ringmeg', {
        url: '/contact/ringmeg',
        
        views: {
            main: {
                templateUrl : 'components/contact/ringmeg.html',
                controller : controllers.ContactController
            }
        }
    });
    
    $stateProvider.state('base.mail', {
        url: '/contact/mail',
        
        views: {
            main: {
                templateUrl : 'components/contact/mail.html',
                controller : controllers.ContactController
            }
        }
    });
    
    $stateProvider.state('base.sendkm', {
        url: '/sendkm',
        
        views: {
            main: {
                templateUrl : 'components/kilometers/send.html',
                controller : controllers.KilometersController
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

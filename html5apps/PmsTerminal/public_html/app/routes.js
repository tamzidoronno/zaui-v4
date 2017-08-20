angular.module('TrackAndTrace')
.config(['$stateProvider', function ($stateProvider) {
    $stateProvider.state('base', {
        url: '',
        templateUrl: 'components/general/main.html',
        abstract: true,
        controller : controllers.BaseController,

    });
    $stateProvider.state('base.home', {
        url: '/home',

        params: {
            action : { refreshData : false } 
        },

        views: {
            main: {
                templateUrl : 'components/home/home.html',
                controller : controllers.HomeController
            }
        }
    });
    $stateProvider.state('base.startbooking', {
        url: '/startbooking',

        params: {
            action : { refreshData : false } 
        },

        views: {
            main: {
                templateUrl : 'components/startbooking/startbooking.html',
                controller : controllers.StartBookingController
            },
            footer: {
                templateUrl : 'components/home/footer.html',
                controller : controllers.BaseController
            }
        }
    });
    $stateProvider.state('base.editbooking', {
        url: '/editbooking',

        params: {
            action : { refreshData : false } 
        },

        views: {
            main: {
                templateUrl : 'components/editbooking/editbooking.html',
                controller : controllers.EditBookingController
            },
            footer: {
                templateUrl : 'components/home/footer.html',
                controller : controllers.BaseController
            }
        }
    });
    $stateProvider.state('base.checkout', {
        url: '/checkout',

        params: {
            action : { refreshData : false } 
        },

        views: {
            main: {
                templateUrl : 'components/checkout/checkout.html',
                controller : controllers.checkoutController
            },
            footer: {
                templateUrl : 'components/home/footer.html',
                controller : controllers.BaseController
            }
        }
    });
    
    $stateProvider.state('base.login', {
        url: '/login',
        
        views: {
            main: {
                templateUrl : 'components/login/login.html',
                controller : controllers.LoginController
            }
        }
    });
    $stateProvider.state('base.existingbooking', {
        url: '/existingbooking',

        params: {
            action : { refreshData : false } 
        },

        views: {
            main: {
                templateUrl : 'components/existingbooking/existingbooking.html',
                controller : controllers.ExistingBookingController
            },
            footer: {
                templateUrl : 'components/home/footer.html',
                controller : controllers.BaseController
            }
        }
    });

 
   
}]);

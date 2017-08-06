angular.module('TrackAndTrace')
.config(['$stateProvider', function ($stateProvider) {
    $stateProvider.state('base', {
        url: '',
        templateUrl: 'components/general/main.html',
        abstract: true,
        controller : controllers.BaseController,

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
 
    $stateProvider.state('base.home', {
        url: '/home',
        
        params: { 
            action : { refreshData : false } 
        },
        
        views: {
            main: {
                templateUrl : 'components/home/home.html',
                controller : controllers.HomeController
            },
            footer: {
                templateUrl : 'components/home/footer.html',
                controller : controllers.HomeController
            }
        }
    });
    
    $stateProvider.state('base.newaccess', {
        url: '/newaccess',
        
        params: { 
            action : { refreshData : false } 
        },
        
        views: {
            main: {
                templateUrl : 'components/newaccess/newaccess.html',
                controller : controllers.NewAccessController
            },
            footer: {
                templateUrl : 'components/newaccess/footer.html',
                controller : controllers.NewAccessController
            }
        }
    });
   
    $stateProvider.state('base.guestinfo', {
        url: '/guestinfo/:accessId',
        
        params: { 
            action : { refreshData : false } 
        },
        
        views: {
            main: {
                templateUrl : 'components/guestinfo/overview.html',
                controller : controllers.GuestInfoController
            },
            footer: {
                templateUrl : 'components/guestinfo/footer.html',
                controller : controllers.GuestInfoFooterController
            }
        }
    });

    $stateProvider.state('base.doors', {
        url: '/doors',
        
        params: { 
            action : { refreshData : false } 
        },
        
        views: {
            main: {
                templateUrl : 'components/doors/doors.html',
                controller : controllers.DoorsController
            },
            footer: {
                templateUrl : 'components/doors/footer.html',
                controller : controllers.DoorsController
            }
        }
    });
    
    $stateProvider.state('base.door', {
        url: '/doors/:externalId',
        
        params: { 
            action : { refreshData : false } 
        },
        
        views: {
            main: {
                templateUrl : 'components/doors/door.html',
                controller : controllers.DoorController
            },
            footer: {
                templateUrl : 'components/doors/footer.html',
                controller : controllers.DoorController
            }
        }
    });
    
    $stateProvider.state('base.mastercodes', {
        url: '/mastercodes',
        
        params: { 
            action : { refreshData : false } 
        },
        
        views: {
            main: {
                templateUrl : 'components/mastercodes/mastercodes.html',
                controller : controllers.MasterCodesController
            },
            footer: {
                templateUrl : 'components/mastercodes/footer.html',
                controller : controllers.MasterCodesController
            }
        }
    });
    
    $stateProvider.state('base.incomingmessages', {
        url: '/incomingmessages',
        
        params: { 
            action : { refreshData : false } 
        },
        
        views: {
            main: {
                templateUrl : 'components/messages/messages.html',
                controller : controllers.MessagesController
            },
            footer: {
                templateUrl : 'components/messages/footer.html',
                controller : controllers.MessagesController
            }
        }
    });
   
}]);

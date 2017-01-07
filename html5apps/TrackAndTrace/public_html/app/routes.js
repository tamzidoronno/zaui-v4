angular.module('TrackAndTrace')
.config(['$stateProvider', function ($stateProvider) {
    $stateProvider.state('base', {
        url: '',
        templateUrl: 'components/general/main.html',
        abstract: true,
        controller : controllers.BaseController,
        
//        views: {
//            header: {
//                templateUrl : 'components/header/header.html',
//                controller : controllers.HeaderController
//            }
//        }
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
    
    $stateProvider.state('base.routeoverview', {
        url: '/routeoverview/:routeId',
        
        views: {
            main: {
                templateUrl : 'components/routeoverview/routeoverview.html',
                controller: controllers.RouteOverviewController
            },
            footer: {
                templateUrl : 'components/routeoverview/footer.html',
                controller: controllers.RouteOverviewController
            }
        }
    });
    
    $stateProvider.state('base.home', {
        url: '/home',
        
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
    
    $stateProvider.state('base.destination', {
        url: '/destination/:destinationId/:routeId',
        
        views: {
            main: {
                templateUrl : 'components/destination/destination.html',
                controller : controllers.DestinationController
            },
            footer: {
                templateUrl : 'components/destination/footer.html',
                controller : controllers.DestinationController
            }
        }
    });
    
    $stateProvider.state('base.destinationsignature', {
        url: '/destinationsignature/:destinationId/:routeId',
        
        views: {
            main: {
                templateUrl : 'components/destination/departing.html',
                controller : controllers.DepartingController
            }
        }
    });
    
    $stateProvider.state('base.task', {
        url: '/destination/:destinationId/:routeId/:taskId',
        
        views: {
            main: {
                templateUrl : 'components/task/task.html',
                controller : controllers.TaskController
            },
            footer: {
                templateUrl : 'components/task/footer.html',
                controller : controllers.TaskController
            }
        }
    });
    
    $stateProvider.state('base.taskexceptions', {
        url: '/destination/exceptions/:destinationId/:routeId/:taskId',
        
        views: {
            main: {
                templateUrl : 'components/task/exceptions.html',
                controller : controllers.TaskController
            },
            footer: {
                templateUrl : 'components/task/footer.html',
                controller : controllers.TaskController
            }
        }
    });
    
    $stateProvider.state('base.destinationexception', {
        url: '/destinationexception/exceptions/:destinationId/:routeId/:taskId',
        
        views: {
            main: {
                templateUrl : 'components/destination/exceptions.html',
                controller : controllers.DestinationExceptionController
            }
        }
    });
    
    $stateProvider.state('base.ordercorrection', {
        url: '/destination/exceptions/:destinationId/:routeId/:taskId/:orderId/:type',
        
        views: {
            main: {
                templateUrl : 'components/task/correction.html',
                controller : controllers.TaskCorrectionController
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

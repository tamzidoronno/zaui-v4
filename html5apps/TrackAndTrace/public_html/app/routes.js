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
    
    $stateProvider.state('base.errorhandler', {
        url: '/errorhandler',
        
        views: {
            main: {
                templateUrl : 'components/errorhandler/errorprinter.html',
                controller : controllers.ErrorHandlerController
            }
        }
    });
    
    $stateProvider.state('base.settings', {
        url: '/settings',
        
        views: {
            main: {
                templateUrl : 'components/settings/settings.html',
                controller : controllers.SettingsController
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
    
    $stateProvider.state('base.pool', {
        url: '/pool',
        
        views: {
            main: {
                templateUrl : 'components/pool/pool.html',
                controller : controllers.PoolController
            },
            footer: {
                templateUrl : 'components/pool/footer.html',
                controller : controllers.PoolControllerFooter
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
            },
            footer: {
                templateUrl : 'components/destination/footer.html',
                controller : controllers.DepartingController
            }
        }
    });
    
    $stateProvider.state('base.addPickupTask', {
        url: '/addPickupTask/:destinationId/:routeId',
        
        views: {
            main: {
                templateUrl : 'components/destination/addPickupTask.html',
                controller : controllers.AddPickupTaskController
            }
        }
    });
    
    $stateProvider.state('base.task', {
        url: '/destination/:destinationId/:routeId/:taskId',
        params: { 
            action : { state : 'normal' } 
        },
        
        views: {
            main: {
                templateUrl : 'components/task/task.html',
                controller : controllers.TaskController
            },
            footer: {
                templateUrl : 'components/task/footer.html',
                controller : controllers.TaskFooterController
            }
        }
    });
    
    $stateProvider.state('base.collection', {
        url: '/collection/:destinationId/:routeId/:collectionType/:collectionSubType',
        
        params: { 
            action : { state : 'normal' },
            collectionData : {
                collectionType : '',
                collectionSubType : '',
                newAmount : 0,
                action: false
            }
        },
        
        views: {
            main: {
                templateUrl : 'components/collection/collection.html',
                controller : controllers.CollectionController
            },
            footer: {
                templateUrl : 'components/collection/footer.html',
                controller : controllers.CollectionController
            }
        }
    });
    
    $stateProvider.state('base.keyreference', {
        url: '/keyreference/:destinationId/:routeId/:taskId',
        
        views: {
            main: {
                templateUrl : 'components/task/keyreference.html',
                controller : controllers.KeyReferenceController
            },
            footer: {
                templateUrl : 'components/task/footerkeyreference.html',
                controller : controllers.KeyReferenceController
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
    
    $stateProvider.state('base.pickupexception', {
        url: '/destination/pickupexception/:destinationId/:routeId/:taskId/:orderId',
        
        views: {
            main: {
                templateUrl : 'components/task/pickupexceptions.html',
                controller : controllers.TaskController
            },
            footer: {
                templateUrl : 'components/task/footerpickupexception.html',
                controller : controllers.TaskController
            }
        }
    });
    
    $stateProvider.state('base.deliveryexception', {
        url: '/destination/deliveryexception/:destinationId/:routeId/:taskId/:orderId',
        
        views: {
            main: {
                templateUrl : 'components/task/deliveryexceptions.html',
                controller : controllers.TaskController
            },
            footer: {
                templateUrl : 'components/task/footerpickupexception.html',
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
        
        params : {
            collectionData : {
                collectionType : 'collectionType',
                collectionSubType : '',
                action: false
            }
        },
        
        views: {
            main: {
                templateUrl : 'components/task/correction.html',
                controller : controllers.TaskCorrectionController
            },
            footer: {
                templateUrl : 'components/task/correctionfooter.html',
                controller : controllers.TaskCorrectionController
            }
        }
    });
    
    $stateProvider.state('base.completeRoute', {
        url: '/completeRoute/:routeId',
        
        views: {
            main: {
                templateUrl : 'components/routeoverview/completeRoute.html',
                controller : controllers.RouteCompleteController
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

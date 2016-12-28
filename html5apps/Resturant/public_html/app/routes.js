angular.module('TrackAndTrace')
.config(['$stateProvider', function ($stateProvider) {
    $stateProvider.state('base', {
        url: '',
        templateUrl: 'components/general/main.html',
        abstract: true,
        controller : controllers.BaseController
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
    
    $stateProvider.state('base.selectroom', {
        url: '/selectroom',
        
        views: {
            main: {
                templateUrl : 'components/selectroom/selectroom.html',
                controller : controllers.SelectRoomController
            },
            footer: {
                templateUrl : 'components/selectroom/footer.html',
                controller : controllers.SelectRoomController
            }
        }
    });
    
    $stateProvider.state('base.settings', {
        url: '/settings',
        
        views: {
            main: {
                templateUrl : 'components/settings/settings.html',
                controller : controllers.SettingsController
            },
            footer: {
                templateUrl : 'components/settings/footer.html',
                controller : controllers.SettingsController
            }
        }
    });
    
    $stateProvider.state('base.tableoverview', {
        url: '/tableoverview/:tableId',
        
        views: {
            main: {
                templateUrl : 'components/tableoverview/table.html',
                controller : controllers.TableController
            },
            footer: {
                templateUrl : 'components/tableoverview/footer.html',
                controller : controllers.TableController
            }
        }
    });
    
    $stateProvider.state('base.tableloadsession', {
        url: '/tableoverview_loadsession/:tableId',
        
        views: {
            main: {
                templateUrl : 'components/tableloadsession/loadoldsessions.html',
                controller : controllers.TableLoadSessionController
            },
            footer: {
                templateUrl : 'components/tableloadsession/footer.html',
                controller : controllers.TableLoadSessionController
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

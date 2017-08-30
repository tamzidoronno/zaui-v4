if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.TableLoadSessionController = function($scope, $rootScope, $api, $state, datarepository, $stateParams) {
    $scope.table = datarepository.getTableById($stateParams.tableId);
    
    $scope.sessions = [];
    
    $scope.loadAllSessions = function() {
        var resturantManager = $api.getApi().ResturantManager;
        resturantManager.getAllSessionsForTable($scope.table.id).done(function(res) {
            $scope.sessions = res;
            $scope.$evalAsync();
            console.log(res);
        })
    }
   
    $scope.goBack = function() {
        $state.transitionTo('base.tableoverview', { tableId: $scope.table.id });
    }
    
    $scope.changeToDifferentSession = function(session) {
        $api.getApi().ResturantManager.changeToDifferentSession(session.id, $scope.table.id).done(function(res) {
            datarepository.setCartItems(res, $scope.table.id);
            $state.transitionTo('base.tableoverview', { tableId: $scope.table.id });
        })
    }
    
    $scope.loadAllSessions();
    
};
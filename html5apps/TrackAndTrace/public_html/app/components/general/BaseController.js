/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.BaseController = function($scope, $rootScope, datarepository, $api) {
    if($api.getApi()) {
        $scope.counter = $api.getApi().getUnsentMessageCount();
    }
    
    $rootScope.$on('refreshRouteEven1', function(msg, data) {
        if(datarepository.updateRoute(data, $api)) {
            $rootScope.$broadcast('refreshRoute', data);
        }
    });
    
    $rootScope.$on('refreshData', function() {
        $('.loadingData').show();
        datarepository.loadAllData($api, $scope);
    });
    
    $rootScope.$on('messageCountChanged', function() {
        $scope.counter = $api.getApi().messagesToSendJson.length;
        if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
            $scope.$apply();
        }
    })
};
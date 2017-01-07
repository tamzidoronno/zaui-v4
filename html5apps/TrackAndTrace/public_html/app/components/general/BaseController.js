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
    
    $rootScope.$on('messageCountChanged', function() {
        $scope.counter = $api.getApi().messagesToSendJson.length;
        if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
            $scope.$apply();
        }
    })
};
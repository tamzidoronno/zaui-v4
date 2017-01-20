if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.conferenceController = function ($scope, $state, $stateParams, $location) {
    $scope.conferences = [];
    
    $scope.load = function() {
        getshopclient.PmsManager.getFutureConferenceData(getMultilevelName()).done(function(res) {
            $scope.conferences = res;
            $scope.$apply();
            console.log($scope.conferences);
        });
    }
    
    $scope.load();
};
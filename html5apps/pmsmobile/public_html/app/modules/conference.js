if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.conferenceController = function ($scope, $state, $stateParams, $location) {
    $scope.conferences = [];
    
    $scope.getConference = function(date) {
        var retData = $scope.data[date.getTime()];
        return retData;
    }
    
    $scope.load = function() {
        getshopclient.PmsManager.getGroupedConferenceData(getMultilevelName()).done(function(res) {
            $scope.dates = Object.keys(res);
            
            $scope.dates.sort();
            
            var dates = [];
            for (var i in $scope.dates) {
                var javaDate = new Date(parseInt($scope.dates[i]));
                dates.push(javaDate);
            }
            
            $scope.dates = dates;
            $scope.data = res;
            $scope.$apply();
        });
        
//        getshopclient.PmsManager.getFutureConferenceData(getMultilevelName()).done(function(res) {
//            $scope.conferences = res;
//            $scope.$apply();
            
//            console.log($scope.conferences);
//        });
    }
    
    $scope.load();
};
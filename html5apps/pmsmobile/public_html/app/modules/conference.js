if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.conferenceController = function ($scope, $state, $stateParams, $location) {
    $scope.conferences = [];
    $scope.date = new Date();
    
    $scope.getConference = function(date) {
        var retData = $scope.data[date.getTime()];
        return retData;
    };
     $scope.formatDate = function(date) {
        var today = date;
        var dd = today.getDate();
        var mm = today.getMonth()+1; //January is 0!

        var yyyy = today.getFullYear();
        if(dd<10){
            dd='0'+dd;
        } 
        if(mm<10){
            mm='0'+mm;
        } 
        
        var today = dd+'/'+mm+'/'+yyyy;
        
        return today;
    };
    $scope.getWeekNumber = function(d) {
        // Copy date so don't modify original
        d = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
        // Set to nearest Thursday: current date + 4 - current day number
        // Make Sunday's day number 7
        d.setUTCDate(d.getUTCDate() + 4 - (d.getUTCDay()||7));
        // Get first day of year
        var yearStart = new Date(Date.UTC(d.getUTCFullYear(),0,1));
        // Calculate full weeks to nearest Thursday
        var weekNo = Math.ceil(( ( (d - yearStart) / 86400000) + 1)/7);
        // Return array of year and week number
        return weekNo;
    };
    $scope.load = function() {
        $scope.dates = {};
        $scope.data = {};
        
        getshopclient.PmsManager.getGroupedConferenceData(getMultilevelName(),$scope.date).done(function(res) {
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
    };
    
    $scope.load();
};
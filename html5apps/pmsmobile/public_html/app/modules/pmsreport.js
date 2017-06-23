if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.pmsreportController = function($scope, $state) {
    $scope.loadOverviews = function() {
        var exclude = false;
        if($scope.excludeClosedRooms) {
            exclude = true;
        }
        var loading = getshopclient.PmsReportManager.getReport(getMultilevelName(), $scope.start, $scope.end, $scope.comparePeriode, exclude);
        $scope.showLoading = true;
        loading.done(function(res) {
            $scope.pmsreportlist = res;
            $scope.$apply();
            $scope.showLoading = false;
        });
    }
    
    $scope.loadRoomCoverage = function() {
        var loading = getshopclient.PmsReportManager.getRoomCoverage(getMultilevelName(), $scope.start, $scope.end);
        loading.done(function(res) {
            $scope.roomCoverageList = res;
            $scope.$apply();
            console.log(res);
        });
    }
    
    $scope.loadNeedsFixes = function() {
        var loading = getshopclient.PmsManager.getCareTakerJobs(getMultilevelName());
        loading.done(function(res) {
            $scope.careTakerList = res;
            for(var k in res) {
                res[k].rowCreatedDateFormatted = $scope.formatDate(new Date(res[k].rowCreatedDate));
            }
            $scope.$apply();
        });
    }
    $scope.round = function(val) {
        return round(val);
    }
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
    }
    
    $scope.reloadOverview = function() {
        $scope.loadView();
    }
    
    $scope.loadView = function() {
        $scope.loadOverviews();
        $scope.loadNeedsFixes();
        $scope.loadRoomCoverage();
    }
    
    $scope.end = new Date();
    $scope.start = new Date();
    $scope.start.setDate($scope.start.getDate()-7);
    $scope.comparePeriode = "-1week";
    
    $scope.loadView();
}
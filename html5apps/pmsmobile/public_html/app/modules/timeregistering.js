if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.timeregisteringController = function($scope, $state) {
    $scope.startdate = new Date();
    $scope.enddate = new Date();
    $scope.starttime = new Date(1970, 0, 1, 08, 00, 0);
    $scope.endtime = new Date(1970, 0, 1, 16, 00, 0);
    $scope.addingHour = false;
    $scope.hourlist = new Array();
    
    $scope.loadMyHours = function() {
        var loading = getshopclient.TimeRegisteringManager.getMyHours();
        loading.done(function(res) {
            $scope.hourlist = res;
            $scope.$apply();
        });
    };
    
    $scope.deleteDate = function(id) {
        var confirmed = confirm("Are you sure you want to delete this?");
        if(confirmed) {
            var deleted = getshopclient.TimeRegisteringManager.deleteTimeUnsecure(id);
            deleted.done(function() {
                $scope.loadMyHours();
            });
         }
    }
    
    $scope.registerNewTime = function() {
        $scope.addingHour = true;
        $('.addTimeButton').html('<i class="fa fa-spin fa-spinner"></i>');
        $scope.startdate.setHours($scope.starttime.getHours());
        $scope.startdate.setMinutes($scope.starttime.getMinutes());
        $scope.startdate.setSeconds($scope.starttime.getSeconds());
        
        $scope.enddate.setHours($scope.endtime.getHours());
        $scope.enddate.setMinutes($scope.endtime.getMinutes());
        $scope.enddate.setSeconds($scope.endtime.getSeconds());
        
        var saving = getshopclient.TimeRegisteringManager.registerTime($scope.startdate, $scope.enddate, $scope.comment);
        
        saving.done(function(res) {
            $scope.loadMyHours();
            $scope.addingHour = false;
            setTimeout(function() {
                $('.addTimeButton').html('<i class="fa fa-check"></i>');
            }, "500");
            setTimeout(function() {
                $('.addTimeButton').html('Register');
            }, "2500");
        });
    };
    
    $scope.loadMyHours();
}; 
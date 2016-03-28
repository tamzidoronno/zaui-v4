if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.doorlogController = function ($scope, $state, $stateParams) {
    $scope.formatTime = function (time) {
        Number.prototype.padLeft = function (base, chr) {
            var len = (String(base || 10).length - String(this).length) + 1;
            return len > 0 ? new Array(len).join(chr || '0') + this : this;
        }
        var d = new Date();
        d.setTime(time);
        var dformat = [d.getDate().padLeft(),
            (d.getMonth() + 1).padLeft(),
            d.getFullYear()].join('/') + ' ' +
                [d.getHours().padLeft(),
                    d.getMinutes().padLeft(),
                    d.getSeconds().padLeft()].join(':');
        return  dformat;
    }

    $scope.doorname = $stateParams.name;
    var date = new Date();
    date.setHours(23);
    date.setMinutes(0);
    date.setSeconds(0);
    var end = date.getTime();
    var start = end - ((86400 * 1000) * 2);
    
    var startDate = new Date();
    startDate.setTime(start);
    var endDate = new Date();
    endDate.setTime(end);
    
    $scope.startTime = startDate;
    $scope.endTime = endDate;
    $scope.updateLogData = function() {
        $scope.loading = true;
        var start = $scope.startTime.getTime();
        var end = $scope.endTime.getTime();
        var entries = getshopclient.DoorManager.getLogForDoor(getMultilevelName(), $stateParams.externalId, start, end);
        entries.done(function (data) {
            $scope.loading = false;
            $scope.logentries = data;
            if(data.length === 0) {
                $scope.notfound = true;
            }
            $scope.$apply();
        });
    }
    
    $scope.updateLogData();
};
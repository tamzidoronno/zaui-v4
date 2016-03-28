if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.findguestController = function($scope, $state) {
    var filter = {};
    filter.filterType = "active";
    var start = new Date();
    start.setHours(00);
    start.setMinutes(00);
    start.setSeconds(00);
    
    var end = new Date();
    end.setHours(23);
    end.setMinutes(59);
    end.setSeconds(59);
    
    filter.startDate = start;
    filter.endDate = end;
    var loadBookings = getshopclient.PmsManager.getSimpleRooms(getMultilevelName(), filter);
    $scope.convertDate = function(time) {
        var d = new Date();
        d.setTime(time);
        var dformat = ("00" + d.getDate()).slice(-2) + "." + 
        ("00" + (d.getMonth() + 1)).slice(-2) + "." + 
        d.getFullYear() + " " + 
        ("00" + d.getHours()).slice(-2) + ":" + 
        ("00" + d.getMinutes()).slice(-2);
           return dformat;
    };
    $scope.doSearch = function(searchWord) {
        var filter = {};
        console.log('searching');
        filter.searchWord = searchWord;
        var searching = getshopclient.PmsManager.getSimpleRooms(getMultilevelName(), filter);
        searching.done(function(res) {
            $scope.rooms = res;
            $scope.$apply();
        });
    };
    $scope.urlEncoder = function(text) {
        return encodeURIComponent(text);
    };
    loadBookings.done(function(data) {
        $scope.rooms = data;
        $scope.$apply();
    });
};
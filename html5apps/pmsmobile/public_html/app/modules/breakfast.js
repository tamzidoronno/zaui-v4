if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.breakfastController = function ($scope, $state, $stateParams) {
    $scope.selectedDay = new Date();
    
    $scope.updateBreakfastList = function() {
        $scope.loading = true;
        var time = $scope.selectedDay.getTime();
        var date = new Date();
        date.setTime(time);
        $scope.breakfastlist = [];
        
        var filter = {};
        filter.filterType = "active";
        var start = new Date();
        start.setTime(time);
        start.setHours(00);
        start.setMinutes(00);
        start.setSeconds(00);

        var end = new Date();
        end.setTime(time);
        end.setHours(23);
        end.setMinutes(59);
        end.setSeconds(59);

        filter.startDate = start;
        filter.endDate = end;
        filter.sorting = "room";
        $scope.loading = true;
        $scope.breakfastCount = 0;
        var loadBookings = getshopclient.PmsManager.getSimpleRooms(getMultilevelName(), filter);
        loadBookings.done(function(res) {
            var found = false;
            for(var key in res) {
                var room = res[key];
                for(var addonkey in room.addons) {
                    var addon = room.addons[addonkey];
                    
                    if(addon.addonType != 1) {
                        continue;
                    }
                    
                    var addonDate = new Date(addon.date);
                    var addonTime = addonDate.getTime() + (86400*1000);
                    addonDate.setTime(addonTime);
                    if(addonDate.toDateString() === date.toDateString()) {
                        var breakfast = {};
                        found = true;
                        var count = addon.count;
                        breakfast.count = count;
                        breakfast.name = "";
                        for(var key in room.guest) {
                            var guest = room.guest[key];
                            if(breakfast.name) {
                                breakfast.name += ", ";
                            }
                            breakfast.name += guest.name;
                        }
                        breakfast.room = room.room;
                        
                        $scope.breakfastCount += count;
                        $scope.breakfastlist.push(breakfast);
                    }
                }
            }
            $scope.notfound = !found;
            $scope.loading = false;
            $scope.$apply();
        });
    };

    $scope.updateBreakfastList();
    
};


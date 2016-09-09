if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.findguestController = function($scope, $state) {
    var filter = {};
    $scope.filterType = "active";
    filter.filterType = $scope.filterType;
    $scope.listDate = new Date();
    
    $scope.toggleFireCheck = function($event) {
        console.log($event);
        var box = $($event.target);
        if(box.hasClass('checked')) {
            box.removeClass('checked');
        } else {
            box.addClass('checked');
        }
    },

    $scope.forceCleaning = function(room) {
        getshopclient.PmsManager.forceMarkRoomAsCleaned(getMultilevelName(), room.bookingItemId);
        room.progressState="active";
    },
    $scope.convertDate = function(time) {
        var d = new Date();
        d.setTime(time);
        var dformat = ("00" + d.getDate()).slice(-2) + "." + 
        ("00" + (d.getMonth() + 1)).slice(-2) + " " + 
        ("00" + d.getHours()).slice(-2) + ":" + 
        ("00" + d.getMinutes()).slice(-2);
           return dformat;
    };
    
    $scope.displayList = function() {
        var start = $scope.listDate;
        start.setHours(00);
        start.setMinutes(00);
        start.setSeconds(00);

        var end = $scope.listDate;
        end.setHours(23);
        end.setMinutes(59);
        end.setSeconds(59);
        filter.startDate = start;
        filter.endDate = end;
        filter.filterType = $scope.filterType;
        filter.sorting = "room";
        $scope.loading = true;
        $scope.notfound = false;
        var loadBookings = getshopclient.PmsManager.getSimpleRooms(getMultilevelName(), filter);
        loadBookings.done(function(data) {
            $scope.loading = false;
            $scope.rooms = data;
            
            var d = new Date();
            var now = d.getTime();
            var totalcount = 0;
            var guestcount = 0;
            var today = new Date();
            for(var k in data) {
                var room = data[k];
                room.checkingIntToday = false;
                guestcount += room.guest.length;
                if(room.start > now) {
                    room.checkingIntToday = true;
                }
                totalcount++;
            }
            $scope.totalcount = totalcount;
            $scope.guestcount = guestcount;
            if(data.length === 0) {
                $scope.notfound = true;
            }

            $scope.$apply();
        });
    },
    $scope.doSearch = function(searchWord) {
        var filter = {};
        $scope.loading = true;
        filter.searchWord = searchWord;
        var searching = getshopclient.PmsManager.getSimpleRooms(getMultilevelName(), filter);
        $scope.notfound = false;
        searching.done(function(res) {
            $scope.loading = false;
            $scope.rooms = res;
            if(res.length === 0) {
                $scope.notfound = true;
            }
            $scope.$apply();
        });
    };
    $scope.urlEncoder = function(text) {
        return encodeURIComponent(text);
    };
    $scope.notfound = false;
    $scope.displayList();

};
if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.careTakerController = function ($scope, $state, $stateParams) {
    $scope.careTakerList = [];
    $scope.view = "overview";
    
    $scope.repeatinterval = 1;
    $scope.repeattype = "0";
    $scope.repeatstart = new Date();
    $scope.repeatend = new Date();
    
    $scope.createRepeatingTask = function() {
        var data = {
            "name" : $scope.repeatname,
            "text" : $scope.repeattext,
            "repeaterData" : {
                "repeatPeride" : parseInt($scope.repeattype),
                "firstEvent" : {
                    "start" : $scope.repeatstart,
                    "end" : $scope.repeatstart
                },
                "endingAt" : $scope.repeatend,
                "repeatEachTime" : $scope.repeatinterval,
                "repeatMonday" : $scope.repeat_mon,
                "repeatTuesday" : $scope.repeat_tue,
                "repeatWednesday" : $scope.repeat_wed,
                "repeatThursday" : $scope.repeat_thu,
                "repeatFriday" : $scope.repeat_fri,
                "repeatSaturday" : $scope.repeat_sat,
                "repeatSunday": $scope.repeat_sun
            }
        };
        
        data['roomId'] = "";
        if($scope.selectedRoom) {
            data['roomId'] = $scope.selectedRoom.id;
        }
        
        var saving = getshopclient.CareTakerManager.addRepeatingTask(getMultilevelName(), data);
        saving.done(function() {
            $scope.loadRepeaterList();
        });
    },
            
    $scope.translateRepeatType = function(type, repeaterData) {
        switch(type) {
            case 0:
                return "Daily";
            case 1:
                var text = "Weekly - ";
                if(repeaterData.repeatMonday) { text += "mon,"; }
                if(repeaterData.repeatTuesday) { text += "tue,"; }
                if(repeaterData.repeatWednesday) { text += "wed,"; }
                if(repeaterData.repeatThursday) { text += "thu,"; }
                if(repeaterData.repeatFriday) { text += "fri,"; }
                if(repeaterData.repeatSaturday) { text += "sat,"; }
                if(repeaterData.repeatSunday) { text += "sun,"; }
                text = text.substring(0, text.length - 1);
                
                return text;
            case 2:
                return "Monthly (each: " + repeaterData.repeatEachTime + " month)";
        }
    },
    
    $scope.removeRepeatingTask = function(task) {
        var deleting = getshopclient.CareTakerManager.deleteRepeatingTask(getMultilevelName(), task.id);
        deleting.done(function(res) {
            $scope.loadRepeaterList();
        });
    },

    $scope.loadRepeaterList = function() {
        var loader = getshopclient.CareTakerManager.getRepeatingTasks(getMultilevelName());
        loader.done(function(res) {
            $scope.repeaterList = res;
            $scope.$apply();
        });
    },
    
    $scope.changeView = function(view) {
        $('.changebtn').removeClass('btn-success');
        $('.changebtn').addClass('btn-info');
        $('.changebtn[view="'+view+'"]').removeClass('btn-info');
        $('.changebtn[view="'+view+'"]').addClass('btn-success');
        $scope.view = view;
        $scope.listViewLoaded = false;
        if(view !== "overview" && view !== "addtask") {
            $scope.loadListView(view);
        }
    },
    $scope.assignTask = function() {
        var userId = $scope.assignToUser;
        var taskId = $scope.currentTask;
        if(!userId) {
            userId = "";
        }
        var assigning = getshopclient.CareTakerManager.assignTask(getMultilevelName(), taskId, userId);
        $('.assignmentbox').hide();
        assigning.done(function() {
            $scope.loadListView($scope.currentView);
        });
    },
    $scope.displayAssignmentBox = function(res, currentTask) {
        $scope.currentTask = currentTask;
        var target = $(res.target);
        var box = $('.assignmentbox');
        var top = target.offset().top-box.height();
        box.css('top', top + "px");
        box.toggle();
    },
    $scope.reportToCareTaker = function() {
        var job = {};
        job['description'] = $scope.caretakermsg;
        if($scope.selectedRoom) {
            job['roomId'] = $scope.selectedRoom.id;
        }
             
        var saving = getshopclient.PmsManager.saveCareTakerJob(getMultilevelName(), job);
        saving.done(function() {
            $scope.selectedRoom = "";
            $scope.reportPanel = false;
            $scope.caretakermsg = "";
            $scope.$apply();
        });
    },
    $scope.completeJob = function(jobId) {
        var done = getshopclient.CareTakerManager.completeTask(getMultilevelName(), jobId);
        done.done(function() {
            $scope.loadListView($scope.currentView);
        });
    },
    $scope.loadListView = function(view) {
        $scope.listViewLoaded = true;
        $scope.currentView = view;
        var loading = getshopclient.CareTakerManager.getCareTakerList(getMultilevelName(), {
            "view" : view
        });
        loading.done(function(res) {
            $scope.overviewList = res;
            $scope.$apply();
        });
        
        var loading2 = getshopclient.CareTakerManager.getCaretakers(getMultilevelName());
        loading2.done(function(res) {
            $scope.caretakerlist = res;
            $scope.$apply();
        });
    },
    $scope.completeTask = function(object) {
        var completing = getshopclient.PmsManager.completeCareTakerJob(getMultilevelName(), object.id);
        completing.done(function() {
            $scope.loadCareTakerTasks();
        });
    },
    $scope.reloadOverview = function() {
        $scope.loadRoomList();
    },
    $scope.loadRoomList = function() {
        var defects = false;
        if($scope.defectsonly) {
            defects = true;
        }
        loading = getshopclient.CareTakerManager.getRoomOverview(getMultilevelName(), defects);
        loading.done(function(res) {
            $scope.overView = res;
            $scope.$apply();
        });
    },
    $scope.loadBookingItems = function() {
        var loading = getshopclient.BookingEngine.getBookingItems(getMultilevelName());
        loading.done(function(res){
            $scope.items = res;
        });
    }
            
    $scope.loadCareTakerTasks = function() {
        var loading = getshopclient.PmsManager.getCareTakerJobs(getMultilevelName());
        loading.done(function(res) {
            console.log(res);
            var result = [];
            for(var k in res) {
                if(!res[k].completed) {
                    result.push(res[k]);
                }
            }
            $scope.careTakerList = result;
            $scope.$apply();
        });
    };
    $scope.loadBookingItems();      
    $scope.loadCareTakerTasks();
    $scope.loadRoomList();
    $scope.loadRepeaterList();
};
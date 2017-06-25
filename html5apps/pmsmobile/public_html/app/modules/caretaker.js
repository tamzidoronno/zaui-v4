if (typeof (getshop) === "undefined") {
    var getshop = {};
}
getshop.careTakerController = function ($scope, $state, $stateParams) {
    $scope.careTakerList = [];
    $scope.view = "overview";
    
    $scope.changeView = function(view) {
        $('.changebtn').removeClass('btn-success');
        $('.changebtn').addClass('btn-info');
        $('.changebtn[view="'+view+'"]').removeClass('btn-info');
        $('.changebtn[view="'+view+'"]').addClass('btn-success');
        $scope.view = view;
        $scope.listViewLoaded = false;
        if(view !== "overview") {
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
    $scope.loadCareTakerTasks = function() {
        var loading = getshopclient.PmsManager.getCareTakerJobs(getMultilevelName());
        loading.done(function(res) {
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
            
    $scope.loadCareTakerTasks();
    $scope.loadRoomList();
};
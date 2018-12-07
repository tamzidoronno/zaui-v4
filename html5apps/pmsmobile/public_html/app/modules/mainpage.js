if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.mainpageController = function($scope, $state) {
    $scope.address = localStorage.getItem("address");
    var loadNames = getshopclient.StoreManager.getMultiLevelNames();
    $scope.hasOtherInstructions = false;
    $scope.hasFireInstructions = false;
    $scope.hasDoorControl = false;
    $scope.hasCleaning = true;
    $scope.hasCaretaker = true;
    $scope.hasConference = false;
    $scope.hasStatistics = false;
    $scope.timeregistering = false;
    
    
    $scope.showAll = function() {
        $scope.findguest = true;
        $scope.otherinstruction = true;
        $scope.fire = true;
        $scope.sms = true;
        $scope.log = true;
        $scope.doors = true;
        $scope.cleaning = true;
        $scope.caretaker = true;
        $scope.hasStatistics = true;
        $scope.timeregistering = true;
    },
    
    $scope.LoadConfig = function() {
        $scope.hasOtherInstructions = false;
        $scope.hasFireInstructions = false;
        $scope.hasDoorControl = false;
        $scope.hasBreakfast = false;
        $scope.hasConference = false;
        var config = getshopclient.PmsManager.getConfiguration(getMultilevelName());
        config.done(function(res) {
            $scope.views = res.mobileViews;
            if(res.arxHostname || res.hasDoorLockSystem) {
                $scope.hasDoorControl = true;
            }
            if(res.otherinstructions) {
                $scope.hasOtherInstructions = true;
            }
            if(res.fireinstructions) {
                $scope.hasFireInstructions = true;
            }
            
            $scope.hasConference = res.functionsEnabled;
            
            for(var key in res.addonConfiguration) {
                var addon = res.addonConfiguration[key];
                if(addon.addonType === 1) {
                    $scope.hasBreakfast = true;
                }
            }
            var loadUser = getshopclient.UserManager.getLoggedOnUser();
            loadUser.done(function(user) {
                var list = res.mobileViewRestrictions[user.id];
                if(!list || list.length === 0) {
                    $scope.showAll();
                    for(var k in res.mobileViews) {
                        $scope[res.mobileViews[k].name] = true;
                    }
                } else {
                    for(var k in list) {
                        $scope[list[k]] = true;
                    }
                }
                $scope.$apply();
            });
            
            $scope.$apply();
        });
    };
    
    loadNames.done(function(data) {
        var names = [];
        var current = getMultilevelName();
        var isActive = {};
        var resolved = 0;
        var toResolve = data.length;
        var found = false;
        for(var key in data) {
            var nameToSet = data[key];
            var isActive = getshopclient.PmsManager.isActive(nameToSet);
            $.when(isActive, nameToSet).done(function(res, name) {
                if(res) {
                    names.push({"id": name, "name" : name});
                    if(name === current) {
                        found = true;
                    }
                    $scope.multilevelnames = names;
                    $scope.$apply();
                }
                resolved++;
                
                if(toResolve == resolved) {
                    if(names.length == 1) {
                        $scope.hideEngineSelection = true;
                        $scope.dataSelected = names[0].id;
                        localStorage.setItem("multilevelname", $scope.dataSelected);
                        $scope.multilevelnames = names;
                        $scope.$apply();
                    }
                    if(found) {
                        $scope.dataSelected = current;
                        localStorage.setItem("multilevelname", $scope.dataSelected);
                        $scope.$apply();
                    } else {
                        $scope.dataSelected = names[0].id;
                        localStorage.setItem("multilevelname", names[0].id);
                        $scope.$apply();
                    }
                    $scope.LoadConfig();
                }
            });
        }
        
    });
    $scope.updateMultilevelName = function(name) {
        localStorage.setItem("multilevelname", name);
        $scope.LoadConfig();
    };
    $scope.logout = function() {
        getshopclient.UserManager.logout();
        $state.go("login");
    };
};
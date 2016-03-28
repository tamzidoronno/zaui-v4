if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.mainpageController = function($scope, $state) {
    $scope.address = localStorage.getItem("address");
    var loadNames = getshopclient.StoreManager.getMultiLevelNames();
    $scope.hasOtherInstructions = false;
    $scope.hasFireInstructions = false;
    $scope.hasDoorControl = false;
    
    
    $scope.LoadConfig = function() {
        $scope.hasOtherInstructions = false;
        $scope.hasFireInstructions = false;
        $scope.hasDoorControl = false;
        var config = getshopclient.PmsManager.getConfiguration(getMultilevelName());
        config.done(function(res) {
            if(res.arxHostname) {
                $scope.hasDoorControl = true;
            }
            if(res.otherinstructions) {
                $scope.hasOtherInstructions = true;
            }
            if(res.fireinstructions) {
                $scope.hasFireInstructions = true;
            }
            $scope.$apply();
        });
    };
    
    loadNames.done(function(data) {
        console.log(data);
        var names = [];
        var current = getMultilevelName();
        for(var key in data) {
            if(!current) {
                current = data[key];
                localStorage.setItem("multilevelname", current);
            }
            names.push({"id": data[key], "name" : data[key]});
        }
        $scope.multilevelnames = names;
        $scope.dataSelected = current;
        $scope.$apply();
        
        $scope.LoadConfig();
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
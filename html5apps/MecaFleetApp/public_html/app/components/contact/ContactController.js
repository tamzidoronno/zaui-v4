if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.ContactController = function($scope, $api) {
    $scope.cellphone = localStorage.getItem("cellphone");
    $scope.settings = null;
    
    $scope.sendContact = function(res) {
        alert("Takk, vi vil ta kontakt med deg så snart som mulig");
    }
    
    $scope.messageSent = function() {   
        $scope.mailtext = "";
        $scope.$apply();
        alert("Takk, vi vil behandle din forespørsel så raskt som mulig");
    }
    
    $scope.sendMessage = function() {
        $api.getApi().MecaManager.sendEmail($scope.cellphone, $scope.mailtext).done($scope.messageSent);
    }
    
    $scope.askToBeContacted = function() {
        $api.getApi().MecaManager.callMe($scope.cellphone).done($scope.sendContact)
    }
    
    $scope.fetchData = function() {
        $api.getApi().StoreApplicationInstancePool.getApplicationInstance('c4998fc1-29bc-4bcd-877a-9a9fcafedad2').done(function(res) {
            if (res) {
                $scope.settings = res.settings;
                
                $scope.$apply();
                console.log($scope.settings);
            }
        })
    }
    
    $scope.fetchData();
}
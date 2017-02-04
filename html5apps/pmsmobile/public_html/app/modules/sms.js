if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.smsController = function($scope, $state) {
    $scope.sendmessage = function(msg) {
        $scope.isDisabled = true;
        var sending = getshopclient.PmsManager.sendMessageToAllTodaysGuests(getMultilevelName(), msg);
        sending.done(function() {
            alert('Message sent');
            $scope.isDisabled = false;
            $scope.loadSmsMessages();
        });
    };
    
    $scope.loadSmsMessages = function() {
        var end = new Date();
        var d = new Date();
        d.setDate(d.getDate()-3);
//        d.setTime(start);
        
        var data = getshopclient.MessageManager.getAllSmsMessages(d, end);
        data.done(function(res) {
            $scope.smsMessages = res;
            $scope.$apply();
            console.log(res);
        });
    }
    $scope.loadSmsMessages();
};
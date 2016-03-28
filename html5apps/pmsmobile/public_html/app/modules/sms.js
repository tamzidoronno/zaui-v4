if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.smsController = function($scope, $state) {
    $scope.sendmessage = function(msg) {
        $scope.isDisabled = true;
        var sending = getshopclient.PmsManager.sendMessageToAllTodaysGuests(getMultilevelName(), msg);
        sending.done(function() {
            alert('Message sent');
            $scope.isDisabled = false;
        });
    };
};
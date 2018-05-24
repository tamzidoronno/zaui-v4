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
    $scope.formatDate = function(date) {
        var today = date;
        var dd = today.getDate();
        var mm = today.getMonth()+1; //January is 0!

        var yyyy = today.getFullYear();
        if(dd<10){
            dd='0'+dd;
        } 
        if(mm<10){
            mm='0'+mm;
        } 
        
        var hour = today.getHours();
        var minute = today.getMinutes();
        if(hour < 10) { hour = "0" + hour; }
        if(minute < 10) { minute = "0" + minute; }
        
        var today = dd+'/'+mm+'/'+yyyy + " " + hour + ":" + minute;
        
        return today;
    },
    $scope.loadSmsMessages = function() {
        var end = new Date();
        var d = new Date();
        d.setDate(d.getDate()-3);
//        d.setTime(start);
        
        var data = getshopclient.MessageManager.getAllSmsMessages(d, end);
        data.done(function(res) {
            for(var k in res) {
                var sms = res[k];
                sms.createdDateFormatted = $scope.formatDate(new Date(sms.rowCreatedDate));
            }
            $scope.smsMessages = res;
            $scope.$apply();
            console.log(res);
        });
    }
    $scope.loadSmsMessages();
};
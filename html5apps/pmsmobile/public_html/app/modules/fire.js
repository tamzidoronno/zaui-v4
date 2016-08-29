if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.fireController = function($scope, $state) {
    var getconfig = getshopclient.PmsManager.getConfiguration(getMultilevelName());
    getconfig.done(function(res) {
        console.log(res);
        document.getElementById("fireinstruction").innerHTML = res.fireinstructions;
    });
}
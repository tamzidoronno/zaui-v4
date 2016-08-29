if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.otherInstructionController = function($scope, $state) {
    var getconfig = getshopclient.PmsManager.getConfiguration(getMultilevelName());
    getconfig.done(function(res) {
        document.getElementById("otherinstruction").innerHTML = res.otherinstructions;
    });
}
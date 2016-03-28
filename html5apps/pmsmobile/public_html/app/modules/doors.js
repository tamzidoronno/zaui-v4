if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.doorsController = function($scope, $state) {
    alert('tset');
    var loadDoors = getshopclient.DoorManager.getAllDoors(getMultilevelName());
    loadDoors.done(function(res) {
        console.log(res);
        this.doors = res;
    });
};
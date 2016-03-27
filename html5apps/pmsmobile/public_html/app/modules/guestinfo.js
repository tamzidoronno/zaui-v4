if(typeof(getshop) === "undefined") { var getshop = {}; }
getshop.guestInfoController = function($scope, $state, $stateParams) {
    alert($stateParams.bookingid);
    alert($stateParams.roomid);
}
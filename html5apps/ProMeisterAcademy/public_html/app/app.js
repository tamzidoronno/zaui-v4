angular.module('ProMeisterAcademy', [
    'ui.router'
]);


// Make sure to reconnect on startup
angular.module('ProMeisterAcademy').run(['$api', function ($api) {
    $api.reconnect();
}]);


// Filter date
angular.module('ProMeisterAcademy').filter('translate', function() {
    return function(inOut) {
        return inOut;
    }
});


angular.module('ProMeisterAcademy').filter('dateformatter', function() {
    return function(inDate) {
        if (!inDate)
            return "";
        
	var day = moment(new Date(inDate));
        return day.format("D / M - Y");
    };
});


angular.module('ProMeisterAcademy')
 .run( function($rootScope, $location) {
    if ( localStorage.getItem("loggedInUserId") == null || localStorage.getItem("loggedInUserId") === "") {
        $location.path( "/login" );
    } else if ($location.$$path === "") {
        $location.path( "/home" );
    }
 })
angular.module('MecaFleetApp', [
    'ui.router'
]);


// Make sure to reconnect on startup
angular.module('MecaFleetApp').run(['$api', function ($api) {
    $api.reconnect();
}]);


// Filter date
angular.module('MecaFleetApp').filter('dateformatter', function() {
    return function(inDate) {
        if (!inDate)
            return "";
        
	var day = moment(new Date(inDate));
        return day.format("D / M - Y");
    };
});

angular.module('MecaFleetApp').filter('dateformatterwithtime', function() {
    return function(inDate) {
        if (!inDate)
            return "";
        
	var day = moment(new Date(inDate));
        return day.format("D / M - Y HH:mm");
    };
});



angular.module('MecaFleetApp')
 .run( function($rootScope, $location) {
    
    if ( localStorage.getItem("loggedInUserId") == null || localStorage.getItem("loggedInUserId") === "") {
        $location.path( "/login" );
    } else if ($location.$$path === "") {
        $location.path( "/home" );
    }
 })
angular.module('TrackAndTrace', [
    'ui.router'   
]);


// Make sure to reconnect on startup
angular.module('TrackAndTrace').run(['$api', function ($api) {
    $api.reconnect();
}]);

angular.module('TrackAndTrace').factory('datarepository', function($api) {
    return adata;
});

angular.module('TrackAndTrace')
 .run( function($rootScope, $location) {
    $rootScope.$on("$locationChangeSuccess",function(event, next, current){
        window.scroll(0,0);
    });
    
    if ( localStorage.getItem("loggedInUserId") == null || localStorage.getItem("loggedInUserId") === "") {
        $location.path( "/login" );
    } else if ($location.$$path === "") {
        $location.path( "/home" );
    }
 })

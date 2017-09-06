logonError = "";

angular.module('TrackAndTrace', [
    'ui.router', 'bc.AngularKeypad', 'ngTouch', 'ngDialog'
]);

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


angular.module('TrackAndTrace').factory('datarepository', function($api) {
    return adata;
});

angular.module('TrackAndTrace').config(['$qProvider', function ($qProvider) {
    $qProvider.errorOnUnhandledRejections(false);
}]);

angular.module('TrackAndTrace').directive('ngRightClick', function($parse) {
    return function(scope, element, attrs) {
        var fn = $parse(attrs.ngRightClick);
        element.bind('contextmenu', function(event) {
            scope.$evalAsync(function() {
                event.preventDefault();
                fn(scope, {$event:event});
            });
        });
    };
});


angular.module('TrackAndTrace').factory('$exceptionHandler', 

    function($injector) {

        return function(exception, cause) {
            var $state = $injector.get("$state");
            var datarepository = $injector.get('datarepository');
            datarepository.lastError = exception;
            $state.transitionTo('base.errorhandler');
        };
    });
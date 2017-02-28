angular.module('TrackAndTrace', [
    'ui.router', 'bc.AngularKeypad'
]);


// Make sure to reconnect on startup
angular.module('TrackAndTrace').run(['$api', function ($api) {
    $api.reconnect();
}]);

angular.module('TrackAndTrace').factory('datarepository', function($api) {
    return adata;
    
});

angular.module('TrackAndTrace').factory('$exceptionHandler', 

    function() {
      return function(exception, cause) {
            exception.message += ' (caused by "' + cause + '")';
            alert("Unexpected error: \n" + exception.message + "\n" + exception.stack);
            throw exception;
      };
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


var app = document.URL.indexOf( 'http://' ) === -1 && document.URL.indexOf( 'https://' ) === -1;
if (app) {
    angular.module('TrackAndTrace').directive("ngMobileClick", [function () {
        return function (scope, elem, attrs) {
            elem.bind("touchstart", function (e) {
                e.preventDefault();
                e.stopPropagation();

                scope.$apply(attrs["ngMobileClick"]);
            });
        }
    }])
    angular.module('TrackAndTrace').directive("ngMobileClickEnd", [function () {
        return function (scope, elem, attrs) {
            elem.bind("touchend", function (e) {
                e.preventDefault();
                e.stopPropagation();

                scope.$apply(attrs["ngMobileClickEnd"]);
            });
        }
    }])
} else {
    angular.module('TrackAndTrace').directive("ngMobileClick", [function () {
        return function (scope, elem, attrs) {
            elem.bind("click", function (e) {
                e.preventDefault();
                e.stopPropagation();

                scope.$apply(attrs["ngMobileClick"]);
            });
        }
    }])
    angular.module('TrackAndTrace').directive("ngMobileClickEnd", [function () {
        return function (scope, elem, attrs) {
            elem.bind("click", function (e) {
                e.preventDefault();
                e.stopPropagation();

                scope.$apply(attrs["ngMobileClickEnd"]);
            });
        }
    }])
}

stopShowingOfGpsFetching = function() {
    $('.fetchingGpsCoordinates').hide();
}

startShowingOfGpsFetching = function() {
    window.scroll(0,0);
    $('.fetchingGpsCoordinates').show();
}
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
    
    if ( localStorage.getItem("username") == null || localStorage.getItem("password") === "") {
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

                scope.$evalAsync(attrs["ngMobileClick"]);
            });
        }
    }])
    angular.module('TrackAndTrace').directive("ngMobileClickEnd", [function () {
        return function (scope, elem, attrs) {
            elem.bind("touchend", function (e) {
                e.preventDefault();
                e.stopPropagation();

                scope.$evalAsync(attrs["ngMobileClickEnd"]);
            });
        }
    }])
} else {
    angular.module('TrackAndTrace').directive("ngMobileClick", [function () {
        return function (scope, elem, attrs) {
            elem.bind("click", function (e) {
                e.preventDefault();
                e.stopPropagation();

                scope.$evalAsync(attrs["ngMobileClick"]);
            });
        }
    }])
    angular.module('TrackAndTrace').directive("ngMobileClickEnd", [function () {
        return function (scope, elem, attrs) {
            elem.bind("click", function (e) {
                e.preventDefault();
                e.stopPropagation();
                scope.$evalAsync(attrs["ngMobileClickEnd"]);
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

var db = null;

angular.module('TrackAndTrace').run(function($state, $api) {
    document.addEventListener('deviceready', function() {
        db = window.sqlitePlugin.openDatabase({name: 'tntdatabase.db', location: 'default'});
        
        if (typeof(adata) !== "undefined" && adata) {
            adata.loadFromLocalStorage($state);
        }
        
        if (typeof(messagePersister) !== "undefined" && messagePersister) {
            messagePersister.load($api);
        }
        
    });
});

var timeout = (new Date).getTime();

$(document).on('keyup', function() { timeout = (new Date).getTime(); });
$(document).on('mouseup', function() { timeout = (new Date).getTime(); });

function idleTimeout() {
    var diff = ((new Date).getTime() - timeout)/1000;
    if(diff > 260) {
        
    }
    if(diff > 300) {
        window.location.href="/pmsterminal/";
    }
    setTimeout(idleTimeout, "1000");
}

function showWaitingOverLay() {
    $('.waitoverlay').show();
}

function hideWaitingOverLay() {
    $('.waitoverlay').hide();
}

idleTimeout();
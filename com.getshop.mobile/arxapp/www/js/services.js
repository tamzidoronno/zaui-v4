var arxAppServices = angular.module('ArxAppServices', ['ionic']);


arxAppServices.service('LoginService', ['GetshopService', '$q', function(getshop, $q) {
    return {
        loginUser: function(host, name, pw) {
            var deferred = $q.defer();
            var promise = deferred.promise;
 
            if (getshop.client.ArxManager.logonToArx(host, name, pw, false)) {
                deferred.resolve('Welcome ' + name + '!');
            } else {
                deferred.reject('Wrong credentials.');
            }
            promise.success = function(fn) {
                promise.then(fn);
                return promise;
            }
            promise.error = function(fn) {
                promise.then(null, fn);
                return promise;
            }
            return promise;
        }
    }
}]);

arxAppServices.factory('GetshopService', ['$window', function($window) {
  return {

    client: null,

    connectToGetshop: function() {
      // Connect to getshop web api
      this.client = new GetShopApiWebSocket("arx.getshop.com");
      this.client.setConnectedEvent(function () {
        console.log('Connected to getshop');
      });
      this.client.connect();

    }
  }
}]);

arxAppServices.factory('LocalStorage', ['$window', function($window) {
  return {
    set: function(key, value) {
      $window.localStorage[key] = value;
    },
    get: function(key, defaultValue) {
      console.log('retrieving ' + key);
      return $window.localStorage[key] || defaultValue;
    },
    setObject: function(key, value) {
      $window.localStorage[key] = JSON.stringify(value);
    },
    getObject: function(key) {
      return JSON.parse($window.localStorage[key] || '{}');
    }
  }
}]);
var arxAppServices = angular.module('ArxAppServices', ['ionic']);


arxAppServices.service('LoginService', ['GetshopService', '$q', function(getshop, $q) {
    return {
        loginUser: function(host, name, pw) {
            var deferred = $.Deferred();
 
            if(!host) {
                deferred.reject('Wrong credentials.');
                return deferred;
            }
 
            var res = getshop.client.ArxManager.logonToArx(host, name, pw, false);

            res.done(function(test) {
                if(test) {
                    deferred.resolve('Welcome ' + name + '!');
                } else {
                    deferred.reject('Wrong credentials.');
                }
            });
            
            return deferred;
        }
    }
}]);

arxAppServices.factory('GetshopService', ['$window', function($window) {
  return {

    client: null,

    connectToGetshop: function() {
      // Connect to getshop web api
        this.client = new GetShopApiWebSocket("arx.mpal.getshop.com", "31330");
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
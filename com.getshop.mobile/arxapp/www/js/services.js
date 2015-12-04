var arxAppServices = angular.module('ArxAppServices', ['ionic']);


arxAppServices.service('LoginService', ['GetshopService', '$q', function(getshop, $q) {
    return {
        loginUser: function(host, name, pw) {
            var deferred = $.Deferred();
 
            if(!host) {
                deferred.reject('Wrong credentials.');
                return deferred;
            }

            var connecting = getshop.connectToGetshop(host);
            connecting.done(function() {
                var res = getshop.client.ArxManager.logonToArx(host, name, pw, false);

                res.done(function(test) {
                    if(test === true) {
                        deferred.resolve('Welcome ' + name + '!');
                    } else {
                        deferred.reject('Wrong credentials.');
                    }
                });
            });
            
            return deferred;
        }
    }
}]);

arxAppServices.factory('GetshopService', ['$window','LocalStorage', function($window, LocalStorage) {
  return {

    client: null,

    connectToGetshop: function(host) {
      // Connect to getshop web api
      
      var postfixaddr = "getshop.com";
      
      var def = $.Deferred();
      if(!host) {
        var currentHost = LocalStorage.getObject("currentCredentials");
            host = currentHost.host;
            if(!currentHost || !currentHost.host) {
                console.log('Do not connect');
                return;
            }
        }
        var hostToUse = "arx_"+host+"." + postfixaddr;
        this.client = new GetShopApiWebSocket(hostToUse, "31332");
        this.client.connect();
        this.client.setConnectedEvent(function () {
        });
        var autocreate = this.client.StoreManager.autoCreateStore("arx_"+host);
        
        autocreate.done(function(store) {
            def.resolve();
        });
        return def;
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
angular.module('TrackAndTrace').factory('$api', [ '$state', '$rootScope', function ($state, $rootScope) {
    
    var getApiWrapper = function(state) {
        this.$state = state;
        
        
        this.setConnectionDetails = function(identifier) {
            this.api = new GetShopApiWebSocket('trackandtrace.getshop.com', '31332', identifier, true);
//            this.api = new GetShopApiWebSocket('trackandtrace.3.0.local.getshop.com', '31330', identifier, true);
//            this.api = new GetShopApiWebSocket('192.168.1.240', '31330', identifier, true);
//            this.api = new GetShopApiWebSocket('trackandtrace.3.0.mpal.getshop.com', '31330', identifier, true);

            this.api.setTransferCompletedFirstTimeAfterUnsentMessageSent(function() {
                $rootScope.$broadcast("refreshData", "");
            });
            
            this.api.setMessageCountChangedEvent(function() {
                $rootScope.$broadcast("messageCountChanged", "");
            });
            
            var me = this;
            
            this.api.setConnectedEvent(function() {
                $rootScope.$broadcast("connectionEstablished", "");
                $rootScope.$digest();
                
                me.logon(false);
            });
            
            this.api.addListener("com.thundashop.core.trackandtrace.Route", this.refreshRoute, me);
            this.api.addListener("com.thundashop.core.trackandtrace.DriverMessage", this.messageReceived, me);
            this.api.addListener("com.thundashop.core.trackandtrace.DriverRemoved", this.driverRemoved, me);
        },
                
        this.refreshRoute = function(route) {
            if (this.api.sessionId === route.sentFromSessionId)
                return;
  
            $rootScope.$broadcast("refreshRouteEven1", route);
            $rootScope.$apply();
            
        },
                
        this.messageReceived = function(msg) {
            if (this.api.sessionId === msg.sentFromSessionId)
                return;
  
            $rootScope.$broadcast("messageReceived", msg);
            $rootScope.$apply();
        },
                
        this.driverRemoved = function(msg) {
            if (this.api.sessionId === msg.sentFromSessionId)
                return;
  
            $rootScope.$broadcast("driverRemoved", msg);
            $rootScope.$apply();
        },
                
        this.logon = function(fromLogin) {
            var username = localStorage.getItem("username");
            var password = localStorage.getItem("password");
            
            $getShopApi = this.getApi();
            var me = this;
            
            $getShopApi.UserManager.logOn(username, password).done(function(user) {
                if (user.errorCode) {
                    if (fromLogin) {
                        alert("Wrong username or password, please try again.");
                    }
                    me.$state.transitionTo('base.login');
                    $('.loginbutton').find('.login-shower').remove();
                } else {
                    $getShopApi.sendUnsentMessages();
                    localStorage.setItem("loggedInUserId", JSON.stringify(user));
                    if (fromLogin)
                        me.$state.transitionTo('base.home');
                }
            });
        },
        
        this.getLoggedOnUser = function() {
            return JSON.parse(localStorage.getItem("loggedInUserId"));
        },
        
        /**
         * 
         * @returns GetShopApiWebSocket
         */
        this.getApi = function() {
            return this.api;
        }
        
        this.reconnect = function(fromLogin) {
            var username = localStorage.getItem("username");
            var password = localStorage.getItem("password");
            var company = localStorage.getItem("company");
            
            if (!username || !password) {
                if (fromLogin) {
                    alert("Wrong username or password, please try again .");
                }
             
                $('.loginbutton').find('.login-shower').remove();
                this.$state.transitionTo('base.login');
                return;
            }
            
            this.setConnectionDetails(company);
            
            $getShopApi = this.getApi();
            $getShopApi.connect();
            var me = this;
            
            this.logon(fromLogin);
        }
    }
    
    var apiWrapperRet = new getApiWrapper($state);
    return apiWrapperRet;
}]);
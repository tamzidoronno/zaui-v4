angular.module('TrackAndTrace').factory('$api', [ '$state', '$rootScope', function ($state, $rootScope) {
    
    var getApiWrapper = function(state) {
        this.$state = state;
        
        
        this.setConnectionDetails = function(identifier) {
//            this.api = new GetShopApiWebSocket('resturantsystem.getshop.com', '31332', identifier, false);
            this.api = new GetShopApiWebSocket('192.168.10.190', '31330', identifier, false);
//            this.api = new GetShopApiWebSocket('resturantsystem.3.0.mpal.getshop.com', '31330', identifier, false);
            
            this.api.setMessageCountChangedEvent(function() {
                $rootScope.$broadcast("messageCountChanged", "");
            });
            
            var me = this;
            
            this.api.setConnectedEvent(function() {
                $rootScope.$digest();
                me.logon(false);
                $rootScope.$broadcast("connectionEstablished", "");
                $rootScope.$digest();
                
            });
            
            var me = this;
            me.rootScope = $rootScope;
            
            this.api.addListener("com.thundashop.core.resturantmanager.RefreshMessage", this.refreshMessageReceived, me);
        },
                
        this.refreshMessageReceived = function(refreshMessage) {
            if (this.api.sessionId === refreshMessage.sentFromSessionId)
                return;
  
            $rootScope.$broadcast("refreshTable", refreshMessage.tableId);
        },
                
        this.logon = function(fromLogin) {
            var username = localStorage.getItem("username");
            var password = localStorage.getItem("password");
            
            $getShopApi = this.getApi();
            var me = this;
            
            $getShopApi.UserManager.logOn(username, password).done(function(user) {
                if (user.errorCode) {
                    alert("Wrong username or password, please try again.");
                    me.$state.transitionTo('base.login');
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
                    alert("Wrong username or password, please try again.");
                }
                
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
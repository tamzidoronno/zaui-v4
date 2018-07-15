angular.module('TrackAndTrace').factory('$api', [ '$state', '$rootScope', function ($state, $rootScope) {
    
    var getApiWrapper = function(state) {
        this.$state = state;
        
        
        this.setConnectionDetails = function(identifier) {
          gsisdevmode = false; this.api = new GetShopApiWebSocket('resturantsystem.getshop.com', '31332', identifier, false);
//            gsisdevmode = true; this.api = new GetShopApiWebSocket('utsiktenhotell.3.0.local.getshop.com', '31330', identifier, false);
//            this.api = new GetShopApiWebSocket('192.168.10.190', '31330', identifier, false);
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
            
            this.api.setGlobalErrorHandler(function(error) {
                if (error.errorCode == 1000010) {
                    var now = new Date().getTime();
                    var diff = now - this.lastShownError;
                    if (diff < 1000) {
                        return;
                    }
                    
                    this.lastShownError = new Date().getTime();
                    alert("Did not find the company you specified, please check your details.");
                    me.$state.transitionTo('base.login');
                    $('.loginbutton').find('.login-shower').remove();
                    this.lastShownError = new Date().getTime();
                } else if (error.errorCode == 13) {
                    var now = new Date().getTime();
                    var diff = now - this.lastShownError;
                    if (diff < 1000) {
                        return;
                    }
                    
                    
                    this.lastShownError = new Date().getTime();
                    logonError = "Wrong username or password, please try again.";
                    
                    me.$state.transitionTo('base.login');
                    $('.loginbutton').find('.login-shower').remove();
                    $('.logonError').show();
                    setTimeout(function() {
                        $('.logonError').hide();
                        logonError = "";
                    }, 3000);
                   this.lastShownError = new Date().getTime();
                } else {
                    if (error != null && error.errorCode  != null && error.errorCode == 26 && !localStorage.getItem('username')) {
                        return;
                    }
                    showErrorMessage(errorTextMatrix[error.errorCode]);
                }
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
                    logonError = "Wrong username or password, please try again.";
                    me.$state.transitionTo('base.login');
                    $('.loginbutton').find('.login-shower').remove();
                    $('.logonError').show();
                    setTimeout(function() {
                        $('.logonError').hide();
                        logonError = "";
                    }, 3000);
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
                    logonError = "Wrong username or password, please try again.";
                    $('.loginbutton').find('.login-shower').remove();
                    $('.logonError').show();
                    setTimeout(function() {
                        $('.logonError').hide();
                        logonError = "";
                    }, 3000);
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
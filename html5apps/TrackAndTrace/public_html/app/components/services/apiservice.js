angular.module('TrackAndTrace').factory('$api', [ '$state', '$rootScope', function ($state, $rootScope) {
    
    var getApiWrapper = function(state) {
        this.$state = state;
        this.lastShownError = 0;
        
        
        this.setConnectionDetails = function(identifier) {
            this.api = new GetShopApiWebSocket('trackandtrace.getshop.com', '31332', identifier, true);
//            this.api = new GetShopApiWebSocket('trackandtrace.3.0.local.getshop.com', '31330', identifier, true);
//            this.api = new GetShopApiWebSocket('192.168.10.190', '31330', identifier, true);
//            this.api = new GetShopApiWebSocket('trackandtrace.3.0.mpal.getshop.com', '31330', identifier, true);

            this.api.setMessageCountChangedEvent(function() {
                $rootScope.$broadcast("messageCountChanged", "");
            });
            
            this.api.setGlobalErrorHandler(function(error) {
                if (error.errorCode == 1000010) {
                    var now = new Date().getTime();
                    var diff = now - this.lastShownError;
                    if (diff < 1000) {
                        return;
                    }
                    
                    this.lastShownError = new Date().getTime();
                    me.showErrorMessage("Did not find the company you specified, please check your details.");
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
                    me.showErrorMessage("Wrong username or password, please try again.");
                    
                    me.$state.transitionTo('base.login');
                    $('.loginbutton').find('.login-shower').remove();
                    this.lastShownError = new Date().getTime();
                } else {
                    if (error != null && error.errorCode  != null && error.errorCode == 26 && !localStorage.getItem('username')) {
                        return;
                    }
                    this.showErrorMessage(errorTextMatrix[error.errorCode]);
                }
            });
            
            var me = this;
            
            this.api.setConnectedEvent(function() {
                $rootScope.$broadcast("connectionEstablished", "");
                $rootScope.$digest();
                
                me.logon(false);
            });
            
            this.api.listeners = [];
            this.api.addListener("com.thundashop.core.trackandtrace.Route", this.refreshRoute, me);
            this.api.addListener("com.thundashop.core.trackandtrace.DriverMessage", this.messageReceived, me);
            this.api.addListener("com.thundashop.core.trackandtrace.DriverRemoved", this.driverRemoved, me);
        },
              
        this.showErrorMessage = function(msg) {
            $('.gs_header_error_field').html(msg);
            $('.gs_header_error_field').show();
            
            setTimeout(function() {
                $('.gs_header_error_field').hide();
            }, 3000);

        },
                
        this.refreshRoute = function(route) {
            if (this.api.sessionId === route.sentFromSessionId)
                return;
            
            if (!localStorage.getItem("username")) {
                return;
            }
  
            $rootScope.$broadcast("refreshRouteEven1", route);
            $rootScope.$apply();
            
        },
                
        this.messageReceived = function(msg) {
            if (this.api.sessionId === msg.sentFromSessionId)
                return;
            
            if (!localStorage.getItem("username")) {
                return;
            }
  
            $rootScope.$broadcast("messageReceived", msg);
            $rootScope.$apply();
        },
                
        this.driverRemoved = function(msg) {
            if (this.api.sessionId === msg.sentFromSessionId)
                return;
            
            if (!localStorage.getItem("username")) {
                return;
            }
 
  
            $rootScope.$broadcast("driverRemoved", msg);
            $rootScope.$apply();
        },
                
        this.logon = function(fromLogin) {
            var username = localStorage.getItem("username");
            var password = localStorage.getItem("password");
            
            if (!username || !password) {
                return;
            }
            
            $getShopApi = this.getApi();
            var me = this;
            
            var deffered = $getShopApi.UserManager.logOn(username, password);
            deffered.done(function(user) {
                if($getShopApi) {
                    $getShopApi.sendUnsentMessages();
                }
                localStorage.setItem("loggedInUserId", JSON.stringify(user));

                if (fromLogin) {
                    me.loadDataAndGoToHome(me);
                }
            });
        },
                
        this.loadDataAndGoToHome = function($api) {
            $state.transitionTo('base.home', 
             {
                'action' : {
                    refreshData : true
                }
            });
        },
        
        this.getLoggedOnUser = function() {
            if (!localStorage.getItem("loggedInUserId")) {
                return null;
            }
            
            return JSON.parse(localStorage.getItem("loggedInUserId"));
        },
        
        /**
         * 
         * @returns GetShopApiWebSocket
         */
        this.getApi = function() {
            if (!this.api && localStorage.getItem("company")) {
                this.setConnectionDetails(localStorage.getItem("company"));
                this.api.connect();
            }
            
            return this.api;
        }
        
        this.reconnect = function(fromLogin) {
            var username = localStorage.getItem("username");
            var password = localStorage.getItem("password");
            var company = localStorage.getItem("company");
            
            if (!username || !password) {
                if (fromLogin) {
                    this.showErrorMessage("Wrong username or password, please try again .");
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
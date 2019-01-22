angular.module('TrackAndTrace').factory('$api', [ '$state', '$rootScope', function ($state, $rootScope) {
    
    var getApiWrapper = function(state) {
        this.$state = state;
        this.lastShownError = 0;
        
        this.fetchQueue = function() {
            var me = this;
            
            if (!me.api || !me.api.GdsManager || !localStorage.getItem("username")) {
                setTimeout(function() {
                    me.fetchQueue.apply(me);
                }, 5000);
                return;
            }
            
            var pullService = me.api.GdsManager.getMessageForUser(false, true);
            
            pullService.done(function(res) {
                try {
                    if (res) {
                        for (var i in res) {
                            var msg = res[i];
                            if (msg.className === "com.thundashop.core.trackandtrace.DriverRemoved") {
                                me.driverRemoved(msg);
                            }
                            if (msg.className === "com.thundashop.core.trackandtrace.DriverMessage") {
                                me.messageReceived(msg);
                            }
                            if (msg.className === "com.thundashop.core.trackandtrace.Route") {
                                me.refreshRoute(msg);
                            }
                        }
                    }
                } catch (ex) {
                    console.log(ex);
                }
                
                me.fetchQueue();
            });
            
            pullService.fail(function(res) {
                console.log("Restarting due to failed", me);
                setTimeout(function() {
                    me.fetchQueue.apply(me);
                }, 5000);
            });
        }
        
        this.showErrorMessage = function(text) {
            
        }
        
        this.setConnectionDetails = function(identifier) {
            identifier = identifier.toLowerCase();
            
            if (identifier === "prologix") {
                this.api = new GetShopApiWebSocket('prologix.getshop.com', '31332', identifier, true);
            } else if (identifier === "local") {
                this.api = new GetShopApiWebSocket('trackandtrace.3.0.local.getshop.com', '31330', identifier, true);
            } else if (identifier === "acculogix") {
                this.api = new GetShopApiWebSocket('trackandtrace.getshop.com', '31332', identifier, true);
            } else {
                alert("Warning, unknown company, please check your company inputs");
                this.api = new GetShopApiWebSocket('unkown.getshop.com', '31332', identifier, true);
            }

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
            this.api.listeners = [];
            
            setTimeout(function() {
                me.fetchQueue();
            }, 10000);
            
            this.api.sendUnsentMessages();
        },
              
        this.showErrorMessage = function(msg) {
            $('.gs_header_error_field').html(msg);
            $('.gs_header_error_field').show();
            
            setTimeout(function() {
                $('.gs_header_error_field').hide();
            }, 3000);

        },
                
        this.refreshRoute = function(route) {
            if (!localStorage.getItem("username")) {
                return;
            }
  
            $rootScope.$broadcast("refreshRouteEven1", route);
            $rootScope.$apply();  
        },
                
        this.messageReceived = function(msg) {
            if (!localStorage.getItem("username")) {
                return;
            }
  
            $rootScope.$broadcast("messageReceived", msg);
            $rootScope.$apply();
        },
                
        this.driverRemoved = function(msg) {
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
            
            var deffered = $getShopApi.UserManager.logOn(username, password, false, true);
            deffered.done(function(user) {
                if($getShopApi) {
                    $getShopApi.sendUnsentMessages();
                }
                localStorage.setItem("loggedInUserId", JSON.stringify(user));

                if (fromLogin) {
                    me.loadDataAndGoToHome(me);
                }
            });
            
            deffered.fail(function(res) {
                if (fromLogin) {
                    alert("Logon failed, make sure you entered correct username and password and that your device is online");
                    $('.login-shower').removeClass('login-shower');
                    $('.loginbutton').find('i').remove();
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
//            
            this.logon(fromLogin);
        }
    }
    
    var apiWrapperRet = new getApiWrapper($state);
    return apiWrapperRet;
}]);
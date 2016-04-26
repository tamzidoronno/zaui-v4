angular.module('ProMeisterAcademy').factory('$api', [ '$state', function ($state) {
    
    var getApiWrapper = function(state) {
        this.$state = state;
        
        /**
         * 
         * @returns GetShopApiWebSocket
         */
        this.getApi = function() {
            return this.api;
        }
        
        this.getLoggedInUser = function() {
            return JSON.parse(localStorage.getItem("loggedInUserId"));
        }
        
        this.reconnect = function(fromLogin) {
            var username = localStorage.getItem("username");
            var password = localStorage.getItem("password");
            
//            this.api = new GetShopApiWebSocket('promeisterse30.getshop.com', '31332');
            this.api = new GetShopApiWebSocket('promeisterse30.3.0.local.getshop.com', '31330');

            this.api.transferStarted = function() {
                $('.transferInProgress').addClass('transferInProgress_on');
            }
            
            this.api.transferCompleted = function() {
                $('.transferInProgress').removeClass('transferInProgress_on');
            }
            
            $getShopApi = this.getApi();
            $getShopApi.connect();
            var me = this;
            
            $getShopApi.UserManager.logOn(username, password).done(function(user) {
                localStorage.setItem("loggedInUserId", JSON.stringify(user));
                
                if (fromLogin && user.customerId)
                    me.$state.transitionTo('base.home');
            });
               
        }
    }
    
    var apiWrapperRet = new getApiWrapper($state);
    return apiWrapperRet;
}]);



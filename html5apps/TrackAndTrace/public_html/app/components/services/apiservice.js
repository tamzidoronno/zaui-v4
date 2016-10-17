angular.module('MecaFleetApp').factory('$api', [ '$state', '$rootScope', function ($state, $rootScope) {
    
    var getApiWrapper = function(state) {
        this.$state = state;
        
        
        this.setConnectionDetails = function(identifier) {
            this.api = new GetShopApiWebSocket('55377cf3-a5bc-459a-bd96-f00300db4df9.getshop.com.getshop.com', '31332', identifier);
//            this.api = new GetShopApiWebSocket('no.3.0.local.getshop.com', '31330', identifier);
//            this.api = new GetShopApiWebSocket('no.3.0.mpal.getshop.com', '31330', identifier);
            
            this.api.setConnectedEvent(function() {
                $rootScope.$broadcast("connectionEstablished", "");
                $rootScope.$digest();
            });
        }
        
        /**
         * 
         * @returns GetShopApiWebSocket
         */
        this.getApi = function() {
            return this.api;
        }
        
        this.reconnect = function(fromLogin) {
            var identifier = localStorage.getItem("identifier");
            var cellphone = localStorage.getItem("cellphone");
            
            if (!identifier || !cellphone) {
                if (fromLogin) {
                    alert("Feil verksted eller telefonnr, prøv igjen");
                }
                
                this.$state.transitionTo('base.login');
                return;
            }
            
            this.setConnectionDetails(identifier);
            $getShopApi = this.getApi();
            $getShopApi.connect();
            var me = this;
            
            $getShopApi.MecaManager.getCarsByCellphone(cellphone).done(function(res) {
                if (res.length > 0) {
                    localStorage.setItem("loggedInUserId", cellphone);
                    if (fromLogin)
                        me.$state.transitionTo('base.home');
                } else {
                    alert("Feil verksted eller telefonnr, prøv igjen");
                    me.$state.transitionTo('base.login');
                }
            });
        }
    }
    
    var apiWrapperRet = new getApiWrapper($state);
    return apiWrapperRet;
}]);



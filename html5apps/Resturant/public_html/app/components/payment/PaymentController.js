if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.PaymentController = function($scope, $rootScope, $api, $state, datarepository, $stateParams) {
    
    $scope.numbers = 10;
    $scope.numpadnumber = "";
    $scope.paymentMethodId = $stateParams.paymentMethodId;
    
    $scope.contains = function(a, obj) {
        for (var i = 0; i < a.length; i++) {
            if (a[i] === obj) {
                return true;
            }
        }
        return false;
    }
    
    $scope.getTotal = function() {
        var j = 0;
        
        for (var i in datarepository.cartItemsToPay) {
            var item = datarepository.cartItemsToPay[i];
            var product = datarepository.getProductById(item.productId);
            j += product.price;
        }
        
        return j;
    }
    
    $scope.getSelectedCount = function(product) {
        var j = 0;
        for (var i in datarepository.cartItemsToPay) {
            var item = datarepository.cartItemsToPay[i];
            if (item.productId == product.id) {
                j++;
            }
        }
        
        return j;
    }
    
    $scope.getSelectedProducts = function() {
        var retProducts = [];
        for (var i in datarepository.cartItemsToPay) {
            var cartItem = datarepository.cartItemsToPay[i];
            var product = datarepository.getProductById(cartItem.productId);
            if (!$scope.contains(retProducts, product)) {
                retProducts.push(product);
            }
        }
        
        return retProducts;
    }
    
    $scope.calculateRefund = function() {
        
        var number = $('#enteredNumpadNumber').val();
        if (!number) {
            return "-";
        }
        
        var entered = parseInt(number);
        var returnValue = $scope.getTotal()-entered;
        
        if (returnValue > 0) {
            $scope.returnValue = 0;
        } else {
            $scope.returnValue = returnValue;
        }
        
        return $scope.returnValue;
    }
    
    $scope.getIds = function() {
        var ids = [];
        for (var i in datarepository.cartItemsToPay) {
            var item = datarepository.cartItemsToPay[i];
            ids.push(item.id);
        }
        return ids;
    }
    
    $scope.markCompleted = function(ids) {
        for (var i in ids) {
            datarepository.forceRemoveCartItem(ids[i]);
        }
        datarepository.clearCheckoutList();
        
        if (!datarepository.getAllCartItems($stateParams.tableId).length) {
            $state.transitionTo('base.tableoverview', { tableId: $stateParams.tableId })
        } else {
            $state.transitionTo('base.checkouttable', { tableId: $stateParams.tableId })
        }
    }
    
    $scope.completePayment = function() {
        var ids = $scope.getIds();
        
        $api.getApi().ResturantManager.completePayment($scope.paymentMethodId, ids).done(function() {
            $scope.markCompleted(ids);
        });
    }
    
    $scope.loadRoomList = function() {
        var method = datarepository.getPaymentMethodById("f86e7042-f511-4b9b-bf0d-5545525f42de");
        var pmsManager = $api.getApi().PmsManager;
        
        var filter = {
            filterType: 'active',
            startDate: new Date(),
            endDate: new Date()
        };
        
        pmsManager.getSimpleRooms(method.settings.bookingengine.value, filter).done(function(res) {
            $scope.rooms = res;
            $scope.$apply();
        });
    }
    
    $scope.payOnRoom = function(room) {
        var ids = $scope.getIds();
        $api.getApi().ResturantManager.payOnRoom(room, ids).done(function() {
            $scope.markCompleted(ids);
        });
    }
    
    $scope.init = function() {
        // Pay on room
        if ($scope.paymentMethodId === "f86e7042-f511-4b9b-bf0d-5545525f42de") {
            $scope.loadRoomList();
        }
    }
    
    $scope.goBack = function() {
        if ($stateParams.tableId) {
            $state.transitionTo('base.checkouttable', { tableId: $stateParams.tableId })
        }
    }
    
    $scope.init();
}
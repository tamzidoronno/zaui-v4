if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.TableController = function($scope, $rootScope, $api, $state, datarepository, $stateParams) {
    $scope.table = datarepository.getTableById($stateParams.tableId);
    $scope.products = datarepository.getProducts();
    $scope.room = datarepository.getSelectedRoom();
    $scope.productLists = datarepository.getProductLists();
    $scope.selectedPerson = 0;
    
    $scope.getCount = function() {
        return 0;
    }
    
    $scope.goBack = function() {
        $state.transitionTo('base.home');
    }
    
    $scope.getProductsForList = function(productList) {
        var retList = [];
        for (var i in productList.productIds) {
            var product = datarepository.getProductById(productList.productIds[i]);
            if (product)
                retList.push(product);
        }
        return retList;
    }
    
    $scope.getPersonName = function() {
        if ($scope.selectedPerson == 0) {
            return "General";
        }
        return "Person " + $scope.selectedPerson;
    }
    
    $scope.changeSelectedPerson = function(up) {
        if (up) {
            $scope.selectedPerson++;
        } else {
            $scope.selectedPerson--;
        }
        
        if ($scope.selectedPerson < 0) {
            $scope.selectedPerson = 0;
        }
    }
    
    $scope.addToCart = function(product) {
        datarepository.addToCart(product.id, $scope.selectedPerson, $scope.table.id);
        $scope.currentSwiping = null;
    }
    
    $scope.removeProduct = function(product) {
        datarepository.removeCartItem(product.id, $scope.selectedPerson, $scope.table.id);
    }
    
    $scope.canSendToKitched = function() {
        var cartItems = datarepository.getAllCartItems($scope.table.id);
        
        for (var i in cartItems) {
            if (!cartItems[i].sentToKitchen) {
                return true;
            }
        }
        
        if (datarepository.getDeletedCartItems($scope.table.id).length > 0) {
            return true;
        }
        
        return false;
    }
    
    $scope.toggleMenu = function() {
        if ($('.tablemenu').is(':visible')) {
            $('.tablemenu').slideUp();
        } else {
            $('.tablemenu').slideDown();
        }
    }
    
    $scope.sendToKitchen = function() {
        var cartItems = datarepository.getAllCartItems($scope.table.id);
        var resturantManager = $api.getApi().ResturantManager;
        resturantManager.addCartItems(cartItems, $scope.table.id);
        
        for (var i in cartItems) {
            var cartItem = cartItems[i];
            cartItem.sentToKitchen = true;
        }
        
        datarepository.clearDeletedItems($scope.table.id);
        datarepository.saveCartItems();
    }
    
    $scope.showLoadOldSessions = function() {
        $state.transitionTo('base.tableloadsession', { tableId: $scope.table.id });
    }
    
    $scope.createNewSession = function() {
        if ($scope.canSendToKitched()) {
            alert("Can not create new session, you have orders that has not been sent yet");
            return;
        }
        
        var resturantManager = $api.getApi().ResturantManager;
        
        resturantManager.createTableSession($scope.table.id);
        datarepository.clearCartItemsForTable($scope.table.id);
    }
    
    $scope.getProductCount = function(product) {
        var cartItems = datarepository.getAllCartItems($scope.table.id);
        var count = 0;
        for (var i in cartItems) {
            var cartItem = cartItems[i];
            if (cartItem.productId == product.id && (!$scope.selectedPerson || $scope.selectedPerson == cartItem.tablePersonNumber)) {
                count++;
            }
        }
        
        return count;
    }
 
};
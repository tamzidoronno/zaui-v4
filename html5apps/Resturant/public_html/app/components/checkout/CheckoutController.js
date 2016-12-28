if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.CheckoutController = function($scope, $rootScope, $api, $state, datarepository, $stateParams) {
    $scope.total = 0;
    $scope.tables = [];
    $scope.tables[0] = datarepository.getTableById($stateParams.tableId);
    $scope.paymentMethods = datarepository.getActivatedPaymentMethods();
    
    $scope.transferAllItemForTableAndPerson = function(table, personnumber) {
        var items = $scope.getCartItems(table, personnumber);
        for (var i in items) {
            datarepository.cartItemsToPay.push(items[i]);
        }
        
        datarepository.saveItemsToPay();
    }
    
    $scope.addSingleProduct = function(product, table, personnumber) {
        var items = $scope.getCartItems(table, personnumber);
        for (var i in items) {
            var item = items[i];
            if (item.productId == product.id) {
                datarepository.cartItemsToPay.push(items[i]);
                datarepository.saveItemsToPay();
                return;
            }
        }
    }
    
    $scope.goBack = function() {
        $state.transitionTo('base.tableoverview', {tableId: $scope.tables[0].id});
    }
    
    $scope.getTables = function() {
        return $scope.tables;
    }
    
    $scope.getProductCount = function(product, table, personnumber) {
        var items = $scope.getCartItems(table, personnumber, true);
        var j = 0;
        
        for (var i in items) {
            var item = items[i];
            if (item.productId === product.id) {
                j++;
            }
        }
        
        return j;
    }
    
    $scope.getAllCartItems = function(tableId) {
        return datarepository.getAllCartItems(tableId);
    }
    
    $scope.getCartItems = function(table, tablePersonNumber, filter) {
        var items = $scope.getAllCartItems(table.id);
        var retItems = [];
        
        for (var i in items) {
            var item = items[i];
            if (item.tablePersonNumber === tablePersonNumber) {
                if (filter) {
                    if (!$scope.containsWithIdCheck(datarepository.cartItemsToPay, item)) {
                        retItems.push(item);
                    }
                } else {
                    if (!$scope.contains(datarepository.cartItemsToPay, item)) {
                        retItems.push(item);
                    }
                }
            }
        }
        
        return retItems;
    }
    
    $scope.getProducts = function(table, tablePersonNumber) {
        var items = $scope.getAllCartItems(table.id);
        var retItems = [];
        
        for (var i in items) {
            var item = items[i];
            if (item.tablePersonNumber === tablePersonNumber) {
                var product = datarepository.getProductById(item.productId);
                if (!$scope.contains(retItems, product)) {
                    retItems.push(product);
                }
                
            }
        }
        
        return retItems;
    }
 
    $scope.getPersons = function(table) {
        var items = $scope.getAllCartItems(table.id);
        var retItems = [];
        
        for (var i in items) {
            var item = items[i];
            if (!$scope.contains(retItems, item.tablePersonNumber)) {
                retItems.push(item.tablePersonNumber);
            }
        }
        
        return retItems;
    }
    
    $scope.contains = function(a, obj) {
        for (var i = 0; i < a.length; i++) {
              if (a[i] === obj) {
                return true;
            }
        }
        return false;
    },
            
    $scope.containsWithIdCheck = function(a, obj) {
        for (var i = 0; i < a.length; i++) {
            if (typeof(a[i].id) !== "undiefined") {
                if (a[i].id == obj.id) {
                    return true;
                }
            }
            
            if (a[i] === obj) {
                return true;
            }
        }
        return false;
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
    
    $scope.goToPaymentWindow = function(paymentMethod) {
        $state.transitionTo('base.paymentwindow', {tableId: $scope.tables[0].id, paymentMethodId: paymentMethod.id})
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
    
    $scope.getPaymentName = function(paymentApp) {
        if (paymentApp.appName == "PayOnDelivery")
            return "Kontant";
        
        return paymentApp.appName;
    }
    
    $scope.clearSavedCheckoutList = function() {
        datarepository.clearCheckoutList();
    }
    
    $scope.removeProduct = function(product) {
        for (var i in datarepository.cartItemsToPay) {
            var item = datarepository.cartItemsToPay[i];
            if (item.productId === product.id) {
                datarepository.cartItemsToPay.splice(i, 1);
                datarepository.saveItemsToPay();
                return;
            }
        }
    }
    
    $scope.clearSavedCheckoutList();
}

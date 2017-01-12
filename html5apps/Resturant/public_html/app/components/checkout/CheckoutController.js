if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.CheckoutController = function($scope, $rootScope, $api, $state, datarepository, $stateParams) {
    $scope.total = 0;
    $scope.tables = datarepository.getCurrentlyCheckingOutTables($stateParams.tableId);
    $scope.paymentMethods = datarepository.getActivatedPaymentMethods();
    $scope.productLists = datarepository.getProductLists();
    $scope.standalone = $stateParams.tableId === "direct";
    $scope.rooms = datarepository.rooms;
    
    $scope.closeAddTable = function() {
        $('.checkout_addTable').hide();
    }
    
    $scope.toggleSticky = function(listId) {
        datarepository.toggleStickList(listId);    
    }
    
    $scope.isListSticky = function(listId) {
        return datarepository.isListSticky(listId);
    }
    
    $scope.toggleHiddenState = function(listId) {
        var isVisible = $('.productlistinnner[listid="'+listId+'"]').is(':visible');
        $('.productlistinnner').addClass('hidden');
        if (!isVisible) {
            $('.productlistinnner[listid="'+listId+'"]').removeClass('hidden');
        }   
    }
    
    $scope.showAddTable = function() {
        $('.checkout_addTable').show();
    }
    
    $scope.addTable = function(table) {
        for (var i in $scope.tables) {
            if ($scope.tables[i].id == table.id) {
                return;
            }
        }
        
        datarepository.addCurrentlyCheckingOutTables(table);
        $scope.tables = datarepository.getCurrentlyCheckingOutTables($stateParams.tableId);
        $scope.closeAddTable();
    }
            
    $scope.changePrice = function(product) {
        var value = prompt("Price", product.price);
        
        if (value === null)
            return;
        
        datarepository.setNewTemporaryProductPrice(product.id, value);
    }
    
    $scope.transferAllItemForTableAndPerson = function(table, personnumber) {
        var items = $scope.getCartItems(table, personnumber);
        for (var i in items) {
            datarepository.cartItemsToPay.push(items[i]);
        }
        
        datarepository.saveItemsToPay();
    }
    
    $scope.addToCart = function(product) {
        var tableId = $scope.tables[0] ? $scope.tables[0].id : "direct";
        var item = datarepository.addToCart(product.id, 0, tableId);
        datarepository.cartItemsToPay.push(item);
        datarepository.save();
        
        $('.productcartbutton[productid="'+product.id+'"]').css({ 'background-color': "green" });
        setTimeout(function() {
            $('.productcartbutton[productid="'+product.id+'"]').css({ 'background-color': "#6c9bc6" });
        }, 100)
    }
   
    $scope.getProductsForList = function(productList) {
        var retList = [];
        for (var i in productList.productIds) {
            var product = datarepository.getProductById(productList.productIds[i]);
            if (product)
                retList.push(product);
        }
        
        retList.sort(function(a, b){
            if(a.name < b.name) return -1;
            if(a.name > b.name) return 1;
            return 0;
        });
        
        return retList;
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
    
    $scope.closeCheckoutList = function() {
        $('.checkout_productList').hide();
    }
    
    $scope.showMoreProducts = function() {
        $('.checkout_productList').show();
    }
    
    $scope.goBack = function() {
        if (!$scope.tables[0]) {
            $state.transitionTo('base.home', {});    
            return;
        }
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
        if (tableId === "direct")
            return [];
        
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
        if (!table)
            return [];
        
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
        
        retProducts.sort(function(a, b){
            if(a.name < b.name) return -1;
            if(a.name > b.name) return 1;
            return 0;
        });
        
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
        var tableId = $scope.tables[0] ? $scope.tables[0].id : "direct";
        $state.transitionTo('base.paymentwindow', {tableId: tableId, paymentMethodId: paymentMethod.id})
    }
    
    $scope.getTotal = function() {
        return datarepository.getTotal();
    }
    
    $scope.getPaymentName = function(paymentApp) {
        if (paymentApp.appName == "PayOnDelivery")
            return "Kontant";
        
        return paymentApp.appName;
    }
    
    $scope.removeProduct = function(product) {
        for (var i in datarepository.cartItemsToPay) {
            var item = datarepository.cartItemsToPay[i];
            if (item.productId === product.id) {
                if (!item.sentToKitchen) {
                    datarepository.forceRemoveCartItem(item.id);
                }
                datarepository.cartItemsToPay.splice(i, 1);
                datarepository.saveItemsToPay();
                return;
            }
        }
    }
}

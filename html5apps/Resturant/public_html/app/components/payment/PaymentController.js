if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.PaymentController = function($scope, $rootScope, $api, $state, datarepository, $stateParams) {
    
    $scope.numbers = 10;
    $scope.numpadnumber = "";
    $scope.paymentMethodId = $stateParams.paymentMethodId;
    $scope.printers = datarepository.getPrinters();
    $scope.pincode = "";
    $scope.errorMessage = "";
    
    $scope.getStars = function() {
        var text = "";
        for (var i in $scope.pincode) {
            text += "*";
        }
        return text;
    }
    
    $scope.checkPinCode = function($event, numbers) {
        $api.getApi().ResturantManager.checkPinCode(numbers, $stateParams.bookingId, $stateParams.pmsRoomId).done(function(res) {
            
            if (!res.length && res instanceof Array) {
                $scope.errorMessage = "Wrong pincode, please try again";
                $scope.pincode = "";
                $scope.$evalAsync();
            } else {
                $scope.payOnRoom(res);
            }
        });
    }
    
    
    $scope.contains = function(a, obj) {
        for (var i = 0; i < a.length; i++) {
            if (a[i] === obj) {
                return true;
            }
        }
        return false;
    }
    
    $scope.getTotal = function() {
        return datarepository.getTotal();
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
            var overridePrice = datarepository.getOveriddenPrice(item.productId);
            
            if (overridePrice && overridePrice.price) {
                item.discountedPrice = overridePrice.price;
                item.useDiscountedPrice = true;
            } else {
                item.useDiscountedPrice = false;
            }
            
            ids.push(item);
        }
        return ids;
    }
    
    $scope.receiptFinished = function() {
        if ($stateParams.tableId === "direct") {
            $state.transitionTo('base.checkouttable', { tableId: $stateParams.tableId })
            return;
        }
        
        if (!datarepository.getAllCartItems($stateParams.tableId).length) {
            $state.transitionTo('base.tableoverview', { tableId: $stateParams.tableId })
        } else {
            $state.transitionTo('base.checkouttable', { tableId: $stateParams.tableId })
        }
    }
    
    $scope.printInvoice = function(printer) {
        var orderId = $stateParams.orderId;
        var printerId = printer.id;
        $api.getApi().OrderManager.printInvoice(orderId, printerId);
    }
    
    $scope.markCompleted = function(ids, order, room) {
        for (var i in ids) {
            datarepository.forceRemoveCartItem(ids[i].id);
        }

        datarepository.clearCheckoutList();
        
        if (order !== null) {
            $state.transitionTo('base.printInvoice', { tableId: $stateParams.tableId, orderId: order.id });
            return;
        }
        
        if (room) {
            var msg = "Items put on room: " + room.room;
            if (room.guest && room.guest[0]) {
                msg += ", Guest: " + room.guest[0].name;
            }
            
            alert(msg);
        }
        
        $scope.receiptFinished();
    }
    
    $scope.getTotalForOrder = function(order) {
        var total = 0;
        for (var i in order.cart.items) {
            var item = order.cart.items[i];
            var productPrice = item.product.price;
            var totalForProduct = productPrice * item.count;
            total += totalForProduct;
        }
        
        return total;
    }
    
    $scope.showErrorMessage = function() {
        alert("The order created is not the same amount as what you have stored on your device, check if the products are updated. This should not happen and will cause problems.");
    }
    
    $scope.prePrintInvoice = function(printer) {
        var ids = $scope.getIds();
        $api.getApi().ResturantManager.prePrint($scope.paymentMethodId, ids, printer.id).done(function(res) {
            alert("Forhåndsvisning skrevet ut."); 
       });
    }
    
    $scope.completePayment = function() {
        var ids = $scope.getIds();
        
        $api.getApi().ResturantManager.isOrderPriceCorrect($scope.paymentMethodId, ids, datarepository.getTotal()).done(function(res) {
            if (!res) {
                $scope.showErrorMessage();
                return;
            }
            
            $api.getApi().ResturantManager.completePayment($scope.paymentMethodId, ids).done(function(order) {
                var totalFromOrder = $scope.getTotalForOrder(order);

                if (totalFromOrder != datarepository.getTotal()) {
                    $scope.showErrorMessage();
                    return;
                }

                $scope.markCompleted(ids, order);
            });
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
        
        if (!method.settings.bookingengine) {
            alert("Pay On Room paymentmethod is not setup correctly, check that you have specified the booking engine name under settings");
            return;
        }
        
        pmsManager.getSimpleRooms(method.settings.bookingengine.value, filter).done(function(res) {
            $scope.rooms = res;
            
            $scope.rooms.sort(function(a, b){
                if(a.room < b.room) return -1;
                if(a.room > b.room) return 1;
                return 0;
            });
            
            $scope.removeRoomsNotCheckedInYet();
            $scope.$evalAsync();
        });
    }
    
    $scope.removeRoomsNotCheckedInYet = function() {
        if (!$scope.rooms)
            return;
        
        var now = new Date();
        
        var newArray = new Array();
        for (var i in $scope.rooms) {
            var room = $scope.rooms[i];
            var start = new Date(room.start);
            start.setMinutes(0);
            start.setSeconds(0);
            start.setHours(5);
            
            if (start > now) {
                // Ignoring
            } else {
                newArray.push(room);
            }
        }
        
        $scope.rooms = newArray;
    }
    
    $scope.askForPincode = function(room) {
        if (datarepository.shouldAskForPincode()) {
            $state.go('base.askforpincode', { bookingId: room.bookingId, pmsRoomId: room.pmsRoomId, tableId: $stateParams.tableId, paymentMethodId: $stateParams.paymentMethodId });
        } else {
            $scope.payOnRoom(room);
        }
    }
    
    $scope.payOnRoom = function(room) {
        var ids = $scope.getIds();
        $api.getApi().ResturantManager.payOnRoom(room, ids).done(function() {
            $scope.markCompleted(ids, null, room);
        });
    }
    
    $scope.init = function() {
        // Pay on room
        if ($scope.paymentMethodId === "f86e7042-f511-4b9b-bf0d-5545525f42de") {
            $scope.loadRoomList();
        }
    }
    
    $scope.goBack = function() {
        if ($stateParams.pmsRoomId) {
            $state.transitionTo('base.paymentwindow', {tableId: $stateParams.tableId, paymentMethodId: $stateParams.paymentMethodId})
            return;
        }
        if ($stateParams.tableId) {
            $state.transitionTo('base.checkouttable', { tableId: $stateParams.tableId })
            return;
        }
        
    }
    
    $scope.init();
}
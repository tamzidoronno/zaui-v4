/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


adata = {
    selectedRoom: "",
    products: [],
    rooms: [],
    productLists: [],
    cartItems: [],
    deletedItems: [],
    cartItemsToPay: [],
    paymentMethods: [],
    temporaryProductPrices: [],
    currentlyTablesToCheckout: [],
    
    setActivatedPaymentMethods: function(methods) {
        for (var i in this.paymentMethods) {
            var oldMethod = this.paymentMethods[i];
            for (var j in methods) {
                if (methods[j].id === oldMethod.id) {
                    methods[j].isActiveForDevice = oldMethod.isActiveForDevice;
                }
            }
        }
        
        this.paymentMethods = methods;
        this.save();
    },
    
    getPrinters: function() {
        return this.printers;
    },
    
    setPrinters: function(printers) {
        this.printers = printers;
        this.save();
    },
    
    addCurrentlyCheckingOutTables: function(table) {
        this.currentlyTablesToCheckout.push(table);
        this.saveItemsToPay();
    },
    
    getCurrentlyCheckingOutTables: function(tableId) {
        var retValues = [];
        
        retValues.push(this.getTableById(tableId));
        
        for (var i in this.currentlyTablesToCheckout) {
            retValues.push(this.currentlyTablesToCheckout[i]);
        }
        
        
        return retValues;
    },
    
    getActivatedPaymentMethods: function() {
        var ret = [];
        
        $(this.paymentMethods).each(function() {
            if (this.isActiveForDevice)
                ret.push(this);
        });
        
        return ret;
    },
    
    setProducts: function(products) {
        this.products = products;
        this.save();
    },
    
    setRooms: function(rooms) {
        this.rooms = rooms;
        this.save();
    },
    
    setProductLists: function(lists) {
        this.productLists = lists;
    },
    
    getPaymentMethodById: function(id) {
        for (var i in this.paymentMethods) {
            var pay = this.paymentMethods[i];
            if (pay.id == id) {
                return pay;
            }
        }
        
        return null;
    },
    
    setSelectedRoom: function(roomId) {
        this.selectedRoom = roomId;
        this.save();
    },
    
    getSelectedRoom: function() {
        if (this.selectedRoom) {
            for (var i in this.rooms) {
                var room = this.rooms[i];
                if (room.id == this.selectedRoom) {
                    return room;
                }
            }
        }
        
        return null;
    },

    finalizeProduct: function(product) {
        var overriddenPrice = this.getOveriddenPrice(product.id);
        
        if (overriddenPrice && overriddenPrice.price !== "") {
            product.tempDiscountedPrice = overriddenPrice.price;
        } else {
            product.tempDiscountedPrice = null;
        }
    },
    
    getProductById: function(productId) {
        for (var i in this.products) {
            if (this.products[i].id == productId) {
                var retProduct = this.products[i];
                this.finalizeProduct(retProduct);
                return retProduct;
            }
        }
        
        return null;
    },
    
    getTableById: function(tableId) {
        if (this.selectedRoom) {
            for (var i in this.rooms) {
                var room = this.rooms[i];
                for (var j in room.tables) {
                    if (room.tables[j].id == tableId) {
                        return room.tables[j];
                    }
                }
            }
        }
        
        return null;
    },
    
    getProducts: function() {
        return this.products;
    },
    
    getProductLists: function() {
        return this.productLists;
    },
    
    saveItemsToPay: function() {
        localStorage.setItem("cartItemsToPay", JSON.stringify(this.cartItemsToPay));
        localStorage.setItem("temporaryProductPrices", JSON.stringify(this.temporaryProductPrices));
        localStorage.setItem("currentlyTablesToCheckout", JSON.stringify(this.currentlyTablesToCheckout));
    },
    
    save: function() {
        localStorage.setItem("selectedRoom", this.selectedRoom);
        localStorage.setItem("rooms", JSON.stringify(this.rooms));
        localStorage.setItem("products", JSON.stringify(this.products));
        localStorage.setItem("productlists", JSON.stringify(this.productLists));
        localStorage.setItem("paymentMethods", JSON.stringify(this.paymentMethods));
        localStorage.setItem("printers", JSON.stringify(this.printers));
    },
    
    load: function() {
        this.selectedRoom = localStorage.getItem("selectedRoom");
        this.rooms = JSON.parse(localStorage.getItem("rooms"));
        this.products = JSON.parse(localStorage.getItem("products"));
        this.productLists = JSON.parse(localStorage.getItem("productlists"));
        this.cartItems = JSON.parse(localStorage.getItem("cartItems"));
        this.paymentMethods = JSON.parse(localStorage.getItem("paymentMethods"));
        this.cartItemsToPay = JSON.parse(localStorage.getItem("cartItemsToPay"));
        this.temporaryProductPrices = JSON.parse(localStorage.getItem("temporaryProductPrices"));
        this.currentlyTablesToCheckout = JSON.parse(localStorage.getItem("currentlyTablesToCheckout"));
        this.printers = JSON.parse(localStorage.getItem("printers"));
        
        if (!this.cartItems) 
            this.cartItems = [];
        
        if (!this.cartItemsToPay) 
            this.cartItemsToPay = [];
        
        if (!this.temporaryProductPrices) 
            this.temporaryProductPrices = [];
        
        if (!this.currentlyTablesToCheckout) 
            this.currentlyTablesToCheckout = [];
        
        if (!this.printers) 
            this.printers = [];
    },
    
    getDeletedCartItems: function(tableId) {
        var retItems = [];
        
        for (var i in this.deletedItems) {
            if (this.deletedItems[i].tableId === tableId) {
                retItems.push(this.deletedItems[i]);
            }
        }
        
        return retItems;
    },
    
    clearDeletedItems: function(tableId) {
        var newSet = [];
        
        for (var i in this.deletedItems) {
            if (this.deletedItems[i].tableId !== tableId) {
                newSet.push(tableId);
            }
        }
        
        this.deletedItems = newSet;
    },
    
    setCartItems: function(items, tableId) {
        this.clearCartItemsForTable(tableId);
        var table = this.getTableById(tableId);
        
        if (table)
            table.refreshing = false;
        
        for (var i in items) {
            items[i].sentToKitchen = true;
            this.cartItems.push(items[i]);
        }
        this.saveCartItems();
    },

    getAllCartItems: function(tableId) {
        var retItems = [];
        for (var i in this.cartItems) {
            var retItem = this.cartItems[i];
            if (retItem.tableId == tableId) {
                retItems.push(retItem);
            }
        }
        
        return retItems;
    },
    
    forceRemoveCartItem: function(cartItemId) {
        for (var i in this.cartItems) {
            var item = this.cartItems[i];
            if (item.id === cartItemId) {
                var removedItems = this.cartItems.splice(i, 1);
                this.saveCartItems();
                return;
            }
        }
        
    },
    
    clearCheckoutList: function() {
        this.cartItemsToPay = [];
        this.temporaryProductPrices = [];
        this.currentlyTablesToCheckout = [];
        this.saveItemsToPay();
    },
    
    removeCartItem: function(productId, tablePersonNumber, tableId) {
        for (var i in this.cartItems) {
            var item = this.cartItems[i];
            if (item.productId == productId && item.tableId == tableId && item.tablePersonNumber == tablePersonNumber) {
                var removedItems = this.cartItems.splice(i, 1);
                this.saveCartItems();
                
                if (removedItems[0].sentToKitchen) {
                    this.deletedItems.push(removedItems[0]);
                }
                
                return;
            }
        }
    },
    
    isStandAlone: function() {
        return localStorage.getItem("standalone") === "true";
    },
    
    toggleStandAlone: function() {
        if (this.isStandAlone()) {
            localStorage.setItem("standalone", "false");
        } else {
            localStorage.setItem("standalone", "true");
        }
    },
    
    getOveriddenPrice: function(productId) {
        for (var i in this.temporaryProductPrices) {
            var old = this.temporaryProductPrices[i];
            if (old.productId === productId) {
                return old;
            }
        }
        
        return null;
    },
    
    setNewTemporaryProductPrice: function(productId, price) {
        var overridenPrice = this.getOveriddenPrice(productId);
        if (overridenPrice) {
            overridenPrice.price = price;
        } else {
            overridenPrice = {
                productId : productId,
                price: price
            }
            this.temporaryProductPrices.push(overridenPrice);
        }
        
        localStorage.setItem("temporaryProductPrices", JSON.stringify(this.temporaryProductPrices));
    },
    
    getTotal: function() {
        var j = 0;
        
        for (var i in this.cartItemsToPay) {
            var item = this.cartItemsToPay[i];
            var product = this.getProductById(item.productId);
            if (product.tempDiscountedPrice) {
                j += parseInt(product.tempDiscountedPrice);
            } else {
                j += product.price;
            }
        }
        
        return j;
    },
    
    addToCart : function(productId, tablePersonNumber, tableId, variation, option) {
        var cartItem = {
            productId: productId,
            tablePersonNumber: tablePersonNumber,
            tableId: tableId,
            id: this.guid(), 
            sentToKitchen: false,
            options: {}
        };
        
        if (variation) {
            cartItem.options[variation.id] = option.id;
        }
        
        this.cartItems.push(cartItem);
        this.saveCartItems();
        return cartItem;
    },
    
    saveCartItems: function() {
        localStorage.setItem("cartItems", JSON.stringify(this.cartItems));
    },
    
    clearCartItems: function() {
        this.cartItems = [];
        this.saveCartItems();
    },
    
    clearCartItemsForTable: function(tableId) {
        var newCartItems = [];
        for (var i in this.cartItems) {
            if (this.cartItems[i].tableId != tableId) {
                newCartItems.push(this.cartItems[i]);
            }
            
            if (this.cartItems[i].tableId == tableId && !this.cartItems[i].sentToKitchen) {
                newCartItems.push(this.cartItems[i]);
            }
        }
        
        this.cartItems = newCartItems;
        this.saveCartItems();
    },
    
    guid: function() {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
          s4() + '-' + s4() + s4() + s4();
    }
}

adata.load();
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
    
    setActivatedPaymentMethods: function(methods) {
        this.paymentMethods = methods;
        this.save();
    },
    
    getActivatedPaymentMethods: function() {
        return this.paymentMethods;
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

    getProductById: function(productId) {
        for (var i in this.products) {
            if (this.products[i].id == productId) {
                return this.products[i];
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
    },
    
    save: function() {
        localStorage.setItem("selectedRoom", this.selectedRoom);
        localStorage.setItem("rooms", JSON.stringify(this.rooms));
        localStorage.setItem("products", JSON.stringify(this.products));
        localStorage.setItem("productlists", JSON.stringify(this.productLists));
        localStorage.setItem("paymentMethods", JSON.stringify(this.paymentMethods));
    },
    
    load: function() {
        this.selectedRoom = localStorage.getItem("selectedRoom");
        this.rooms = JSON.parse(localStorage.getItem("rooms"));
        this.products = JSON.parse(localStorage.getItem("products"));
        this.productLists = JSON.parse(localStorage.getItem("productlists"));
        this.cartItems = JSON.parse(localStorage.getItem("cartItems"));
        this.paymentMethods = JSON.parse(localStorage.getItem("paymentMethods"));
        this.cartItemsToPay = JSON.parse(localStorage.getItem("cartItemsToPay"));
        
        if (!this.cartItems) 
            this.cartItems = [];
        
        if (!this.cartItemsToPay) 
            this.cartItemsToPay = [];
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
    
    addToCart : function(productId, tablePersonNumber, tableId) {
        var cartItem = {
            productId: productId,
            tablePersonNumber: tablePersonNumber,
            tableId: tableId,
            id: this.guid(), 
            sentToKitchen: false
        };
        
        this.cartItems.push(cartItem);
        this.saveCartItems();
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
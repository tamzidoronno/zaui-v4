<?php

class OrderManager extends TestBase {
    function OrderManager($api) {
        $this->api = $api;
    }
    
    /**
     * When the cart has been set up properly, and the order is ready
     * to be processed.
     */
    function test_createOrder() {
        $cartManager = $this->getApi()->getCartManager();
        $orderManager = $this->getApi()->getOrderManager();
        $productManager = $this->getApi()->getProductManager();
        
        //First set up a cart properly.
        //Create a test product.
        $product = $productManager->createProduct();
        
        //Add the product.
        $cartManager->addProduct($product->id, 1);
        
        //Okey the cart is ready to be created.
        $cart = $cartManager->getCart();
        
        //Attach an address to the cart, remember to do this.
        $cart->address = $this->getApiObject()->core_usermanager_data_Address();
        
        $orderManager->createOrder($cart);
    }
    
    /**
     * Need to fetch all the orders added to this store?
     */
    function test_getOrders() {
        $orderManager = $this->getApi()->getOrderManager();
        
        //Fetch all orders.
        $orderIds = array();
        $allOrders = $orderManager->getOrders($orderIds, 0, 100);
        foreach($allOrders as $order) {
            /* @var $order core_ordermanager_data_Order */
        }
    }
    
    /**
     * Need to update / save an already existing order?
     */
    function test_saveOrder() {
        //First all this boring stuff to create the order.
        $cartManager = $this->getApi()->getCartManager();
        $orderManager = $this->getApi()->getOrderManager();
        $productManager = $this->getApi()->getProductManager();
        
        //First set up a cart properly.
        //Create a test product.
        $product = $productManager->createProduct();
        
        //Add the product.
        $cartManager->addProduct($product->id, 1);
        
        //Okey the cart is ready to be created.
        $cart = $cartManager->getCart();
        
        //Attach an address to the cart, remember to do this.
        $cart->address = $this->getApiObject()->core_usermanager_data_Address();
        
        $order = $orderManager->createOrder($cart);
        
        
        //And now the fun part.. updating it.
        /* @var $order core_ordermanager_data_Order */
        $order->status = 23;
        $orderManager->saveOrder($order);
    }
    
    /**
     * This function is not available in public.
     */
    public function test_setOrderStatus() {
        
    }
    
}

?>

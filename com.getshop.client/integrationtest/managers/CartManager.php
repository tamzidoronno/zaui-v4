<?php

class CartManager extends TestBase {
    public function CartManager($api) {
        $this->api = $api;
    }
    
    /**
     * Add a product to your cart.
     */
    public function test_addProduct() {
        $manager = $this->getApi()->getCartManager();
        
        //Create a test product.
        $product = $this->getApi()->getProductManager()->createProduct();
        
        //Add the product.
        $manager->addProduct($product->id, 1);
    }
    
    /**
     * Simple way to fetch the cart.
     */
    public function test_getCart() {
        $manager = $this->getApi()->getCartManager();
        $cart = $manager->getCart();
    }
    
    /**
     * Don't want your product added anymore?
     */
    public function test_removeProduct() {
        $manager = $this->getApi()->getCartManager();
        
        //Create a test product.
        $product = $this->getApi()->getProductManager()->createProduct();
        
        //Add the product.
        $manager->addProduct($product->id, 1);
        
        //Remove the product.
        $manager->removeProduct($product->id);
    }
    
    /**
     * Don't want your product added anymore?
     */
    public function test_updateProductCount() {
        $manager = $this->getApi()->getCartManager();
        
        //Create a test product.
        $product = $this->getApi()->getProductManager()->createProduct();
        
        //Add the product.
        $manager->addProduct($product->id, 1);
        
        //Set a new count.
        $manager->updateProductCount($product->id, 2, array());
    }
}

?>

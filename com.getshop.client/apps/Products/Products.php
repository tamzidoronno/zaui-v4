<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Products
 *
 * @author ktonder
 */
namespace ns_e073a75a_87c9_4d92_a73a_bc54feb7317f;

class Products extends \WebshopApplication implements \Application {
    public function getDescription() {
        return $this->__f("Setup and manage your products");
    }

    public function getName() {
        return $this->__f("Products");
    }

    public function renderConfig() {
        $this->includefile("frontpage");
    }
    
    public function render() {
        
    }
   
    public function createProduct() {
        $product = $this->getApi()->getProductManager()->createProduct();
        $product->name = $_POST['title'];
        $product->price = $_POST['price'];
        $product->sku = $_POST['sku'];
        $this->getApi()->getProductManager()->saveProduct($product);
    }
    
    public function deleteProduct() {
        $this->getApi()->getProductManager()->removeProduct($_POST['value']);
    }
}

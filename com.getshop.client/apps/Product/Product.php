<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
namespace ns_06f9d235_9dd3_4971_9b91_88231ae0436b;

class Product extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__f("Display a product for the given page.");
    }

    public function getName() {
        return $this->__f("Product");
    }

    public function render() {
        $this->includefile("product");
    }

    public function addProductToCart() {
        $productId = $_POST['data']['productId'];
        $this->getApi()->getCartManager()->addProduct($productId, 1, []);
    }
}

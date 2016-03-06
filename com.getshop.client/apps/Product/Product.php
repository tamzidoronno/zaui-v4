<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
namespace ns_06f9d235_9dd3_4971_9b91_88231ae0436b;

class Product extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__w("Product view for your store.");
    }

    public function getName() {
        return $this->__f("Product");
    }

    public function renderOnStartup() {
        echo "<div class='gsaddedtocartbox' style='display:none;'>";
        echo "<div class='gsproductaddedtext'>" . $this->__w("Your product has been added to the cart.") . "</div>";
        echo "<span class='shop_button gshideproductaddedbox'>".$this->__w("Continue shopping")."</span>";
        echo "<span class='shop_button gsgotocart'>".$this->__w("Continue to cart")."</span>";
        echo "</div>";
    }
    
    public function render() {
        $this->includefile("product");
    }

    public function addProductToCart() {
        $productId = $_POST['data']['productId'];
        $arr = array();
        $this->getApi()->getCartManager()->addProduct($productId, 1, $arr);
    }
}

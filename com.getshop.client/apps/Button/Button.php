<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Button
 *
 * @author ktonder
 */
namespace ns_2996287a_c23e_41ad_a801_c77502372789;

class Button extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__f("A button that can be used for multiple purposes, for instance it can be used for going to a page, add a produc to cart etc.");
    }

    public function getName() {
        return $this->__f("Button");
    }

    public function render() {
        $this->includefile("button");
    }
    
    public function saveText() {
        $this->setConfigurationSetting("text", $_POST['data']['text']);
    }

    public function showSetup() {
        $this->includefile("setup");
    }
    
    public function searchForProduct() {
        $this->includefile("product_search_result");
    }
    
    public function setProductId() {
        $this->setConfigurationSetting("type", "add_to_cart");
        $this->setConfigurationSetting("product_id", $_POST['data']['product_id']);
    }
    
    public function addProductToCart() {
        $productId = $_POST['data']['productId'];
        $this->getApi()->getCartManager()->addProduct($productId, 1, []);
    }
}

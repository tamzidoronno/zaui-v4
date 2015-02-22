<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of GetShopDonate
 *
 * @author ktonder
 */

namespace ns_1d2fa300_bd01_464f_b3b6_d77758e3fb25;

class GetShopDonate extends \ApplicationBase implements \Application {
    public function getDescription() {
        return "GetshopDonate";
    }

    public function getName() {
        return "GetShopDonate";
    }

    public function render() {
        $this->includefile("donatetemplate");
    }
    
    public function addProductToCart() {
        $productId = $_POST['data']['productId'];
        $this->getApi()->getCartManager()->addProduct($productId, 1, []);
    }
}

<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of WilhemsenHouseWashingOrder
 *
 * @author ktonder
 */

namespace ns_934421f5_7107_4b93_829e_74e49ce7d160;

class WilhemsenHouseWashingOrder extends \ApplicationBase implements \Application {
    public function getDescription() {
        return "A washing order app";
    }

    public function getName() {
        return "WilhemsenHouseWashingOrder";
    }

    public function render() {
        $this->includefile("washing");
    }

    public function canOrderWash() {
        echo $this->getApi()->getHotelBookingManager()->getUserIdForRoom($_POST['data']['room']);
        die();
    }
    
    public function addProductToCart() {
        $productId = $_POST['data']['productId'];
        $this->getApi()->getCartManager()->addProduct($productId, 1, []);
    }

//put your code here
}

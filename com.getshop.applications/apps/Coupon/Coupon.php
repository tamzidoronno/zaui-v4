<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of PromotionCode
 *
 * @author ktonder
 */
namespace ns_90cd1330_2815_11e3_8224_0800200c9a66;

class Coupon extends \WebshopApplication implements \Application {
    
    public function getDescription() {
        return $this->__f("Increase your sale potential by giving your valued customers discounts with promotion codes.");
    }

    public function getName() {
        return $this->__f("Coupon");
    }

    public function render() {
        if (!$this->isValidCode()) {
            $this->includefile('cartcoupon');
        }
    }
    
    public function renderConfig() {
        $this->includeFile("couponconfig");
    }
    
    public function applyCode() {
        $this->getApi()->getCartManager()->applyCouponToCurrentCart($_POST['data']['code']);
    }
    
    public function isValidCode() {
        
        $cart = $this->getApi()->getCartManager()->getCart();
        if (!isset($cart->coupon) || $cart->coupon == null) {
            return false;
        }

        return true;
    }
    
    private function int_ok($val) {
        return ($val !== true) && ((string)(int) $val) === ((string) $val);
    }
    
    public function createCoupons($data, $fixed) {
        
        $coupon = new \core_cartmanager_data_Coupon();
        $coupon->code = $data['name'];
        $coupon->timesLeft = $data['times'];
        $coupon->amount = $data['discount'];
        
        if ($coupon->code == "" ) {
            echo "BlankCodeField";
            die();
        }
        
        if (!is_numeric($coupon->timesLeft) || !is_numeric($coupon->amount)) {
            echo "NotInteger";
            die();
        }
        
        if (!$this->int_ok($coupon->timesLeft) || $coupon->timesLeft <= 0 || !$this->int_ok($coupon->amount) || $coupon->amount <= 0) {
            echo "NotInteger";
            die();
        }
        
        if (!$fixed && ($coupon->amount < 1 || $coupon->amount > 99)) {
            echo "NotValidPercentage";
            die();
        }
        
        if ($fixed) {
            $coupon->type = "FIXED";
        } else {
            $coupon->type = "PERCENTAGE";
        }
        
        $this->getApi()->getCartManager()->addCoupon($coupon);
    }
    
    public function getCoupons() {
        return $this->getApi()->getCartManager()->getCoupons();
    }
    
    public function save() {
        if (isset($_POST['data']['fixed'])) {
            $this->createCoupons($_POST['data']['fixed'], true);
        } 
        
        if (isset($_POST['data']['percentage'])) {
            $this->createCoupons($_POST['data']['percentage'], false);
        }
        
        $this->includefile('couponconfig');
    }
    
    public function applicationDeleted() {
        $this->getApi()->getCartManager()->removeAllCoupons();
    }
    
    public function deleteCode() {
        $this->getApi()->getCartManager()->removeCoupon($_POST['data']['code']);
        $this->includefile('couponconfig');
    }
}

?>

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
    
    public function updateUser() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['userid']);
        $user->couponId = $_POST['couponid'];
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function renderUserSettings($user) {
                ?>
        <div class="gss_overrideapp" gss_use_app_id="90cd1330-2815-11e3-8224-0800200c9a66">
            
            <input type='hidden' gs_model='companymodel' gs_model_attr='userid' value='<?php echo $user->id; ?>'>
            <div class="textfield gss_setting">
                <span class="title"><?php echo $this->__f("Selected a coupon"); ?></span>
                <?
                    $coupons = $this->getCoupons();
                    echo "<select class='gsschangeusercompany backsideselect' gs_model_attr='couponid' gs_model='companymodel'>";
                    echo "<option value=''>Set a coupon</option>";
                    foreach($coupons as $coupon) {
                        $sel = "";
                        if($coupon->id == $user->couponId) {
                            $sel = "SELECTED";
                        }
                        echo "<option value='".$coupon->id."' $sel>" . $coupon->code . "</option>";
                    }
                    echo "</select>";
                    
                ?>
                <div class="description">
                    <?php echo $this->__("When adding this coupon, everything bought by this user will get this discount."); ?>
                </div>
            </div>
            <div class='gss_button_area'>
                  <div class="gss_button" gss_method="updateUser" gss_model="companymodel" gss_success_message="Saved successfully"><i class='fa fa-save'></i><?php echo $this->__("Update"); ?></div>
            </div>
        </div>
        
        <?php
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
        
        if (!$fixed && ($coupon->amount < 0 || $coupon->amount > 100)) {
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
    
    /**
     * @return \core_cartmanager_data_Coupon[]
     */
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

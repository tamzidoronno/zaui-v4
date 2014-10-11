<?php

namespace ns_c7736539_4523_4691_8453_a6aa1e784fc1;

class PayPal extends \PaymentApplication implements \Application {

    public $singleton = true;

    public function getDescription() {
        return $this->__("Lets you easily connect to paypal, and give your customers option to pay using paypal.");
    }

    public function getName() {
        return "PayPal";
    }
    
    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $details = $this->getDetails();
        $this->addPaymentMethod("PayPal", "/showApplicationImages.php?appNamespace=$namespace&image=PayPal.png", "paypal", $details);
    }
    
    private function getDetails() {
        return $this->__w("* Can pay with Visa/MasterCard trough PayPal");
    }

    public function paymentCallback() {
        $this->includefile("paypalverification");
    }

    /**
     * Will only be executed if
     * this payment has been selected
     * on checkout 
     */
    public function postProcess() {
        
    }

    function curPageURL() {
        return @$_SERVER['HTTP_ORIGIN'];
    }

    /**
     * Will only be executed if
     * this payment has been selected
     * on checkout  
     */
    public function preProcess() {
        $order = $this->getOrder();

        $returnAddress = "http://" . $this->getFactory()->getStore()->webAddress . "/index.php?page=home";
        $orderId = $order->id;

        $settings = $this->getConfiguration()->settings;
        if (isset($settings->{'sandbox'}->value) && $settings->{'sandbox'}->value == "true") {
            $url = "https://www.sandbox.paypal.com/cgi-bin/webscr";
            $seller = $settings->{'paypaltestaddress'}->value;
        } else {
            $url = "https://www.paypal.com/cgi-bin/webscr";
            if(isset($this->getConfiguration()->settings->{"paypalemailaddress"})) {
                $seller = $this->getConfiguration()->settings->{"paypalemailaddress"}->value;
            }
        }
        if(!isset($seller) || !$seller) {
            if($this->isEditorMode()) {
                echo $this->__f("The paypal account has not been configured correctly. Go to settings and configure it properly");
            }
            return;
        }
        
        $appSettingsId = $this->getConfiguration()->appSettingsId;
        $callback = $this->curPageURL()."/callback.php?app=".$appSettingsId."&orderid=" . $orderId . "&NO_SHIPPING_OPTION_DETAILS=1";
        $currency = $this->getFactory()->getCurrency();

        echo "<center><b>".$this->__w("You are being transferred to paypal, please wait...") ."</b></center>";
        
        echo "<form action='$url' method='POST' id='paypalform'>";

        //CALLBACK...
        echo '<input type="hidden" name="notify_url" value="' . $callback . '">';
        echo '<INPUT TYPE="hidden" NAME="return" value="' . $returnAddress . '">';

        echo '<input type="hidden" name="cmd" value="_cart">';
        echo '<input type="hidden" name="upload" value="1">';
        echo '<input type="hidden" name="currency_code" value="' . $currency . '">';
        echo '<input type="hidden" name="business" value="' . $seller . '">';
        $i = 1;
        foreach($order->cart->items as $cartItem) {
            $product = $cartItem->product;
            $variations = isset($cartItem->variations) ? $cartItem->variations : array(); 
            $price = $this->getApi()->getProductManager()->getPrice($product->id, $variations);
            $count = $cartItem->count;
            
            $helpertext = \HelperCart::getVartionsText($cartItem);
            if($helpertext) { $helpertext= "(".$helpertext.")"; }
            
            echo '<input type="hidden" name="item_name_'.$i.'" value="'.$product->name. ' ' . $helpertext.'">';
            echo '<input type="hidden" name="amount_'.$i.'" value="' . $price . '">';
            echo '<input type="hidden" name="quantity_'.$i.'" value="' . $count . '">';
            $i++;
        }
        
        if(isset($order->shipping) && isset($order->shipping->cost) && $order->shipping->cost > 0) {
            echo '<input type="hidden" name="item_name_'.$i.'" value="'.$this->__w("Shipping cost").'">';
            echo '<input type="hidden" name="amount_'.$i.'" value="' . $order->shipping->cost . '">';
            $i++;
        }
        
        if(isset($order->cart->couponCost) && $order->cart->couponCost > 0) {
            echo '<input type="hidden" name="discount_amount_cart" value="' . $order->cart->couponCost . '">';
        }
        
        echo "</form>";
        echo "<script>";
        echo "$('#paypalform').submit();";
        echo "</script>";
    }

    /**
     * 
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("paypalconfig");
    }

	public function getMode() {
		return null;
	}
	
    public function render() {
        
    }

}

?>

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
        $actual_link = "http://$_SERVER[HTTP_HOST]";
        return $actual_link;
    }

    /**
     * Will only be executed if
     * this payment has been selected
     * on checkout  
     */
    public function preProcess() {
        $order = $this->getOrder();

        $returnAddress = $this->curPageURL();
        $orderId = $order->id;

        $sandbox = $this->getConfigurationSetting("sandbox");
        
        $testEmailAddress = $this->getConfigurationSetting("paypaltestaddress");
        $payPalEmail = $this->getConfigurationSetting("paypalemailaddress");

        if (isset($sandbox) && $sandbox == "true") {
            $url = "https://www.sandbox.paypal.com/cgi-bin/webscr";
            $seller = $testEmailAddress;
        } else {
            $url = "https://www.paypal.com/cgi-bin/webscr";
            if ($payPalEmail) {
                $seller = $payPalEmail;
            }
        }

        if (!isset($seller) || !$seller) {
            if ($this->isEditorMode()) {
                echo $this->__f("The paypal account has not been configured correctly. Go to settings and configure it properly");
            }
            return;
        }

        $appSettingsId = $this->getApplicationSettings()->id;
        $callback = $this->curPageURL() . "/callback.php?app=" . $appSettingsId . "&orderid=" . $orderId . "&NO_SHIPPING_OPTION_DETAILS=1";
        $currency = \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::fetchCurrencyCode();

        echo "<form action='$url' method='POST' id='paypalform'>";

        //CALLBACK...
        echo '<INPUT TYPE="hidden" name="charset" value="utf-8">';
        echo '<input type="hidden" name="notify_url" value="' . $callback . '">';
        echo '<INPUT TYPE="hidden" NAME="return" value="' . $returnAddress . '">';

        echo '<input type="hidden" name="cmd" value="_cart">';
        echo '<input type="hidden" name="upload" value="1">';
        echo '<input type="hidden" name="currency_code" value="' . $currency . '">';
        echo '<input type="hidden" name="business" value="' . $seller . '">';
        $i = 1;
        foreach ($order->cart->items as $cartItem) {
            $product = $cartItem->product;
            $variations = isset($cartItem->variations) ? $cartItem->variations : array();
            $price = $product->price;
            $count = $cartItem->count;

            $helpertext = \HelperCart::getVartionsText($cartItem);
            if ($helpertext) {
                $helpertext = "(" . $helpertext . ")";
            }

            echo '<input type="hidden" name="item_name_' . $i . '" value="' . $product->name . ' ' . $helpertext . '">';
            echo '<input type="hidden" name="amount_' . $i . '" value="' . $price . '">';
            echo '<input type="hidden" name="quantity_' . $i . '" value="' . $count . '">';
            $i++;
        }

        if (isset($order->shipping) && isset($order->shipping->cost) && $order->shipping->cost > 0) {
            echo '<input type="hidden" name="item_name_' . $i . '" value="' . $this->__w("Shipping cost") . '">';
            echo '<input type="hidden" name="amount_' . $i . '" value="' . $order->shipping->cost . '">';
            $i++;
        }

        if (isset($order->cart->couponCost) && $order->cart->couponCost > 0) {
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
    
    public function isSandbox() {
        return $this->getConfigurationSetting("sandbox") == "true";
    }

    public function getMode() {
        return null;
    }

    public function render() {
        
    }

    public function saveSettings() {
        $this->setConfigurationSetting("paypalemailaddress", $_POST['paypalemail']);
        $this->setConfigurationSetting("sandbox", $_POST['issandbox']);
        $this->setConfigurationSetting("paypaltestaddress", $_POST['testpaypalemail']);
    }

}

?>

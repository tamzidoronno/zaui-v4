<?php
namespace ns_d02f8b7a_7395_455d_b754_888d7d701db8;

class Dibs extends \PaymentApplication implements \Application {
    public $singleton = true;
    
    public function getDescription() {
        return $this->__("dibs payment services allows you to start charging with visa / mastercard / and other card vendors. its extremly easy to use. prices for using this application is provided by dibs, and you will be contacted by dibs when adding this application.");
    }

    public function getName() {
        return "DIBS";
    }
    
    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod("Visa / MasterCard", "/showApplicationImages.php?appNamespace=$namespace&image=Dibs.png", "dibs");
    }

    public function postProcess() {
        
    }
    
    public function paymentCallback() {
        
    }

    public function preProcess() {
        $merchid = $this->getConfigurationSetting("merchantid");
        $currency = \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::fetchCurrencyCode();
        $orderId = $this->getOrder()->id;
        $amount = $this->getApi()->getCartManager()->calculateTotalCost($this->order->cart)*100;
        if(isset($this->order->shipping)) {
            $amount += ($this->order->shipping && $this->order->shipping->cost) ? $this->order->shipping->cost : 0;
        }
        
        $settings = $this->getFactory()->getSettings();
        $language = $this->getFactory()->getSelectedLanguage();
        $callBack = "https://". $_SERVER['SERVER_NAME']."/callback.php?orderid=".$orderId."&app=".$this->getApplicationSettings()->id;
        $exitUrl = "http://". $_SERVER['SERVER_NAME']."/index.php?page=home";
        echo '<form method="post" id="dibsform" action="https://sat1.dibspayment.com/dibspaymentwindow/entrypoint">
            <input value="' . $merchid . '" name="merchant" type="hidden" />
            <input value="' . $currency . '" name="currency" type="hidden" />
            <input value="' . $orderId . '" name="orderId" type="hidden" />
            <input value="' . $amount . '" name="amount" type="hidden" />
            <input value="' . $language . '" name="language" type="hidden" />
            <input value="' . $exitUrl . '" name="acceptReturnUrl" type="hidden" />
            <input value="' . $exitUrl . '" name="cancelReturnUrl" type="hidden" />
            <input value="' . $callBack . '" name="callbackUrl" type="hidden" />
            <input type="hidden" name="test" value="1"/>
            </form>';

        echo "<script>";
        echo "$('#dibsform').submit();";
        echo "</script>";
    }

    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("dibsconfiguration");
    }

    public function render() {
        
    }
    
    public function isAvailable() {
        return true;
    }

    public function saveSettings() {
        $this->setConfigurationSetting("merchantid", $_POST['merchantid']);
    }
}

?>

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
    
    public function isPublicPaymentApp() {
        return true;
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

    public function renderButton() {
        $this->preProcess();
    }
    
    /**
     * Will only be executed if
     * this payment has been selected
     * on checkout  
     */
    public function preProcess() {
        $this->includefile("paypalbutton");
    }
    
    public function hasPaymentLink() {
        return true;
    }
    
    /**
     * 
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("paypalconfig");
    }
    
    public function simplePayment() {
        $this->preProcess();
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

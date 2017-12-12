<?php

namespace ns_565ea7bd_c56b_41fe_b421_18f873c63a8f;

class PayOnDelivery extends \PaymentApplication implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;

    function __construct() {
        
    }

    public function getDescription() {
        return $this->__f("Allows you to charge users by paying cash and keep track of those.");
    }

    public function getAvailablePositions() {
        return "left";
    }

    public function getName() {
        return $this->__w("Cash payment");
    }

    public function postProcess() {
        
    }

    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $details = $this->getPaymentDetails();
        $this->addPaymentMethod($this->getName(), "/showApplicationImages.php?appNamespace=$namespace&image=skin/images/pod.png", "payondelivery", $details);
    }
    
    private function getPaymentDetails() {
        $details = "";
        if ($this->getPaymentFee()) 
            $details = $this->__w("* PayOnDelivery fee").": ".$this->getPaymentFee().", ".$this->__w("will be added to your invoice.");
        return $details;
    }

    
    public function renderConfig() {
        $this->includefile('payondeliveryconfig');
    }
    
    public function getPaymentFee() {
        $paymentfee = $this->getConfigurationSetting("paymentfee");
        if ($paymentfee) {
            return $paymentfee;
        }
        
        return 0;
    }

    public function preProcess() {
        return true;
    }

    public function getStarted() {
        
    }

    public function render() {
        $this->includefile("PayOnDelivery");
    }

    public function saveSettings() {
        $this->setConfigurationSetting("paymentfee", $_POST['fee']);
    }
}
?>

<?php

namespace ns_565ea7bd_c56b_41fe_b421_18f873c63a8f;

class PayOnDelivery extends \PaymentApplication implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;

    function __construct() {
        
    }

    public function getDescription() {
        return $this->__f("Allows your customers to specify that they will pay on delivery.");
    }

    public function getAvailablePositions() {
        return "left";
    }

    public function getName() {
        return $this->__w("Pay on pickup");
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
        echo "<div style='text-align:center; font-size: 16px;'>";
        echo $this->__w("Thank you for your order.");
        echo "</div>";
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

<?php
namespace ns_7587fdcb_ff65_4362_867a_1684cbae6aef;

class PaymentMethod1 extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "A special payment method to recieve payments manually from different sources. Activate this payment method and rename it to something that fits your need.";
    }

    public function getName($getOriginal = false) {
        $name = $this->getConfigurationSetting("name");
        if(!$name || $getOriginal) {
            $name = "PaymentMethod1";
        }
        return $name;
    }

    public function render() {
        
    }
      
    public function renderConfig() {
        $this->includeFile("config");
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("name", $_POST['name']);
    }
    
    public function paymentCallback() {
        $this->order = $this->getApi()->getOrderManager()->getOrder($_GET['orderid']);
        if(!$this->order) {
            echo "Order where not found, are you sure you are using a correct order id?";
        } else {
            $this->getApi()->getOrderManager()->markAsPaidWithPassword($this->order->id, $this->convertToJavaDate(time()), $this->getApi()->getOrderManager()->getTotalAmount($this->order), "fdsvb4354345345");
        }
    }    
}
?>
<?php
namespace ns_0b4c0e65_6785_46a1_a91b_63a72999cc37;

class ExpediaPayment extends \PaymentApplication implements \Application {
    
    public $singleton = true;

    public function getDescription() {
        return $this->__("Mark orders as expedia collect payments.");
    }

    public function getName() {
        return "ExpediaCollect";
    }

    public function addPaymentMethods() {
    }

    public function paymentCallback() {
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

    public function saveSettings() {
    }

}
?>

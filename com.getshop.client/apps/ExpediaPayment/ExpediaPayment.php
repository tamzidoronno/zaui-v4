<?php

namespace ns_92bd796f_758e_4e03_bece_7d2dbfa40d7a;

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

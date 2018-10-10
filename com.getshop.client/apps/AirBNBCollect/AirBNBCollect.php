<?php

namespace ns_639164bc_37f2_11e6_ac61_9e71128cae77;

class AirBNBCollect extends \PaymentApplication implements \Application {

    public $singleton = true;

    public function getDescription() {
        return $this->__("Mark orders as airbnb collect payments.");
    }

    public function getName() {
        return "AirBNBCollect";
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
        $this->includeFile("config");
    }

    public function getMode() {
        return null;
    }

    public function render() {
        
    }

    public function saveSettings() {
        $this->setConfigurationSetting("automarkpaid", $_POST['automarkpaid']);
    }

}

?>

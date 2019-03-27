<?php

namespace ns_92bd796f_758e_4e03_bece_7d2dbfa40d7a;

class ExpediaPayment extends \PaymentApplication implements \Application  {

    public $singleton = true;

    public function getDescription() {
        return $this->__("If you are using expedia collect to recieve payment, this application will be automatically used. Guests will gain access automatically to the room whenever this application is used.");
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

    public function getMode() {
        return null;
    }

    public function render() {
        
    }

    public function renderConfig() {
        $this->includeFile("config");
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("automarkpaid", $_POST['automarkpaid']);
    }

    public function getColor() {
        return "yellow";
    }    
}

?>

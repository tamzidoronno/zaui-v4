<?php
namespace ns_f1c8301d_9900_420a_ad71_98adb44d7475;

class Vipps extends \PaymentApplication implements \Application {
    public function getDescription() {
        
    }

    public function getSavedCards($userId) {
    }
    
    public function getName() {
        return "Vipps";
    }
    
    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod("Vipps / Faktura", "");
    }
        /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("vippsconfig");
    }
    
    public function render() {
        
    }
    
    
    public function simplePayment() {
        $this->includefile("vippspaymentwindow");
    }

    public function saveSettings() {
        $this->setConfigurationSetting("userid", $_POST['userid']);
        $this->setConfigurationSetting("auth_secret", $_POST['auth_secret']);
        $this->setConfigurationSetting("xrequestid", $_POST['xrequestid']);
        $this->setConfigurationSetting("merchantid", $_POST['merchantid']);
    }
}
?>

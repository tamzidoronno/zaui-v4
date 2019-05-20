<?php
namespace ns_f1c8301d_9900_420a_ad71_98adb44d7475;

class Vipps extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Recieve payments using vipps payments. Vipps is by experience unstable, expect bugs and startup problems using this application.";
    }

    public function getSavedCards($userId) {
    }
    
    public function getIcon() {
        return "invoice.svg";
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
        $this->setConfigurationSetting("clientid", $_POST['clientid']);
        $this->setConfigurationSetting("secret", $_POST['secret']);
        $this->setConfigurationSetting("subscriptionkeyprimary", $_POST['subscriptionkeyprimary']);
        $this->setConfigurationSetting("subscriptionkeysecondary", $_POST['subscriptionkeysecondary']);
        $this->setConfigurationSetting("serialNumber", $_POST['serialNumber']);
    }
}
?>

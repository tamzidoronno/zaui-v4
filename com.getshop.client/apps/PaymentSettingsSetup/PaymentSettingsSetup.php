<?php
namespace ns_f474e3f0_7ef6_4611_9202_9332302a5e38;

class PaymentSettingsSetup extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PaymentSettingsSetup";
    }
    
    public function deactivateApp() {
        $this->getApi()->getStoreApplicationPool()->deactivateApplication($_POST['data']['id']);
    }
    
    public function activateApp() {
        $this->getApi()->getStoreApplicationPool()->activateApplication($_POST['data']['id']);
    }

    public function saveDefaultPaymentMethod() {
        $ecom = $this->getApi()->getStoreApplicationPool()->getApplication("9de54ce1-f7a0-4729-b128-b062dc70dcce");
        $ecomsettings = $this->getFactory()->getApplicationPool()->createInstace($ecom);
        $ecomsettings->setConfigurationSetting("defaultPaymentMethod", $_POST['data']['defualtmethod']);
    }
    
    public function render() {
        $this->includefile("printapplications");
        $this->includefile("activatedpaymentmethods");
    }
    
    public function runOcrScan() {
        $this->getApi()->getStoreOcrManager()->checkForPayments();
    }
    
}
?>

<?php
namespace ns_4b1dc2e3_a4a7_4874_9be7_3ff66e5e5a6c;

class SelfServiceTerminalSettings extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SelfServiceTerminalSettings";
    }

    public function render() {
        $this->includefile("config");
    }
    
    public function saveTerminalConfiguration() {
        $setting = new \core_paymentterminalmanager_PaymentTerminalSettings();
        $setting->ip = $_POST['data']['ip'];
        $setting->verifoneTerminalId = $_POST['data']['terminalid'];
        $setting->offset = $_POST['data']['offset'];
        $this->getApi()->getPaymentTerminalManager()->saveSettings($setting);
    }
    
}
?>

<?php
namespace ns_f474e3f0_7ef6_4611_9202_9332302a5e38;

class PaymentSettingsSetup extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PaymentSettingsSetup";
    }

    public function render() {
        $this->includefile("printapplications");
    }
}
?>

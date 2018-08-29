<?php
namespace ns_972b6269_44fb_4dd6_b6f1_c3ecf2d27dd2;

class PgaSettings extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PgaSettings";
    }

    public function render() {
        $this->includefile("pga");
    }
    
    public function savePgaSettings() {
        $settings = $this->getApi()->getPgaManager()->getSettings($this->getSelectedMultilevelDomainName());
        $settings->costForLateCheckout = $_POST['data']['costForLateCheckout'];
        $settings->hoursExtraForLateCheckout = $_POST['data']['hoursExtraForLateCheckout'];
        $settings->extraCleaningCost = $_POST['data']['extraCleaningCost'];
        $settings->staticWifiPassword = $_POST['data']['staticWifiPassword'];
        $settings->staticWifiSSID = $_POST['data']['staticWifiSSID'];
        $this->getApi()->getPgaManager()->saveSettings($this->getSelectedMultilevelDomainName(), $settings);
    }
}
?>

<?php
namespace ns_47dec929_fc34_4d7d_9356_a9600d04797e;

class SalesPointOtherSettings extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPointOtherSettings";
    }

    public function render() {
        $this->includefile("othersettings");
    }
    
    public function saveSettings() {
        $this->getApi()->getOrderManager()->changeAutoClosePeriodesOnZRepport($_POST['data']['autoclose']);
    }
}
?>

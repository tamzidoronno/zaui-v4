<?php
namespace ns_66b4483d_3384_42bb_9058_2ac915c77d80;

class Amesto extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "Amesto";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("config");
    }
    
    public function saveCredentials() {
        $this->setConfigurationSetting("hostname", $_POST['hostname']);
    }
    
    public function syncStockQuantity() {
        $this->getApi()->getAmestoManager()->syncStockQuantity($this->getConfigurationSetting("hostname"), $_POST['data']['productid']);
    }
    
    public function syncAllStockQuantity() {
        $this->getApi()->getAmestoManager()->syncAllStockQuantity($this->getConfigurationSetting("hostname"));
    }
    
    public function syncAllOrders() {
        $this->getApi()->getAmestoManager()->syncAllOrders($this->getConfigurationSetting("hostname"));
    }
    
    public function syncAllCostumers() {
        $this->getApi()->getAmestoManager()->syncAllCostumers($this->getConfigurationSetting("hostname"));
    }
}
?>

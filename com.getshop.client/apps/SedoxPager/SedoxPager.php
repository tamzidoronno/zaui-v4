<?php
namespace ns_5876c6fd_af9b_48a2_a034_d93c9809c346;

class SedoxPager extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxPager";
    }

    public function render() {
        $connectedTo = $this->getConfigurationSetting("connectedAppId");
        
        if (!$connectedTo) {
            $this->printWarning();
        } else {
            $this->includefile("paging");
        }
    }
    
    public function printWarning() {
        echo "<span style='font-size:10px;'>Please! this app needs to be connected to another app</span>";
    }
    
    public function getConnectedApplication() {
        $connectedTo = $this->getConfigurationSetting("connectedAppId");
        $appInstance = $this->getFactory()->getApplicationPool()->getApplicationInstance($connectedTo);
        return $appInstance;
    }
    
    public function setConnectedAppId() {
        $this->setConfigurationSetting("connectedAppId", $_POST['data']['appId']);
    }
    
    public function setPageNumber() {
        $appId = $this->getConfigurationSetting("connectedAppId");
        $_SESSION[$appId."_currentPage"] = $_POST['data']['pageNumber'];
    }
}
?>

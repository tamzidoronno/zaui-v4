<?php
namespace ns_99d42f8c_e446_40fe_a038_af9f316dfb3a;

class GetShopLockAdmin extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopLockAdmin";
    }

    public function removeLock() {
        $this->getApi()->getGetShopLockManager()->deleteLock($this->getSelectedName(), "", $lockId);
    }
    
    public function connectLock() {
        $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $_POST['data']['item']);
        $item->bookingItemAlias = $_POST['data']['lock'];
        $this->getApi()->getBookingEngine()->saveBookingItem($this->getSelectedName(), $item);
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function setEngine() {
        $engine = $_POST['data']['engine'];
        $this->setConfigurationSetting("engine_name", $engine);
    }
    
    public function runCheck() {
        $this->getApi()->getGetShopLockManager()->checkIfAllIsOk($this->getSelectedName());
    }
    
    public function render() {
         if (!$this->getSelectedName()) {
            echo "You need to specify a booking engine first<br>";
            $engines = $this->getApi()->getStoreManager()->getMultiLevelNames();
            foreach($engines as $engine) {
                echo "<span gstype='clicksubmit' style='font-size: 20px; cursor:pointer; display:inline-block; margin-bottom: 20px;' method='setEngine' gsname='engine' gsvalue='$engine'>$engine</span><br>"; 
            }
            return;
        }
        
        $this->includefile("locklist");
    }
}
?>

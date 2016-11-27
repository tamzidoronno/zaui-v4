<?php
namespace ns_99d42f8c_e446_40fe_a038_af9f316dfb3a;

class GetShopLockAdmin extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function deleteLocks() {
        $this->getApi()->getGetShopLockManager()->deleteAllDevices($this->getSelectedName(), "fdsafbvvre4234235t");
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
    
    public function removeUnusedLocks() {
        $this->getApi()->getGetShopLockManager()->removeAllUnusedLocks($this->getSelectedName());
    }
    
    public function updateMasterCode() {
        $codes = $this->getApi()->getGetShopLockManager()->getMasterCodes($this->getSelectedName());
        $codes->codes->{$_POST['data']['offset']} = $_POST['data']['code'];
        $this->getApi()->getGetShopLockManager()->saveMastercodes($this->getSelectedName(), $codes);
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
    
    public function resetLock() {
        $this->getApi()->getGetShopLockManager()->refreshLock($this->getSelectedName(), $_POST['data']['id']);
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
    
    public function toggleLockUpdate() {
        $this->getApi()->getGetShopLockManager()->stopUpdatesOnLock($this->getSelectedName());
    }

    /**
     * @param \core_getshop_data_GetShopLock $lock
     */
    public function printCodeList($lock) {
        $total = $lock->maxNumberOfCodes;
        
        $inuse = 0;
        $toUpdate = 0;
        $toRemove = 0;
        foreach($lock->codes as $code) {
            /* @var $code \core_getshop_data_GetShopLockCode */
            if($code->used) {
                $inuse++;
            }
            if(!$code->addedToLock) {
                $toUpdate++;
            }
            if($code->needToBeRemoved) {
                $toRemove++;
            }
        }
        $title = "In use : $inuse<br>";
        $title .= "To update : $toUpdate<br>";
        $title .= "To remove : $toRemove<br>";
        $title .= "Total codes : $total<br>";
        
        echo "<span title='$title'>$inuse - $toUpdate - $toRemove - $total</span>";
    }

    public function getBattery($lock) {
//        print_r($lock);
        return $lock->batteryStatus;
    }

}
?>

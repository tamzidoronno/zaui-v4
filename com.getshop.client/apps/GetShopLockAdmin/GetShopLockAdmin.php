<?php
namespace ns_99d42f8c_e446_40fe_a038_af9f316dfb3a;

class GetShopLockAdmin extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function deleteLocks() {
        $this->getApi()->getGetShopLockManager()->deleteAllDevices($this->getSelectedName(), "fdsafbvvre4234235t", $_POST['data']['serverSource']);
    }
    
    public function updateLockData() {
        
        $serverSource = $_POST['data']['source'];
        $id = $_POST['data']['lockid'];
        $locks = $this->getApi()->getGetShopLockManager()->getAllLocks($this->getSelectedName(), $serverSource);
        
        foreach($locks as $lock) {
            if($lock->id != $id) {
                continue;
            }
            $newarray = array();
            foreach($lock->codes as $key => $code) {
                $code->used = ($_POST['data']['used_'.$key] == "true");
                if($_POST['data']['addedToLock_'.$key] != "true") {
                    $code->addedToLock = null;
                }
                $code->needToBeRemoved = ($_POST['data']['needToBeRemoved_'.$key] == "true");
                if($_POST['data']['code_'.$key] != $code->code) {
                    $code->code = $_POST['data']['code_'.$key];
                    $code->addedToLock = (boolean)false;
                }
                $newarray[$key] = $code;
            }
            $lock->needPriority = true;
            $lock->codes = $newarray;
            echo "<pre>";
            print_r($lock);
            $this->getApi()->getGetShopLockManager()->saveLock($this->getSelectedName(), $lock);
        }
        
    }
    
    public function loadLockList() {
        $serverSource = $_POST['data']['source'];
        $id = $_POST['data']['id'];
        $locks = $this->getApi()->getGetShopLockManager()->getAllLocks($this->getSelectedName(), $serverSource);
        foreach($locks as $lock) {
            if($lock->id != $id) {
                continue;
            }
            echo '<div gstype="form" method="updateLockData">';
            echo "<input type='hidden' gsname='lockid' value='".$id."'>";
            echo "<input type='hidden' gsname='source' value='".$serverSource."'>";
            echo "<table>";
            echo "<tr>";
            echo "<td></td>";
            echo "<td>Code</td>";
            echo "<td>In use</td>";
            echo "<td>Added</td>";
            echo "<td>Remove</td>";
            echo "</tr>";
            
            foreach($lock->codes as $key => $code) {
                echo "<tr>";
                echo "<td>" . $key . " </td><td> <input type='text' gsname='code_$key' value='" .  $code->code . "'></td>";
                echo "<td align='center'>";
                $used = $code->used ? "CHECKED" : "";
                echo "<input type='checkbox' gsname='used_$key' $used>";
                echo "</td>";
                echo "<td align='center'>";
                $addedToLock = $code->addedToLock ? "CHECKED" : "";
                echo "<input type='checkbox' gsname='addedToLock_$key' $addedToLock>";
                echo "</td>";
                echo "<td align='center'>";
                $needToBeRemoved = $code->needToBeRemoved ? "CHECKED" : "";
                echo "<input type='checkbox' gsname='needToBeRemoved_$key' $needToBeRemoved>";
                echo "</td>";
                echo "</tr>";
            }
            echo "</table>";
            echo "<br><br>";
            echo "<br><b>WARNING: This has to be handled with, care.. if you are unsure what you are doing, please contact GetShop support team.</b>";
            echo "<br><br>";
            echo "<input type='button' value='Save settings' gstype='submit'>";
            echo "</div>";
        }
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
        $this->getApi()->getGetShopLockManager()->removeAllUnusedLocks($this->getSelectedName(), $_POST['data']['serverSource']);
    }
    
    public function updateMasterCode() {
        $codes = $this->getApi()->getGetShopLockManager()->getMasterCodes($this->getSelectedName());
        $codes->codes->{$_POST['data']['offset']} = $_POST['data']['code'];
        $this->getApi()->getGetShopLockManager()->saveMastercodes($this->getSelectedName(), $codes);
    }
    
    public function refreshAllLocks() {
        $this->getApi()->getGetShopLockManager()->refreshAllLocks($this->getSelectedName(), $_POST['data']['source']);
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
    
    public function createlockserverconfig() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $config->lockServerConfigs->{$_POST['data']['servername']} = new \stdClass();
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $config);
    }
    
    public function saveConfig() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $lockconfig = $config->lockServerConfigs->{$_POST['data']['servername']};
        
        $lockconfig->locktype = $_POST['data']['locktype'];
        $lockconfig->arxHostname = $_POST['data']['arxHostname'];
        $lockconfig->arxUsername = $_POST['data']['arxUsername'];
        $lockconfig->arxPassword = $_POST['data']['arxPassword'];
        $lockconfig->arxCardFormat = $_POST['data']['arxCardFormat'];
        $lockconfig->arxCardFormatsAvailable = $_POST['data']['arxCardFormatsAvailable'];
        $lockconfig->codeSize = $_POST['data']['codeSize'];
        
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $config);
    }
    
    public function removeConfig() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        unset($config->lockServerConfigs->{$_POST['data']['servername']});
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $config);
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
        $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        if($user->showHiddenFields) {
            $this->includefile("lockserveradmin");
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
        $i = 0;
        $title .= "<br>Code list:<br>";
        foreach($lock->codes as $code) {
             $title .= $i . " : " . $code->code;
             if($code->addedToLock) {
                 $title .= ", added to lock";
             } else {
                 $title .= ", to update";
             }
             if($code->needToBeRemoved) {
                 $title .= ", need removing";
             }
             if($code->used) {
                 $title .= ", in use";
             }
             $title .= "<br>";
             $i++;
        }
        
        echo "<span title='$title'>$inuse - $toUpdate - $toRemove - $total</span>";
    }

    public function getBattery($lock) {
//        print_r($lock);
        return $lock->batteryStatus;
    }

}
?>

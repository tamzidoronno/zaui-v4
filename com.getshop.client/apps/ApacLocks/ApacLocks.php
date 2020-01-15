<?php
namespace ns_01b8bd0c_0375_42fa_a44e_d177485db704;

class ApacLocks extends \MarketingApplication implements \Application {
    private $lock = null;
    private $server = null;
    private $slot = null;
    
    public function getDescription() {
        
    }
    
    public function typeOfLock($data) {
        if($data->typeOfLock == "LocstarLock") {
            return "SmartLock";
        }
        return $data->typeOfLock;
    }
    public function setSlot($slot) {
        $this->slot = $slot;
    }
    
    public function getSlot() {
        return $this->slot;
    }

    public function getName() {
        return "ApacLocks";
    }
    
    public function ApacLocks_getLockData() {
        $this->setLockServer();
        $this->includefile("lock");
    }
    
    public function saveZwaveLockSettings() {
        $lockSettings = new \core_getshoplocksystem_LockSettings();
        $lockSettings->lockId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_lockid'];
        $lockSettings->zwaveDeviceId = $_POST['data']['updateDeviceId'];
        $this->getApi()->getGetShopLockSystemManager()->lockSettingsChanged($lockSettings);
    }
    
    /**
     * 
     * @param \core_getshoplocksystem_Lock $lock
     * @return int
     */
    public function formatSlotsInUse($lock) {
        $cunt = 0;
        foreach ($lock->userSlots as $slut) {
            if ($slut->takenInUseDate) {
                $cunt++;
            }
        }
        return $cunt;
    }
    
    /**
     * 
     * @param \core_getshoplocksystem_Lock $lock
     * @return int
     */
    public function formatFreeSlots($lock) {
        $cunt = 0;
        foreach ($lock->userSlots as $slut) {
            if (!$slut->belongsToGroupId) {
                $cunt++;
            }
        }
        return $cunt;
    }
    
    public function getCountOfCodesToUpdate($lock) {
        $cunt = 0;
        foreach ($lock->userSlots as $slut) {
            if ($slut->toBeAdded || $slut->toBeRemoved) {
                $cunt++;
            }
        }
        return $cunt;
    }
    
    public function deleteLock() {
        $this->getApi()->getGetShopLockSystemManager()->deleteLock($_POST['data']['serverid'], $_POST['data']['lockid']);    
    }
    
    public function formatStatus($lock) {
        $ret = "";
        
        if ($lock->prioritizeLockUpdate) {
            $ret .= "<i class='fa fa-warning' title='prioritize activated'></i> ";
        }
        if ($lock->dead) {
            $ret .= "<i class='fa fa-ban' title='This device was found dead last update: ".$lock->markedDateAtDate."'></i>";
        } else if ($lock->currentlyUpdating) {
            $ret .= "<i class='fa fa-spinner fa-spin' title='Currently updating device, attempt: ".$lock->currentlyAttempt."'></i>";
        } else {
            $ret .= "<i class='fa fa-check' title='Last tried to update: ".$lock->lastStartedUpdating."'></i>";
        }
        
        return $ret;
    }
    
    public function render() {
        $servers = $this->getApi()->getGetShopLockSystemManager()->getLockServers();
        
        $total = 0;
        foreach ($servers as $server) {
            foreach ($server->locks as $lock) {
                $total += $this->getCountOfCodesToUpdate($lock);
            }
        }
        
        echo "<h2>Total codes to update: ".$total."</h2>";
        
        foreach ($servers as $server) {
            $args = array();
            
            echo "<h2>".$server->givenName."</h2>";
            
            $attributes = array(
                array("lockid", "gs_hidden", 'id'),
                array("zwaveDeviceId", "DeviceId", 'zwaveDeviceId'),
                array("typeOfLock", "Type", null, "typeOfLock"),
                array("name", "Name", 'name'),
                array("routing", "Route", null, "routing", "formatRouting"),
                array("maxnumberOfCodes", "MaxCodes", 'maxnumberOfCodes'),
                array("slotsToUpdate", "SlotsToUpdate", null, 'getCountOfCodesToUpdate'),
                array("slotsInUse", "SlotsInUse", null, 'formatSlotsInUse'),
                array("freeSlots", "Free Slots", null, 'formatFreeSlots'),
                array("status", "STATUS", null, 'formatStatus')
            );
            
            $extraData = array();
            $extraData['serverid'] = $server->id;
            
            $sorting = array();
            $sorting[] = "zwaveDeviceId";
            $sorting[] = "name";
            $sorting[] = "typeOfLock";
            $sorting[] = "slotsToUpdate";
            $sorting[] = "slotsInUse";
            $sorting[] = "slotsToUpdate";
            $sorting[] = "freeSlots";
            $sorting[] = "status";
            
            $table = new \GetShopModuleTable($this, "ApacLocks", "getLockData", null, $attributes, $extraData);
            $table->setSorting($sorting);
            $table->setData($server->locks);
            $table->sortByColumn('zwaveDeviceId', true);
            $table->render();
        }
        
    }

    public function routing($row) {
        if (isset($row->routing)) {
            return join(",", $row->routing);
        }
        
        return "";
    }
    
    public function getLock() {
        return $this->lock;
    }
    
    public function setLock($lock) {
        $this->lock = $lock;
    }
    
    public function getServer() {
        return $this->server;
    }

    public function setLockServer() {
       $servers = $this->getApi()->getGetShopLockSystemManager()->getLockServers();
       foreach ($servers as $server) {
           if ($server->id == $_POST['data']['serverid']) {
               $this->server = $server;
           }
       }
    }

    public function activateForceUpdate() {
        $this->getApi()->getGetShopLockSystemManager()->prioritizeLockUpdate($_POST['data']['serverid'], $_POST['data']['lockid']);
    }
    
    public function deactivateForceUpdate() {
        $this->getApi()->getGetShopLockSystemManager()->deactivatePrioritingOfLock($_POST['data']['serverid'], $_POST['data']['lockid']);
    }
    
    public function resendCodes() {
        $lock = $this->getApi()->getGetShopLockSystemManager()->getLock($_POST['data']['serverid'], $_POST['data']['lockid']);
        foreach ($lock->userSlots as $slot) {
            if ($slot->code && $slot->code->pinCode) {
                $this->getApi()->getGetShopLockSystemManager()->markCodeForResending($_POST['data']['serverid'], $_POST['data']['lockid'], $slot->slotId);
            }
        }
    }
    
    public function forceRemoveCodesOnSlot() {
        $lock = $this->getApi()->getGetShopLockSystemManager()->getLock($_POST['data']['serverid'], $_POST['data']['lockid']);
        foreach ($lock->userSlots as $slot) {
            if ($slot->code && $slot->code->pinCode) {
                $this->getApi()->getGetShopLockSystemManager()->forceDeleteSlot($_POST['data']['serverid'], $_POST['data']['lockid'], $slot->slotId);
            }
        }
    }
    
    public function markAllCodesChecked() {
        $lock = $this->getApi()->getGetShopLockSystemManager()->getLock($_POST['data']['serverid'], $_POST['data']['lockid']);
        foreach ($lock->userSlots as $slot) {
            if ($slot->code && $slot->code->pinCode) {
                $this->getApi()->getGetShopLockSystemManager()->markCodeAsUpdatedOnLock($_POST['data']['serverid'], $_POST['data']['lockid'], $slot->slotId);
            }
        }
    }
    
    public function markCodeAsSent() {
        $this->getApi()->getGetShopLockSystemManager()->markCodeAsUpdatedOnLock($_POST['data']['serverid'], $_POST['data']['lockid'], $_POST['data']['slotid']);
    }

    public function markCodeForResend() {
        $this->getApi()->getGetShopLockSystemManager()->markCodeForResending($_POST['data']['serverid'], $_POST['data']['lockid'], $_POST['data']['slotid']);
    }
    
    public function saveName() {
        $this->getApi()->getGetShopLockSystemManager()->renameLock($_POST['data']['serverid'], $_POST['data']['lockid'], $_POST['data']['name']);
    }
    
    public function deleteSlot() {
        $this->getApi()->getGetShopLockSystemManager()->deleteSlot($_POST['data']['serverid'], $_POST['data']['lockid'], $_POST['data']['slotid']);
    }
}
?>

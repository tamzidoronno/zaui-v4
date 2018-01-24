<?php
namespace ns_01b8bd0c_0375_42fa_a44e_d177485db704;

class ApacLocks extends \MarketingApplication implements \Application {
    private $lock = null;
    private $server = null;
    private $slot = null;
    
    public function getDescription() {
        
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
    
    public function formatCodesToUpdate($lock) {
        $cunt = 0;
        foreach ($lock->userSlots as $slut) {
            if ($slut->toBeAdded || $slut->toBeRemoved) {
                $cunt++;
            }
        }
        return $cunt;
    }
    
    public function render() {
        $servers = $this->getApi()->getGetShopLockSystemManager()->getLockServers();
        foreach ($servers as $server) {
            $args = array();
            
            echo "<h2>".$server->givenName."</h2>";
            
            $attributes = array(
                array("lockid", "gs_hidden", 'id'),
                array("zwaveDeviceId", "DeviceId", 'zwaveDeviceId'),
                array("typeOfLock", "Type", 'typeOfLock'),
                array("name", "Name", 'name'),
                array("maxnumberOfCodes", "MaxCodes", 'maxnumberOfCodes'),
                array("slotsToUpdate", "SlotsToUpdate", null, 'formatCodesToUpdate'),
                array("slotsInUse", "SlotsInUse", null, 'formatSlotsInUse'),
                array("freeSlots", "Free Slots", null, 'formatFreeSlots')
            );
            
            $extraData = array();
            $extraData['serverid'] = $server->id;
            
            $table = new \GetShopModuleTable($this, "ApacLocks", "getLockData", null, $attributes, $extraData);
            $table->setData($server->locks);
            $table->sortByColumn('zwaveDeviceId', true);
            $table->render();
        }
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
            $this->getApi()->getGetShopLockSystemManager()->markCodeForResending($_POST['data']['serverid'], $_POST['data']['lockid'], $slot->slotId);
        }
    }
}
?>

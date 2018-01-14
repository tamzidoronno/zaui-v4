<?php
namespace ns_3e89173c_42e2_493f_97bb_2261c0418bfe;

class GetShopLockSystem extends \WebshopApplication implements \Application {
    public function getDescription() {
        return "";
    }
    
    public function isEditingLock() {
        if (isset($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_serverid'])) {
            return true;
        }
        
        return false;
    }

    public function getName() {
        return "GetShopLockSystem";
    }

    public function render() {
        $this->includefile('header');
        $this->includefile("leftmenu");
        
        echo "<div class='rightside'>";
        $this->includefile($this->getMenuFile());
        echo "</div>";
    }
    
    public function showEditSlot() {
        
    }
    
    public function getMenuFile() {
        if ($this->isEditingLock()) {
            return "editlock";
        }
        
        if (!isset($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_menuaction'])) {
            return "addnewserver";
        }
        
        
        if ($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_menuaction'] == "server") {
            return "server";
        }
        
        if ($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_menuaction'] == "addnewserver") {
            return "addnewserver";
        }
        
        if ($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_menuaction'] == "addlockgroup") {
            return "addlockgroup";
        }
        
        if ($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_menuaction'] == "editgroup") {
            return "editgroup";
        }
        
        if ($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_menuaction'] == "editslot") {
            return "editslot";
        }
    }
    
    public function changeToMenu() {
        if (!isset($_POST['data']['dontclear'])) {
            $this->clearSessionVariables();
        }
        
        $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_menuaction'] = $_POST['data']['menu'];
        if (isset($_POST['data']['serverid'])) {
            $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_serverid'] = $_POST['data']['serverid'];
        }
        if (isset($_POST['data']['groupid'])) {
            $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid'] = $_POST['data']['groupid'];
        }
        if (isset($_POST['data']['slotid'])) {
            $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_slotid'] = $_POST['data']['slotid'];
        }
    }
    
    public function createNewLockGroup() {
        $this->getApi()->getGetShopLockSystemManager()->createNewLockGroup($_POST['data']['name'], $_POST['data']['numberofslots']);
    }
    
    public function createNewServer() {
        $this->getApi()->getGetShopLockSystemManager()->createServer(
                $_POST['data']['type'], 
                $_POST['data']['hostname'], 
                $_POST['data']['username'], 
                $_POST['data']['password'], 
                $_POST['data']['name']);
    }
    
    public function deleteServer() {
        $this->getApi()->getGetShopLockSystemManager()->deleteServer($_POST['data']['serverid']);
        $this->clearSessionVariables();
    }
    
    public function startFetchingOfLocks() {
        $this->getApi()->getGetShopLockSystemManager()->startFetchingOfLocksFromServer($_POST['data']['serverid']);
    }
    
    public function editSettingsForLock() {
        $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_serverid'] = $_POST['data']['serverid'];
        $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_lockid'] = $_POST['data']['lockid'];
    }
    
    public function cancelEditingLock() {
        unset($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_serverid']);
        unset($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_lockid']);
    }

    /**
     * 
     * @return \core_getshoplocksystem_Lock 
     */
    public function getLock() {
        $serverId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_serverid'];
        $lockId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_lockid'];
        return $this->getApi()->getGetShopLockSystemManager()->getLock($serverId, $lockId);
    }

    public function isRazberryLock() {
        $lock = $this->getLock();
        
        if (isset($lock->zwaveDeviceId)) {
            return true;
        }
        
        return false;
    }
    
    public function saveZwaveLockSettings() {
        $lockSettings = new \core_getshoplocksystem_LockSettings();
        $lockSettings->lockId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_lockid'];
        $lockSettings->zwaveDeviceId = $_POST['data']['updateDeviceId'];
        $this->getApi()->getGetShopLockSystemManager()->lockSettingsChanged($lockSettings);
    }
    
    public function markCodeForResend() {
        $serverId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_serverid'];
        $lockId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_lockid'];
        $this->getApi()->getGetShopLockSystemManager()->markCodeForResending($serverId, $lockId, $_POST['data']['slotid']);
    }
    
    public function markCodeForDeletion() {
        $serverId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_serverid'];
        $lockId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_lockid'];
        $this->getApi()->getGetShopLockSystemManager()->markCodeForDeletion($serverId, $lockId, $_POST['data']['slotid']);
    }
    
    public function generateNewCodesForLock() {
        $serverId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_serverid'];
        $lockId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_lockid'];
        $this->getApi()->getGetShopLockSystemManager()->generateNewCodesForLock($serverId, $lockId);
    }
    
    public function prioritizeLockUpdate() {
        $serverId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_serverid'];
        $lockId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_lockid'];
        $this->getApi()->getGetShopLockSystemManager()->prioritizeLockUpdate($serverId, $lockId);
    }

    /**
     * 
     * @return LockServerBase
     */
    public function getServers() {
        if (!isset($this->servers)) {
            $this->servers = $this->getApi()->getGetShopLockSystemManager()->getLockServers();
        }
        
        return $this->servers;
    }

    public function getServer() {
        foreach ($this->getServers() as $server) {
            if ($server->id == $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_serverid']) {
                return $server;
            }
        }
        
        return null;
    }

    public function clearSessionVariables() {
        unset($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_serverid']);
        unset($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_serverid']);
        unset($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_lockid']);
        unset($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_menuaction']);
        unset($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_serverid']);
        unset($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid']);
        unset($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_slotid']);
    }

    public function getGroups() {
        if (!isset($this->groups)) {
            $this->groups = $this->getApi()->getGetShopLockSystemManager()->getAllGroups();
        }
        
        return $this->groups;
    }

    public function getGroup() {
        if (!isset($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid']))
            return false;
        
        return $this->getApi()->getGetShopLockSystemManager()->getGroup($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid']);
    }

    public function saveGroup() {
        $this->getApi()->getGetShopLockSystemManager()->setLocksToGroup($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid'], $_POST['data']['servers']);
    }
    
    public function deleteGroup() {
        $this->getApi()->getGetShopLockSystemManager()->deleteGroup($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid']);
        $this->clearSessionVariables();
    }

    public function getSlot() {
        return $this->slot;
    }

    public function setSlot($slot) {
        $this->slot = $slot;
    }
    
    public function saveSlotInformation() {
        $groupId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid'];   
        $slotId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_slotid'];
        $code = $_POST['data']['pincode'];
        
        if ($code) {
            $this->getApi()->getGetShopLockSystemManager()->changeCode($groupId, $slotId, $code, "");
        }
        
        $startDate = $this->convertToJavaDate(strtotime($_POST['data']['from']));
        $endDate = $this->convertToJavaDate(strtotime($_POST['data']['to']));

        if ($startDate && $endDate) {
            $this->getApi()->getGetShopLockSystemManager()->changeDatesForSlot($groupId, $slotId, $startDate, $endDate);
        }
        
        $this->goBackToGroup();
    }
    
    public function activateForceUpdate() {
        $serverId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_serverid'];
        $lockId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_lockid'];
        $this->getApi()->getGetShopLockSystemManager()->prioritizeLockUpdate($serverId, $lockId);
    }
    
    public function deactivateForceUpdate() {
        $serverId = $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_editlock_serverid'];
        $this->getApi()->getGetShopLockSystemManager()->deactivatePrioritingOfLock($serverId);
    }

    public function goBackToGroup() {
        $_POST['data']['menu'] = 'editgroup';
        $_POST['data']['dontclear'] = 'true';
        $this->changeToMenu();
    }

}
?>
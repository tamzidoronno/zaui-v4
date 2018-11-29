<?php
namespace ns_25c15968_4b9b_4c23_9e44_dc5cdb83244c;

class ApacAccessGroups extends \MarketingApplication implements \Application {
    private $cachedLocks = array();
    
    public function getDescription() {
        
    }

    public function getName() {
        return "ApacAccessGroups";
    }

    public function render() {
        $this->showList();
        $this->includefile("addlockgroup");
    }
    
    public function formatInUse($lockGroup) {
        $count = 0;
        foreach ($lockGroup->groupLockCodes as $group) {
            if ($group->takenInUseDate) {
                $count++;
            }
        }
        return $count;
    }
    
    public function formatStatus($lockGroup) {
        $ret = "";
        
        foreach ($lockGroup->groupLockCodes as $serverId => $masterSlot) {
            if (count($masterSlot->slotsNotOk)) {
                if ($ret) {
                    $ret .= " | ";
                }
                $title = $this->generateTitle($serverId, $masterSlot);
                $ret .= "<span title='$title'>".count($masterSlot->slotsNotOk)."</span>";
            }
        }
        
        return $ret;
    }
    
    public function showList() {
        $args = array();
        
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('name', 'Group Name', 'name'),
            array('numberOfSlotsInGroup', 'Slotsize', 'numberOfSlotsInGroup'),
            array(null, 'Status', null, 'formatStatus'),
            array(null, 'Slots in use', null, 'formatInUse'),
        );
        
        $table = new \GetShopModuleTable($this, 'GetShopLockSystemManager', 'getAllGroups', $args, $attributes);
        $table->render();
    }
    
    public function createNewLockGroup() {
        $this->getApi()->getGetShopLockSystemManager()->createNewLockGroup($_POST['data']['name'], $_POST['data']['numberofslots'], $_POST['data']['codesize']);
    }
    
    public function GetShopLockSystemManager_getAllGroups() {
        $_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid'] = $_POST['data']['id'];
        $this->lockGroup = $this->getApi()->getGetShopLockSystemManager()->getGroup($_POST['data']['id']);
        $this->includefile("editgroup");
    }
    
    public function setSlot($slot) {
        $this->slot = $slot;
    }
    
    public function getSlot() {
        return $this->slot;
    }
    
    public function getGroup() {
        return $this->lockGroup;
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
    
    public function saveGroup() {
        $this->getApi()->getGetShopLockSystemManager()->setLocksToGroup($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid'], $_POST['data']['servers']);
        $this->getApi()->getGetShopLockSystemManager()->setGroupVirtual($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid'], isset($_POST['data']['isvirtual']));
    }
    
    public function deleteGroup() {
        $this->getApi()->getGetShopLockSystemManager()->deleteGroup($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid']);
    }
    
    public function changeSlotCode() {
        $this->getApi()->getGetShopLockSystemManager()->changeCode($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid'], $_POST['data']['slotid'], $_POST['data']['code'], "");
    }
    
    public function generateTitle($serverId, $masterSlot) {
        
        $ret = "";
        $ret .= "Slot: ".$masterSlot->slotId;
        $ret .= "<br/><br/><b>The following locks has not been updated</b>";
        foreach ($masterSlot->slotsNotOk as $slotNotOk) {
            $lock = $this->getLock($slotNotOk->connectedToServerId, $slotNotOk->connectedToLockId);
            $ret .= "<br/>Lockname: ".$lock->name;
        }
        
        return $ret;
    }

    public function getLock($serverId, $lockId) {
        $key = $serverId."_".$lockId;
    
        if (isset($this->cachedLocks[$key])) {
            return $this->cachedLocks[$key];
        }
        
        $lock = $this->getApi()->getGetShopLockSystemManager()->getLock($serverId, $lockId);
        $this->cachedLocks[$key] = $lock;
        
        return $lock;
    }

}
?>

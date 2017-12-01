<?php
namespace ns_25c15968_4b9b_4c23_9e44_dc5cdb83244c;

class ApacAccessGroups extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ApacAccessGroups";
    }

    public function render() {
        $this->showList();
        $this->includefile("addlockgroup");
    }
    
    public function showList() {
        $args = array();
        
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('name', 'Group Name', 'name'),
            array('numberOfSlotsInGroup', 'Slotsize', 'numberOfSlotsInGroup')
        );
        
        $table = new \GetShopModuleTable($this, 'GetShopLockSystemManager', 'getAllGroups', $args, $attributes);
        $table->render();
    }
    
    public function createNewLockGroup() {
        $this->getApi()->getGetShopLockSystemManager()->createNewLockGroup($_POST['data']['name'], $_POST['data']['numberofslots']);
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
    }
    
    public function deleteGroup() {
        $this->getApi()->getGetShopLockSystemManager()->deleteGroup($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid']);
    }
    
    public function changeSlotCode() {
        $this->getApi()->getGetShopLockSystemManager()->changeCode($_SESSION['ns_3e89173c_42e2_493f_97bb_2261c0418bfe_groupid'], $_POST['data']['slotid'], $_POST['data']['code'], "");
    }
}
?>

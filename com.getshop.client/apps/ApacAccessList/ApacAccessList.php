<?php
namespace ns_93a55f7a_07ef_4199_8ab2_1a3019c160cd;

class ApacAccessList extends \MarketingApplication implements \Application {
    private $accessGroups = array();
    
    public function getDescription() {
        
    }

    public function getName() {
        return "ApacAccessList";
    }

    public function render() {
        $this->loadAllAccessGroups();
        $this->renderAccessList();
    }

    public function formatGroup($userAccess) {
        $group = $this->getAccessGroup($userAccess->lockGroupId);
        if(!$group) {
            return "";
        }
        return $group->name;
    }
    
    public function formatPinCode($userAccess) {
        return $userAccess->lockCode->pinCode;
    }
    
    public function formatRowCreatedDate($userAccess) {
        return \GetShopModuleTable::formatDate($userAccess->rowCreatedDate);
    }
    
    public function GetShopLockSystemManager_getAllAccessUsersFlat() {
        $view = new \ns_0cf90108_6e9f_49fd_abfe_7541d1526ba2\ApacAccessView();
        $view->setUserId($_POST['data']['id']);
        $view->renderApplication(true, $this);
    }
    
    public function renderAccessList() {
        $options = new \core_common_FilterOptions();

        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('date', 'Created', null, 'formatRowCreatedDate'),
            array('name', 'Name', 'fullName'),
            array('email', 'Email', 'email'),
            array('prefix', 'Prefix', 'prefix'),
            array('phonenumber', 'Phone', 'phonenumber'),
            array('groupid', 'Access Group', null, 'formatGroup'),
            array('code', 'PinCode', null, 'formatPinCode')
        );
        
        $args = array();
                
        $table = new \GetShopModuleTable($this, 'GetShopLockSystemManager', 'getAllAccessUsersFlat', $args, $attributes);
        $table->sortByColumn("groupid");
        $table->loadContentInOverlay = true;
        $table->render();
    }

    public function loadAllAccessGroups() {
        $this->accessGroups = $this->getApi()->getGetShopLockSystemManager()->getAllGroups();
    }
    
    /**
     * 
     * @param type $groupId
     * @return \core_getshoplocksystem_LockGroup 
     */
    public function getAccessGroup($groupId) {
        foreach ($this->accessGroups as $group) {
            if ($group->id == $groupId) {
                return $group;
            }
        }
        
        return null;
    }

}
?>

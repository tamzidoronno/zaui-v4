<?php
namespace ns_7332bf2f_8fd3_422a_aabc_d77db883e472;

class ProfilePrinter extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProfilePrinter";
    }

    public function render() {
        $this->includefile("profile");
    }
    
    public function getUser() {
        if (isset($this->userId)) {
            return $this->getApi()->getUserManager()->getUserById($this->userId);
        }
        
        $userId = $this->getConfigurationSetting("selecteduser");
        
        if (!$userId)
            return null;
        
        return $this->getApi()->getUserManager()->getUserById($userId);
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function renderUserSettings($user) {
        $this->selectedUser = $user;
        $this->includefile("companyUserSelection");
    }

    /**
     * 
     * @return \core_usermanager_data_User
     */
    public function getSelectedUser() {
        return $this->selectedUser;
    }

    public function setUserId($userId) {
        $this->userId = $userId;
    }

}
?>

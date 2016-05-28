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
        return $this->getApi()->getUserManager()->getLoggedOnUser();
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

}
?>

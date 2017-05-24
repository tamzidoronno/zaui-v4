<?php
namespace ns_ed9b4b90_a5c8_4b25_99a4_d143f643dc2f;

class UserRoles extends \SystemApplication implements \Application {
    private $user = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "UserRoles";
    }

    public function render() {
        
    }
    
    public function renderUserSettings($user) {
        $this->user = $user;
        $this->includefile("usersetting");
    }
    
    /**
     * @return \core_usermanager_data_User
     */
    public function getCurrentUser() {
        return $this->user;
    }
    
    public function deleteRole() {
        $this->setUser();
        $id = $_POST['value2'];
        $this->getApi()->getUserManager()->deleteUserRole($id);
    }
    
    public function saveAndCreate() {
        $this->setUser();
        
        if (isset($_POST['rolename']) && $_POST['rolename']) {
            $newRole = new \core_usermanager_data_UserRole();
            $newRole->name = $_POST['rolename'];
            $this->getApi()->getUserManager()->saveUserRole($newRole);
        }
        
        $roles = $this->getApi()->getUserManager()->getUserRoles();
        $this->user->userRoleIds = array();
        foreach ($roles as $role) {
            if (isset($_POST[$role->id]) && $_POST[$role->id] === "true") {
                $this->user->userRoleIds[] = $role->id;
            }
        }
        $this->getApi()->getUserManager()->saveUser($this->user);
        \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::refresh();
    }

    public function setUser() {
        if (isset($_POST['userid'])) {
            $this->user = $this->getApi()->getUserManager()->getUserById($_POST['userid']);
        }
    }

}
?>

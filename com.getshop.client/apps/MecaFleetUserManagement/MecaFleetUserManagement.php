<?php
namespace ns_f7b3bfd9_0bd7_483b_abe5_01b0ff0d67db;

class MecaFleetUserManagement extends \SystemApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetUserManagement";
    }

    public function render() {
        $this->includefile("createuser");
    }
    
    public function deleteUser() {
        $this->getApi()->getUserManager()->deleteUser($_POST['data']['userid']);
    }
    
    public function createuser() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['navn'];
        $user->emailAddress = $_POST['data']['epost'];
        $user->password = $_POST['data']['passord'];
        $user->type = 10;
        
        
        $userCreated = $this->getApi()->getUserManager()->createUser($user);
        $this->getApi()->getUserManager()->addMetaData($userCreated->id, "mecafleetportalid", $_POST['data']['fleetid']);
        
        $this->getApi()->getUserManager()->updatePasswordSecure($userCreated->id, $_POST['data']['passord']);
    }
}
?>

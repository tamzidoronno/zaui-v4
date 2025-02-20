<?php
namespace ns_b485ee62_3163_4612_93d4_a36b44e05a8d;

class SedoxProfileUpdater extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxProfileUpdater";
    }

    public function render() {
        $this->includefile("profileupdater");
    }
    
    public function saveUser() {
        $user = $this->getApi()->getUserManager()->getLoggedOnUser();
        $user->fullName = $_POST['data']['name'];
        $user->emailAddress = $_POST['data']['email'];
        $user->cellPhone = $_POST['data']['cellphone'];
        $this->getApi()->getUserManager()->saveUser($user);
        $this->getApi()->getSedoxProductManager()->setEvcId($user->id, $_POST['data']['evcid']);
    }
}
?>

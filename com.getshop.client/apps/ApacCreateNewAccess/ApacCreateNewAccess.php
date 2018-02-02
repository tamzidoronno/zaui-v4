<?php
namespace ns_b698ac77_15f3_45b7_a412_47186a2defb6;

class ApacCreateNewAccess extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ApacCreateNewAccess";
    }

    public function render() {
        $this->includefile("createnewaccess");
    }
    
    public function createAccess() {
        $accessUser = new \core_getshoplocksystem_AccessGroupUserAccess();
        $accessUser->fullName = $_POST['data']['name'];
        $accessUser->email = $_POST['data']['email'];
        $accessUser->phonenumber = $_POST['data']['phonenumber'];
        $accessUser->prefix = $_POST['data']['prefix'];
        $user = $this->getApi()->getGetShopLockSystemManager()->grantAccessDirect($_POST['data']['room'], $accessUser);
        echo $user->id;
        die();
    }
}
?>

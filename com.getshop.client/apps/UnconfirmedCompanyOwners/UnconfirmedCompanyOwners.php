<?php
namespace ns_267a9796_0fb2_4634_b1a5_d935ad4b43bc;

class UnconfirmedCompanyOwners extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "UnconfirmedCompanyOwners";
    }

    public function render() {
        $this->includefile("unconfirmedowners");
    }
    
    public function confirm() {
        $this->getApi()->getUserManager()->confirmCompanyOwner($_POST['data']['userid']);
    }
    
    public function reject() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        $user->wantToBecomeCompanyOwner = false;
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
}
?>

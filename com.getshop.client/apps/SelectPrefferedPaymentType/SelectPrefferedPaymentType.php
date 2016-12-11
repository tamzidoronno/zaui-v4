<?php
namespace ns_ccf7ea4c_5367_49e6_85c2_8b776905503a;

class SelectPrefferedPaymentType extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SelectPrefferedPaymentType";
    }

    public function render() {
        $this->includefile("selectview");
        if(isset($_POST['event']) && $_POST['event'] == "selectPaymentMethod") {
            echo "* Ditt valg er nå oppdatert.";
        } 
    }
    
    public function selectPaymentMethod() {
        $user = $this->getApi()->getUserManager()->getLoggedOnUser();
        $user->preferredPaymentType = $_POST['data']['paymenttype'];
        $this->getApi()->getUserManager()->saveUser($user);
    }
}
?>

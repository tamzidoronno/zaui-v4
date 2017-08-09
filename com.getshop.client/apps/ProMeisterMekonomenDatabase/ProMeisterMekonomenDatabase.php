<?php
namespace ns_b794bba8_d25c_49a7_96c9_0aea81a408ee;

class ProMeisterMekonomenDatabase extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterMekonomenDatabase";
    }

    public function render() {
        if ($_SESSION['connectToUser']) {
            $this->includefile("connectToUser");
            return; 
        }
        
        $this->includefile("search");
    }
    
    public function searchForUsers() {
        // helperfunction for serach, can not be removed.
    }
    
    public function showConnectToUser() {
        $_SESSION['connectToUser'] = $_POST['data']['mekonomenusername'];
    }
    
    public function cancel() {
        unset($_SESSION['connectToUser']);
    }
    
    public function connectToUser() {
        $this->getApi()->getMekonomenManager()->addUserId($_POST['data']['userid'], $_SESSION['connectToUser']);
        $this->cancel();
    }
    
    
}
?>

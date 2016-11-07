<?php
namespace ns_b19c7f89_2e3e_4a2d_aaa5_1e9ef8e6cb93;

class LoggedOnInfoApp extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "LoggedOnInfoApp";
    }

    public function render() {
        $user = $this->getApi()->getUserManager()->getLoggedOnUser();
        if($user) {
            echo $this->__w("Logged on as"). " : " . $user->fullName . "<br>";
            echo "<a onclick='document.location.href=\"/logout.php?goBackToHome=true\"' style='cursor:pointer;'>" . $this->__w("Logout") . "</a>";
        }
    }
}
?>

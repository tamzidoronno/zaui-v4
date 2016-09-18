<?php
namespace ns_e8159b67_815e_4c78_8723_cedf3678f5b8;

class LoginWithRefCode extends \SystemApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "LoginWithRefCode";
    }

    public function render() {
        $this->includefile("login");
    }
    
    public function loginWithKey() {
        $userLoggedIn = $this->getApi()->getUserManager()->logonUsingRefNumber($_POST['data']['refcode']);
        
        
        if ($userLoggedIn != null && isset($userLoggedIn)) {
            unset($_SESSION['tempaddress']);
            unset($_SESSION['gs_currently_showing_modal']);
            $_SESSION['loggedin'] = serialize($userLoggedIn);
            // Need to refresh the page in order to make sure that all data is loaded.
            // This login is after data has been initialized to factory.
            $page = $_SERVER['PHP_SELF'];
            header("Refresh:0");
            exit(0);
        }   
        
    }
}
?>

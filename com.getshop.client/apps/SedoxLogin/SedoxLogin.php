<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
namespace ns_05b2baef_5fba_4f01_9fcb_04a8c80b2907;

class SedoxLogin extends \ApplicationBase implements \Application {
    
    public function getDescription() {
        return "SedoxLogin";
    }

    public function getName() {
        return "SedoxLogin";
    }
    
    public function login() {
        $emailAddress = @$_POST['emailAddress'];
        $password = @$_POST['password'];
        if ($emailAddress != null && $password != null) {
            $user = $this->getApi()->getSedoxProductManager()->login($emailAddress, $password);
            \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::setLoggedOn($user);
//            echo "<script>document.location = /?page=home;</script>";
        }
    }
    
    public function preProcess() {
        if (isset($_POST['sedoxlogin']) == "login") {
            $this->login();
        }
    }

    public function render() {
        
        $user = $this->getUser();
        if (!$user) {
            $this->includefile("login");
        } else {
            $this->includefile("userinfo");
        }
    }    
}
?>

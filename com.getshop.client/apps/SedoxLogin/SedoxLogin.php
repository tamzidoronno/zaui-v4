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
        $emailAddress = $_POST['data']['emailAddress'];
        $password = $_POST['data']['password'];
        $user = $this->getApi()->getSedoxProductManager()->login($emailAddress, $password);
        \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::setLoggedOn($user);
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

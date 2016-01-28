<?php
namespace ns_af4ca00c_4007_42c1_a895_254710311191;

class SedoxLogin extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxLogin";
    }

    public function render() {
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null) {
            return;
        }
        
        $this->includefile("login");
    }
    
    public function login() {
        $emailAddress = @$_POST['data']['username'];
        $password = @$_POST['data']['password'];
        if ($emailAddress != null && $password != null) {
            $user = $this->getApi()->getSedoxProductManager()->login($emailAddress, $password);
            if ($user != null) {
                echo "success";
            }
            
            \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::setLoggedOn($user);
            return;
            
        }
        
        echo "failed";
    }

}
?>
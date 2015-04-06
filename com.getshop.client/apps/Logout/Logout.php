<?php
namespace ns_681e581f_3abb_448d_b935_fb8af9327821;

class Logout extends \SystemApplication implements \Application {

    public function getDescription() {
        
    }

    public function getName() {
        return "logout app";
    }

    public function render() {
        $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        
        if($user) {
            if(\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
                echo "You are not automatically being logged out of this page since your an administrator, use the logout button in the menu to the left.";
                return;
            }
            $this->getApi()->getUserManager()->logout();
            session_destroy();
            ?>
            <script>location.reload(); </script>
            <?
        }
    }
}
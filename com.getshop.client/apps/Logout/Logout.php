<?php
namespace ns_29e56415_334d_460e_8011_c40f2fdaf9da;

class Logout extends \SystemApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "Logout";
    }

    public function render() {
        $this->doLogout();
    }

    public function doLogout() {
        if($this->getApi()->getUserManager()->getLoggedOnUser()->showHiddenFields) {
            echo "<div style='text-align: center; margin-top: 20px;'><h1>Administrators is logging out using the menu to the left.</h1></div>";
        } else {
            $this->logout();
        }
    }

    public function logout() {
        session_destroy();
        ?>
        <script>
            avoidLoggingOut = true;
            thundashop.common.goToPage("loggedout");
        </script>
        <?
    }
}
?>

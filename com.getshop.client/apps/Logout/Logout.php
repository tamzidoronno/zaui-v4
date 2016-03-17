<?php
namespace ns_29e56415_334d_460e_8011_c40f2fdaf9da;

class Logout extends \SystemApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "Logout";
    }

    public function render() {
        $temp = $this->getConfigurationSetting("template");
        if(!$temp || $temp == 1) {
            $this->doLogout();
        } else {
            $this->includefile("template_" . $temp);
        }
    }

    public function doLogout() {
        if(!$this->isEditorMode()) {
            $this->logout();
        } else {
            echo "<h1>Administrators is logging out using the menu to the left.</h1>";
        }
    }

    public function logout() {
        session_destroy();
    }

}
?>

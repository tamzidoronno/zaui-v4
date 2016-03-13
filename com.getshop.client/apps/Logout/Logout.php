<?php
namespace ns_29e56415_334d_460e_8011_c40f2fdaf9da;

class Logout extends \SystemApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "Logout";
    }

    public function render() {
        if(!$this->isEditorMode()) {
            session_destroy();
        } else {
            echo "<h1>Administrators is logging out using the menu to the left.</h1>";
        }
    }
}
?>

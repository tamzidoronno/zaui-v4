<?php
namespace ns_42ca6c04_872e_452a_8a50_72b95ab35902;

class UpdateRegistrationData extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "UpdateRegistrationData";
    }

    public function render() {
        $this->includefile("updateuserform");
    }
}
?>

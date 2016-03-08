<?php
namespace ns_56991737_d371_4ed3_8232_19bb9eff39b7;

class UsersOrderDetails extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "UsersOrderDetails";
    }

    public function render() {
        $this->includefile("detailsview");
    }
}
?>

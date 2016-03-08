<?php
namespace ns_eefde94c_9a3a_4233_8df5_86765c28b0fc;

class UsersOrderItems extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "UsersOrderItems";
    }

    public function render() {
        $this->includefile("orderSummary");
    }
}
?>

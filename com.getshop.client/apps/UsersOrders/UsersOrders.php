<?php
namespace ns_d124feed_8e5e_4977_b6d2_70d3524c269e;

class UsersOrders extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "UsersOrders";
    }

    public function render() {
        $this->includefile("orderlist");
    }
}
?>

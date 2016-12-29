<?php
namespace ns_659cac48_9502_4b79_b11b_e3fa1535f85a;

class ResturantRoomBuilder extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ResturantRoomBuilder";
    }

    public function render() {
        $this->includefile("rooms");
    }
    
    public function deleteTable() {
        $this->getApi()->getResturantManager()->deleteTable($_POST['data']['tableid']);
    }
    
    public function createTable() {
        $this->getApi()->getResturantManager()->createTable($_POST['data']['roomid'], $_POST['data']['roomname']);
    }
    
    public function createRoom() {
        $this->getApi()->getResturantManager()->createRoom($_POST['data']['roomname']);
    }
    
    
}
?>

<?php
namespace ns_020b57c2_8f80_46e1_a420_cb163f4fb2d2;

class HotelbookingCleaning extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return "My application";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "My application";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function markRoom() {
        $this->getApi()->getHotelBookingManager()->markRoomAsReady($_POST['data']['roomId']);
    }
    
    public function render() {
        $this->includefile("cleaning");
    }
}
?>

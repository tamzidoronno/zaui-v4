<?php
namespace ns_020b57c2_8f80_46e1_a420_cb163f4fb2d2;

class HotelbookingCleaning extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return "Administration for room cleaning in booking.";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "Hotelbooking Cleaning";
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
        if(!$this->isEditorMode()) {
            echo "<center>";
        echo "<br>";
        echo "<br>";
            $login = new \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login();
            $login->render();
        echo "<br>";
        echo "<br>";
            echo "</center>";
        } else {
            $this->includefile("cleaning");
        }
    }
}
?>

<?php
namespace ns_037663a4_6e54_4c63_a54d_9f9dbf91d973;

class HotelUsers extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return "Hotel users";
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

    public function render() {
        $this->includefile("userspage");
    }
}
?>

<?php
namespace ns_d16b27d9_579f_4d44_b90b_4223de0eb6f2;

class Hotelbooking extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }
 
   public function getDescription() {
        return "Hotelbooking";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "Hotelbooking";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() { 
   }

    public function render() {
        $this->includefile("Hotelbooking");
    }
}
?>

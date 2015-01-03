<?php
namespace ns_b6b0d74f_c802_401e_9bb7_facf3e420f61;

class ApplicationPromotor extends \WebshopApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return "ApplicationPromotor";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "ApplicationPromotor";
    }

    public function convertUUIDtoString($namespace) {
        return "ns_" . str_replace("-", "_", $namespace);
    }
    
    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("helloworld");
    }
}
?>

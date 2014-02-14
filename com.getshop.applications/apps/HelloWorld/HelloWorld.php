<?php
namespace ns_b0db6ad0_8cc2_11e2_9e96_0800200c9a66;

class HelloWorld extends \ApplicationBase implements \Application {
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

    public function render() {
        $this->includefile("helloworld");
    }
}
?>

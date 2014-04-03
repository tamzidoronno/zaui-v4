<?php
namespace ns_af271ec1_2b21_4e61_a240_f6daba26f52b;

class SignUpForm extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return "SignUpForm";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "SignUpForm";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("SignUpForm");
    }
}
?>

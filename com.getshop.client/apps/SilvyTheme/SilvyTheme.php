<?php
namespace ns_dfffd91f_f2f9_4928_ab7d_249b3e1017cf;

class SilvyTheme extends \ThemeApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
   
    function __construct() {
        $this->api = $this->getApi();
    
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 50);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 300);
    }

    public function getDescription() {
        return "SilvyTheme";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "SilvyTheme";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("SilvyTheme");
    }
}
?>

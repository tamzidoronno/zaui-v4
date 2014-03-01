<?php
namespace ns_e72c1c7d_4af8_41ca_bb1b_22d8468edf0a;

class PinBoardTheme extends \ThemeApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
        $this->api = $this->getApi();
    
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 80);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 500);
 
    }

    public function getDescription() {
        return "PinBoardTheme";
    }
    
        public function addScripts() {
        echo "\n" . "<link href='http://fonts.googleapis.com/css?family=Esteban' rel='stylesheet' type='text/css'>";
    }    


    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "PinBoardTheme";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("PinBoardTheme");
    }
}
?>

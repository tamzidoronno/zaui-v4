<?php
namespace ns_26a517ac_c519_412b_9266_59df49355c82;

class WilhelmsenTheme extends \ThemeApplication implements \Application { 
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 75);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 400);
    }

    public function getDescription() {
        return "WilhelmsenTheme";
    }
    

    public function getAvailablePositions() {
        return "left";
    }
    
    public function addScripts() {
        echo "\n" . "<link href='http://fonts.googleapis.com/css?family=Open+Sans:400,600' rel='stylesheet' type='text/css'>";
        echo "\n" . "<link href='http://fonts.googleapis.com/css?family=Droid+Serif:400,700,400italic,700italic' rel='stylesheet' type='text/css'>";
    }

    
    
    public function getName() {
        return "WilhelmsenTheme";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("WilhelmsenTheme");
    }
}
?>

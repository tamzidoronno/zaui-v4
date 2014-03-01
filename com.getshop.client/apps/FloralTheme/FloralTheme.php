<?php

namespace ns_e30b4f2c_f1f4_4c33_9222_d5d863829ce4;

class FloralTheme extends \ThemeApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
        $this->api = $this->getApi();
    
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 140);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 300);
    }

    
    public function getDescription() {
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "FloralTheme";
    } 
    
    
    
    public function addScripts() {
        echo "<link href='http://fonts.googleapis.com/css?family=Bubbler+One' rel='stylesheet' type='text/css'>";
    }    

    public function postProcess() {
        
    }
    

    public function preProcess() {
        
    }
    
    public function render() {
    }
    
    public function addApplications() {
        
    }
}
?>

<?php
namespace ns_3d4a322c_94f5_41a5_bdf5_7e2a1b43c70b;

class GetShopSignUpTheme extends \ThemeApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return "GetShopSignUpTheme";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "GetShopSignUpTheme";
    }

    public function addScripts() {
        echo "<link href='https://fonts.googleapis.com/css?family=Niconne&subset=latin,latin-ext' rel='stylesheet' type='text/css'>";
    }    
    
    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("GetShopSignUpTheme");
    }
}
?>

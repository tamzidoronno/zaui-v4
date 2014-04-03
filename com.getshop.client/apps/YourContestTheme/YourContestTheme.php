<?php
namespace ns_62cb9004_a080_4190_bb4a_74adf39f5d0a;

class YourContestTheme extends \ThemeApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return "YourContestTheme";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "YourContestTheme";
    }

    public function postProcess() {
        
    }

    public function addScripts() {
        echo "\n" . "<link href='http://fonts.googleapis.com/css?family=Alegreya' rel='stylesheet' type='text/css'>";
    }    
    
    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("YourContestTheme");
    }
}
?>

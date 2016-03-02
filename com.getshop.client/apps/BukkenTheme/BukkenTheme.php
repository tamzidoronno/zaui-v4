<?php
namespace ns_0acf824b_9dc4_4ad3_bb3c_3c8b9ffb1fa8;

class BukkenTheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "BukkenTheme";
    }

    public function addScripts() {
        echo "\n" . "<link href='https://fonts.googleapis.com/css?family=Slabo+27px|Open+Sans' rel='stylesheet' type='text/css'>";
        echo "\n" . "<link href='https://fonts.googleapis.com/css?family=Raleway|Indie+Flower' rel='stylesheet' type='text/css'>";
        echo "\n" . "<link href='https://fonts.googleapis.com/css?family=Quicksand' rel='stylesheet' type='text/css'>";
    }   
    
    public function render() {
        
    }
    
    public function getThemeClasses() {
         return ["floatingboxes"];
    }
    
    public function printArrowsOutSideOnCarousel() {
        return false;
    }
}
?>

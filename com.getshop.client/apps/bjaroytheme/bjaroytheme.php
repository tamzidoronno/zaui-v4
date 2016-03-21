<?php
namespace ns_2dc6c222_527d_4297_aa8c_dc9830dc3404;

class bjaroytheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function addScripts() {
        echo "\n" . "<link href='https://fonts.googleapis.com/css?family=Alegreya+Sans+SC' rel='stylesheet' type='text/css'>";
        echo "\n" . "<link href='https://fonts.googleapis.com/css?family=Quicksand:400,300,100' rel='stylesheet' type='text/css'>";
        echo "\n" . "<link href='https://fonts.googleapis.com/css?family=Anton' rel='stylesheet' type='text/css'>";
        echo "\n" . "<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet' type='text/css'>";
    }   
    
    public function getName() {
        return "bjaroytheme";
    }

    public function render() {
        
    }
    
    public function getThemeClasses() {
        return ["subpages"];
    }
}
?>

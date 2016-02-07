<?php
namespace ns_67ed96b8_ee47_4fdc_8fc6_f5c90b6414d3;

class PartyBoxTheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PartyBoxTheme";
    }
    
    public function addScripts() {
        echo "\n" . "<link href='https://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>";
    }   
    
    public function render() {
        
    }
    
    public function getThemeClasses() {
        return ["front_page_box"];
    }
}
?>

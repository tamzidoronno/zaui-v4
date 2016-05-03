<?php
namespace ns_969e6b86_3cdb_4653_a5b2_8f1aa3cbddac;

class RenaTreningsTheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "RenaTreningsTheme";
    }

    public function getThemeClasses() {
        return ["pagebox"];
    }    
    
    public function addScripts() {
        echo "<link href='https://fonts.googleapis.com/css?family=Roboto+Condensed:400,300,300italic' rel='stylesheet' type='text/css'>";
    }
    
    public function render() {
        
    }
}
?>

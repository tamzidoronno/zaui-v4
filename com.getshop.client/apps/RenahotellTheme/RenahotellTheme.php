<?php
namespace ns_7b4f9efd_f5a2_433f_8c48_10d297c974e7;

class RenahotellTheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "RenahotellTheme";
    }

    public function render() {
        
    }
    
    public function addScripts() {
        return "\n"."<link href='https://fonts.googleapis.com/css?family=Rufina' rel='stylesheet' type='text/css'>";
        return "\n"."<link href='https://fonts.googleapis.com/css?family=Sintony' rel='stylesheet' type='text/css'>";
    }
    
    public function getThemeClasses() {
        return ["frontpagebox"];
    }
}
?>

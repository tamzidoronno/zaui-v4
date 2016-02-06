<?php
namespace ns_8a48c4d7_09e5_40e2_bdb3_72f706729d27;

class ProMeisterTheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterTheme";
    }

    public function getThemeClasses() {
        return ["frontbox"];
    }
    
    public function render() {
        
    }
    
    public function addScripts() {
        echo "<link href='https://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>";
    }
    
    public function isAllowingSideBar() {
        return true;
    }
}
?>

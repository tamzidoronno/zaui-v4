<?php
namespace ns_4f89c95c_99dc_4ed7_9352_1e1c51f4630c;

class SedoxDatabankTheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxDatabankTheme";
    }

    public function render() {
        
    }
    
    public function isAllowingSideBar() {
        return true;
    }
    
    public function addScripts() {
        echo "\n" . "<link href='https://fonts.googleapis.com/css?family=Lato|Roboto' rel='stylesheet' type='text/css'>";
    }   
    
    public function getThemeClasses() {
        return ["banner_redbox"];
    }
    
}
?>

<?php
namespace ns_566fb905_a4a7_43bf_ad09_b9b6f15b8ead;

class SkyTecTheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SkyTecTheme";
    }

    public function render() {
        
    }
    
    public function addScripts() {
        return "\n"."<link href='https://fonts.googleapis.com/css?family=PT+Sans:400,700' rel='stylesheet' type='text/css'>";
        return "\n"."<link href='https://fonts.googleapis.com/css?family=Lato:700' rel='stylesheet' type='text/css'>";
    }
    
    public function isAllowingSideBar() {
        return true;
    }

}
?>

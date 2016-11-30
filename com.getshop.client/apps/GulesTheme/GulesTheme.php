<?php
namespace ns_ad2c142a_27f3_44e6_940e_f40bffc586dc;

class GulesTheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GulesTheme";
    }

    public function render() {
        
    }
    
    public function addScripts() {
        echo "<link href='https://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>";
    }

    public function getThemeClasses() {
        return ["box_white_background", "box_white_background_withtoppicture"];
    }

}
?>

<?php
namespace ns_310a5440_fd6f_4be8_b4a9_6eb1f47605c0;

class HolandTheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "HolandTheme";
    }
    
    public function addScripts() {
        $text = "";
        $text .= "\n"."<link href='https://fonts.googleapis.com/css?family=Open+Sans:200,300,400,400,600,700&subset=latin,latin-ext' rel='stylesheet' type='text/css'>";
        $text .= "\n"."<link href='https://fonts.googleapis.com/css?family=Raleway:200,300,400,400,600,700&subset=latin,latin-ext' rel='stylesheet' type='text/css'>";
        echo $text;
    }
    
    public function render() {
        
    }
}
?>

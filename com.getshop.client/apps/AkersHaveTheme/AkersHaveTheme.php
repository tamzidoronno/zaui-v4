<?php
namespace ns_c61304ad_a89c_46bf_8e84_18718eb40e97;

class AkersHaveTheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AkersHaveTheme";
    }


    public function addScripts() {
        $text = "";
        $text .= "\n"."<script src='//use.typekit.net/wqi1odn.js'></script>";
        $text .= "\n"."<script>try{Typekit.load();}catch(e){}</script>";
        echo $text;
    }
        
    
    public function render() {
        
    }
}
?>

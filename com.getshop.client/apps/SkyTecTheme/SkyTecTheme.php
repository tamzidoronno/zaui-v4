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
        $text = "";
        $text .= "\n"."<link href='https://fonts.googleapis.com/css?family=PT+Sans:400,700' rel='stylesheet' type='text/css'>";
        $text .= "\n"."<link href='https://fonts.googleapis.com/css?family=Lato:700' rel='stylesheet' type='text/css'>";
        return $text;
    }
    
    public function isAllowingSideBar() {
        return true;
    }

    
    public function getThemeClasses() {
        return ["people"];
    }
    
    
    public function getAvailableProductTemplates() {
        return ["ecommerce_product_template_1", "ecommerce_product_template_2"];
    }
    
}
?>

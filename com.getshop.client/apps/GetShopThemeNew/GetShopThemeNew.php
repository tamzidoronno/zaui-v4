<?php
namespace ns_ef98f548_09c5_4386_83e8_6ad8cda38817;

class GetShopThemeNew extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopThemeNew";
    }

    public function render() {
        
    }
    
    public function getThemeClasses() {
         return ["frontbox", "productdescriptionrow"];
    }

    
    public function addScripts() {
        echo "<link href='https://fonts.googleapis.com/css?family=Source+Sans+Pro' rel='stylesheet' type='text/css'>";
    }
    
    public function isAllowingSideBar() {
        return true;
    }
}
?>

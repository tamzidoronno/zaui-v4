<?php
namespace ns_e377a31e_b48e_4758_aa1a_cbdf5aa47cea;

class Verksgate12StavangerTheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "Verksgate12StavangerTheme";
    }

    public function render() {
       
    }
    
    public function addScripts() {
        echo '<link href="https://fonts.googleapis.com/css?family=Work+Sans" rel="stylesheet">';
    }
    
    public function getThemeClasses() {
        return ["frontbox", "frontbox_odd"];
    }

    public function isAllowingSideBar() {
        return true;
    }    
}
?>

<?php
namespace ns_b2ba3713_c655_4f48_b5ac_2822edb8a38e;

class OsloTheme extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }
    
    public function addScripts() {
        echo "\n" . "<link href='https://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>";
    }   
    
    public function getName() {
        return "OsloTheme";
    }

    public function includeExtraCss() {
        ?>
        <style>
            .gsarea[area="header"],.gsarea[area="footer"] { display: none; }
            .gsarea[area="body"] .gs_page_width { box-shadow: none; }
            body { 
                background-color: #fff;
            }
        </style>
        <?php
    }
    
    public function render() {
        
    }
}
?>

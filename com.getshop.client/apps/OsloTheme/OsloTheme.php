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
            .gs_page_width { max-width: 1024px; width: inherit; }
            @media only all and (min-width: 1000px) {
                .gs_page_width {
                    -webkit-box-shadow: -1px 0px 15px 0px rgba(0,0,0,0.82);
                    -moz-box-shadow: -1px 0px 15px 0px rgba(0,0,0,0.82);
                    box-shadow: -1px 0px 15px 0px rgba(0,0,0,0.82);
                }
            }
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

<?php
namespace ns_0c9deebe_7c99_4697_bc93_54628c74c157;

class AktivitetsHusetK1 extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AktivitetsHusetK1";
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
    public function addScripts() {
        echo "<link href='https://fonts.googleapis.com/css?family=Noto+Serif' rel='stylesheet'>";
    }
    public function isAllowingSideBar() {
        return true;
    }

    public function sideBarShouldBeInner() {
        return true;
    }
}
?>

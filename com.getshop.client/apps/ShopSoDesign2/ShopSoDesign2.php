<?php

namespace ns_f2fc7a50_266f_4638_949b_4ffdb228f6f0;

class ShopSoDesign2 extends \ThemeApplication implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;

    function __construct() {
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 80);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 300);
        
        //product manager small view iamge size.
        $this->setVariable("ns_8402f800_1e7e_43b5_b3f7_6c7cabbf8942", "product_small_height", 600);
        $this->setVariable("ns_8402f800_1e7e_43b5_b3f7_6c7cabbf8942", "product_small_width", 600);
        
    }

    public function getDescription() {
        return "ShopSoDesign2";
    }

    public function getAvailablePositions() {
        return "left";
    }

    public function getName() {
        return "ShopSoDesign2";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function addScripts() {
        echo "\n" . '<link id="google_webfont_1" rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Open+Sans:400,600">';
    }    

    public function getStarted() {
        
    }

    public function render() {
        $this->includefile("ShopSoDesign2");
    }

}

?>

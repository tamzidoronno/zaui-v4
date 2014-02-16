<?php

namespace ns_902cccc1_c315_4a93_8005_d080d414ecee;

/**
 * Description of LeftMenu
 *
 * @author boggi
 */
class Amandus extends \ThemeApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
        $this->api = $this->getApi();
    
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 45);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 195);
       
        //product manager small view iamge size.
        $this->setVariable("ns_8402f800_1e7e_43b5_b3f7_6c7cabbf8942", "product_small_height", 600);
        $this->setVariable("ns_8402f800_1e7e_43b5_b3f7_6c7cabbf8942", "product_small_width", 600);
    }

    
    public function getDescription() {
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "Amandus";
    } 
    
    public function addScripts() {
        echo "\n" . '<link id="google_webfont_1" rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Open+Sans:400,600">';
    }    

    public function postProcess() {
        
    }
    

    public function preProcess() {
        
    }
    
    public function render() {
    }
    
    public function addApplications() {
        
    }
}
?>

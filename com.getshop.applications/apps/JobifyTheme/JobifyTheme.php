<?php
namespace ns_3b414d4e_25e7_4552_ade1_e37f5b3210ad;

class JobifyTheme extends \ThemeApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
        
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 45);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 195);
       
        //product manager small view iamge size.
        $this->setVariable("ns_8402f800_1e7e_43b5_b3f7_6c7cabbf8942", "product_small_height", 600);
        $this->setVariable("ns_8402f800_1e7e_43b5_b3f7_6c7cabbf8942", "product_small_width", 600);
    }

    public function getDescription() {
        return "JobifyTheme";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "JobifyTheme";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("JobifyTheme");
    }
}
?>

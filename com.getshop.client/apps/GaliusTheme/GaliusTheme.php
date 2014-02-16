<?php

namespace ns_29fbf912_8d65_41f3_abbb_259354cf0322;

/**
 * Description of LeftMenu
 *
 * @author boggi
 */
class GaliusTheme extends \ThemeApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
      $this->api = $this->getApi();
        
        $this->setTotalWidth(998);
        $this->setWidthLeft(208);
        $this->setWidthRight(208);
        $this->setWidthMidle(567);
        $this->setHeaderHeight(70);
        $this->setFooterHeight(202);
    
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 45);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 195);
        
        $this->setCategoryColumnCount2Layout(4);
        $this->setCategoryColumnCount3Layout(3);        
    }

    public function getDescription() {
        return "GaliusTheme";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "GaliusTheme";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("helloworld");
    }
}
?>

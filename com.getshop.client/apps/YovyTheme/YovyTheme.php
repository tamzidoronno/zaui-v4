<?php

namespace ns_43d02b7f_cde1_4e10_8e8e_8a6ba17dfc9c;

/**
 * Description of LeftMenu
 *
 * @author boggi
 */
class YovyTheme extends \ThemeApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
        $this->api = $this->getApi();
        
        $this->setTotalWidth(1000);
        $this->setWidthLeft(208);
        $this->setWidthRight(208);
        $this->setWidthMidle(567);
        $this->setHeaderHeight(100);
        $this->setFooterHeight(202);
    
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 45);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 195);
        
        $this->setCategoryColumnCount2Layout(4);
        $this->setCategoryColumnCount3Layout(3);          
    }

    public function getDescription() {
        return "YovyTheme";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "YovyTheme";
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

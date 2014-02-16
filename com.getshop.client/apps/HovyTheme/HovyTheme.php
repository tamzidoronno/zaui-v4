<?php

namespace ns_f49636a8_b50b_4ec1_a569_d2a72e14f5cb;

/**
 * Description of LeftMenu
 *
 * @author boggi
 */
class HovyTheme extends \ThemeApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
        $this->api = $this->getApi();
        
        $this->setTotalWidth(1000);
        $this->setWidthLeft(208);
        $this->setWidthRight(208);
        $this->setWidthMidle(567);
        $this->setHeaderHeight(80);
        $this->setFooterHeight(202);
    
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 85);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 295);
        
        
        $this->setCategoryColumnCount2Layout(4);
        $this->setCategoryColumnCount3Layout(3);          
    }

    public function getDescription() {
        return "HovyTheme";
    }
    
    
    public function applicationAdded() {
        $store = $this->getFactory()->getStore();
        $store->configuration->colors->textColor = "000000";
        $store->configuration->colors->backgroundColor = "FFFFFF";
        $store->configuration->colors->buttonBackgroundColor = "BBBBBB";
        $store->configuration->colors->buttonTextColor = "000000";
        $store->configuration->colors->backgroundColor = "FFFFFF";
        $store->configuration->colors->baseColor = "FFFFFF";
        $store->configuration->hasSelectedDesign = true;
        
        $this->getApi()->getStoreManager()->saveStore($store->configuration);
    }
    
    

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "HovyTheme";
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

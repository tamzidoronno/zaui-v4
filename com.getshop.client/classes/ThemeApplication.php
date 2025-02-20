<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ThemeApplication
 *
 * @author ktonder
 */
class ThemeApplication extends PredefinedPagesConfig {
    private $LOGO_WIDTH = 900;
    private $LOGO_HEIGHT = 168;
    private $CATEGORY_IMAGE_WIDTH=140;
    private $CATEGORY_IMAGE_HEIGHT=140;
    private $CAT_COL_COUNT_3_COL_LAYOUT = 3;
    private $CAT_COL_COUNT_2_COL_LAYOUT = 4;

    private $variables = array();
    private $dimension;
    
    public function getName() {
        $className = explode('\\', get_class($this));
        return $className[1];
    }
    
    /**
     * @return Dimensions;
     */
    public function getDimension() {
        if (!isset($this->dimension)) {
            $this->dimension = new Dimensions();
        }
        
        return $this->dimension;
    }
    
    /**
     * Sets the total width of your theme. 
     * This width should be equal or greater then
     * col1 + col2 + col3 
     * @param type $totalWidth
     */
    public function setTotalWidth($width) {
        $this->getDimension()->totalWidth = $width;
    }
    
    public function setWidthLeft($width) {
        $this->getDimension()->widthLeft = $width;
    }
    
    public function setWidthRight($width) {
        $this->getDimension()->widthRight = $width;
    }
    
    public function setWidthMidle($width) {
        $this->getDimension()->widthMidle = $width;
    }
    
    public function setHeaderHeight($height) {
        $this->getDimension()->headerHeight = $height;
    }
    
    public function setFooterHeight($height) {
        $this->getDimension()->footerHeight = $height;
    }
 
    public function setCategoryImageWidth($CATEGORY_IMAGE_WIDTH) {
        $this->CATEGORY_IMAGE_WIDTH = $CATEGORY_IMAGE_WIDTH;
    }

    public function setCategoryImageHeight($CATEGORY_IMAGE_HEIGHT) {
        $this->CATEGORY_IMAGE_HEIGHT = $CATEGORY_IMAGE_HEIGHT;
    }

    public function setCategoryColumnCount3Layout($CAT_COL_COUNT_3_COL_LAYOUT) {
        $this->CAT_COL_COUNT_3_COL_LAYOUT = $CAT_COL_COUNT_3_COL_LAYOUT;
    }

    public function setCategoryColumnCount2Layout($CAT_COL_COUNT_2_COL_LAYOUT) {
        $this->CAT_COL_COUNT_2_COL_LAYOUT = $CAT_COL_COUNT_2_COL_LAYOUT;
    }
    
    public function setVariable($namespace, $variablename, $value) {
        
        $variables = $this->getVariableArray($namespace);
        $variables[$variablename] = $value;
        
        $this->variables[$namespace] = $variables;
    }
    
    public function getVariableArray($namespace) {
        if (!isset($this->variables[$namespace])) {
            $this->variables[$namespace] = array();
        }
        return $this->variables[$namespace];
    }
    
    public function getSiteBuilder() {
        return new \SiteBuilder();
    }
 
    public function getText($pageTag, $index, $pageTagGroup) {
        $welcome = '&nbsp;<div style="text-align: center;"><br><span style="font-size:22px;"><span style="font-size:24px;">'.$this->__w("Welcome to my page").'</span></span><br><br>&nbsp;</div>';
        
        if ($index == 0 && $pageTagGroup == "frontpage") {
            if ($pageTag == "frontpage_0" || $pageTag == "frontpage_2") {
                return $welcome;
            }

            if ($pageTag == "frontpage_1") {
                return '&nbsp;<div style="text-align: center;"><br><span style="font-size:22px;"><span style="font-size:24px;">'.$this->__w("My images speeks for itself").'</span></span><br><br>&nbsp;</div>';
            }
            
            return $welcome;
        }
        
        return null;
    }
    
    public function addBannerSlider($pageTag, $index, $pageTagGroup, $pageId, $area) {
        $appconf = $this->getApi()->getPageManager()->addApplicationToPage($pageId, "d612904c-8e44-4ec0-abf9-c03b62159ce4", $area);
        $this->getApi()->getBannerManager()->addImage($appconf->id, "4dfd5fb0-6c0f-4342-bbb2-a1a0ccd03dbd");
        $this->getApi()->getBannerManager()->addImage($appconf->id, "87a62af8-372c-4e94-8d94-9a8ab61af278");
    }
    
    public function getProductId($pageTag, $pageTagGroup, $index) {
        $products = $this->getApi()->getProductManager()->getLatestProducts(4);
        
        if(sizeof($products) > ($index+1)) {
            return $products[$index]->id;
        }
        
        return null;
    }
    
    public function sideBarShouldBeInner() {
        return false;
    }
 
    public function isAllowingSideBar() {
        return false;
    }
    
    public function getThemeClasses() {
        return [];
    }
    
    public function printArrowsOutSideOnCarousel() {
        return true;
    }
    
    public function isBodyFooterEnabled() {
        return false;
    }
    
    public function isMobileAndResponsiveDesignDisabled() {
        return false;
    }
}

?>

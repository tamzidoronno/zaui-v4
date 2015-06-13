<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of StyleSheet
 *
 * @author ktonder
 */
class StyleSheet {
    /* @var $factory Factory */

    var $factory = null;
    var $leftMenu = null;
    public $LOGO_WIDTH = 80;
    public $LOGO_HEIGHT = 80;
    public $CATEGORY_IMAGE_WIDTH = 80;
    public $CATEGORY_IMAGE_HEIGHT = 80;
    public $CATEGORY_PRODUCT_LIST_IMAGE_HEIGHT = 120;
    public $CATEGORY_PRODUCT_LIST_IMAGE_WIDTH = 120;
    public $CAT_COL_COUNT_1_COL_LAYOUT = 6;
    public $CAT_COL_COUNT_2_COL_LAYOUT = 5;
    public $CAT_COL_COUNT_3_COL_LAYOUT = 3;

    function StyleSheet() {
        $this->leftMenu = new LeftMenuSkinSettings();
        $this->factory = IocContainer::getFactorySingelton();
        $this->selectedThemeApplication = $this->factory->getApplicationPool()->getSelectedThemeApp();
        $this->initialize();
    }

    function setLeftMenuSkinSetting(LeftMenuSkinSettings $leftMenu) {
        $this->leftMenu = $leftMenu;
    }

    function getWidthTotal() {
        if (method_exists($this->selectedThemeApplication, "getDimension"))
            return $this->selectedThemeApplication->getDimension()->totalWidth;
    }

    function getWidthLeft() {
        if (method_exists($this->selectedThemeApplication, "getDimension"))
            return $this->selectedThemeApplication->getDimension()->widthLeft;
    }

    function getWidthMidle() {
        if (method_exists($this->selectedThemeApplication, "getDimension"))
            return $this->selectedThemeApplication->getDimension()->widthMidle;
    }

    function getWidthRight() {
        if (method_exists($this->selectedThemeApplication, "getDimension"))
            return $this->selectedThemeApplication->getDimension()->widthRight;
    }

    function getColors() {
        $colors = $this->factory->getStoreConfiguration()->colors;
        foreach ($colors as $index => $val) {
            $colors->{$index} = $val;
        }

        return $colors;
    }

    function getHeaderHeight() {
        if(method_exists($this->selectedThemeApplication, "getDimension"))
            return $this->selectedThemeApplication->getDimension()->headerHeight;
    }

    function getFooterHeight() {
        if(method_exists($this->selectedThemeApplication, "getDimension"))
            return $this->selectedThemeApplication->getDimension()->footerHeight;
    }

    private function initialize() {
        $this->setThemeVariablesToApplications();
    }

    function render($includeHeader = true) {
        if ($includeHeader) {
            header('Content-type: text/css;charset=utf-8');
            header("Cache-Control: must-revalidate");
        }

        include_once("skin/default/framework.php");
        include_once("skin/default/elements.php");
        include_once("skin/default/layout.php");
        include_once("skin/default/breadcrumb.php");

        $this->includeApplications();
    }

    public function setThemeVariablesToApplications() {
        $themeApp = $this->factory->getApplicationPool()->getSelectedThemeApp();

        foreach ($this->factory->getApplicationPool()->getAllAddedInstances() as $app) {
            $appId = $this->factory->convertUUIDtoString($app->getApplicationSettings()->id);
            if (method_exists($themeApp, "getVariableArray")) {
                $array = $themeApp->getVariableArray($appId);
                foreach (array_keys($array) as $variableName) {
                    $value = $array[$variableName];
                    $app->setSkinVariable($variableName, $value, "");
                }
            }
        }
        
        $themeApp;
    }

    function includeApplications() {
        $themeApp = $this->factory->getApplicationPool()->getSelectedThemeApp();

        $done = array();
        foreach ($this->factory->getApplicationPool()->getAllApplicationSettings() as $app) {
            if ($app->type != "ThemeApplication") {
                $appSettingsId = $app->id;
                if (in_array($appSettingsId, $done)) {
                    continue;
                }
                $this->doApp($app);
                $done[] = $appSettingsId;
            }
        }
        
        $this->doApp($themeApp->getApplicationSettings());
    }

    private function doApp($app) {
        $appId = $this->factory->convertUUIDtoString($app->id);
        $folder = "../app/" . $appId . "/skin/" . $app->appName . ".css";

        if (file_exists($folder)) {
            $cssFile = file_get_contents($folder);
            $cssFile = str_replace("{IMAGEFOLDER}", "/showApplicationImages.php?appNamespace=" . urlencode($appId) . "&image=skin/images/", $cssFile);
            $cssFile = str_replace("{FOOTERHEIGHT}", $this->getFooterHeight(), $cssFile);
            $cssFile = str_replace("{BASECOLOR}", $this->getColors()->baseColor, $cssFile);
            $cssFile = str_replace("{TEXTCOLOR}", $this->getColors()->textColor, $cssFile);
            $cssFile = str_replace("{WIDTHTOTAL}", $this->getWidthTotal(), $cssFile);
            echo $cssFile;
        }
    }

    /**
     * @return LeftMenuSkinSettings
     */
    public function getLeftMenuSkinSettings() {
        return $this->leftMenu;
    }

}

?>

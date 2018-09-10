<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ModulePage
 *
 * @author ktonder
 */
class ModulePage {

    /**
     *
     * @var ModulePageRow[]
     */
    private $rows = array();
    private $pageId = "";
    private $extraApps = array();
    private $extraAppInstances = array();
    private $leftMenu = null;

    function __construct($pageId) {
        $this->pageId = $pageId;
    }

    public function getTitle() {
        return "PMS";
    }

    public function getMenu() {
        return \ModulePageMenu::getTopMenuPms();
    }

    /**
     * 
     * @return ModulePageRow[]
     */
    public function getRows() {
        return $this->rows;
    }

    public function createRow() {
        $row = new ModulePageRow($this);
        $this->rows[] = $row;
        return $row;
    }

    public function getId() {
        return $this->pageId;
    }

    public function createApplicationInstances() {
        foreach ($this->rows as $row) {
            foreach ($row->getColumns() as $column) {
                $column->createAppInstance();
            }
        }

        foreach ($this->extraApps as $extraAppId) {
            $column = new ModulePageColumn($extraAppId, "", $this);
            $column->createAppInstance();

            $instance = $column->getAppInstance();
            if ($instance) {
                $this->extraAppInstances[] = $instance;
            }
        }
    }

    /**
     * 
     * @return ApplicationBase[]
     */
    public function getAllApplicationInstances() {
        $instances = array();
        foreach ($this->rows as $row) {
            foreach ($row->getColumns() as $column) {
                $intance = $column->getAppInstance();
                if ($intance)
                    $instances[] = $intance;
            }
        }

        return array_merge($instances, $this->extraAppInstances);
    }

    public function loadAppsCss() {
        foreach ($this->getAllApplicationInstances() as $instance) {
            $this->doApp($instance->getApplicationSettings()->id);
        }

        $this->doApp("a11ac190-4f9a-11e3-8f96-0800200c9a66");
    }

    private function doApp($appId) {
        $appId = "ns_" . str_replace("-", "_", $appId);
        $folder = "../app/" . $appId . "/skin/";

        if (!file_exists("cssfolder")) {
            mkdir("cssfolder");
            chmod("cssfolder", 777);
        }
        if (!file_exists("cssfolder/$appId")) {
            mkdir("cssfolder/$appId");
        }

        if (file_exists($folder)) {
            $files = scandir($folder);

            foreach ($files as $file) {
                $cssFile = "$folder$file";

                if (is_file($cssFile)) {
                    if (!strstr($cssFile, ".css")) {
                        continue;
                    }
                    $cssFile = file_get_contents($cssFile);
                    $cssFile = str_replace("{IMAGEFOLDER}", "/showApplicationImages.php?appNamespace=" . urlencode($appId) . "&image=skin/images/", $cssFile);
                    file_put_contents("cssfolder/$appId/$file", $cssFile);

                    echo '<link class=\'appstylesheet\' rel="stylesheet" type="text/css" media="all" href="' . "cssfolder/$appId/$file" . '">' . "\n";
                }
            }
        }
    }

    public function loadAppsJavascripts() {

        foreach ($this->getAllApplicationInstances() as $appInstance) {

            if ($appInstance) {
                if (method_exists($appInstance, "getJavaScriptVariables") && $appInstance->getJavaScriptVariables()) {
                    echo "<script>";
                    echo str_replace("\\", "_", get_class($appInstance)) . "_extravariables = " . json_encode($appInstance->getJavaScriptVariables());
                    echo "</script>";
                }

                if (method_exists($appInstance, "includeExtraJavascript")) {
                    $extraJavascript = $appInstance->includeExtraJavascript();
                    if (is_array($extraJavascript)) {
                        foreach ($extraJavascript as $extraJavascript) {
                            echo "<script async src='$extraJavascript'></script>";
                        }
                    }
                }
            }

            $namespace = "ns_" . str_replace("-", "_", $appInstance->getApplicationSettings()->id);
            $this->loadByNamespace($namespace);
        }
    }

    private function loadByNamespace($namespace) {
        $startupCount = 0;
        $allInOne = false;
        $javascriptFolder = "../app/$namespace/javascript";

        if (is_dir($javascriptFolder) && $handle = opendir($javascriptFolder)) {

            while (false !== ($entry = readdir($handle))) {
                if ($this->endsWith(strtolower($entry), ".js")) {
                    if ($allInOne) {
                        $fileContentAllInOne .= file_get_contents($javascriptFolder . "/" . $entry) . "\n";
                    } else {
                        $filecontent = file_get_contents($javascriptFolder . "/" . $entry);
                        $fileName = "javascripts/" . $namespace . "_" . $startupCount . "_" . $entry;
                        @file_put_contents($fileName, $filecontent);
                        echo '<script type="text/javascript" class="javascript_app_file" src="' . $fileName . '"></script>';
                        echo "<script>";
                        echo 'if (typeof(getshop) === "undefined") { getshop = {}; }';
                        echo 'if (typeof(getshop.gs_loaded_javascripts) === "undefined") { getshop.gs_loaded_javascripts = []; }';
                        echo ' getshop.gs_loaded_javascripts.push("' . $fileName . '");';
                        echo "</script>";
                    }
                }
            }
        }
    }

    private function endsWith($haystack, $needle) {
        $length = strlen($needle);
        if ($length == 0) {
            return true;
        }

        return (substr($haystack, -$length) === $needle);
    }

    public function getAppInstance($applicationInstanceId) {
        foreach ($this->getAllApplicationInstances() as $appInstance) {
            if ($appInstance->getApplicationSettings()->id == $applicationInstanceId) {
                return $appInstance;
            }
        }

        return null;
    }

    public function addExtraApplications($appId) {
        $this->extraApps[] = $appId;
    }

    public function getExtraApplication($appId) {
        foreach ($this->getAllApplicationInstances() as $extraApp) {
            if ($extraApp->applicationSettings->id == $appId) {
                return $extraApp;
            }
        }

        return null;
    }

    public function setModuleId() {
        \PageFactory::$moduleId = \PageFactory::getGetShopModule();
    }

    function getLeftMenu() {
        return $this->leftMenu;
    }

    function setLeftMenu($leftMenu) {
        $this->leftMenu = $leftMenu;
    }

    public function renderPage() {
        $topRow = true;
        echo "<div class='gs_page_area' gs_page_content_id='$this->pageId'>";
        $rowcount = 0;
        foreach ($this->getRows() as $row) {
            $topRowClass = $topRow && !$this->leftMenu ? 'module_toprow' : "";
            echo "<div class='gs_page_row $topRowClass'>";
            $cols = $row->getColumns();
            $colcount = 0;
            foreach ($cols as $column) {
                $instance = $column->getAppInstance();
                $colclass = "col" . count($cols);
                echo "<div class='gs_page_col $colclass colcount_$colcount rowcount_$rowcount'>";
                if (!$instance && $column->text) {
                    echo $column->text;
                } else {
                    $instance->renderApp();
                }
                echo "</div>";
                $colcount++;
            }
            echo "</div>";
            $topRow = false;
            $rowcount++;
        }
        echo "</div>";
    }
}

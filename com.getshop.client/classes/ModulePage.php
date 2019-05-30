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
    private $extraAppNoneInstances = array();
    private $leftMenu = null;
    private $module = "pms";

    function __construct($pageId, $module = "pms") {
        $this->pageId = $pageId;
        $this->module = $module; 
   }

    public function getTitle() {
        return "PMS";
    }

    public function getMenu($module=false) {
        if ($this->module == "pms") {
            return $this->getTopMenuPms();
        }
        if ($this->module == "comfort") {
            return $this->getTopMenuComfort();
        }
        
        if ($this->module == "srs") {
            return $this->getTopMenuSrs();
        }
        if ($this->module == "getshopsupport") {
            return $this->getTopMenuGetShopSupport();
        }
        if ($this->module == "intranet") {
            return $this->getTopMenuIntranet();
        }
        
        if ($this->module == "salespoint") {
            return $this->getTopMenuSalesPoint();
        }
        
        if ($this->module == "apac") {
            return $this->getTopMenuApac();
        }
        
        if ($this->module == "invoicing") {
            return $this->getTopMenuInvoicing();
        }
        
        if ($this->module == "pmsconference") {
            return $this->getTopMenuConference();
        }
        
        if ($this->module == "settings") {
            return $this->getSettingsMenu();
        }
        
        if ($this->module == "getshop") {
            return $this->getGetShopMenu();
        }
        
    }

    /**
     * 
     * @return ModulePageRow[]
     */
    public function getRows() {
        return $this->rows;
    }

    public function createRow($ignoreTopRow=false) {
        $row = new ModulePageRow($this);
        $row->ignoreTopRow = $ignoreTopRow;
        $this->rows[] = $row;
        return $row;
    }

    public function getId() {
        return $this->pageId;
    }

    public function createApplicationInstances() {
        foreach ($this->rows as $row) {
            foreach ($row->getColumns() as $column) {
                $column->createAppInstance($this->module);
            }
        }

        foreach ($this->extraApps as $extraAppId) {
            $column = new ModulePageColumn($extraAppId, "", $this);
            $column->createAppInstance($this->module);

            $instance = $column->getAppInstance();
            if ($instance) {
                $this->extraAppInstances[] = $instance;
            } else {
                $this->extraAppNoneInstances[] = $extraAppId;
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
        
        $activatedPaymentApps = IocContainer::getFactorySingelton()->getApi()->getStoreApplicationPool()->getPaymentApplicationsIds();
        $activatedPaymentApps[] = "486009b1_3748_4ab6_aa1b_95a4d5e2d228";
        
        foreach($activatedPaymentApps as $appId) {
           $this->addExtraApplicationsNoneInstance($appId); 
        }
        
        foreach ($this->extraAppNoneInstances as $id) {
            $this->doApp($id);
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

                    echo '<link class=\'appstylesheet\' rel="stylesheet" type="text/css" media="all" href="' . "cssfolder/$appId/$file?". calculateCacheName() . '">' . "\n";
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
        
        foreach ($this->extraAppNoneInstances as $id) {
            $namespace = "ns_" . str_replace("-", "_", $id);
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
                        echo '<script type="text/javascript" class="javascript_app_file" src="' . $fileName . '?'. calculateCacheName().'"></script>';
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

    public function addExtraApplications($instanceId) {
        if (!in_array($instanceId, $this->extraApps)) {
            $this->extraApps[] = $instanceId;
        }
    }
    
    public function addExtraApplicationsNoneInstance($instanceId) {
        if (!in_array($instanceId, $this->extraAppNoneInstances)) {
            $this->extraAppNoneInstances[] = $instanceId;
        }
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

    public function renderPage($modal=false) {
        $topRow = true;
        echo "<div class='gs_page_area' gs_page_content_id='$this->pageId'>";
        $rowcount = 0;
        foreach ($this->getRows() as $row) {
            $topRowClass = $topRow && !$row->ignoreTopRow && !$this->leftMenu ? 'module_toprow' : "";
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

    public function getTopMenuSrs() {
        $menu = new \ModulePageMenu("srs");
        $menu->entries[] = new ModulePageMenuItem("Dashboard", "home", "gsicon-gs-dashboard");
        $menu->entries[] = new ModulePageMenuItem("Reservations", "3a0bc113-d800-4658-a68e-a0086973eb80", "gsicon-dinner");
        $menu->entries[] = new ModulePageMenuItem("Menu", "94c0e942-a7f5-4bbb-89f0-598c0e080ec1", "gsicon-list");
        $menu->entries[] = new ModulePageMenuItem("Bar", "9c87fd8c-e44a-467a-a65b-1734f974a553", "gsicon-glass-cocktail");
        $menu->entries[] = new ModulePageMenuItem("Settings", "3234b76a-f960-4d51-96ac-959a36ca31f8", "gsicon-gs-gears");
        return $menu;
    }

    public function getTopMenuGetShopSupport() {
        $menu = new \ModulePageMenu("support");
        $menu->entries[] = new ModulePageMenuItem("Dashboard", "home", "gsicon-gs-dashboard");
        $menu->entries[] = new ModulePageMenuItem("Dev center", "getshopdevcenter", "gsicon-list");
        $menu->entries[] = new ModulePageMenuItem("Manuals", "getshopusermanual", "fa-support");
        return $menu;
    }

    public function getTopMenuPms() {
        $menu = new \ModulePageMenu("pms");
        $menu->entries[] = new ModulePageMenuItem("Dashboard", "home", "gsicon-gs-dashboard");
        $menu->entries[] = new ModulePageMenuItem("Bookings", "a90a9031-b67d-4d98-b034-f8c201a8f496", "gsicon-gs-booking");
        $menu->entries[] = new ModulePageMenuItem("New", "048e2e10-1be3-4d77-a235-4b47e3ebfaab", "gsicon-gs-new");
        $menu->entries[] = new ModulePageMenuItem("Availability", "0da68de9-da08-4b60-9652-3ac456da2627", "gsicon-gs-availability");
        $menu->entries[] = new ModulePageMenuItem("Reports", "afe687b7-219e-4396-9e7b-2848f5ed034d", "gsicon-gs-reports");
        $menu->entries[] = new ModulePageMenuItem("Prices", "394bb905-8448-45c1-8910-e9a60f8aebc5", "gsicon-gs-prices");
        $menu->entries[] = new ModulePageMenuItem("Cleaning", "e03b19de-d1bf-4d1c-ac40-8c100ef53366", "gsicon-gs-cleaning");
        $menu->entries[] = new ModulePageMenuItem("CRM", "4f66aad0-08a0-466c-9b4c-71337c1e00b7", "gsicon-users");
        $menu->entries[] = new ModulePageMenuItem("Checklist", "checklist", "gsicon-list");
        $menu->entries[] = new ModulePageMenuItem("Settings", "messages", "gsicon-gs-gears");
        $menu->entries[] = new ModulePageMenuItem("Support", "getshopsupport", "fa-support");
        return $menu;
    }

    public function getTopMenuComfort() {
        $menu = new \ModulePageMenu("comfort");
        $menu->entries[] = new ModulePageMenuItem("Dashboard", "home", "gsicon-gs-dashboard");
        $menu->entries[] = new ModulePageMenuItem("Gateways", "gateways", "fa-server");
        $menu->entries[] = new ModulePageMenuItem("Configuration", "configuration", "gsicon-gs-gears");
        return $menu;
    }

    public function getTopMenuSalesPoint() {
        $menu = new \ModulePageMenu("salespoint");
        $menu->entries[] = new ModulePageMenuItem("Dashboard", "home", "gsicon-gs-dashboard");
        $menu->entries[] = new ModulePageMenuItem("New Sale", "new", "gsicon-gs-new");
        $menu->entries[] = new ModulePageMenuItem("Products", "products", "gsicon-basket");
        $menu->entries[] = new ModulePageMenuItem("Reports", "reports", "fa fa-line-chart");
        $menu->entries[] = new ModulePageMenuItem("Gift Cards", "giftcards", "fa fa-gift");
        $menu->entries[] = new ModulePageMenuItem("Invoicing", "invoicing", "gsicon-gs-reports");
        $menu->entries[] = new ModulePageMenuItem("Settings", "settings", "gsicon-gs-gears");
        return $menu;
    }

    public function getTopMenuIntranet() {
        $menu = new \ModulePageMenu("intranet");
        $menu->entries[] = new ModulePageMenuItem("Dashboard", "home", "gsicon-gs-dashboard");
        $menu->entries[] = new ModulePageMenuItem("Dev center", "getshopdevcenter", "gsicon-list");
        $menu->entries[] = new ModulePageMenuItem("Servers", "serverstatus", "fa-server");
        $menu->entries[] = new ModulePageMenuItem("Manuals", "getshopusermanual", "fa-support");
        return $menu;
    }

    public function getTopMenuApac() {
        $menu = new \ModulePageMenu("apac");
        $menu->entries[] = new ModulePageMenuItem("Access List", "home", "gsicon-gs-user");
        $menu->entries[] = new ModulePageMenuItem("New Access", "newaccess", "gsicon-gs-new");
        $menu->entries[] = new ModulePageMenuItem("Doors", "doors", "fa fa-history");
        $menu->entries[] = new ModulePageMenuItem("Configuration", "configuration", "gsicon-gs-gears");
        return $menu; 
    }

    public function getTopMenuInvoicing() {
        $menu = new \ModulePageMenu("invoicing");
        $menu->entries[] = new ModulePageMenuItem("Dashboard", "home", "gsicon-gs-dashboard");
        $menu->entries[] = new ModulePageMenuItem("Overdue", "overduelist", "gsicon-alarm-error");
        $menu->entries[] = new ModulePageMenuItem("All", "allinvoices", "gsicon-receipt");
        $menu->entries[] = new ModulePageMenuItem("Incomes", "incomes", "fa-dollar");
        $menu->entries[] = new ModulePageMenuItem("Products", "products", "gsicon-basket");
        return $menu;
    }

    public function getSettingsMenu() {
        $menu = new \ModulePageMenu("settings");
        $menu->entries[] = new ModulePageMenuItem("Dashboard", "home", "gsicon-gs-dashboard");
        $menu->entries[] = new ModulePageMenuItem("Departments", "departments", "gsicon-site-map");
        $menu->entries[] = new ModulePageMenuItem("Other", "othersettings", "gsicon-gs-gears");
        return $menu;
    }

    public function getGetShopMenu() {
        $menu = new \ModulePageMenu("settings");
        $menu->entries[] = new ModulePageMenuItem("Dashboard", "home", "gsicon-gs-dashboard");
        $menu->entries[] = new ModulePageMenuItem("Customers", "customers", "gsicon-factory");
        $menu->entries[] = new ModulePageMenuItem("Inbox", "inbox", "fa-inbox");
        $menu->entries[] = new ModulePageMenuItem("Suppliers", "suppliers", "fa-truck");
        $menu->entries[] = new ModulePageMenuItem("IotDevices", "iotdevices", "fa-server");
        $menu->entries[] = new ModulePageMenuItem("Inventory", "inventory", "gsicon-calculator");
        $menu->entries[] = new ModulePageMenuItem("Support", "inventory", "fa-support");
        $menu->entries[] = new ModulePageMenuItem("Invoicing", "invoicing", "fa-dollar");
        $menu->entries[] = new ModulePageMenuItem("Other", "othersettings", "gsicon-gs-gears");
        return $menu;
    }

    public function renderBottom() {
        $this->getMenu()->renderNumPad();
    }

    public function getTopMenuConference() {
        $menu = new \ModulePageMenu("pmsconference");
        $menu->entries[] = new ModulePageMenuItem("Conference", "home", "gsicon-gs-dashboard");
        $menu->entries[] = new ModulePageMenuItem("Event report", "eventreport", "fa-pie-chart");
        $menu->entries[] = new ModulePageMenuItem("Report", "report", "fa-line-chart");
        return $menu;
    }

}
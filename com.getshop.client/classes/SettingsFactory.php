<?php

class SettingsFactory extends FactoryBase {
    private $currentApplication = null;

    public function initApp() {
        if (isset($_POST['appid'])) {
            $this->setApp($_POST['appid']);
        }

        if ($this->currentApplication == null) {
            $this->currentApplication = new ns_b81bfb16_8066_4bea_a3c6_c155fa7119f8\DashBoard();
        }
    }

    private function setApp($id) {
        $settings = $this->getApi()->getStoreApplicationPool()->getApplication($id);
        $appName = "ns_" . str_replace("-", "_", $settings->id) . "\\" . $settings->appName;
        $this->currentApplication = new $appName();
        $this->currentApplication->setApplicationSettings($settings);
    }

    public function renderContent() {
        
    }

    public function includefile($filename, $overrideappname = NULL, $printError = true) {
        
    }
    
    public function renderTopMenu() {
        include '../template/default/Common/settingstopmenu.phtml';
    }

    public function json() {
        $data = [];
        ob_start();
        $this->initApp();
        if (isset($_POST['gss_method']) && $_POST['gss_method'] != "gs_show_fragment") {
            $this->currentApplication->{$_POST['gss_method']}();
        }

        if (!isset($_POST['gss_method']) || !isset($_POST['gss_fragment'])) {
            $appNameWithNamespace = get_class($this->currentApplication);
            $appname = substr(strstr($appNameWithNamespace, "\\"), 1);
            echo "<div class='app' app='$appname'>";
            $this->currentApplication->renderConfig();
            echo "</div>";
        }

        if (isset($_POST['gss_fragment'])) {
            $this->currentApplication->gs_show_fragment();
        }
        $html = ob_get_contents();
        
        ob_end_clean();
        
        ob_start();
        $this->renderTopMenu();
        $topMenu = ob_get_contents();
        ob_end_clean();
        $data['data'] = $html;
        $data['topMenu'] = $topMenu;
        
        return json_encode($data);
    }

    public function printExtraUserApps() {
        $factory = IocContainer::getFactorySingelton();
        $certegoapp = $factory->getApplicationPool()->getApplicationSetting("27a320a3-e983-4f55-aae8-cf94add661c2");
        if ($certegoapp) {
            echo "<div class='gss_submenu_entry' gss_goto_app='27a320a3-e983-4f55-aae8-cf94add661c2'><i class='fa fa-tint'></i> Systems</div>";
        }
    }
}

?>
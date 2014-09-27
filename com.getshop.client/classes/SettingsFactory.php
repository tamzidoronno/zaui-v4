<?php

class SettingsFactory extends FactoryBase {
	
	private $currentApplication = null;
	
	public function initApp() {
		if (isset($_POST['appid'])) {
			$this->setApp($_POST['appid']);
			$_SESSION['gss_settings_current_app_name'] = $_POST['appid'];
		}
		
		if ($this->currentApplication == null && isset($_SESSION['gss_settings_current_app_name'])) {
			$this->setApp($_SESSION['gss_settings_current_app_name']);
		}
		
		if ($this->currentApplication == null) {
			$this->currentApplication = new ns_b81bfb16_8066_4bea_a3c6_c155fa7119f8\DashBoard();
		}
	}
	
	private function setApp($id) {
		$settings = $this->getApi()->getAppManager()->getApplication($id);
		$appName = "ns_".str_replace("-", "_", $settings->id)."\\".$settings->appName;
		$this->currentApplication = new $appName();
		$this->currentApplication->setApplicationSettings($settings);
	}
    
    public function renderContent() {
		$this->initApp();
        $this->currentApplication->renderConfig();
    }
    
    public function includefile($filename, $overrideappname = NULL, $printError = true) {
       
    }
	
	public function json() {
		$data = [];
		ob_start();
        $this->renderContent();
        $html = ob_get_contents();
        ob_end_clean();
		$data['data'] = $html;
        return json_encode($data);
	}
}

?>
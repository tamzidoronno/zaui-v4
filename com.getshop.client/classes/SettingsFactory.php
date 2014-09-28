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
		$settings = $this->getApi()->getAppManager()->getApplication($id);
		$appName = "ns_".str_replace("-", "_", $settings->id)."\\".$settings->appName;
		$this->currentApplication = new $appName();
		$this->currentApplication->setApplicationSettings($settings);
	}
    
    public function renderContent() {
		
    }
    
    public function includefile($filename, $overrideappname = NULL, $printError = true) {
       
    }
	
	public function json() {
		$data = [];
		ob_start();
        $this->initApp();
		if (isset($_POST['gss_method'])) {
			$this->currentApplication->{$_POST['gss_method']}();
		}
        $this->currentApplication->renderConfig();
        $html = ob_get_contents();
        ob_end_clean();
		$data['data'] = $html;
        return json_encode($data);
	}
}

?>
<?php
namespace ns_d755efca_9e02_4e88_92c2_37a3413f3f41;

class Settings extends \SystemApplication implements \Application {
    public $singleton = true;
    private $application;
    
    public function getDescription() {}
    
    public function getName() {
        return "Store settings";
    }
    
    public function postProcess() {}
    public function preProcess() {}
    
    public function getApplications() {
        $apps = $this->getFactory()->getApplicationPool()->getAllApplicationSettings();
        
        $retApps = array();
        $i = 1;
        foreach ($apps as $appSetting) {
            $appInstace = $this->getFactory()->getApplicationPool()->createInstace($appSetting);
            
            if(!method_exists($appInstace, "renderConfig")) {
                continue;
            }
            
            $addedApps = $this->getFactory()->getApi()->getPageManager()->getApplicationsBasedOnApplicationSettingsId($appSetting->id);
            if (!is_array($addedApps)) {
                continue;
            }
            
            foreach ($addedApps as $addedApp) {
                if ($appSetting->id == "d755efca-9e02-4e88-92c2-37a3413f3f41") {
                    $retApps[0] = $this->getFactory()->getApplicationPool()->createAppInstance($addedApp);
                } else {
                    $retApps[$i] = $this->getFactory()->getApplicationPool()->createAppInstance($addedApp);
                    $i++;    
                }
            }
        }
        
        $retApps[] = $this->createAppDisplayer();
        ksort($retApps);
        return $retApps;
    }
    
    private function createAppDisplayer() {
        $displayer = new \ns_c841f5a5_ecd5_4007_b9da_2c7538c07212\ApplicationDisplayer();
        $displayer->configuration = $this->getApiObject()->core_storemanager_data_StoreConfiguration();
        $displayer->configuration->id = "aisdf29-asdf712-asdf23451-asdf-asdfasfd-asdf23-54-das-12";
        $displayer->configuration->settings = "";
        $displayer->setName($this->__f("More..."));
        return $displayer;
    }
    
    private function getApplication($appId) {
        $apps = $this->getApplications();
        foreach ($apps as $app) {
            if ($app->configuration->id == $appId) {
                return $app;
            }
        }
        
        return null;
    }
    
    public function render() {
        if (!$this->isEditorMode()) {
            echo "Access denied";
            return;
        }
        $this->includefile("leftmenu");
        
        if (!isset($_GET['applicationId'])) {
            foreach($this->getApplications() as $key => $app) {
                if($app instanceof Settings) {
                    $_GET['applicationId'] = $app->configuration->id;
                }
            }
        }
       
        $appId = $_GET['applicationId'];
        $this->application = $this->getApplication($appId);
        
        if ($this->application) {
            $name = $this->application->getName();
            $appSettings = $this->application->getApplicationSettings();
            $appName = isset($appSettings) ? $appSettings->appName : "";
            echo "<div id='settingsarea' app='$appName' class='inline $appName' appsettingsid='$appId'>";
            if (get_class($this->application) != "ns_d755efca_9e02_4e88_92c2_37a3413f3f41\Settings" 
                    && get_class($this->application) != "ns_c841f5a5_ecd5_4007_b9da_2c7538c07212\ApplicationDisplayer") {
                echo "<div appid='$appId' style='float: right;' class='systembutton removesingletonapplication'><ins>Remove $name application</ins></div>";
            }
            $this->application->renderConfig();

            if (isset($this->application->getConfiguration()->settings)) {
                echo "<script>";
                echo "currentSettings = ".json_encode($this->application->getConfiguration()->settings).";";
                echo "thundashop.app.Settings.init();";
                echo "</script>";
            }
            echo "</div>";
        }
    }
    
    public function SendExtendedModeRequest() {
        $from = "system@getshop.com";
        $fromName = "GetShop System Message";
        $title = "Extended mode request";
        $to = "post@getshop.com";
        $toName = "GetShop support";
        
        $content = "Email: " . \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->emailAddress . "<br>";
        $content .= "Storeid: " . \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->storeId . "<br>";
        $content .= "Store address: ".$this->getFactory()->getStore()->webAddress . "<br>";
        $content .= "Store address primary: ".$this->getFactory()->getStore()->webAddressPrimary . "<br>";
        $sendMail->content = $content;
        
        $this->getApi()->getMessageManager()->sendMail($to, $toName, $title, $content, $from, $fromName);
        echo "Email has been sent!";
    }
    
    public function SaveSettings() {
        $data = $_POST['data'];
        
        $settings = array();
        foreach (array_keys($data) as $key) {
            if ($key == "appid") continue;
            $setting =  $this->getApiObject()->core_common_Setting();
            $setting->id = $key;
            if (isset($data[$key]['type']) && $data[$key]['type'] == "table") {
                $setting->value = json_encode($data[$key]['value']);
            } else {
                if(is_array($data[$key]['value'])) {
                    $setting->value = json_encode($data[$key]['value']);
                } else {
                    $setting->value = $data[$key]['value'];
                }
            }
            $setting->type = isset($data[$key]['type']) ? $data[$key]['type'] : "";
            $setting->secure = $data[$key]['secure'];
            $settings[] = $setting;
        }
        
        if ($data['appid'] == $this->getConfiguration()->id) {
            $email = $data['mainemailaddress']['value'];
            if ($email) {
                $store = $this->getFactory()->getStoreConfiguration();
                $store->emailAdress = $email;
                $this->getApi()->getStoreManager()->saveStore($store);
            }
        }
        
        $sendCore = $this->getApiObject()->core_common_Settings();
        $sendCore->settings = $settings; 
        $sendCore->appId = $data['appid'];
        print_r($sendCore);
        $this->getApi()->getPageManager()->setApplicationSettings($sendCore);
    }
    
    public function renderConfig() {
        $this->includefile("storesettings");
    }
    
    public function getMainEmailAddress() {
        if(isset($this->getFactory()->getStoreConfiguration()->emailAdress)) {
            return $this->getFactory()->getStoreConfiguration()->emailAdress;
        }
        return "";    
    }
    
    public function isAvailable() {
        return false;
    }
}

?>
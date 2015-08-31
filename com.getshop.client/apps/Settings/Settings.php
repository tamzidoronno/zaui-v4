<?php

namespace ns_d755efca_9e02_4e88_92c2_37a3413f3f41;

class Settings extends \SystemApplication implements \Application {

    public function getDescription() {
        
    }

    public function getName() {
        return "Store settings";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function render() {
        
    }
    
    public function addLanguage() {
        
        $languages = $this->getConfigurationSetting("languages");
        if (!$languages) {
            $languages = [];
        } else {
            $languages = json_decode($languages);
        }
        $languages[] = $_POST['value'];
        $languages = json_encode($languages);
        $this->setConfigurationSetting("languages", $languages);
    }

    public function renderDashBoardWidget() {
        $this->includefile("dashboardwidget");
    }

    public function renderConfig() {
        $this->includefile("storesettings");
    }
    
    public function removeLanguage() {
        $languages = $this->getConfigurationSetting("languages");
        $saveLanagues =  [];
        if ($languages) {
            $languages = json_decode($languages);
            foreach ($languages as $lang) {
                if ($lang != $_POST['value']) {
                    $saveLanagues[] = $lang;
                }
            }
        }
        
        $languages = json_encode($saveLanagues);
        $this->setConfigurationSetting("languages", $languages);
    }

    public function getMainMobilePhone() {
        if (isset($this->getFactory()->getStoreConfiguration()->phoneNumber)) {
            return $this->getFactory()->getStoreConfiguration()->phoneNumber;
        }
        return "";
    }
    
    public function getMainEmailAddress() {
        if (isset($this->getFactory()->getStoreConfiguration()->emailAdress)) {
            return $this->getFactory()->getStoreConfiguration()->emailAdress;
        }
        return "";
    }

    public function isAvailable() {
        return false;
    }
    
    public function getTitle() {
        return $this->getConfigurationSetting("title");
    }
    
    public function getUrl() {
        return $this->getApi()->getStoreManager()->getMyStore()->webAddressPrimary;
    }

    public function saveStoreSettings() {
        echo "Setting : " .  $_POST['uniqueusersonemail'];
        $storeSettings = $this->getFactory()->getStoreConfiguration();
        $storeSettings->emailAdress = $_POST['emailaddress'];
        $storeSettings->phoneNumber = $_POST['phoneNumber'];
        $this->getApi()->getStoreManager()->saveStore($storeSettings);
        
        $this->setConfigurationSetting("language", $_POST['language']);
        $this->setConfigurationSetting("title", $_POST['title']);
        $this->setConfigurationSetting("uniqueusersonemail", $_POST['uniqueusersonemail']);
        $this->setConfigurationSetting("doubleauthentication", $_POST['doubleauthentication']);
        $this->setConfigurationSetting("singlegrouptouser", $_POST['singlegrouptouser']);
        $this->getApi()->getStoreManager()->setPrimaryDomainName($_POST['url']);
        $this->setConfigurationSetting("currencycode", "NOK");
    }

    public function deleteStore() {
        $this->getApi()->getStoreManager()->delete();
    }

}

?>
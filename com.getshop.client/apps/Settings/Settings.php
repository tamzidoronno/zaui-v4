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
    
    public function isDisableMobileMode() {
        if (isset($this->getFactory()->getStoreConfiguration()->disableMobileMode)) {
            return $this->getFactory()->getStoreConfiguration()->disableMobileMode;
        }
        return "";
    }
    
    public function getMainEmailAddress() {
        if (isset($this->getFactory()->getStoreConfiguration()->emailAdress)) {
            return $this->getFactory()->getStoreConfiguration()->emailAdress;
        }
        return "";
    }
    
    public function getPaymentMethod() {
        if (isset($this->getFactory()->getStoreConfiguration()->paymentMethod)) {
            return $this->getFactory()->getStoreConfiguration()->paymentMethod;
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
        $storeSettings = $this->getFactory()->getStoreConfiguration();
        $storeSettings->emailAdress = $_POST['emailaddress'];
        $storeSettings->phoneNumber = $_POST['phoneNumber'];
        $storeSettings->paymentMethod = $_POST['paymentMethod'];
        $storeSettings->disableMobileMode = $_POST['disableMobileMode'];
        $storeSettings->defaultPrefix = $_POST['defaultPrefix'];
        
        $this->getApi()->getStoreManager()->saveStore($storeSettings);
        
        $this->setConfigurationSetting("language", $_POST['language']);
        $this->setConfigurationSetting("title", $_POST['title']);
        $this->setConfigurationSetting("disableEditorBackendAccess", $_POST['disableEditorBackendAccess']);
        $this->setConfigurationSetting("uniqueusersonemail", $_POST['uniqueusersonemail']);
        $this->setConfigurationSetting("uniqueusersoncellphone", $_POST['uniqueusersoncellphone']);
        $this->setConfigurationSetting("doubleauthentication", $_POST['doubleauthentication']);
        $this->setConfigurationSetting("singlegrouptouser", $_POST['singlegrouptouser']);
        $this->getApi()->getStoreManager()->setPrimaryDomainName($_POST['url']);
        $this->setConfigurationSetting("currencycode", "NOK");
        $this->setConfigurationSetting("autonavigatetocart", $_POST['autonavigatetocart']);
        $this->setConfigurationSetting("seo",  $_POST['seo']);
        
        $this->getApi()->getStoreManager()->setStoreIdentifier($_POST['identifier']);
    }

    public function deleteStore() {
        $this->getApi()->getStoreManager()->delete();
    }

    public function getDefaultPrefix() {
        if (isset($this->getFactory()->getStoreConfiguration()->defaultPrefix)) {
            return $this->getFactory()->getStoreConfiguration()->defaultPrefix;
        }
        
        return "";
    }

    public function getIdentifier() {
        return $this->getApi()->getStoreManager()->getMyStore()->identifier;
    }

    public function isAccessToBackedForEditorDisabled() {
        return $this->getConfigurationSetting("disableEditorBackendAccess");
    }

}

?>
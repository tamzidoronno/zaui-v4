<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_9de54ce1_f7a0_4729_b128_b062dc70dcce; 

        
class ECommerceSettings extends \ApplicationBase implements \Application {
    static $currencyCode = false;
    
    private $storeSettingsInstance;
    
    public function getDescription() {
        
    }

    public function getName() {
        
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("ecommercesetting");
    }
    
    private function getStoreSettingsApp() {
        if (!$this->storeSettingsInstance) {
            $app = $this->getApi()->getStoreApplicationPool()->getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
            $this->storeSettingsInstance = $this->getFactory()->getApplicationPool()->createInstace($app);
        }
        
        return $this->storeSettingsInstance;
    }
    
    public static function fetchCurrencyCode() {
        if (!ECommerceSettings::$currencyCode) {
            $factory = \IocContainer::getFactorySingelton();
            $settingsApp = new ECommerceSettings();
            ECommerceSettings::$currencyCode = $settingsApp->getStoreSettingsApp()->getConfigurationSetting("currencycode");
        }
        return ECommerceSettings::$currencyCode;
    }
    
    public function getCurrencyCode() {
        return ECommerceSettings::fetchCurrencyCode();
    }

    public function save() {
        $this->getStoreSettingsApp()->setConfigurationSetting("currencycode", $_POST['currency']);
    }
    
    public function isCurrency($currency) {
        $selected = $this->getCurrencyCode() == $currency ? "selected='true'" : "";
        echo $selected;
    }
    
    public static function formatPrice($price) {
        $code = ECommerceSettings::fetchCurrencyCode();
        if ($code == "NOK") {
            return "Kr ".number_format((float)$price, 2, '.', '').",-";
        }
        
        echo "Price $".$price;
    }
}
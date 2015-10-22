<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_9de54ce1_f7a0_4729_b128_b062dc70dcce; 

        
class ECommerceSettings extends \ApplicationBase implements \Application {
    static $currencyCode = false;
    static $defaultPayment = false;
    
    private $storeSettingsInstance;
    
    public function getDescription() {
        return $this->__w("Tune your online store by setting currency, does customers need to register to buy from your store, show cookie warning, etc");
    }

    public function getName() {
        return $this->__w("Ecommerce settings");
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("ecommercesetting");
    }
    
    public function getStoreSettingsApp() {
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
    
    public static function fecthCurrencyCodeTranslated() {
        $code = ECommerceSettings::fetchCurrencyCode();
        if($code == "USD") {
            return "$";
        }
        if($code == "CAD") {
            return "$";
        }
        if($code == "AUD") {
            return "$";
        }
        if($code == "NOK") {
            return "kr";
        }
    }
    
    public static function fetchDefaultPaymentAppWhenCustomerIdIsSet() {
        if (!ECommerceSettings::$defaultPayment) {
            $factory = \IocContainer::getFactorySingelton();
            $settingsApp = new ECommerceSettings();
            ECommerceSettings::$defaultPayment = $settingsApp->getStoreSettingsApp()->getConfigurationSetting("defaultpaymentwhencartcustomeridisset");
        }
        return ECommerceSettings::$defaultPayment;
    }
    
    public function getCurrencyCode() {
        return ECommerceSettings::fetchCurrencyCode();
    }

    public function save() {
        $this->getStoreSettingsApp()->setConfigurationSetting("currencycode", $_POST['currency']);
        $this->getStoreSettingsApp()->setConfigurationSetting("defaultpaymentwhencartcustomeridisset", $_POST['defaultpaymentwhencartcustomeridisset']);
        
    }
    
    public function isCurrency($currency) {
        $selected = $this->getCurrencyCode() == $currency ? "selected='true'" : "";
        echo $selected;
    }
    
    public static function formatPrice($price, $numberOfDecimals = 2) {
        $code = ECommerceSettings::fetchCurrencyCode();
        if ($code == "NOK") {
            return "Kr ".number_format((float)$price, $numberOfDecimals, '.', '').",-";
        }
        
        echo "Price $".$price;
    }
}
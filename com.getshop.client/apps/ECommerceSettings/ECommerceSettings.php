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
    
    /**
     * @var ECommerceSettings
     */
    static $staticEcommerceApp = false;
    
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
    
    public static function getNumberOfDecimals() {
        ECommerceSettings::setApplicationInstance();
        $decimals = ECommerceSettings::$staticEcommerceApp->getConfigurationSetting("numberOfDecimals");
        if ($decimals == "")
            return 2;
        
        return $decimals;
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
        $this->getStoreSettingsApp()->setConfigurationSetting("registrationRequired", $_POST['registrationRequired']);
        $this->getStoreSettingsApp()->setConfigurationSetting("defaultKidMethod", $_POST['defaultKidMethod']);
        $this->getStoreSettingsApp()->setConfigurationSetting("kidSize", $_POST['kidSize']);
        $this->setConfigurationSetting("defaultPaymentMethod", $_POST['defaultPaymentMethod']);
        $this->setConfigurationSetting("numberOfDecimals", $_POST['numberOfDecimals']);
        
        $selected = array();
        foreach($_POST as $key => $val) {
            if(stristr($key, "canbechosen_payment_app_")) {
                if($val == "true") {
                    $selected[] = str_replace("canbechosen_payment_app_", "", $key);
                }
            }
        }
        echo join(",", $selected);
        $this->setConfigurationSetting("paymentapps_available_for_choosing", join(",", $selected));
        
    }
    
    public function isCurrency($currency) {
        $selected = $this->getCurrencyCode() == $currency ? "selected='true'" : "";
        echo $selected;
    }
    
    public static function formatPrice($price, $numberOfDecimals = 2, $printZero = false) {
        $code = ECommerceSettings::fetchCurrencyCode();
        
        $numberOfDecimals = ECommerceSettings::getNumberOfDecimals();
        
        if($price == 0 && !$printZero) {
            return "&nbsp";
        }
        if ($code == "NOK") {
            return "Kr ".number_format((float)$price, $numberOfDecimals, '.', '').",-";
        }
        
        if ($code == "EUR") {
            return "€ ".number_format((float)$price, $numberOfDecimals, '.', '').",-";
        }
        if ($code == "SEK") {
            return "kr ".number_format((float)$price, $numberOfDecimals, '.', '').",-";
        }
        
        if ($code == "GBP") {
            return "£ ".number_format((float)$price, $numberOfDecimals, '.', '').",-";
        }
        
        $price = round($price, 2);
        return "Price $".$price;
    }

    public static function setApplicationInstance() {
        if (!ECommerceSettings::$staticEcommerceApp) {
            $factory = \IocContainer::getFactorySingelton();
            $app = $factory->getApi()->getStoreApplicationPool()->getApplication("9de54ce1-f7a0-4729-b128-b062dc70dcce");
            ECommerceSettings::$staticEcommerceApp = $factory->getApplicationPool()->createInstace($app);
        }
    }

    public static function fetchStockQuantity() {
        
    }

    public static function fetchPaymethodsToChooseFrom() {
        
        ECommerceSettings::setApplicationInstance();
        $toChooseFrom = ECommerceSettings::$staticEcommerceApp->getConfigurationSetting("paymentapps_available_for_choosing");
        
        if($toChooseFrom) {
            $toChooseFrom = explode(",", $toChooseFrom);
        } else {
            $toChooseFrom = array();
        }
        return $toChooseFrom;
    }

}
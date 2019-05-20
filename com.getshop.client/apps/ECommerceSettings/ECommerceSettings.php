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
    
    public static function getCurrencies() {
        $currencies = array();
        $currencies["AED"] = "United Arab Emirates dirham";
        $currencies["AFN"] = "Afghan afghani";
        $currencies["ALL"] = "Albanian lek";
        $currencies["AMD"] = "Armenian dram";
        $currencies["ANG"] = "Netherlands Antillean guilder";
        $currencies["AOA"] = "Angolan kwanza";
        $currencies["ARS"] = "Argentine peso";
        $currencies["AUD"] = "Australian dollar";
        $currencies["AWG"] = "Aruban florin";
        $currencies["AZN"] = "Azerbaijani manat";
        $currencies["BAM"] = "Bosnia and Herzegovina convertible mark";
        $currencies["BBD"] = "Barbados dollar";
        $currencies["BDT"] = "Bangladeshi taka";
        $currencies["BGN"] = "Bulgarian lev";
        $currencies["BHD"] = "Bahraini dinar";
        $currencies["BIF"] = "Burundian franc";
        $currencies["BMD"] = "Bermudian dollar";
        $currencies["BND"] = "Brunei dollar";
        $currencies["BOB"] = "Boliviano";
        $currencies["BOV"] = "Bolivian Mvdol";
        $currencies["BRL"] = "Brazilian real";
        $currencies["BSD"] = "Bahamian dollar";
        $currencies["BTN"] = "Bhutanese ngultrum";
        $currencies["BWP"] = "Botswana pula";
        $currencies["BYR"] = "Belarusian ruble";
        $currencies["BZD"] = "Belize dollar";
        $currencies["CAD"] = "Canadian dollar";
        $currencies["CDF"] = "Congolese franc";
        $currencies["CHE"] = "WIR Euro (complementary currency)";
        $currencies["CHF"] = "Swiss franc";
        $currencies["CHW"] = "WIR Franc (complementary currency)";
        $currencies["CLF"] = "Unidad de Fomento (funds code)";
        $currencies["CLP"] = "Chilean peso";
        $currencies["CNY"] = "Chinese yuan";
        $currencies["COP"] = "Colombian peso";
        $currencies["COU"] = "Unidad de Valor Real";
        $currencies["CRC"] = "Costa Rican colon";
        $currencies["CUC"] = "Cuban convertible peso";
        $currencies["CUP"] = "Cuban peso";
        $currencies["CVE"] = "Cape Verde escudo";
        $currencies["CZK"] = "Czech koruna";
        $currencies["DJF"] = "Djiboutian franc";
        $currencies["DKK"] = "Danish krone";
        $currencies["DOP"] = "Dominican peso";
        $currencies["DZD"] = "Algerian dinar";
        $currencies["EGP"] = "Egyptian pound";
        $currencies["ERN"] = "Eritrean nakfa";
        $currencies["ETB"] = "Ethiopian birr";
        $currencies["EUR"] = "Euro";
        $currencies["FJD"] = "Fiji dollar";
        $currencies["FKP"] = "Falkland Islands pound";
        $currencies["GBP"] = "Pound sterling";
        $currencies["GEL"] = "Georgian lari";
        $currencies["GHS"] = "Ghanaian cedi";
        $currencies["GIP"] = "Gibraltar pound";
        $currencies["GMD"] = "Gambian dalasi";
        $currencies["GNF"] = "Guinean franc";
        $currencies["GTQ"] = "Guatemalan quetzal";
        $currencies["GYD"] = "Guyanese dollar";
        $currencies["HKD"] = "Hong Kong dollar";
        $currencies["HNL"] = "Honduran lempira";
        $currencies["HRK"] = "Croatian kuna";
        $currencies["HTG"] = "Haitian gourde";
        $currencies["HUF"] = "Hungarian forint";
        $currencies["IDR"] = "Indonesian rupiah";
        $currencies["ILS"] = "Israeli new sheqel";
        $currencies["INR"] = "Indian rupee";
        $currencies["IQD"] = "Iraqi dinar";
        $currencies["IRR"] = "Iranian rial";
        $currencies["ISK"] = "Icelandic króna";
        $currencies["JMD"] = "Jamaican dollar";
        $currencies["JOD"] = "Jordanian dinar";
        $currencies["JPY"] = "Japanese yen";
        $currencies["KES"] = "Kenyan shilling";
        $currencies["KGS"] = "Kyrgyzstani som";
        $currencies["KHR"] = "Cambodian riel";
        $currencies["KMF"] = "Comoro franc";
        $currencies["KPW"] = "North Korean won";
        $currencies["KRW"] = "South Korean won";
        $currencies["KWD"] = "Kuwaiti dinar";
        $currencies["KYD"] = "Cayman Islands dollar";
        $currencies["KZT"] = "Kazakhstani tenge";
        $currencies["LAK"] = "Lao kip";
        $currencies["LBP"] = "Lebanese pound";
        $currencies["LKR"] = "Sri Lankan rupee";
        $currencies["LRD"] = "Liberian dollar";
        $currencies["LSL"] = "Lesotho loti";
        $currencies["LTL"] = "Lithuanian litas";
        $currencies["LVL"] = "Latvian lats";
        $currencies["LYD"] = "Libyan dinar";
        $currencies["MAD"] = "Moroccan dirham";
        $currencies["MDL"] = "Moldovan leu";
        $currencies["MGA"] = "Malagasy ariary";
        $currencies["MKD"] = "Macedonian denar";
        $currencies["MMK"] = "Myanma kyat";
        $currencies["MNT"] = "Mongolian tugrik";
        $currencies["MOP"] = "Macanese pataca";
        $currencies["MRO"] = "Mauritanian ouguiya";
        $currencies["MUR"] = "Mauritian rupee";
        $currencies["MVR"] = "Maldivian rufiyaa";
        $currencies["MWK"] = "Malawian kwacha";
        $currencies["MXN"] = "Mexican peso";
        $currencies["MXV"] = "Mexican Unidad de Inversion";
        $currencies["MYR"] = "Malaysian ringgit";
        $currencies["MZN"] = "Mozambican metical";
        $currencies["NAD"] = "Namibian dollar";
        $currencies["NGN"] = "Nigerian naira";
        $currencies["NIO"] = "Nicaraguan córdoba";
        $currencies["NOK"] = "Norwegian krone";
        $currencies["NPR"] = "Nepalese rupee";
        $currencies["NZD"] = "New Zealand dollar";
        $currencies["OMR"] = "Omani rial";
        $currencies["PAB"] = "Panamanian balboa";
        $currencies["PEN"] = "Peruvian nuevo sol";
        $currencies["PGK"] = "Papua New Guinean kina";
        $currencies["PHP"] = "Philippine peso";
        $currencies["PKR"] = "Pakistani rupee";
        $currencies["PLN"] = "Polish złoty";
        $currencies["PYG"] = "Paraguayan guaraní";
        $currencies["QAR"] = "Qatari rial";
        $currencies["RON"] = "Romanian new leu";
        $currencies["RSD"] = "Serbian dinar";
        $currencies["RUB"] = "Russian rouble";
        $currencies["RWF"] = "Rwandan franc";
        $currencies["SAR"] = "Saudi riyal";
        $currencies["SBD"] = "Solomon Islands dollar";
        $currencies["SCR"] = "Seychelles rupee";
        $currencies["SDG"] = "Sudanese pound";
        $currencies["SEK"] = "Swedish krona/kronor";
        $currencies["SGD"] = "Singapore dollar";
        $currencies["SHP"] = "Saint Helena pound";
        $currencies["SLL"] = "Sierra Leonean leone";
        $currencies["SOS"] = "Somali shilling";
        $currencies["SRD"] = "Surinamese dollar";
        $currencies["SSP"] = "South Sudanese pound";
        $currencies["STD"] = "São Tomé and Príncipe dobra";
        $currencies["SYP"] = "Syrian pound";
        $currencies["SZL"] = "Swazi lilangeni";
        $currencies["THB"] = "Thai baht";
        $currencies["TJS"] = "Tajikistani somoni";
        $currencies["TMT"] = "Turkmenistani manat";
        $currencies["TND"] = "Tunisian dinar";
        $currencies["TOP"] = "Tongan paʻanga";
        $currencies["TRY"] = "Turkish lira";
        $currencies["TTD"] = "Trinidad and Tobago dollar";
        $currencies["TWD"] = "New Taiwan dollar";
        $currencies["TZS"] = "Tanzanian shilling";
        $currencies["UAH"] = "Ukrainian hryvnia";
        $currencies["UGX"] = "Ugandan shilling";
        $currencies["USD"] = "United States dollar";
        $currencies["UYI"] = "Uruguay Peso (URUIURUI)";
        $currencies["UYU"] = "Uruguayan peso";
        $currencies["UZS"] = "Uzbekistan som";
        $currencies["VEF"] = "Venezuelan bolívar fuerte";
        $currencies["VND"] = "Vietnamese đồng";
        $currencies["VUV"] = "Vanuatu vatu";
        $currencies["WST"] = "Samoan tala";
        $currencies["XAF"] = "CFA franc BEAC";
        $currencies["XCD"] = "East Caribbean dollar";
        $currencies["XDR"] = "Special drawing rights";
        $currencies["XFU"] = "UIC franc (special settlement currency)";
        $currencies["XOF"] = "CFA Franc BCEAO";
        $currencies["XPF"] = "CFP franc";
        $currencies["YER"] = "Yemeni rial";
        $currencies["ZAR"] = "South African rand";
        $currencies["ZMK"] = "Zambian kwacha";
        $currencies["ZWL"] = "Zimbabwe dollar";
        return $currencies;
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
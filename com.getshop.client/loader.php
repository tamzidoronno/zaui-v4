<?php
class AppRegister {
    static $register = array( 
        'Deletedapp');
    
    static function getPaymentApplications() {
        foreach (AppRegister::$register as $app) {
            $app = new $app();
            if ($app instanceof PaymentApplication && $app->isAvailable()) {
                $apps[] = $app;
            }
        }
        
        return $apps;
    }

    public static function getShippingApplications() {
        foreach (AppRegister::$register as $app) {
            $app = new $app();
            if ($app instanceof ShipmentApplication)
                $apps[] = $app;
        }
        
        return $apps;
    }
    
    public static function getWebshopApplications() {
        $apps = array();
        foreach (AppRegister::$register as $app) {
            $app = new $app();
//            if (Login::getUserObject() != null && Login::getUserObject()->emailAddress == "post@getshop.com") {
//                $apps[] = $app;
//                continue;
//            }
            if ($app instanceof WebshopApplication)
                $apps[] = $app;
        }
        return $apps;
    }

    public static function getMarketingApplications() {
        $apps = array();
        foreach (AppRegister::$register as $app) {
            $app = new $app();
            if ($app instanceof MarketingApplication)
                $apps[] = $app;
        }
        return $apps;
    }
    
    public static function getReportingApplications() {
        $apps = array();
        foreach (AppRegister::$register as $app) {
            $app = new $app();
            if ($app instanceof ReportingApplication)
                $apps[] = $app;
        }
        return $apps;
    }

    public static function getApplicationsForArea($areaType) {
        $apps = array();
        foreach (AppRegister::$register as $app) {
            $app = new $app();
            $availablePos = $app->getAvailablePositions();
            if ($app instanceof \SystemApplication) {
                continue;
            }
                
            if (strstr($availablePos, $areaType) === false) {
                continue;
            }
            $apps[] = $app;
        }
        return $apps;
    }
}

include_once("events/API.php");
include_once("events/API2.php");
include_once("minifier.php");

function symLinkIfNeeded($class_name) {
    if (!is_writeable("../app")) { 
        die("Please make sure that the app folder is writeable for the web server user");
    }
    
    if (strstr($class_name, "ns_")) {
        $classArray = explode("/", $class_name);
        $namespace = $classArray[0];
        $appName = $classArray[1];
        $namespacedfolder = "../app/$namespace";
        
        if (!file_exists($namespacedfolder) && !file_exists("../apps/".$appName)) {
            $factory = IocContainer::getFactorySingelton(false);
            $appId = substr($namespace, 3);
            $appId = str_replace("_", "-", $appId);
            $appSettings = $factory->getApi()->getGetShopApplicationPool()->get($appId);
            
            if (!$appSettings) {
                $themeApp = $factory->getApi()->getStoreApplicationPool()->getThemeApplication();
                if ($themeApp->id == $appId) {
                    $appSettings = $themeApp;
                }
            }
            
            if ($appSettings) {
                $appName = $appSettings->appName;
            } else {
                $appName = "";
            }
        }
        
        if (!is_link($namespacedfolder) && file_exists("../apps/".$appName) && $appName) {
            @symlink("../apps/$appName", $namespacedfolder);
        }
    }
}

function __autoload($class_name) {
    $class_name = str_replace("\\", "/", $class_name);
    
    symLinkIfNeeded($class_name);
    
    $apipath = str_replace("_", "/", $class_name);
    if (file_exists("../events/".  $apipath.".php")) {
        include_once "../events/".$apipath.".php";
    }
    
    if (file_exists("../".  str_replace("_", "/", $class_name).".php")) {    
        include_once "../".  str_replace("_", "/", $class_name).".php";
    }
    
    if (file_exists("../classes/$class_name.php")) {
        include_once "../classes/$class_name.php";
    }
    
    if (file_exists("../helpers/$class_name.php")) {
        include_once "../helpers/$class_name.php";
    }
    
    if (file_exists("../classes/builders/$class_name.php")) {
        include_once "../classes/builders/$class_name.php";
    }
    
    if (file_exists("../app/$class_name.php")) {
        include_once "../app/$class_name.php";
    }
    if (file_exists("../classes/api/$class_name.php")) {
        include_once "../classes/api/$class_name.php";
    }
    
    foreach (AppRegister::$register as $app) {
        symLinkIfNeeded($class_name);
        
        $app = strtolower($app);
        
        if (file_exists("../app/$app/classes/$class_name.php")) {
            include_once "../app/$app/classes/$class_name.php";
        }
        if (file_exists("../app/$app/$class_name.php")) {
            include_once "../app/$app/$class_name.php";
        }
        
        if (file_exists("../app/$app/classes/data/$class_name.php")) {
            include_once "../app/$app/classes/data/$class_name.php";
        }
        
        if (file_exists("../app/$app/classes/event/$class_name.php")) {
            include_once "../app/$app/classes/event/$class_name.php";
        }
    }
    
    if (file_exists("../classes/helpers/$class_name.php")) {
        include_once "../classes/helpers/$class_name.php";
    }
    
}

?>

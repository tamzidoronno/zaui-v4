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


function __autoload($class_name) {
    $class_name = str_replace("\\", "/", $class_name);
    
    $apipath = str_replace("_", "/", $class_name);
    if (file_exists("../events/".  $apipath.".php")) {
        include "../events/".$apipath.".php";
    }
    
    if (file_exists("../".  str_replace("_", "/", $class_name).".php")) {    
        include "../".  str_replace("_", "/", $class_name).".php";
    }
    
    if (file_exists("../classes/$class_name.php")) {
        include "../classes/$class_name.php";
    }
    
    if (file_exists("../helpers/$class_name.php")) {
        include "../helpers/$class_name.php";
    }
    
    if (file_exists("../app/$class_name.php")) {
        include "../app/$class_name.php";
    }
    if (file_exists("../classes/api/$class_name.php")) {
        include "../classes/api/$class_name.php";
    }
    
    foreach (AppRegister::$register as $app) {
        $app = strtolower($app);
        if (file_exists("../app/$app/classes/$class_name.php")) {
            include "../app/$app/classes/$class_name.php";
        }
        if (file_exists("../app/$app/$class_name.php")) {
            include "../app/$app/$class_name.php";
        }
        
        if (file_exists("../app/$app/classes/data/$class_name.php")) {
            include "../app/$app/classes/data/$class_name.php";
        }
        
        if (file_exists("../app/$app/classes/event/$class_name.php")) {
            include "../app/$app/classes/event/$class_name.php";
        }
    }
    
    if (file_exists("../classes/helpers/$class_name.php")) {
        include "../classes/helpers/$class_name.php";
    }
    
}

?>

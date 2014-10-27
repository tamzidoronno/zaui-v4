<?php

function endsWith($haystack, $needle)
{
    $length = strlen($needle);
    if ($length == 0) {
        return true;
    }

    return (substr($haystack, -$length) === $needle);
}

$factory = IocContainer::getFactorySingelton(false);
$apps = $factory->getApplicationPool()->getAllApplicationSettings();
//if (!$factory->isEditorMode()) {
//    $apps = $factory->getApi()->getAppManager()->getApplicationSettingsUsedByWebPage();
//    $apps[] = $factory->getApplicationPool()->getApplicationByName("ns_bf35979f_6965_4fec_9cc4_c42afd3efdd7\MainMenu");
//    $apps[] = $factory->getApplicationPool()->getApplicationByName("ns_7093535d_f842_4746_9256_beff0860dbdf\BreadCrumb");
////    $apps[] = $factory->getApplicationPool()->getApplicationByName("ns_b741283d_920d_460b_8c08_fad5ef4294cb\ProductWidget");
//    
//    // Login app
//    $appSettings = new core_applicationmanager_ApplicationSettings();
//    $appSettings->id = "df435931-9364-4b6a-b4b2-951c90cc0d70";
//    $apps[] = $appSettings;
//}

foreach ($apps as $app) {
    $appInstance = $factory->getApplicationPool()->createInstace($app);
    if($appInstance) {
        if (method_exists($appInstance, "includeExtraJavascript")) {
            $extraJavascript = $appInstance->includeExtraJavascript();
            if (is_array($extraJavascript)) {
                foreach ($extraJavascript as $extraJavascript) {
                    echo "<script src='$extraJavascript'></script>";
                }
            }
        }
    }
    
    $namespace = $this->convertUUIDtoString($app->id);
    $javascriptFolder = "../app/$namespace/javascript";

    if (is_dir($javascriptFolder) && $handle = opendir($javascriptFolder)) {
        echo "<script>";
        
        while (false !== ($entry = readdir($handle))) {
            if (endsWith(strtolower($entry), ".js"))
              echo file_get_contents($javascriptFolder."/".$entry);
        }
        echo "</script>";
    }
    
}
?>
 
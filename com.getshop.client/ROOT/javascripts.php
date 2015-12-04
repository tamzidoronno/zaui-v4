<?php

include_once '../loader.php';

function endsWith($haystack, $needle) {
    $length = strlen($needle);
    if ($length == 0) {
        return true;
    }

    return (substr($haystack, -$length) === $needle);
}

$factory = IocContainer::getFactorySingelton(false);
$apps = $factory->getApplicationPool()->getAllApplicationSettings();
if (!$factory->isEditorMode()) {
    $apps = $factory->getApi()->getStoreApplicationPool()->getApplications();
    // Login app
    $appSettings = new core_appmanager_data_Application();
    $appSettings->id = "df435931-9364-4b6a-b4b2-951c90cc0d70";
    $apps[] = $appSettings;
}

$theme = $factory->getApplicationPool()->getSelectedThemeApp();
if ($theme) {    
    $apps[] = $theme;
}

$startupCount = $factory->getApi()->getUtilManager()->getStartupCount();
$allInOne = $factory->getApi()->getUtilManager()->isInProductionMode();
$fileContentAllInOne = "";

foreach ($apps as $app) {
    $appInstance = $factory->getApplicationPool()->createInstace($app);
    if ($appInstance) {
        if (method_exists($appInstance, "includeExtraJavascript")) {
            $extraJavascript = $appInstance->includeExtraJavascript();
            if (is_array($extraJavascript)) {
                foreach ($extraJavascript as $extraJavascript) {
                    echo "<script async src='$extraJavascript'></script>";
                }
            }
        }
    }

    $namespace = $factory->convertUUIDtoString($app->id);
    $javascriptFolder = "../app/$namespace/javascript";


    if (is_dir($javascriptFolder) && $handle = opendir($javascriptFolder)) {

        while (false !== ($entry = readdir($handle))) {
            if (endsWith(strtolower($entry), ".js")) {
                if ($allInOne) {
                    $fileContentAllInOne .= file_get_contents($javascriptFolder . "/" . $entry) . "\n";
                } else {
                    $filecontent = file_get_contents($javascriptFolder . "/" . $entry);
                    $fileName = "javascripts/" . $namespace . "_" .$startupCount ."_" . $entry;
                    @file_put_contents($fileName, $filecontent);
                    echo '<script type="text/javascript" class="javascript_app_file" src="' . $fileName . '"></script>';
                    echo "<script>";
                        echo 'if (typeof(getshop) === "undefined") { getshop = {}; }';
                        echo 'if (typeof(getshop.gs_loaded_javascripts) === "undefined") { getshop.gs_loaded_javascripts = []; }';
                        echo ' getshop.gs_loaded_javascripts.push("'.$fileName.'");';
                    echo "</script>";
                }
            }
        }
    }
}

if ($allInOne) {
    $fileName = "javascripts/".$factory->getStore()->id."_get_shop_all_$startupCount.js";
    $fileContentAllInOne = $factory->minify($fileContentAllInOne);
    file_put_contents($fileName, $fileContentAllInOne);
    echo '<script '. $factory->includeSeo() .' type="text/javascript" class="javascript_app_file" src="' . $fileName . '"></script>';
}
?>
 
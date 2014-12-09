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
    $appSettings = new core_applicationmanager_ApplicationSettings();
    $appSettings->id = "df435931-9364-4b6a-b4b2-951c90cc0d70";
    $apps[] = $appSettings;
}

foreach ($apps as $app) {
    $appInstance = $factory->getApplicationPool()->createInstace($app);
    if ($appInstance) {
        if (method_exists($appInstance, "includeExtraJavascript")) {
            $extraJavascript = $appInstance->includeExtraJavascript();
            if (is_array($extraJavascript)) {
                foreach ($extraJavascript as $extraJavascript) {
                    echo "<script src='$extraJavascript'></script>";
                }
            }
        }
    }

    $namespace = $factory->convertUUIDtoString($app->id);
    $javascriptFolder = "../app/$namespace/javascript";


    if (is_dir($javascriptFolder) && $handle = opendir($javascriptFolder)) {
        
        while (false !== ($entry = readdir($handle))) {
            if (endsWith(strtolower($entry), ".js")) {
                $filecontent = file_get_contents($javascriptFolder . "/" . $entry);
                $fileName = "javascripts/".$namespace."_".$entry;
                file_put_contents($fileName, $filecontent);
                echo '<script type="text/javascript" class="javascript_app_file" src="'.$fileName.'"></script>';
            }
        }
        
    }
}
?>
 
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
 
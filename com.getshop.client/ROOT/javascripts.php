<?php

function endsWith($haystack, $needle)
{
    $length = strlen($needle);
    if ($length == 0) {
        return true;
    }

    return (substr($haystack, -$length) === $needle);
}

$apps = IocContainer::getFactorySingelton(false)->getApplicationPool()->getAllApplicationSettings();
foreach ($apps as $app) {
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
 
<?php

/**
 * Description of ApplicationHelper
 *
 * @author ktonder
 */
class ApplicationHelper {
    public static function getApplicationImage($app) {
        $pool = IocContainer::getFactorySingelton(false)->getApplicationPool();
        
        if (method_exists($app,"getConfiguration")) {
            $id = $app->getConfiguration()->appSettingsId;
            $appName = $app->getConfiguration()->appName;
        } else {
            $id = $app->id;
            $appName = $app->appName;
        }
        
        $namespace = $pool->getNameSpace($id);
        
        return "/showApplicationImages.php?appNamespace=".$namespace."&image=$appName".".png";
    }
    
    public static function getApplicationColors($app) {
        $namespace= "ns_".str_replace("-", "_", $app->id);
        $colors = array();
        $backgrounds = array();
        
        if(file_exists("../app/$namespace/skin/colors.css")) {
            $content = explode("\n", file_get_contents("../app/$namespace/skin/colors.css"));
            foreach($content as $line) {
                if($line && stristr($line, "/* color:")) {
                    $color = explode(":", $line);
                    $colors[] = str_replace(" */", "", $color);
                }
            }
        }
        
        return $colors;
    }

    public static function printColorStyle($theme, $color) {
        $namespace= "ns_".str_replace("-", "_", $theme->id);
        $colors = array();
        $backgrounds = array();
        
        if(file_exists("../app/$namespace/skin/colors.css")) {
            $content = explode("\n", file_get_contents("../app/$namespace/skin/colors.css"));
            $found = false;
            foreach($content as $line) {
                if($found) {
                    echo $line;
                }
                
                if(stristr($line, "/* color:") && $found) {
                    break;
                }
                
                if(stristr($line, "/* color:$color")) {
                    $found = true;
                }
            }
        }
    }

    public static function checkForBackground($theme) {
        $namespace= "ns_".str_replace("-", "_", $theme->id);
        if(file_exists("../app/$namespace/skin/colors.css")) {
            $content = explode("\n", file_get_contents("../app/$namespace/skin/colors.css"));
            foreach($content as $line) {
                if(stristr($line, "/* nobg */")) {
                    return false;
                }
            }
        }
        return true;
    }
}

?>

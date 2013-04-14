<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
}

?>

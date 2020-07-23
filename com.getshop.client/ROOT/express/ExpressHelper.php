<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ExpressHelper
 *
 * @author boggi
 */
class ExpressHelper {
  public function createAppInstance($applicationId) {
        $this->applicationId = $applicationId;
        $this->instanceId = "ssss2123";
        $this->page = null;
        
        $cachedApp = $this->getCachedApp();
        
        if ($cachedApp) {
            $this->appInstance = $cachedApp;
            if ($this->appInstance) {
                return;
            }
        }
        
        $api = IocContainer::getFactorySingelton()->getApi();
        $applicationSetting = $api->getStoreApplicationPool()->getApplication($this->applicationId);
        
        $javaInstance = null;
        
        if ($this->instanceId) {
            $javaInstance = $api->getStoreApplicationInstancePool()->getApplicationInstanceWithModule($this->instanceId, "express");
        }
        
        if (!$applicationSetting) {
            return;
        }
        
        $instance = "ns_" . str_replace("-", "_", $this->applicationId) . "\\" . $applicationSetting->appName;
        if (class_exists($instance)) {
            $appInstance = new $instance();
            $appInstance->setApplicationSettings($applicationSetting);
            $appInstance->setConfiguration($javaInstance);
            $appInstance->page = $this->page;
            $this->appInstance = $appInstance;
            file_put_contents('/tmp/app_cached_'.$this->applicationId."_".$this->instanceId, serialize($appInstance));
        } else {
            echo "not exists: $instance :" . $this->instanceId . " : " . $this->applicationId;
        }
    }
    
    public function getCachedApp() {
        if (!file_exists('/tmp/app_cached_'.$this->applicationId."_".$this->instanceId)) {
            return false;
        }
        
        $ser = file_get_contents('/tmp/app_cached_'.$this->applicationId."_".$this->instanceId);
        return unserialize($ser);
    }

    public function getAppInstance() {
        return $this->appInstance;
    }

}

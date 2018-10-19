<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ModulePageColumn
 *
 * @author ktonder
 */
class ModulePageColumn {
    private $applicationId;
    private $instanceId;
    private $appInstance = null;
    private $page;
    public $text;
    
    function __construct($applicationId, $instanceId, $page) {
        $this->applicationId = $applicationId;
        $this->instanceId = $instanceId;
        $this->page = $page;
    }
    
    function getApplicationId() {
        return $this->applicationId;
    }

    function getInstanceId() {
        return $this->instanceId;
    }

    
    public function createAppInstance($moduleName="pms") {
        if (!$this->applicationId)
            return;
            
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
            $javaInstance = $api->getStoreApplicationInstancePool()->getApplicationInstanceWithModule($this->instanceId, $moduleName);
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
    
    public function getAppInstance() {
        return $this->appInstance;
    }

    public function getCachedApp() {
        if (!file_exists('/tmp/app_cached_'.$this->applicationId."_".$this->instanceId)) {
            return false;
        }
        
        $ser = file_get_contents('/tmp/app_cached_'.$this->applicationId."_".$this->instanceId);
        return unserialize($ser);
    }

}
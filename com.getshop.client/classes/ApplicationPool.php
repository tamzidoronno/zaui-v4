<?php

/**
 * Description of ApplicationPool
 *
 * @author ktonder
 */
class ApplicationPool {

    /* @var core_common_AppConfiguration */
    private $addedApplicationInstances = array();

    /** @var Factory */
    private $factory;
    
    /** @var core_applicationmanager_ApplicationSettings[] */
    private $applicationList;

    function __construct($factory) {
        $this->factory = $factory;
        $this->applicationList = $factory->getApi()->getStoreApplicationPool()->getApplications();
    }
    
    
    public function getShipmentApplicationInstances() {
        $instances = $this->getSingletonInstances();
        $apps = array();
        foreach($instances as $instance) {
            if($instance->applicationSettings->type == "ShipmentApplication") {
                $apps[] = $instance;
            }
        }
        return $apps;
    }
    
    
    /**
     * Used for setting the application instances that should be
     * available from the applicationpool. Should be invoked once 
     * for each transaction.
     * 
     * @param core_common_AppConfiguration[] $appConfigurations
     */
    public function setApplicationInstances($appConfigurations) {
        foreach ($appConfigurations as $appConfig) {
            /* @var $appConfig core_common_AppConfiguration */
            $appInstance = $this->createAppInstance($appConfig);
            if ($appInstance) {
                $this->addedApplicationInstances[$appConfig->id] = $appInstance; 
            }
        }
        
        $this->addMainMenu();
        $this->addBreadCrumb();
    }
    
    public function createAppInstance($appConfig) {
        $settings = $this->getApplicationSetting($appConfig->appSettingsId);
        if ($settings == null) {
            
            return;
        }

        $appInstance = $this->createInstace($settings);
        if($appInstance != null) {
            $appInstance->setConfiguration($appConfig);
            return $appInstance;
        }
        
        return null;
    }
    
    /**
     * Creates an empty instance 
     * 
     * @param type $applicationSetting
     * @return ApplicationBase 
     */
    public function createInstace($applicationSetting) {
		if (!$applicationSetting) {
			throw new Exception("Empty application");
		}
        $instance = $this->factory->convertUUIDtoString($applicationSetting->id) . "\\" . $applicationSetting->appName;
        if(class_exists($instance)) {
            $appInstance = new $instance();
            $appInstance->setApplicationSettings($applicationSetting);
            return $appInstance;
        }
        
//        TOOD - Log this.
//        echo "Did not find php code for app: ".$instance."<br>";
        
        return null;
    }
    
    /**
     * Returns of all available applications settings that 
     * this webpage has access to.
     * 
     * @return core_appmanager_data_ApplicationSettings[]
     */
    public function getAllApplicationSettings() {
        return $this->applicationList;
    }
    
    /**
     * Returns of all available applications settings that 
     * this webpage has access to for a specified area.
     * 
     * @return core_appmanager_data_ApplicationSettings[]
     */
    public function getAllApplicationSettingsForArea($area) {
        $apps = array();
        
        foreach ($this->applicationList as $myApp) {
            if(isset($myApp->allowedAreas) && is_array($myApp->allowedAreas) && ($area == null || in_array($area, $myApp->allowedAreas))) {
                $apps[] = $myApp;
            }
        }
        
        return $apps;
    }
    
    
    public function convertApplicationsSettingsToAppinstances($applicationSettingArray) {
        $apps = array();
        foreach($applicationSettingArray as $myApp) {
            /* @var $myApp core_appmanager_data_ApplicationSettings */
            $namespace = $this->factory->convertUUIDtoString($myApp->id);
            $instance = $namespace."\\".$myApp->appName;
            if(class_exists($instance)) {
                $instance = new $instance();
                $configuration = API::core_common_AppConfiguration();
                $configuration->appSettingsId = $myApp->id;
                $configuration->appName = $myApp->appName;
                $instance->setConfiguration($configuration);
                $instance->applicationSettings = $myApp;
                $apps[] = $instance;
            }
        }
        
        return $apps;
    }

    /**
     * Return a specified application setting specified by id
     * 
     * @return core_appmanager_data_ApplicationSettings
     */
    public function getApplicationSetting($id) {
        foreach ($this->getAllApplicationSettings() as $app) {
            /* @var $app core_appmanager_data_ApplicationSettings */
            if ($app->id == $id) {
                return $app;
            }
        }

        return null;
    }

    /**
     * Returns a list of added application instances for a given namespace.
     * 
     * @param type $namespace
     * @return ApplicationBase[]
     */
    public function getApplicationsInstancesByNamespace($namespace) {
        $retval = array();
        foreach ($this->addedApplicationInstances as $app) {
            $ns = $this->factory->convertUUIDtoString($app->getApplicationSettings()->id);
            if ($ns == $namespace) {
                $retval[] = $app;
            }
        }
        return $retval;
    }
    
    /**
     * Returns a specified application instance, if the instance for the specified id
     * does not exists, it will return null.
     * 
     * @param type $id
     * @return ApplicationBase
     */
    public function getApplicationInstance($id) {
        foreach ($this->addedApplicationInstances as $app) {
            if ($app->getConfiguration()->id == $id) {
                return $app;
            }
        }
        
        return null;
    }
    

    /**
     * Returns a list of singleton instances 
     * that has been added to the webpage.
     * 
     * @return ApplicationBase[]
     */
    public function getSingletonInstances() {
        $retval = array();
        
        foreach($this->addedApplicationInstances as $app) {
            /* @var $app ApplicationBase */
            if (isset($app->getApplicationSettings()->isSingleton) && $app->getApplicationSettings()->isSingleton) {
                 $retval[] = $app;
            }
        }
        return $retval;
    }

    /**
     * Returns a list of applications that support the
     * getshop standalone functionallity
     * 
     * @return ApplicationBase[]
     */
    public function getStandaloneInstances() {
        $retval = array();
        foreach($this->addedApplicationInstances as $app) {
            if (isset($app->getApplicationSettings()->renderStandalone) && $app->getApplicationSettings()->renderStandalone) {
                 $retval[] = $app;
            }
        }
        return $retval;
    }

    /**
     * Returns all the added instances
     * 
     * @return ApplicationBase[]
     */
    public function getAllAddedInstances() {
        return $this->addedApplicationInstances;
    }

    /**
     * Returns a list of all application settings for all 
     * singleton applicationsettings.
     * 
     * @return core_applicationmanager_ApplicationSettings[]
     */
    public function getNotAddedSingletonApplicationSettings() {
        $apps = $this->getAllApplicationSettings();
        $retval = array();
        foreach($apps as $app) {
            $namespace = $this->factory->convertUUIDtoString($app->id);
            $count = $this->getApplicationsInstancesByNamespace($namespace);
            if(count($count) == 0 && $app->isSingleton) {
                $retval[] = $app;
            }
        }

        return $retval;
    }

    /**
     * TODO clean this method?
     * maybe remove the the convertUUIDtoString?
     * @param type $id
     * @return type
     */
    public function getNameSpace($id) {
        return $this->factory->convertUUIDtoString($id);
    }

    
    /**
     * 
     * @param type $type
     * @return core_appmanager_data_ApplicationSettings[]
     */
    public function getAllApplicationSettingsByType($type) {
        $retval = array();
        foreach($this->applicationList as $app) {
            /* @var $app core_appmanager_data_ApplicationSettings */
            if($app->type == $type) {
                $retval[] = $app;
            }
        }

        return $retval;
    }

    /**
     * returns the namespace for a given application name
     * @param type $appName
     */
    public function getNamespaceByApplicationName($appName) {
        foreach($this->applicationList as $app) {
            if ($app->appName == $appName) {
                return $this->getNameSpace($app->id);
            }
        }
        
        return null;
    }

    /**
     * Return the current selected theme application, 
     * if no selected theme application is added it will return the default one.
     * @return type
     * @throws ThemeApplication
     */
    public function getSelectedThemeApp() {
		$app = $this->factory->getApi()->getStoreApplicationPool()->getThemeApplication();
		return $app;
    }
    
    /**
     * This function is added because MainMenu is not part of the 
     * webpage application list.
     */
    public function addMainMenu() {
        $mainMenu = new \ns_bf35979f_6965_4fec_9cc4_c42afd3efdd7\MainMenu();
        $config = new core_common_AppConfiguration();
        $config->id = "bf35979f-6965-4fec-9cc4-c42afd3efdd7";
        $mainMenu->setConfiguration($config);
        $appSettings = new core_applicationmanager_ApplicationSettings();
        $appSettings->type = "SystemApplication";
        $mainMenu->setApplicationSettings($appSettings);
        $this->addedApplicationInstances[] = $mainMenu;
    }

    /**
     * This function is added because BreadCrumb is not part of the 
     * webpage application list.
     */
    public function addBreadCrumb() {
        $breadCrumb = new ns_7093535d_f842_4746_9256_beff0860dbdf\BreadCrumb();
        $config = new core_common_AppConfiguration();
        $config->id = "7093535d-f842-4746-9256-beff0860dbdf";
        $breadCrumb->setConfiguration($config);
        $appSettings = new core_applicationmanager_ApplicationSettings();
        $appSettings->type = "SystemApplication";
        $breadCrumb->setApplicationSettings($appSettings);
        $this->addedApplicationInstances[] = $breadCrumb;
    }

    /**
     * Returns a list of applications that 
     * wants to print data for the application widget
     * area
     * 
     * PS: This is a static application instance.
     * It will not have an appConfiguration object.
     * 
     * @param ApplicationBase[]
     */
    public function getApplicationsByWidgetArea($widgetAreaName) {
        $apps = array();
        
        foreach($this->addedApplicationInstances as $app) {
            $settings = $app->getApplicationSettings();
            
            if (!isset($settings->connectedWidgets) || !$settings->connectedWidgets)
                continue;

            if(array_key_exists($widgetAreaName, $app->getApplicationSettings()->connectedWidgets)) {
                $function = $app->getApplicationSettings()->connectedWidgets->{$widgetAreaName};
                $apps[$function] = $app;
            }
        }
        
        return $apps;
    }

    public function getAllPaymentInstances() {
        $instances = $this->getAllAddedInstances();
        $allinstances = array();
        foreach($instances as $app) {
            if($app instanceof PaymentApplication) {
                $allinstances[] = $app;
            }
        }
        
        return $allinstances;
    }

}
?>
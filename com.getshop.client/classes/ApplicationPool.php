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

    public function setApplicationInstances($javaAppinstances) {
        if (isset($javaAppinstances)) {
            foreach ($javaAppinstances as $instance) {
                $this->loadApplicationInstance($instance->id);
            }
        }
    }

    public function getShipmentApplicationInstances() {
        $instances = $this->getSingletonInstances();
        $apps = array();
        foreach ($instances as $instance) {
            if ($instance->applicationSettings->type == "ShipmentApplication") {
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
    private function loadApplicationInstance($applicationInstanceId) {
        if (array_key_exists($applicationInstanceId, $this->addedApplicationInstances)) {
            return;
        }

        $appInstanceRaw = $this->factory->getApi()->getStoreApplicationInstancePool()->getApplicationInstance($applicationInstanceId);

        if ($appInstanceRaw != null) {
            $appInstance = $this->createAppInstance($appInstanceRaw);
            if ($appInstance) {
                $this->addedApplicationInstances[$appInstanceRaw->id] = $appInstance;
            }
        }
    }
    
    public function createNewInstance($appid) {
        return $this->factory->getApi()->getStoreApplicationInstancePool()->createNewInstance($appid);
    }

    public function createAppInstance($appConfig) {
        $settings = $this->getApplicationSetting($appConfig->appSettingsId);

        if ($settings == null) {
            return;
        }

        $appInstance = $this->createInstace($settings);
        if ($appInstance != null) {
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
            return null;
        }
        $instance = $this->factory->convertUUIDtoString($applicationSetting->id) . "\\" . $applicationSetting->appName;
        if (class_exists($instance)) {
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
            if (isset($myApp->allowedAreas) && is_array($myApp->allowedAreas) && ($area == null || in_array($area, $myApp->allowedAreas))) {
                $apps[] = $myApp;
            }
        }

        return $apps;
    }

    public function convertApplicationsSettingsToAppinstances($applicationSettingArray) {
        $apps = array();
        foreach ($applicationSettingArray as $myApp) {
            /* @var $myApp core_appmanager_data_ApplicationSettings */
            $namespace = $this->factory->convertUUIDtoString($myApp->id);
            $instance = $namespace . "\\" . $myApp->appName;
            if (class_exists($instance)) {
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
     * Returns a specified application instance, if the instance for the specified id
     * does not exists, it will return null.
     * 
     * @param type $id
     * @return ApplicationBase
     */
    public function getApplicationInstance($applicationInstanceId) {
        $this->loadApplicationInstance($applicationInstanceId);

        foreach ($this->addedApplicationInstances as $app) {
            if (method_exists($app, "getConfiguration")) {
                if ($app->getConfiguration()->id == $applicationInstanceId) {
                    return $app;
                }
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

        foreach ($this->addedApplicationInstances as $app) {
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
        foreach ($this->addedApplicationInstances as $app) {
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
        foreach ($apps as $app) {
            $namespace = $this->factory->convertUUIDtoString($app->id);
            $count = $this->getApplicationsInstancesByNamespace($namespace);
            if (count($count) == 0 && $app->isSingleton) {
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
        foreach ($this->applicationList as $app) {
            /* @var $app core_appmanager_data_ApplicationSettings */
            if ($app->type == $type) {
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
        foreach ($this->applicationList as $app) {
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
    
    public function getSelectedThemeAppInstance() {
        $app = $this->getSelectedThemeApp();
        if ($app != null) {
            return $this->createInstace($app);
        }
        
        return null;
    }

    public function getAllPaymentInstances() {
        $instances = $this->getAllAddedInstances();
        $allinstances = array();
        foreach ($instances as $app) {
            if ($app instanceof PaymentApplication) {
                $allinstances[] = $app;
            }
        }

        return $allinstances;
    }

    public function initForPage($pagemanager, $page) {
        $applications = $pagemanager->getApplicationsForPage($page->getId());
        $this->setApplicationInstances($applications);
    }

}

?>
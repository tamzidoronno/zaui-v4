<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of IocContainer
 *
 * @author ktonder
 */
class IocContainer {
    static $factory;
    static $settingsFactory;
    
    /**
     * @return Factory
     */
    public static function getFactorySingelton($loadPages = true) {
        if (isset(IocContainer::$factory)) {
            return IocContainer::$factory;
        }
        
        IocContainer::$factory = new Factory();
        IocContainer::$factory->start($loadPages);
        
        return IocContainer::$factory;
    }

    public static function getSettingsFactorySingleton()  {
        if (isset(IocContainer::$settingsFactory)) {
            return IocContainer::$settingsFactory;
        }
        
        IocContainer::$settingsFactory = new SettingsFactory();
        return IocContainer::$settingsFactory;
    }
}

?>

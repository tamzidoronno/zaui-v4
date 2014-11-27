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
    
    /**
     * @return Factory
     */
    public static function getFactorySingelton($loadPages = true, $loadHeader = true) {
        if (isset(IocContainer::$factory)) {
            return IocContainer::$factory;
        }
        
        IocContainer::$factory = new Factory($loadHeader);
        IocContainer::$factory->start($loadPages);
        
        return IocContainer::$factory;
    }
    
}

?>

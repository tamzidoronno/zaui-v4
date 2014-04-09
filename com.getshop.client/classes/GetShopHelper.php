<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of GetShopHelper
 *
 * @author ktonder
 */
class GetShopHelper {
    public static function makeSeoUrl($name) {
        $factory = IocContainer::getFactorySingelton();
        $name = str_replace(" ", "_", $name);
        $name = str_replace("/", "_", $name);
        $name = str_replace("&", "_", $name);
        $name = str_replace("\"", "_''", $name);
        $name = strtolower($name);
        return "/$name.html";
    }
}

?>

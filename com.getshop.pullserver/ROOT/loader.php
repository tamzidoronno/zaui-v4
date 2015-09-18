<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Loader
 *
 * @author ktonder
 */

function __autoload($class_name) {
    
    $class_name = str_replace("\\", "/", $class_name);
    
    $apipath = str_replace("_", "/", $class_name);
    if (file_exists("../events/".  $apipath.".php")) {
        include_once "../events/".$apipath.".php";
    }
    
    if (file_exists("../".  str_replace("_", "/", $class_name).".php")) {    
        include_once "../".  str_replace("_", "/", $class_name).".php";
    }
    
    if (file_exists("../classes/$class_name.php")) {
        include_once "../classes/$class_name.php";
    }
    
    if (file_exists("../helpers/$class_name.php")) {
        include_once "../helpers/$class_name.php";
    }
    
    if (file_exists("../classes/builders/$class_name.php")) {
        include_once "../classes/builders/$class_name.php";
    }
    
    if (file_exists("../app/$class_name.php")) {
        include_once "../app/$class_name.php";
    }
    if (file_exists("../classes/api/$class_name.php")) {
        include_once "../classes/api/$class_name.php";
    }
    
  
    if (file_exists("../classes/helpers/$class_name.php")) {
        include_once "../classes/helpers/$class_name.php";
    }
}

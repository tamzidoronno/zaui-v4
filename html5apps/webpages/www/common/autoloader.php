<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
spl_autoload_register(function ($class_name) {
    if (file_exists('../common/'.$class_name.".php")) {
        include '../common/'.$class_name.".php";
    }
    
    $domain = Config::$domain;
    if (file_exists($domain.'/'.$class_name."/".$class_name.".php")) {
        include $domain.'/'.$class_name."/".$class_name.".php";
    }
    
    $domain = Config::$domain;
    if (file_exists($domain.'/'.$class_name.".php")) {
        include $domain.'/'.$class_name.".php";
    }
    
    if (file_exists('../plugins/'.$class_name."/".$class_name.".php")) {
        include '../plugins/'.$class_name."/".$class_name.".php";
    }
});
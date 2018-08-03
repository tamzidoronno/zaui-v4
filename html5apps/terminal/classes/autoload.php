<?php
include_once("events/API.php");
include_once("events/API2.php");

spl_autoload_register(function ($class_name) {
    $filename = $class_name . '.php';
    if (file_exists('classes/'.$filename)) {
        include "classes/$filename";
    }
    
    if (file_exists('pages/'.strtolower($class_name)."/".$filename)) {
        include 'pages/'.strtolower($class_name)."/".$filename;
    }
    
    if (file_exists('pages/'.$filename)) {
        include 'pages/'.$filename;
    }
    
    $apipath = str_replace("_", "/", $class_name);
    if (file_exists("events/".  $apipath.".php")) {
        include_once "events/".$apipath.".php";
    }
})
?>
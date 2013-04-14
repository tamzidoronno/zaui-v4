<?php
session_start();
include_once("../events/API2.php");
include_once("../events/CommunicationHelper.php");



function __autoload($class_name) {
     if (file_exists($class_name.".php")) {    
         include_once($class_name.".php");
     }
     if (file_exists("managers/".$class_name.".php")) {    
         include_once("managers/".$class_name.".php");
     }
     if (file_exists("../classes/".$class_name.".php")) {    
         include_once("../classes/".$class_name.".php");
     }
     if (file_exists("../events/".  str_replace("_", "/", $class_name).".php")) {    
         include "../events/".  str_replace("_", "/", $class_name).".php";
     }
}

$port = 25554;

if(isset($argv[1]) && $argv[1] == "alttest") {
    $port = 20000;
}

$api = new GetShopApi($port);

//Setting up the environment.
$uid = uniqid("gs_");
$store = $api->getStoreManager()->createStore($uid.".getshop.com", "test@getshop.com", "testpassword");
$api->getStoreManager()->initializeStore($uid.".getshop.com", session_id());

$user = API::core_usermanager_data_User();
$user->username = "test@getshop.com";
$user->password = "getshoptestpassword";

$api->getUserManager()->createUser($user);
$api->getUserManager()->logOn($user->username, $user->password);

if(sizeof($api->transport->errors) > 0) {
    echo "Failed to initialize webshop\n";
    print_r($api->transport->errors);
    exit(0);
}

class error_set {
    static $errors = array();
}

function handleError($errno, $errstr, $errfile, $errline, array $errcontext)
{
    error_set::$errors[] = $errfile." ".$errline." ".$errstr;
}

set_error_handler('handleError');

$files = scandir("./managers");
foreach($files as $file) {
    if($file == ".." || $file == ".") {
        continue;
    }
    echo $file . "\n";
    $managerfile = str_replace(".php", "", $file);
        if(!stristr($managerfile, "Manager")) {
            continue;
    }
    $manager = new $managerfile($api);
    $methods = get_class_methods($manager);
    foreach($methods as $method) {
        if(stristr($method, "test_")) {
            echo "\t" . $method . "\n";
//            if($method == "test_isLoggedIn")
                $manager->$method();  
        }
    }
}

echo "\n\n\n";
echo "--------------------------------------------------------------------\r\n";
echo "-- Report\n";
echo "--------------------------------------------------------------------\r\n";
if(sizeof($api->transport->errors) == 0 && count(error_set::$errors) == 0) {
    echo "No errors where found!\n";
} else {
    if (count(error_set::$errors) > 0)
        print_r(error_set::$errors);
    else
        print_r($api->transport->errors);
}

?>
<?php

header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Credentials: true");
header('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
header('Access-Control-Max-Age: 1000');
header('Access-Control-Allow-Headers: Origin, Content-Type, X-Auth-Token , Authorization');

chdir('../../');
include_once('../loader.php') ;

$storeId = false;

//get some info about which store we are running for
try
{
    $factory = IocContainer::getFactorySingelton();
    $storeId = $factory->getStore()->id;
}
catch(Exception $e)
{
    // do nothing more here....
    header('HTTP/1.1 500');
    die('500 - INVALID REQUEST');
}

include_once ('../apps/GslBooking/zaui/GslZaui.php');

?>
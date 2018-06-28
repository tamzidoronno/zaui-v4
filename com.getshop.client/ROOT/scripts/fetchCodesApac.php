<?php

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$loggedIn = $factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);

if (!$loggedIn || $loggedIn->type < 50) {
    header('HTTP/1.0 403 Forbidden');
    echo 'You are forbidden!';
    die();
}


$slots = $factory->getApi()->getGetShopLockSystemManager()->getCodesInUse($_GET['serverId'], $_GET['lockId']);

$codes = array();

echo ",";

foreach($slots as $slot) {
    echo $slot->code->pinCode . ",";
}

$factory->getApi()->getUserManager()->logout();

?>
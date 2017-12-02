<?php

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$lock = $factory->getApi()->getGetShopLockSystemManager()->getLock($_GET['serverId'], $_GET['lockId']);
$factory->getApi()->getUserManager()->logout();

$codes = array();

echo ",";

foreach($lock->userSlots as $slot) {
    if ($slot->code) {
        echo $slot->code->pinCode . ",";
    }
}

?>
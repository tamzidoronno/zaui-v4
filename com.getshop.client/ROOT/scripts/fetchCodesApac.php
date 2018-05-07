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


$lock = $factory->getApi()->getGetShopLockSystemManager()->getLock($_GET['serverId'], $_GET['lockId']);

$codes = array();

echo ",";

foreach($lock->userSlots as $slot) {
    if ($slot->code) {
        $isInUse = $factory->getApi()->getGetShopLockSystemManager()->isSlotTakenInUseInAnyGroups($_GET['serverId'], $_GET['lockId'], $slot->slotId);
        if ($isInUse) {
            echo $slot->code->pinCode . ",";
        }
    }
}

$factory->getApi()->getUserManager()->logout();

?>
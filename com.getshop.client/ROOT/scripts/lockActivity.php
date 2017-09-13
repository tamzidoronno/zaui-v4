<?php

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getGetShopLockManager()->triggerFetchingOfCodes($_GET['domainname'], $_GET['ip'], $_GET['deviceId']);
?>
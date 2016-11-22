<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getGetShopLockManager()->accessEvent($_GET['engine'], $_GET['id'], $_GET['code'], $_GET['domain']);
?>
<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getGetShop()->changeLeadState($_GET['id'], $_GET['state']);
?>
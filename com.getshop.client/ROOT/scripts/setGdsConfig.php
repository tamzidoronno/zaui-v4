<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton(false);
header("Content-type: application/json; charset=utf-8");
$factory->getApi()->getGdsManager()->setGdsConfig($_GET['token'], $_GET['key'], $_GET['value']);
?>
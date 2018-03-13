<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getOAuthManager()->handleCallback($_GET['code'], $_GET['state']);
header("Location:/");
?>
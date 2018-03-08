<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$state = $_GET['state'];
$code = $_GET['code'];
$redirect = $factory->getApi()->getOAuthManager()->handleCallback($code, $state);
header("location: /");
?>

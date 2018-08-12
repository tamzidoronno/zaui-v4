<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getGetShop()->addLeadHistory($_POST['leadid'], $_POST['comment']);

echo date("d.m.Y H:i", time()) . " : "  . $_POST['comment'];
?>
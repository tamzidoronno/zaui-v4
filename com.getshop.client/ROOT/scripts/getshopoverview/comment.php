<?php

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$cust = $factory->getApi()->getPmsGetShopOverView()->getCustomerObject($_GET['storeid']);
$cust->comment = $_GET['comment'];
$factory->getApi()->getPmsGetShopOverView()->saveCustomerObject($cust);
?>
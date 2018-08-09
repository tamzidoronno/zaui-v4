<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$cust = $factory->getApi()->getPmsGetShopOverView()->getCustomerObject($_GET['storeid']);
$cust->{$_GET['attribute']} = !$cust->{$_GET['attribute']};
$factory->getApi()->getPmsGetShopOverView()->saveCustomerObject($cust);
?>

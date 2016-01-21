<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$res = $factory->getApi()->getXLedgerManager()->createOrderFile();
print_r($res);
?>


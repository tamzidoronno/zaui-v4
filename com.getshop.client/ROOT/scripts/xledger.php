<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$res = $factory->getApi()->getXLedgerManager()->createOrderFile();
foreach($res as $r) {
    echo $r . "<br>";
}
?>


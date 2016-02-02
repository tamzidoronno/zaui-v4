<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

if($_GET['type'] == "user") {
    $res = $factory->getApi()->getXLedgerManager()->createUserFile();
} else {
    $res = $factory->getApi()->getXLedgerManager()->createOrderFile();
}
foreach($res as $r) {
    echo $r . "<br>";
}
?>


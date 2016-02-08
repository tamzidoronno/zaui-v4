<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$date = date("d.m.Y_H_i", time());

if(!isset($_GET['type'])) {
    $_GET['type'] = "order";
}

$name = "orderfile_".$date;
if($_GET['type'] == "user") {
    $name = "userfile_".$date;
}

header("Content-type: text/csv");
header("Content-Disposition: attachment; filename=$name.csv");
header("Pragma: no-cache");
header("Expires: 0");


if($_GET['type'] == "user") {
    $res = $factory->getApi()->getXLedgerManager()->createUserFile();
} else {
    $res = $factory->getApi()->getXLedgerManager()->createOrderFile();
}
if(!$res) {
    $res = array();
}

foreach($res as $r) {
    echo $r;
}
?>


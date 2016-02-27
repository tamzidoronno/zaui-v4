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


if(isset($_GET['id'])) {
    $res = $factory->getApi()->getAccountingManager()->getFile($_GET['id']);
} else if($_GET['type'] == "user") {
    if(isset($_GET['onlynew'])) {
        $res = $factory->getApi()->getAccountingManager()->createUserFile(true);
    } else {
        $res = $factory->getApi()->getAccountingManager()->createUserFile(false);
    }
} else {
    $res = $factory->getApi()->getAccountingManager()->createOrderFile();
}
if(!$res) {
    $res = array();
}

foreach($res as $r) {
    echo $r;
}
?>


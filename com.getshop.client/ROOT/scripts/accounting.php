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
} else if($_GET['type'] == "transfercreditor") {
    $res = $factory->getApi()->getAccountingManager()->transferFilesToCreditor();
} else if($_GET['type'] == "creditor") {
    $res = $factory->getApi()->getAccountingManager()->createCreditorFile($_GET["onlynew"]);
} else if($_GET['type'] == "combined") {
    $res = $factory->getApi()->getAccountingManager()->createCombinedOrderFile($_GET["onlynew"]);
} else if($_GET['type'] == "user") {
    if(isset($_GET['onlynew'])) {
        $res = $factory->getApi()->getAccountingManager()->createUserFile(true);
    } else {
        $res = $factory->getApi()->getAccountingManager()->createUserFile(false);
    }
} else if(isset($_GET['subtype'])) {
    $type = $_GET['type'];
    $subtype = $_GET['subtype'];
    if($subtype == "transfer") {
        $res = $factory->getApi()->getAccountingManager()->transferFiles($_GET["type"]);
    } else {
        $res = $factory->getApi()->getAccountingManager()->getNewFile($_GET["type"]);
    }
} else {
    $res = $factory->getApi()->getAccountingManager()->createOrderFile();
}
if(!$res) {
    $res = array();
}

foreach($res as $r) {
    echo mb_convert_encoding($r, "ISO-8859-1", "UTF-8");
}
?>


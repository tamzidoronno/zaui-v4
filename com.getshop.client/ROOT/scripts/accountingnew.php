<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$start = date("M d, Y h:i:s A", strtotime($_GET['start']));
$end = date("M d, Y h:i:s A", strtotime($_GET['end']));
$file = $factory->getApi()->getAccountingManager()->downloadOrderFileNewType($_GET['configid'], $start, $end);
if(!$file) {
    return;
}

$config = $factory->getApi()->getAccountingManager()->getAccountingConfig($_GET['configid']);

$date = date("d.m.Y_H_i", time());

if(!isset($_GET['type'])) {
    $_GET['type'] = "order";
}

$name = $config->transferType ."_" . $config->subType . "_".$date;

header("Content-type: text/csv");
header("Content-Disposition: attachment; filename=$name.csv");
header("Pragma: no-cache");
header("Expires: 0");

$res = "";
foreach($file->result as $line) {
    $res .= $line;
}
echo mb_convert_encoding($res, "ISO-8859-1", "UTF-8");
?>
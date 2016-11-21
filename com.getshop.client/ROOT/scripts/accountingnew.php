<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$start = date("M d, Y h:i:s A", strtotime($_GET['start']));
$end = date("M d, Y h:i:s A", strtotime($_GET['end']));
if(isset($_GET['configid'])) {
    $file = $factory->getApi()->getAccountingManager()->downloadOrderFileNewType($_GET['configid'], $start, $end);
    $config = $factory->getApi()->getAccountingManager()->getAccountingConfig($_GET['configid']);
}
if(isset($_GET['id'])) {
    $file = $factory->getApi()->getAccountingManager()->getFileById($_GET['id']);
    $config = $factory->getApi()->getAccountingManager()->getAccountingConfig($file->configId);
    $start = $file->startDate;
    $end = $file->endDate;
}

if(!$file) {
    echo "Unable to download this file, check if the specified time periode is correct. Please note that you can not transfer orders a second time, time periode tried downloading: $start - $end";
    return;
}


$date = date("d.m.Y_H_i", time());

if(!isset($_GET['type'])) {
    $_GET['type'] = "order";
}

$name = $config->subType . "_".date("d.m.Y", strtotime($start)) . "-" . date("d.m.Y", strtotime($end));

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
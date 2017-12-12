<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$file = $factory->getApi()->getGetShopAccountingManager()->getOrderFile($_GET['fileid']);

$name = $file->subtype . "_".date("d.m.Y", strtotime($file->startDate)) . "-" . date("d.m.Y", strtotime($file->endDate));

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
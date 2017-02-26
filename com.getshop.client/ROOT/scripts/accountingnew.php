<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

if(isset($_GET['configid'])) {
    $start = date("M d, Y h:i:s A", strtotime($_GET['start']));
    $end = date("M d, Y h:i:s A", strtotime($_GET['end']));
    $config = $factory->getApi()->getAccountingManager()->getAccountingConfig($_GET['configid']);
    $file = $factory->getApi()->getAccountingManager()->downloadOrderFileNewType($config->id, $start, $end);
} else if(isset($_GET['id'])) {
    $file = $factory->getApi()->getAccountingManager()->getFileByIdResend($_GET['id']);
    $config = $factory->getApi()->getAccountingManager()->getAccountingConfig($file->configId);
    $start = $file->startDate;
    $end = $file->endDate;
} else if(isset($_GET['singleids'])) {
    $ids = $_GET['ids'];
    if(!trim($ids)) {
        $ids = array();
    } else {
        $ids = explode(",", $ids);
    }
    
    if(!is_array($ids)) {
        $ids = array();
    }
    print_r($ids);
    $file = $factory->getApi()->getAccountingManager()->transferSingleOrders($_GET['singleids'], $ids);
    $config = $factory->getApi()->getAccountingManager()->getAccountingConfig($_GET['singleids']);
}

if(!$file) {
    echo "Unable to download this file, check if the specified time periode is correct. Please note that you can not transfer orders a second time, time periode tried downloading: $start - $end";
    $logentries = $factory->getApi()->getAccountingManager()->getLatestLogEntries();
    echo "<br><br><b>From log</b><br>";
    foreach($logentries as $entry) {
        echo $entry . "<br>";
    }
    return;
}



$date = date("d.m.Y_H_i", time());

if(!isset($_GET['type'])) {
    $_GET['type'] = "order";
}
if(isset($_GET['singleids'])) {
    $name = $config->subType;
} else {
    $name = $config->subType . "_".date("d.m.Y", strtotime($start)) . "-" . date("d.m.Y", strtotime($end));
}

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
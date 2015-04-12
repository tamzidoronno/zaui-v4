<?php

include '../loader.php';

$sender = $_GET['apid_id'];
$apiMsgId = $_GET['apiMsgId'];
$cliMsgId = $_GET['cliMsgId'];
$timestamp = (int)$_GET['timestamp'];
$status = (int)$_GET['status'];
$charge = $_GET['charge'];

$factory = IocContainer::getFactorySingelton();

$myfile = fopen("/tmp/clickatelltest.txt", "w") or die("Unable to open file!");
fwrite($myfile, json_encode($_GET));
fclose($myfile);
?>
